package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class AES {
    SecretKeySpec key;
    Cipher cipher;

    public AES(byte[] secret) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, JSONException, IllegalBlockSizeException, CertificateException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException {
        this.key = new SecretKeySpec(secret, 0, 16, "AES");
        this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    public byte[] cipher(byte[] message, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        this.cipher.init(Cipher.ENCRYPT_MODE, this.key, ivSpec);

        return this.cipher.doFinal(message);
    }

    public byte[] decipher(byte[] message, byte[] iv) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        this.cipher.init(Cipher.DECRYPT_MODE, this.key, ivSpec);

        return cipher.doFinal(message);
    }

    public byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return iv;
    }

}
