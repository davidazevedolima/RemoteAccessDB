package com.ISTGRoup32.RemoteAccessDocument;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class RemoteAccessDocumentApplication {

	public static void main(String[] args) throws IOException, JSONException {
		SpringApplication.run(RemoteAccessDocumentApplication.class, args);
		System.out.println("Starting communications...");
		Communication communication = new Communication(58032);
		while (true) {
			try {
				communication.handleClient();
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
