package com.p3mail.application.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Note per quando si sviluppa l'interfaccia grafica del server:
 * il file main che eseguito crea la finestra grafica deve essere posto nel package piu' "alto"
 * allo stesso livello dei package 'client' e 'server', pena il mancato funzionamento di javafx.
 */
public class ThreadedEchoServer {
    public static void main(String[] args) {
        System.out.println("Finestra del server: ");
        HandleSocketsConnections handleSocketsConnections = new HandleSocketsConnections("handle1");
        handleSocketsConnections.start();
        Scanner keyboard = new Scanner(System.in);
        while (!keyboard.nextLine().trim().equalsIgnoreCase("quit")) ;
        System.exit(0);
    }
}

class HandleSocketsConnections extends Thread {
    public static final int N_THREADS = 10;

    public HandleSocketsConnections(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
            int i = 1;
            ServerSocket s = new ServerSocket(8189); //trova il socket in rete
            ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);

            while (true) {
                Socket incoming = s.accept(); // si mette in attesa di richiesta di connessione e la apre
                System.out.println("Spawning " + i);
                Runnable singleSocketConnectionHandlr = new ThreadedEchoHandler(incoming, i);
//                new Thread(r).start(); //molto onerosa in termini di gestione della memoria
                exec.execute(singleSocketConnectionHandlr);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 * This class handles the client input for one server socket connection.
 */
class ThreadedEchoHandler implements Runnable {

    private final Socket incoming;
    private final int counter;

    /**
     * Constructs a handler.
     *
     * @param in the incoming socket
     * @param c  the counter for the handlers (used in prompts)
     */
    public ThreadedEchoHandler(Socket in, int c) {
        incoming = in;
        counter = c;
    }

    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);

                out.println("Hello! Enter BYE to exit.");

                // echo client input
                boolean done = false;
                while (!done && in.hasNextLine()) {
                    String line = in.nextLine();
                    out.println("Echo: " + line);
                    System.out.println("ECHO: " + line);
                    if (line.trim().equals("BYE"))
                        done = true;
                }
                System.out.println("I've received a closed operation, I'm going to close this thread!");
            } finally {
                incoming.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

