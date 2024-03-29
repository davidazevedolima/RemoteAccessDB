package com.ISTGRoup32.RemoteAccessDocument;

import com.ISTGRoup32.RemoteAccessDocument.dao.DocumentDao;
import com.ISTGRoup32.RemoteAccessDocument.dao.UserDao;
import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.List;

public class Communication {
    private final ServerSocket serverSocket;
    private Socket clientSocket;
    private byte[] sharedSecret;

    UserDao userDao;
    DocumentDao documentDao;

    public Communication(int port) throws RuntimeException {
        try {
            this.serverSocket = new ServerSocket(port);
            this.userDao = SpringUtils.getBean(UserDao.class);
            this.documentDao = SpringUtils.getBean(DocumentDao.class);
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred");
        }
    }

    public void sendJson(JSONObject json) throws RuntimeException {
        try {
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

            System.out.println("Sending: \n" + json.toString(2));

            AES aes = new AES(this.sharedSecret);
            byte[] iv = aes.generateIV();
            JSONObject message = Json.buildEncryptedMessage(
                    aes.cipher(json.toString().getBytes(), iv),
                    iv);

            System.out.println("Sending Encrypted: \n" + message.toString(2));

            out.write(message.toString().getBytes());
            out.write('\n');
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred");
        } catch (JSONException e) {
            throw new RuntimeException("Error converting JSON to String");
        }
    }

    public JSONObject receiveJson() throws RuntimeException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            JSONObject json = new JSONObject(in.readLine());

            System.out.println("Received Encrypted: \n" + json.toString(2));

            byte[] encryptedMessage = Json.getEncryptedMessage(json);
            byte[] iv = Json.getIV(json);

            AES aes = new AES(this.sharedSecret);

            String message = new String(aes.decipher(encryptedMessage, iv));
            json = new JSONObject(message);

            System.out.println("Received: \n" + json.toString(2));

            if (!Cryptography.verifyIntegrity(json))
                throw new RuntimeException("Modified message received");

            return json;
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred");
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON");
        }
    }

    public Long handshake() {
        try {
            JSONObject jsonIn = receiveJson();
            String request = jsonIn.getString("request");

            if (!request.equals("handshake"))
                throw new RuntimeException("Client request without handshake");

            SecureRandom secureRandom = new SecureRandom();
            Long seq = secureRandom.nextLong();

            sendJson(Json.buildResponse("ok", null, seq));
            return seq + 1;
        } catch (JSONException e) {
            throw new RuntimeException("No such mapping exists in JSON");
        }
    }

    public void handleClient() {
        try {
            clientSocket = this.serverSocket.accept();
            clientSocket.setTcpNoDelay(true);

            System.out.println("Handling client...");

            this.sharedSecret = DH.DHKeyExchange(clientSocket);

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
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred");
        } catch (JSONException e) {
            throw new RuntimeException("No such mapping exists in JSON");
        }
    }

}
