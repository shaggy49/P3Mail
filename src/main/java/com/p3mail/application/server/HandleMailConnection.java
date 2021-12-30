package com.p3mail.application.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandleMailConnection extends Thread {
    public static final int N_THREADS = 4;

    public HandleMailConnection(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
            ServerSocket s = new ServerSocket(8189); //trova il socket in rete
            ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);

            //dove va messo lo shutdown dell'executorService?

            while (true) {
                Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
                Runnable mailConnection = new MailConnection(incoming);
//                new Thread(r).start(); //molto onerosa in termini di gestione della memoria
                exec.execute(mailConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}