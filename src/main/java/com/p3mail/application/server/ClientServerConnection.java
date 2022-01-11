package com.p3mail.application.server;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.ClientRequest;
import com.p3mail.application.connection.request.DeleteRequest;
import com.p3mail.application.connection.request.DisconnectRequest;
import com.p3mail.application.connection.request.TriggerServerRequest;
import com.p3mail.application.connection.NewEmailNotification;
import com.p3mail.application.server.model.RegisteredClient;
import com.p3mail.application.connection.MailNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientServerConnection implements Runnable {
    private Socket incoming;
    private String userEmailAddress;
    ObjectOutputStream out;
    private Vector<ClientServerConnection> clients;


    /**
     * Constructs a handler.
     *
     * @param in the incoming socket
     */
    public ClientServerConnection(Socket in, Vector<ClientServerConnection> clients) {
        incoming = in;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            //è una buona idea gestire tutta l'interazione con un client dentro un unico thread?
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                ObjectInputStream in = new ObjectInputStream(inStream);
                out = new ObjectOutputStream(outStream);

                userEmailAddress = (String) in.readObject();
                RegisteredClient registeredClients = new RegisteredClient();

                System.out.println("Richiesta di connessione dallo user: " + userEmailAddress);

                if(!registeredClients.getRegisteredMails().contains(userEmailAddress)){
                    out.writeObject(new MailNotFoundException());
                    throw new MailNotFoundException();
                }
                System.out.printf("(%s): connessione attiva\n", userEmailAddress);

                //spedizione oggetto mail casella di posta elettronica
                System.out.printf("(%s): about to send inbox emails to the client\n", userEmailAddress);

                //leggo le mail dai file sotto la cartella server e spedisco la lista nel canale socket!
                List<Email> userEMails = getMails();

                out.writeObject(userEMails);

                System.out.printf("(%s): information sent\n", userEmailAddress);

                //maybe here create another thread/task that handles sending messages
                while(true) {
                    System.out.printf("[%s] I'm ready to listen for some client events..\n", Thread.currentThread().getName());
                    ClientRequest request = (ClientRequest) in.readObject(); //è una chiamata bloccante, aspetta che arrivi qualcosa dal canale del socket
                    if(request instanceof DisconnectRequest) {
                        break;
                    }
                    else if (request instanceof DeleteRequest) {
                        int emailId = ((DeleteRequest) request).getEmailId();
                        boolean result = deleteEmailWithId(emailId);
                        out.writeObject(result);
                    }
                    else if (request instanceof TriggerServerRequest) {
                        notifyAllConnectedClients();
                    }
                    //else if socket input type of richiesta di invio => inviaMail()
                    //else error ed esci
                }

                System.out.printf("(%s): connessione chiusa\n", userEmailAddress);

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

    private void notifyAllConnectedClients() throws IOException {
        for (ClientServerConnection connectedClient : clients) {
            if(connectedClient.userEmailAddress.equals("ff@unito.it"))
                connectedClient.out.writeObject(new NewEmailNotification("Fedù"));
        }
    }

    private boolean deleteEmailWithId(int emailId) {
        boolean result;
        String path = String.format("." + File.separator + "server" + File.separator + userEmailAddress +  File.separator + "email_%d.dat", emailId);
        File file = new File(path);
        try {
            result = Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    //rivedere questo perché non va più bene nel caso si faccia partire un client, si cancellano un paio di mail
    //e poi con un altro run si fa partire lo stesso client
    public List<Email> getMails() {
        List<Email> emailList = new ArrayList<>();
        try {
            String pathToUser = "." + File.separator + "server" + File.separator + userEmailAddress;
            File directoryPath = new File(pathToUser);
            String[] userEmails = directoryPath.list((dir, name) -> name.contains("email_"));
            for (String userEmail : userEmails) {
                File file = new File(pathToUser + File.separator + userEmail);
                FileInputStream fos = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fos);

                Email email = (Email) inputStream.readObject();

                emailList.add(email);

                inputStream.close();

                fos.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return emailList;
    }

}