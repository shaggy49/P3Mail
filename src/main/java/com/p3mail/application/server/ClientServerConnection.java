package com.p3mail.application.server;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.ClientRequest;
import com.p3mail.application.connection.request.DisconnectRequest;
import com.p3mail.application.server.model.RegisteredClient;
import com.p3mail.application.server.util.MailNotFoundException;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientServerConnection implements Runnable {
    private Socket incoming;

    /**
     * Constructs a handler.
     *
     * @param in the incoming socket
     */
    public ClientServerConnection(Socket in) {
        incoming = in;
    }

    @Override
    public void run() {
        String userEmail = "";
        try {
            //è una buona idea gestire tutta l'interazione con un client dentro un unico thread?
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                ObjectInputStream in = new ObjectInputStream(inStream);
                ObjectOutputStream out = new ObjectOutputStream(outStream);

                userEmail = (String) in.readObject();
                RegisteredClient registeredClients = new RegisteredClient();

                System.out.println("Richiesta di connessione dallo user: " + userEmail);

                if(!registeredClients.getRegisteredMails().contains(userEmail)){
                    out.writeObject(new MailNotFoundException());
                    throw new MailNotFoundException();
                }
                System.out.printf("(%s): connessione attiva\n", userEmail);

                //spedizione oggetto mail casella di posta elettronica
                System.out.printf("(%s): about to send inbox emails to the client\n", userEmail);

                //leggo le mail dai file sotto la cartella server e spedisco la lista nel canale socket!
                List<Email> userEMails = getMailForClient(userEmail);

                out.writeObject(userEMails);

                System.out.printf("(%s): information sent\n", userEmail);

                //maybe here create another thread/task that handles sending messages
                while(true) {
                    System.out.println("I'm ready to listen for some client events..");
                    ClientRequest request = (ClientRequest) in.readObject(); //è una chiamata bloccante, aspetta che arrivi qualcosa dal canale del socket
                    if(request instanceof DisconnectRequest) {
                        break;
                    }
                    //if socket input type of richiesta di eliminazione => gestisciElimina()
                    //else if socket input type of richiesta di invio => inviaMail()
                    //else error ed esci
                }

                System.out.printf("(%s): connessione chiusa\n", userEmail);

            } catch (MailNotFoundException e) {
                System.out.println("Connessione rifiutata");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                incoming.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Email> getMailForClient(String emailAddress) {
        List<Email> emailList = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                String path = String.format("." + File.separator + "server" + File.separator + emailAddress +  File.separator + "email_%d.dat", i);
                File file = new File(path);
                FileInputStream fos = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fos);

                Email email = (Email) inputStream.readObject();

                emailList.add(email);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return emailList;
    }

    //TODO: implement deleting files
//    public void deleteMailForClient(String emailAddress, )
}