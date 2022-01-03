package com.p3mail.application.server;

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
    //public static final int N_THREADS = 10;
    public static void main(String[] args) {
        // System.out.println("Finestra del server: ");
//        try {
//            ServerSocket s = new ServerSocket(8189); //trova il socket in rete
//            ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);
//            //dove va messo lo shutdown dell'executorService?
//
//            while (true) {
//                Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
//                Runnable connection = new ClientServerConnection(incoming);
//                exec.execute(connection);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("server.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }
}
