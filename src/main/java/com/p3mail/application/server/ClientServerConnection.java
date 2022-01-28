package com.p3mail.application.server;

import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.*;
import com.p3mail.application.connection.response.*;
import com.p3mail.application.server.controller.ServerController;
import com.p3mail.application.server.model.RegisteredClient;
import com.p3mail.application.connection.MailNotFoundException;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ClientServerConnection implements Runnable {
    private final ServerController controller;
    private Socket incoming;
    private String userEmailAddress;
    ObjectInputStream in;
    ObjectOutputStream out;
    private Vector<ClientServerConnection> clients;
    private final Object fileLock = new Object();

    /**
     * Constructs a handler.
     *
     * @param in the incoming socket
     */
    public ClientServerConnection(Socket in, Vector<ClientServerConnection> clients, ServerController controller) {
        incoming = in;
        this.clients = clients;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                in = new ObjectInputStream(inStream);
                out = new ObjectOutputStream(outStream);

                userEmailAddress = (String) in.readObject();
                RegisteredClient registeredClients = new RegisteredClient();

//                System.out.println("Client request connection from user: " + userEmailAddress);
                printToStage("Client request connection from user: " + userEmailAddress);

                if(!registeredClients.getRegisteredUser().contains(userEmailAddress)){
                    out.writeObject(new MailNotFoundException());
                    throw new MailNotFoundException();
                }
//                System.out.printf("(%s): active connection\n", userEmailAddress);
                printToStage(String.format("(%s): active connection\n", userEmailAddress));

                //spedizione oggetto mail casella di posta elettronica
                //System.out.printf("(%s): about to send inbox emails to the client\n", userEmailAddress);
                printToStage(String.format("(%s): about to send inbox emails to the client\n", userEmailAddress));
                //leggo le mail dai file sotto la cartella server e spedisco la lista nel canale socket!
                List<Email> userEMails = getMails();

                out.writeObject(userEMails);

//                System.out.printf("(%s): information sent\n", userEmailAddress);
                printToStage(String.format("(%s): information sent\n", userEmailAddress));

                while(true) {
//                    System.out.printf("[%s] I'm ready to listen for some client events..\n", Thread.currentThread().getName());
                    printToStage(String.format("[%s] I'm ready to listen for some client events..\n", Thread.currentThread().getName()));
                    ClientRequest request = (ClientRequest) in.readObject(); //è una chiamata bloccante, aspetta che arrivi qualcosa dal canale del socket
                    if(request instanceof DisconnectRequest) {
                        out.writeObject(new DisconnectResponse());
                        clients.remove(this);
                        break;
                    }
                    else if (request instanceof DeleteRequest) {
                        Email emailToDelete = ((DeleteRequest) request).getEmailToDelete();
                        int emailId = emailToDelete.getId();
                        //System.out.println("receive a delete request for email: {" + emailToDelete + "}");
                        printToStage("receive a delete request for email: {" + emailToDelete + "}");
                        boolean result = deleteEmailWithId(emailId);
                        DeleteResponse response = new DeleteResponse(result);
                        //System.out.println("email: {" + emailToDelete + "} deleted");
                        printToStage("email: {" + emailToDelete + "} deleted");
                        if(result) {
                            notifyConnectedReceiversForNewDeletedEmail(emailToDelete);
                        }
                        out.writeObject(response);
                    }
//                    else if (request instanceof TriggerServerRequest) {
//                        notifyAllConnectedClients();
//                    }
                    else if (request instanceof SendRequest) {
                        Email emailSended = ((SendRequest) request).getEmailToSend();
                        List<String> receivers = emailSended.getReceivers();
                        //System.out.println("receive a send request for receivers : " + receivers);
                        printToStage("receive a send request for receivers : " + receivers);
                        if(!registeredClients.getRegisteredUser().containsAll(receivers)){
                            //System.out.println("some emails are not registered!");
                            printToStage("some emails are not registered!");
                            out.writeObject(new MailNotFoundException());
                        }
                        else {
                            for (String receiver : receivers) {
                                int indexOfLastEmail = getIndexOfLastEmailForAccount(receiver);
                                emailSended.setId(indexOfLastEmail);
                                addEmailToInboxOf(emailSended, receiver, indexOfLastEmail);
                                updateIndexOfLastEmailForAccount(receiver, indexOfLastEmail);
                            }
                            printToStage("email correctly stored!");
                            printToStage(String.format("about to send notifications to connected client: " + receivers));
                            notifyConnectedReceiversForNewMailMessage(emailSended);
                            out.writeObject(new SendResponse());
                        }
                    }
                }
                printToStage(String.format("(%s): connection closed\n", userEmailAddress));

            } catch (MailNotFoundException e) {
                printToStage("Connessione rifiutata");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                incoming.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyConnectedReceiversForNewMailMessage(Email email) throws IOException {
        List<String> receivers = email.getReceivers();
        for (ClientServerConnection connectedClient : clients) {
            for (String receiver : receivers) {
                if(connectedClient.userEmailAddress.equals(receiver))
                    connectedClient.out.writeObject(new NewEmailNotification(email));
            }
        }
    }

    private void notifyConnectedReceiversForNewDeletedEmail(Email email) throws IOException {
        for (ClientServerConnection connectedClient : clients) {
            if(connectedClient.userEmailAddress.equals(userEmailAddress))
                connectedClient.out.writeObject(new DeleteEmailNotification(email));
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

    private void printToStage(String infoToPrint) {
        Platform.runLater(() -> {
            controller.printToTextArea(infoToPrint);
        });
    }

}