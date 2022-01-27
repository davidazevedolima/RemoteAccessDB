package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class Cryptography {

    public static String encodeMessageDigest(byte[] message) {
        try {
            byte[] md = MessageDigest.getInstance("SHA-256").digest(message);

            return toBase64(md);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm in digest");
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

    public static boolean verifyIntegrity(JSONObject message) throws JSONException {
        String b64md = message.getString("digest");
        message.remove("digest");

        byte[] md = decodeMessageDigest(b64md);
        byte[] verifyMd = decodeMessageDigest(encodeMessageDigest(message.toString().getBytes()));

        return Arrays.equals(md, verifyMd);
    }

    public static void verifySequence(long seq1, long seq2) throws RuntimeException {
        if (seq1 != seq2)
            throw new RuntimeException("Sequence numbers don't match");
    }
}
