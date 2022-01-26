package com.ISTGRoup32.RemoteAccessDocument;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class Json {

    public static JSONObject buildResponse(String status, JSONObject body, Long seq) throws JSONException {
        JSONObject response = new JSONObject();

        response.put("response", status);
        response.put("body", body);
        response.put("seq", seq);

        response.put("digest", Cryptography.encodeMessageDigest(response.toString().getBytes()));

        return response;
    }

    public static JSONObject buildResponseArray(String status, JSONArray body, Long seq) throws JSONException {
        JSONObject response = new JSONObject();

        response.put("response", status);
        response.put("body", body);
        response.put("seq", seq);

        response.put("digest", Cryptography.encodeMessageDigest(response.toString().getBytes()));

        return response;
    }

    public static JSONObject fromUser(User user) throws JSONException {
        Gson gson = new Gson();
        String jsonUser = gson.toJson(user);

        return new JSONObject(jsonUser);
    }

    public static User toUser(JSONObject jsonObject) {
        Gson gson = new Gson();
        User user = gson.fromJson(jsonObject.toString(), User.class);

        return user;
    }

    public static JSONArray fromUserList(List<User> userList) throws JSONException {
        JSONArray array = new JSONArray();
        for (User user : userList) {
            array.put(fromUser(user));
        }
        return array;
    }

    public static List<User> toUserList(JSONArray jsonArray) throws JSONException {
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>(){}.getType();

        return gson.fromJson(jsonArray.toString(), type);
    }

    public static JSONObject fromDocument(Document document) throws JSONException {
        Gson gson = new Gson();
        String jsonDocument = gson.toJson(document);

        return new JSONObject(jsonDocument);
    }

    public static Document toDocument(JSONObject jsonObject) {
        Gson gson = new Gson();
        Document document = gson.fromJson(jsonObject.toString(), Document.class);

        return document;
    }

    public static JSONArray fromDocumentList(List<Document> documentList) throws JSONException {
        JSONArray array = new JSONArray();
        for (Document document : documentList) {
            array.put(fromDocument(document));
        }
        return array;
    }

    public static List<Document> toDocumentList(JSONArray jsonArray) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Document>>(){}.getType();

        return gson.fromJson(jsonArray.toString(), type);
    }
}
