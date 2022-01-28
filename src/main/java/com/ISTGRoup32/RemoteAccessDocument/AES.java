package com.ISTGRoup32.RemoteAccessDocument;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {
    SecretKeySpec key;
    Cipher cipher;

    public AES(byte[] secret) throws RuntimeException {
        try {
            this.key = new SecretKeySpec(secret, 0, 16, "AES");
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No provider found for algorithm");
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("No such padding scheme found");
        }
    }

    public byte[] cipher(byte[] message, byte[] iv) throws RuntimeException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            this.cipher.init(Cipher.ENCRYPT_MODE, this.key, ivSpec);

            return this.cipher.doFinal(message);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key");
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid AES parameters");
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Illegal block size");
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding");
        }
    }

    public byte[] decipher(byte[] message, byte[] iv) throws RuntimeException {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            this.cipher.init(Cipher.DECRYPT_MODE, this.key, ivSpec);

            return cipher.doFinal(message);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key");
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Invalid AES parameters");
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Illegal block size");
        } catch (BadPaddingException e) {
            throw new RuntimeException("Bad padding");
        }
    }

    public byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        return iv;
    }
}
