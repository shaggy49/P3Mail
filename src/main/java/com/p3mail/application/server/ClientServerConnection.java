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
import java.util.List;
import java.util.Vector;

public class ClientServerConnection implements Runnable {
    private Socket incoming;
    private String userEmailAddress;
    ObjectOutputStream out;
    private Vector<ClientServerConnection> clients;
    private final Object fileLock = new Object();

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
            // è una buona idea gestire tutta l'interazione con un client dentro un unico thread? Sì :)
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                ObjectInputStream in = new ObjectInputStream(inStream);
                out = new ObjectOutputStream(outStream);

                userEmailAddress = (String) in.readObject();
                RegisteredClient registeredClients = new RegisteredClient();

                System.out.println("Client request connection from user: " + userEmailAddress);

                if(!registeredClients.getRegisteredUser().contains(userEmailAddress)){
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
//                    else if (request instanceof TriggerServerRequest) {
//                        notifyAllConnectedClients();
//                    }
                    else if (request instanceof SendRequest) {
                        Email emailSended = ((SendRequest) request).getEmailToSend();
                        List<String> receivers = emailSended.getReceivers();
                        System.out.println("receive a send request for receivers : " + receivers);
                        if(!registeredClients.getRegisteredUser().containsAll(receivers)){
                            System.out.println("some emails are not registered!");
                            out.writeObject(new MailNotFoundException());
                        }
                        else {
                            for (String receiver : receivers) {
                                int indexOfLastEmail = getIndexOfLastEmailForAccount(receiver);
                                emailSended.setId(indexOfLastEmail);
                                addEmailToInboxOf(emailSended, receiver, indexOfLastEmail);
                                updateIndexOfLastEmailForAccount(receiver, indexOfLastEmail);
                            }
                            System.out.println("email correctly stored!");
                            System.out.println("about to send notifications to connected client: " + receivers);
                            notifyConnectedReceivers(emailSended);
                            out.writeObject(new SendResponse());
                        }
                    }
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

    private void notifyConnectedReceivers(Email email) throws IOException {
        List<String> receivers = email.getReceivers();
        for (ClientServerConnection connectedClient : clients) {
            for (String receiver : receivers) {
                if(connectedClient.userEmailAddress.equals(receiver))
                    connectedClient.out.writeObject(new NewEmailNotification(email));
            }
        }
    }

    private int getIndexOfLastEmailForAccount(String receiver) throws IOException, ClassNotFoundException {
        String path = "." + File.separator + "server" + File.separator + receiver + File.separator + "info.dat";
        File file = new File(path);
        FileInputStream fos = new FileInputStream(file);
        ObjectInputStream inputStream = new ObjectInputStream(fos);
        Integer index = 0;

//        ReadWriteLock rwl = new ReentrantReadWriteLock();
//        Lock readLock = rwl.readLock();

//        readLock.lock();
        synchronized (fileLock) {
            index = (Integer) inputStream.readObject();
        }
//        readLock.unlock();

        inputStream.close();
        fos.close();
        return index;
    }

    private void updateIndexOfLastEmailForAccount(String receiver, int index) throws IOException {
        String path = "." + File.separator + "server" + File.separator + receiver + File.separator + "info.dat";

        File file = new File(path);

        FileOutputStream fos = new FileOutputStream(file, false);
        ObjectOutputStream outputStream  = new ObjectOutputStream(fos);

//        ReadWriteLock rwl = new ReentrantReadWriteLock();
//        Lock writeLock = rwl.writeLock();

//        writeLock.lock();
        synchronized (fileLock) {
            outputStream.writeObject(index + 1);
        }
//        writeLock.unlock();


        outputStream.close();
        fos.close();
    }

    private void addEmailToInboxOf(Email emailToAdd, String accountReceiver, int indexForEmail) throws IOException {
        String path = String.format("." + File.separator + "server" + File.separator + accountReceiver + File.separator + "email_%d.dat", indexForEmail);
        File file = new File(path);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream outputStream = new ObjectOutputStream(fos);
        outputStream.writeObject(emailToAdd);

        outputStream.close();
        fos.close();
    }

//    private void notifyAllConnectedClients() throws IOException {
//        for (ClientServerConnection connectedClient : clients) {
//            connectedClient.out.writeObject(new NewEmailNotification("notifica dal server per tutti i clienti connessi"));
//        }
//    }

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