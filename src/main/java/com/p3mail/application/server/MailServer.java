package com.p3mail.application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailServer {
    public static final int N_THREADS = 10;

    public static void main(String[] args) {
        System.out.println("Finestra del server: ");
        ServerSocket s = null;
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


