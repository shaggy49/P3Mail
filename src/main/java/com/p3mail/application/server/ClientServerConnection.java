package com.p3mail.application.server;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.*;
import com.p3mail.application.connection.NewEmailNotification;
import com.p3mail.application.connection.response.DeleteResponse;
import com.p3mail.application.connection.response.SendResponse;
import com.p3mail.application.server.model.RegisteredClient;
import com.p3mail.application.connection.MailNotFoundException;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
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

                System.out.println("Client request connection from user: " + userEmailAddress);

                if(!registeredClients.getRegisteredMails().contains(userEmailAddress)){
                    out.writeObject(new MailNotFoundException());
                    throw new MailNotFoundException();
                }
                System.out.printf("(%s): active connection\n", userEmailAddress);

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
                        System.out.println("receive a delete request for email: $" + emailId);
                        boolean result = deleteEmailWithId(emailId);
                        DeleteResponse response = new DeleteResponse(result);
                        if(!result) {
                            response.setErrorMessage("email already deleted");
                        }
                        System.out.println("email: $" + emailId + " deleted");
                        out.writeObject(response);
                    }
                    else if (request instanceof TriggerServerRequest) {
                        notifyAllConnectedClients();
                    }
                    else if (request instanceof SendRequest) {
                        Email emailSended = ((SendRequest) request).getEmailToSend();
                        List<String> receivers = emailSended.getReceivers();
                        System.out.println("receive a send request for receivers : " + receivers);
                        //TODO: check receivers if well formed (@ and .)
                        if(!registeredClients.getRegisteredMails().containsAll(receivers)){
                            System.out.println("some emails are not registered!");
                            out.writeObject(new MailNotFoundException());
                        }
                        else {
                            for (String receiver : receivers) {
                                synchronized (clients) { //not sure if "this" or "clients" are correct
                                    int indexOfLastEmail = getIndexOfLastEmailForAccount(receiver);
                                    addEmailToInboxOf(emailSended, receiver, indexOfLastEmail);
                                    updateIndexOfLastEmailForAccount(receiver, indexOfLastEmail);
                                }
                            }
                            System.out.println("email correctly stored!");
                            //TODO: notify all connected receivers
//                        notifyConnectedReceiver(receivers);
                            System.out.println("about to send notifications to connected client: " + receivers);
                            out.writeObject(new SendResponse());
                            //notifyClients
                        }
                    }
                    //else if socket input type of richiesta di invio => inviaMail()
                    //else error ed esci
                }

                System.out.printf("(%s): connection closed\n", userEmailAddress);

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

    private void updateIndexOfLastEmailForAccount(String receiver, int index) throws IOException {
        String path = String.format("." + File.separator + "server" + File.separator + receiver + File.separator + "info.dat");

        File file = new File(path);

        FileOutputStream fos = new FileOutputStream(file, false);
        ObjectOutputStream outputStream  = new ObjectOutputStream(fos);

        outputStream.writeObject(index + 1);
    }

    private int getIndexOfLastEmailForAccount(String receiver) throws IOException, ClassNotFoundException {
        String path = "." + File.separator + "server" + File.separator + receiver + File.separator + "info.dat";
        File file = new File(path);
        FileInputStream fos = new FileInputStream(file);
        ObjectInputStream inputStream = new ObjectInputStream(fos);
        return (Integer) inputStream.readObject();
    }

    private void addEmailToInboxOf(Email emailToAdd, String accountReceiver, int indexForEmail) throws IOException {
        String path = String.format("." + File.separator + "server" + File.separator + accountReceiver + File.separator + "email_%d.dat", indexForEmail);
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fos);
        outputStream.writeObject(emailToAdd);
    }

//    private void notifyConnectedReceiver(List<String> receivers) throws IOException {
//        for (ClientServerConnection connectedClient : clients) {
//            if(receivers.contains(connectedClient.userEmailAddress))
//                connectedClient.out.writeObject(new NewEmailNotification(receivers));
//        }
//    }

    private void notifyAllConnectedClients() throws IOException {
        for (ClientServerConnection connectedClient : clients) {
            connectedClient.out.writeObject(new NewEmailNotification("notifica dal server per tutti i clienti connessi"));
        }
    }

    private boolean deleteEmailWithId(int emailId) {
        boolean result = false;
        String path = String.format("." + File.separator + "server" + File.separator + userEmailAddress +  File.separator + "email_%d.dat", emailId);
        File file = new File(path);
        try {
            result = Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
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
            if (userEmails != null) {
                for (String userEmail : userEmails) {
                    File file = new File(pathToUser + File.separator + userEmail);
                    FileInputStream fos = new FileInputStream(file);
                    ObjectInputStream inputStream = new ObjectInputStream(fos);

                    Email email = (Email) inputStream.readObject();

                    emailList.add(email);

                    inputStream.close();

                    fos.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return emailList;
    }

}