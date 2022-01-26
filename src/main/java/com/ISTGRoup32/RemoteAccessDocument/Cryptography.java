package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;

public class Cryptography {

    private final PrivateKey privateKey;
    private final PublicKey serverPublicKey;
    private final String privateKeyPath = "keys/db_private_key.der";
    private final String serverPublicKeyPath = "keys/server_public_key.der";

    public Cryptography() throws GeneralSecurityException, IOException {
        KeyPair keys = RSAKeyGenerator.read(privateKeyPath, serverPublicKeyPath);
        this.privateKey = keys.getPrivate();
        this.serverPublicKey = keys.getPublic();
    }

    public byte[] cipher(byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);

            return cipher.doFinal(message);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Cipher error");
        }
    }

    public byte[] decipher(byte[] message) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            return cipher.doFinal(message);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Decipher error");
        }
    }

    public static String encodeMessageDigest(byte[] message) {
        try {
            byte[] md = MessageDigest.getInstance("SHA-256").digest(message);

            return Base64.getEncoder().encodeToString(md);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm in digest");
        }
    }

    public static byte[] decodeMessageDigest(String b64md) {
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
