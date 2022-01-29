package com.p3mail.application;

import com.p3mail.application.server.ClientServerConnection;
import com.p3mail.application.server.controller.ServerController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer extends Application {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
    boolean closeClientConnection = false;
    ServerSocket s = null;
    ThreadConnection tc = null;

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();

        ServerController controller = fxmlLoader.getController();

        tc = new ThreadConnection(controller);
        tc.start();
    }

    public static void main(String[] args) {
        launch();
    }

	@Override
	public void stop() throws Exception {
        closeClientConnection = true;
		super.stop();
		System.exit(0);
	}

    private class ThreadConnection extends Thread {
        private final ServerController controller;

        public ThreadConnection(ServerController controller) {
            this.controller = controller;
        }

        @Override
        public void run() {
            final int N_THREADS = 10;

            Platform.runLater(() -> {
                controller.printToTextArea("Waiting for first client connection...");
            });

            // creiamo i thread che aspettano il collegamento dei client
            try {
                s = new ServerSocket(8189); //trova il socket in rete
                ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);
                Vector<ClientServerConnection> clientsConnected = new Vector<>();

                while (!closeClientConnection) {
                    Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
                    ClientServerConnection connection = new ClientServerConnection(incoming, clientsConnected, controller);
                    exec.execute(connection);
                    clientsConnected.add(connection);
                }
                exec.shutdown();
            } catch (IOException e) {
                //e.printStackTrace();
            } finally {
                if (s != null) {
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
