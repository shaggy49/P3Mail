package com.p3mail.application.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class EchoClient {
    public static void main(String[] args) {
        try {
            String nomeHost = InetAddress.getLocalHost().getHostName();
            System.out.println(nomeHost);

            // vari modi di aprire il socket verso il server
            //Socket s = new Socket("127.0.0.1", 8189);
            //Socket s = new Socket("localhost", 8189);

            try (Socket s = new Socket(nomeHost, 8189)) {
                System.out.println("Ho aperto il socket verso il server");
                InputStream inStream = s.getInputStream();
                Scanner in = new Scanner(inStream);
                OutputStream outStream = s.getOutputStream();
                PrintWriter out = new PrintWriter(outStream, true /* autoFlush */);
                Scanner stin = new Scanner(System.in);

                System.out.println("Sto per ricevere dati dal socket server!");

                /*
                * Queste due linee di codice servono a far stampare "Hello! Enter BYE to exit."
                * Perché altrimenti si avrebbe un ritardo nella comunicazione col socket.
                * */
                String line = in.nextLine(); // attenzione: se il server non scrive nulla questo resta in attesa...
                System.out.println(line);

                boolean done = false;
                while (!done) /* && in.hasNextLine()) */ {

                    String lineout = stin.nextLine();
                    out.println(lineout); //spedisce l'input da tastiera all'output del socket

                    line = in.nextLine();
                    System.out.println(line); //stampa quello che trova in input dal socket
                    if (lineout.equals("BYE"))
                        done = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
