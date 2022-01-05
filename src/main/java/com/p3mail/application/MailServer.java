package com.p3mail.application;

import com.p3mail.application.server.ClientServerConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer extends Application {
	ServerSocket s = null;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 800, 600);
		stage.setTitle("Server");
		stage.setScene(scene);
		stage.show();

		ThreadConnection tc = new ThreadConnection();
		tc.start();

//		stage.setOnCloseRequest(event -> {
//			try {
//				s.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		});
	}

	private class ThreadConnection extends Thread {

		public ThreadConnection() {
			setDaemon(true);
		}

		@Override
		public void run() {
			final int N_THREADS = 10;

			System.out.println("Waiting for first client connection...");

			// creiamo i thread che aspettano il collegamento dei client
			try {
				s = new ServerSocket(8189); //trova il socket in rete
				ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);
				//dove va messo lo shutdown dell'executorService?

				while (true) {
					Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
					Runnable connection = new ClientServerConnection(incoming);
					exec.execute(connection);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
