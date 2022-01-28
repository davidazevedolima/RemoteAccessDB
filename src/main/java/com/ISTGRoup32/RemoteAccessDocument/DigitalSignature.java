package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    public DigitalSignature() throws RuntimeException {
        try {
            this.keyStore = KeyStore.getInstance("PKCS12");
            this.keyStore.load(new FileInputStream(keyStorePath), keyStorePass.toCharArray());

            this.privateKey = (PrivateKey) keyStore.getKey("dbKeyPair", keyStorePass.toCharArray());

            this.certificate = keyStore.getCertificate("serverKeyPair");
            this.publicKey = certificate.getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException("No provider found for given keystore type");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Keystore file not found");
        } catch (IOException e) {
            throw new RuntimeException("I/O problem when loading keystore");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm found");
        } catch (CertificateException e) {
            throw new RuntimeException("Certificate in keystore could not be loaded");
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException("Key could not be recovered");
        }
    }

    public String signMessage(byte[] message) throws RuntimeException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageHash = md.digest(message);

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] signedMessage = cipher.doFinal(messageHash);

            return Cryptography.toBase64(signedMessage);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm found");
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("No such padding scheme found");
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key");
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Illegal block size");
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding");
        }
    }

    public boolean verifySignature(JSONObject message) throws RuntimeException {
        try {
            byte[] signature = Cryptography.fromBase64(message.getString("signature"));
            message.remove("signature");

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] decryptedSignature = cipher.doFinal(signature);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageHash = md.digest(message.toString().getBytes());

            return Arrays.equals(decryptedSignature, messageHash);
        } catch (JSONException e) {
            throw new RuntimeException("No such mapping exists in JSON");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm found");
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("No such padding scheme found");
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key");
        }  catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Illegal block size");
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding");
        }
    }
}
