package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Cryptography {

    public static String encodeMessageDigest(byte[] message) throws RuntimeException {
        try {
            byte[] md = MessageDigest.getInstance("SHA-256").digest(message);

            return toBase64(md);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm for message digest");
        }
    }

    public static byte[] decodeMessageDigest(String b64md) {
        return fromBase64(b64md);
    }

    public static String toBase64(byte[] md) {
        return Base64.getEncoder().encodeToString(md);
    }

    public static byte[] fromBase64(String b64md) {
        return Base64.getDecoder().decode(b64md);
    }

    public static boolean verifyIntegrity(JSONObject message) throws RuntimeException {
        try {
            String b64md = message.getString("digest");
            message.remove("digest");

            byte[] md = decodeMessageDigest(b64md);
            byte[] verifyMd = decodeMessageDigest(encodeMessageDigest(message.toString().getBytes()));

            return Arrays.equals(md, verifyMd);
        } catch (JSONException e) {
            throw new RuntimeException("No such mapping in JSON");
        }
    }

    public static void verifySequence(long seq1, long seq2) throws RuntimeException {
        if (seq1 != seq2)
            throw new RuntimeException("Sequence numbers don't match");
    }
}
