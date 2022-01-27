package com.ISTGRoup32.RemoteAccessDocument;

import com.ISTGRoup32.RemoteAccessDocument.dao.DocumentDao;
import com.ISTGRoup32.RemoteAccessDocument.dao.UserDao;
import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class Communication {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private Cryptography cryptography;
    private DigitalSignature digitalSignature;

    UserDao userDao;
    DocumentDao documentDao;

    public Communication(int port) throws IOException, UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        this.serverSocket = new ServerSocket(port);
        this.userDao = SpringUtils.getBean(UserDao.class);
        this.documentDao = SpringUtils.getBean(DocumentDao.class);
        this.digitalSignature = new DigitalSignature();
    }

    public void sendJson(JSONObject json) throws IOException, JSONException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Sending: \n" + json.toString(2));
        out.write(json.toString().getBytes());
        out.write('\n');
        out.flush();
    }

    public JSONObject receiveJson() throws IOException, JSONException, RuntimeException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        JSONObject json = new JSONObject(in.readLine());

        System.out.println("Received: \n" + json.toString(2));

        if (!Cryptography.verifyIntegrity(json))
            throw new RuntimeException("Modified message received");

        return json;
    }

    public Long handshake() throws JSONException, IOException {
        JSONObject jsonIn = receiveJson();
        String request = jsonIn.getString("request");

        if (!request.equals("handshake"))
            throw new RuntimeException("Client request without handshake");

        SecureRandom secureRandom = new SecureRandom();
        Long seq = secureRandom.nextLong();

        sendJson(Json.buildResponse("ok", null, seq));
        return seq + 1;
    }

    public void handleClient() throws IOException, JSONException, RuntimeException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, UnrecoverableKeyException, NoSuchPaddingException, IllegalBlockSizeException, CertificateException, KeyStoreException, BadPaddingException {
        clientSocket = this.serverSocket.accept();
        clientSocket.setTcpNoDelay(true);

        System.out.println("Handling client...");

        byte[] sharedSecret = DH.DHKeyExchange(clientSocket);
        Long seq = handshake();

        JSONObject jsonIn = receiveJson();

        Cryptography.verifySequence(jsonIn.getLong("seq"), seq);
        seq += 1;

        String request = jsonIn.getString("request");
        JSONObject body;

        JSONObject jsonOut;
        User user;
        Long id;

        switch (request) {
            case "login":
                body = jsonIn.getJSONObject("body");
                user = userDao.verifyCredentials(Json.toUser(body));
                if (user == null)
                    jsonOut = Json.buildResponse("nok", null, seq);
                else
                    jsonOut = Json.buildResponse("ok", Json.fromUser(user), seq);
                sendJson(jsonOut);
                break;

            case "listUsers":
                List<User> users = userDao.getUsers();
                jsonOut = Json.buildResponseArray("ok", Json.fromUserList(users), seq);
                sendJson(jsonOut);
                break;

            case "registerUser":
                body = jsonIn.getJSONObject("body");
                user = Json.toUser(body);
                userDao.register(user);
                break;

            case "deleteUser":
                body = jsonIn.getJSONObject("body");
                id = body.getLong("id");
                userDao.deleteUser(id);
                break;

            case "listDocuments":
                List<Document> documents = documentDao.getDocuments();
                jsonOut = Json.buildResponseArray("ok", Json.fromDocumentList(documents), seq);
                sendJson(jsonOut);
                break;

            case "deleteDocument":
                body = jsonIn.getJSONObject("body");
                id = body.getLong("id");
                documentDao.deleteDocument(id);
                break;
        }

        clientSocket.close();
    }

}
