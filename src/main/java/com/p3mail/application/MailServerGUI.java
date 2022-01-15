package com.p3mail.application;

import com.p3mail.application.server.ClientServerConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServerGUI extends Application {
	boolean closeConnection = false;
	ServerSocket s = null;
	ThreadConnection tc = null;

	public static void main(String[] args) {
		launch();
		System.out.println("mai");
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 800, 600);
		stage.setTitle("Server");
		stage.setScene(scene);
		stage.show();

		tc = new ThreadConnection();
		tc.start();
	}

	@Override
	public void stop() throws Exception {
		closeConnection = true;
		super.stop();
		System.exit(0);
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
				Vector<ClientServerConnection> clientsConnected = new Vector<>();

				//dove va messo lo shutdown dell'executorService?

				while (true) {
					Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
					ClientServerConnection connection = new ClientServerConnection(incoming, clientsConnected);
					exec.execute(connection);
					clientsConnected.add(connection);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(s != null) {
					try {
						s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
