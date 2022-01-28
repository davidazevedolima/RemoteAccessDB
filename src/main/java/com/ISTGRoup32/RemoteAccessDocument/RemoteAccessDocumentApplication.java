package com.ISTGRoup32.RemoteAccessDocument;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RemoteAccessDocumentApplication {

	private static int port;

	@Value("${communication.port}")
	public void setPort(int port) {
		RemoteAccessDocumentApplication.port = port;
	}

	public static void main(String[] args) {
		SpringApplication.run(RemoteAccessDocumentApplication.class, args);
		System.out.println("Starting communications...");
		Communication communication = new Communication(port);
		BackUp thread = new BackUp(communication);
		thread.start();
		while (true) {
			try {
				communication.handleClient();
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
