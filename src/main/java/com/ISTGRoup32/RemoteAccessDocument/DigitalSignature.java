package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;

public class DigitalSignature {
    private KeyStore keyStore;
    private final String keyStorePath = "keys/db_keystore.p12";
    private final String keyStorePass = "remoteaccessdb";

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Certificate certificate;

    public DigitalSignature() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        this.keyStore = KeyStore.getInstance("PKCS12");
        this.keyStore.load(new FileInputStream(keyStorePath), keyStorePass.toCharArray());

        this.privateKey = (PrivateKey) keyStore.getKey("dbKeyPair", keyStorePass.toCharArray());

        this.certificate = keyStore.getCertificate("serverKeyPair");
        this.publicKey = certificate.getPublicKey();
    }

    public String signMessage(byte[] message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageHash = md.digest(message);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] signedMessage = cipher.doFinal(messageHash);

        return Cryptography.toBase64(signedMessage);
    }

    public boolean verifySignature(JSONObject message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JSONException {
        byte[] signature = Cryptography.fromBase64(message.getString("signature"));
        message.remove("signature");

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedSignature = cipher.doFinal(signature);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageHash = md.digest(message.toString().getBytes());

        return Arrays.equals(decryptedSignature, messageHash);
    }
}
