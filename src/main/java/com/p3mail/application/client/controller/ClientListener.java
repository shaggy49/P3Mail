package com.p3mail.application.client.controller;

import com.p3mail.application.connection.MailNotFoundException;
import com.p3mail.application.connection.model.Email;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class ClientListener implements Runnable{
    ClientController controller;
    private Socket socket;
    ObjectInputStream in;

    public ClientListener(ClientController controller, Socket socket) throws IOException {
        this.controller = controller;
        this.socket = socket;
        in =  new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()) {
                Object serverResponse = in.readObject();
                if(serverResponse instanceof MailNotFoundException){
                    throw new MailNotFoundException();
                }
                else if (serverResponse instanceof List) {
                    List<Email> userEMail = (List<Email>) serverResponse;
                    for (Email email : userEMail) {
                        controller.addEmailToInbox(email);
                    }
                }
                else if (serverResponse instanceof Boolean) {
                    Boolean result = (Boolean) serverResponse;
                    if(result) {
                        controller.deleteAndUpdateView();
                    }
                    else {
                        controller.deleteFailed();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (MailNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
