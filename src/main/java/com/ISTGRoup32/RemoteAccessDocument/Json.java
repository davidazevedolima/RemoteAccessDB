package com.ISTGRoup32.RemoteAccessDocument;

import com.ISTGRoup32.RemoteAccessDocument.models.Document;
import com.ISTGRoup32.RemoteAccessDocument.models.User;
import com.ISTGRoup32.RemoteAccessDocument.models.UserDocument;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class Json {

    public static JSONObject buildResponse(String head, JSONObject body, Long seq) throws RuntimeException {
        try {
            JSONObject response = new JSONObject();

            response.put("response", head);
            response.put("body", body);
            response.put("seq", seq);

            response.put("digest", Cryptography.encodeMessageDigest(response.toString().getBytes()));

            return response;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON");
        }
    }

    public static JSONObject buildResponseArray(String head, JSONArray body, Long seq) throws RuntimeException {
        try {
            JSONObject response = new JSONObject();

            response.put("response", head);
            response.put("body", body);
            response.put("seq", seq);

            response.put("digest", Cryptography.encodeMessageDigest(response.toString().getBytes()));

            return response;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON");
        }
    }

    public static JSONObject buildEncryptedMessage(byte[] encrypted, byte[] iv) throws RuntimeException {
        try {
            JSONObject message = new JSONObject();

            message.put("message", Cryptography.toBase64(encrypted));
            message.put("iv", Cryptography.toBase64(iv));

            return message;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON");
        }
    }

    public static byte[] getEncryptedMessage(JSONObject json) throws RuntimeException {
        try {
            return Cryptography.fromBase64(json.getString("message"));
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting from JSON");
        }
    }

    public static byte[] getIV(JSONObject json) throws RuntimeException {
        try {
            return Cryptography.fromBase64(json.getString("iv"));
        } catch (JSONException e) {
            throw new RuntimeException("Error extracting from JSON");
        }
    }

    public static JSONObject fromUser(User user) throws RuntimeException {
        try {
            Gson gson = new Gson();
            String jsonUser = gson.toJson(user);

            return new JSONObject(jsonUser);
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON from User");
        }
    }

    public static User toUser(JSONObject jsonObject) {
        Gson gson = new Gson();

        return gson.fromJson(jsonObject.toString(), User.class);
    }

    public static JSONArray fromUserList(List<User> userList) {
        JSONArray array = new JSONArray();
        for (User user : userList) {
            array.put(fromUser(user));
        }
        return array;
    }

    public static List<User> toUserList(JSONArray jsonArray) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<User>>(){}.getType();

        return gson.fromJson(jsonArray.toString(), type);
    }

    public static JSONObject fromDocument(Document document) throws RuntimeException {
        try {
            Gson gson = new Gson();
            String jsonDocument = gson.toJson(document);

            return new JSONObject(jsonDocument);
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON from document");
        }
    }

    public static Document toDocument(JSONObject jsonObject) {
        Gson gson = new Gson();

        return gson.fromJson(jsonObject.toString(), Document.class);
    }

    public static JSONArray fromDocumentList(List<Document> documentList) {
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

    public static UserDocument toUserDocument(JSONObject jsonObject) {
        Gson gson = new Gson();

        return gson.fromJson(jsonObject.toString(), UserDocument.class);
    }

    public static JSONObject fromUserDocument(UserDocument userDocument) throws RuntimeException {
        try {
            Gson gson = new Gson();
            String jsonUser = gson.toJson(userDocument);

            return new JSONObject(jsonUser);
        } catch (JSONException e) {
            throw new RuntimeException("Error creating JSON from UserDocument");
        }
    }

    public static JSONObject fromsString(String documentShared) throws RuntimeException {
        try {
            JSONObject jsonDocumentShared = new JSONObject();
            jsonDocumentShared.put("response", documentShared);
            return jsonDocumentShared;
        } catch (JSONException e) {
            throw new RuntimeException("Error creating UserDocument from JSON");
        }
    }
}
