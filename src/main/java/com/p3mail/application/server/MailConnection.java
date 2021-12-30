package com.p3mail.application.server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MailConnection implements Runnable {
    private final Socket incoming;

    /**
     * Constructs a handler.
     *
     * @param in the incoming socket
     */
    public MailConnection(Socket in) {
        incoming = in;
    }

    @Override
    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
//                PrintWriter out = new PrintWriter(outStream, true);
                ObjectOutputStream out = new ObjectOutputStream(outStream);

                String userEmail = in.nextLine();
                RegisteredClient registeredClients = new RegisteredClient();

                System.out.println("Richiesta di connessione dallo user: " + userEmail);

                if(!registeredClients.getRegisteredMails().contains(userEmail)){
                    out.writeObject(new MailNotFoundException());
                    throw new MailNotFoundException();
                }
                System.out.printf("(%s): connessione attiva\n", userEmail);

                //spedizione oggetto mail casella di posta elettronica
//                System.out.printf("(%s): about to send information to the client\n", userEmail);
//                non va bene spedire questo oggetto!
//                out.writeObject(registeredClients.getClientInfoByEmail(userEmail));
//                System.out.printf("(%s): information sent\n", userEmail);

                out.writeObject("Tutto ok!");

                System.out.printf("(%s): connessione chiusa\n", userEmail);
            } catch (MailNotFoundException e) {
                e.printStackTrace();
                System.out.println("Connessione rifiutata");
            } finally {
                incoming.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}