package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

@SpringBootApplication
public class RemoteAccessDocumentApplication {

	public static void main(String[] args) throws IOException, JSONException {
		SpringApplication.run(RemoteAccessDocumentApplication.class, args);
		System.out.println("Starting communications...");
		Communication communication = null;
		try {
			communication = new Communication(58032);
		} catch (UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
			throw new RuntimeException();
		}
		while (true) {
			try {
				communication.handleClient();
			} catch (RuntimeException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | UnrecoverableKeyException | NoSuchPaddingException | IllegalBlockSizeException | CertificateException | KeyStoreException | BadPaddingException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
