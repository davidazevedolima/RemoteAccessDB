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

    public void handleClient() throws IOException, JSONException {
        clientSocket = this.serverSocket.accept();
        clientSocket.setTcpNoDelay(true);

        System.out.println("Handling client...");
        JSONObject jsonIn = receiveJson();
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
                    jsonOut = Json.buildResponse("nok", null);
                else
                    jsonOut = Json.buildResponse("ok", Json.fromUser(user));
                sendJson(jsonOut);
                break;
            case "listUsers":
                List<User> users = userDao.getUsers();
                jsonOut = Json.buildResponseArray("ok", Json.fromUserList(users));
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
                jsonOut = Json.buildResponseArray("ok", Json.fromDocumentList(documents));
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

    public void sendJson(JSONObject json) throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        System.out.println("Sending: " + json.toString());
        out.write(json.toString().getBytes());
        out.write('\n');
        out.flush();
    }

    public JSONObject receiveJson() throws IOException, JSONException, RuntimeException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        JSONObject json = new JSONObject(in.readLine());

        System.out.println("Received: " + json.toString());

        if (!Cryptography.verifyIntegrity(json))
            throw new RuntimeException("Modified message received");

        return json;
    }

}
