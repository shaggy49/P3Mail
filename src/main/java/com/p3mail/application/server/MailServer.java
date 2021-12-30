package com.p3mail.application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer {
    public static final int N_THREADS = 10;

    public static void main(String[] args) {
        System.out.println("Finestra del server: ");
        try {
            ServerSocket s = new ServerSocket(8189); //trova il socket in rete
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


