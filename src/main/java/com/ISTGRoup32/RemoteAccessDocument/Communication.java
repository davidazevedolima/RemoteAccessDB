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
    private Cryptography cryptography;

    UserDao userDao;
    DocumentDao documentDao;

    public Communication(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.userDao = SpringUtils.getBean(UserDao.class);
        this.documentDao = SpringUtils.getBean(DocumentDao.class);
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

    public void handleClient() throws IOException, JSONException, RuntimeException {
        clientSocket = this.serverSocket.accept();
        clientSocket.setTcpNoDelay(true);

        System.out.println("Handling client...");
        Long seq = handshake();

        JSONObject jsonIn = receiveJson();

        Cryptography.verifySequence(jsonIn.getLong("seq"), seq);
        seq += 1;

        String request = jsonIn.getString("request");
        JSONObject body;

        JSONObject jsonOut;
        User user;
        Long id;
        Long docId;

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
                body = jsonIn.getJSONObject("body");
                id = body.getLong("userId");
                List<Document> documents = documentDao.getDocuments(id);
                jsonOut = Json.buildResponseArray("ok", Json.fromDocumentList(documents), seq);
                sendJson(jsonOut);
                break;

            case "deleteDocument":
                body = jsonIn.getJSONObject("body");
                id = body.getLong("id");
                documentDao.deleteDocument(id);
                break;

            case "getDocument":
                body = jsonIn.getJSONObject("body");
                id = body.getLong("id");
                Document document = documentDao.getDocument(id);
                jsonOut = Json.buildResponse("ok", Json.fromDocument(document), seq);
                sendJson(jsonOut);
                break;

            case "newDocument":
                body = jsonIn.getJSONObject("body");
                Long ownerId = body.getLong("ownerId");
                Document newDocument = documentDao.newDocument(ownerId);
                jsonOut = Json.buildResponse("ok", Json.fromDocument(newDocument), seq);
                sendJson(jsonOut);
                break;

            case "shareDocument":
                body = jsonIn.getJSONObject("body");
                docId = body.getLong("docId");
                String username = body.getString("username");
                documentDao.shareDocument(docId, username);
                break;

            case "saveDocument":
                body = jsonIn.getJSONObject("body");
                docId = body.getLong("docId");
                String title = body.getString("title");
                String content = body.getString("content");
                System.out.println(docId+title+content);
                documentDao.saveDocument(docId, title, content);
                break;
        }

        clientSocket.close();
    }

}
