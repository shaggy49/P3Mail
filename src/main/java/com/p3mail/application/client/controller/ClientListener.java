package com.p3mail.application.client.controller;

import com.p3mail.application.connection.MailNotFoundException;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.NewEmailNotification;
import javafx.application.Platform;
import javafx.scene.control.Alert;

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
            while (true) {
                Object serverResponse = in.readObject();
                if(serverResponse instanceof MailNotFoundException){
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("The emailAddress is not registered!");
                        alert.show();
                    });
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
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("There was a problem deleting this email!");
                            alert.show();
                        });
                    }
                }
                else if (serverResponse instanceof NewEmailNotification) {
                    String emailAddress = ((NewEmailNotification) serverResponse).getFromEmailAddress();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Notification");
                        alert.setHeaderText("Something arrived from " + emailAddress);
                        alert.show();
                    });
                    //metodo del controller che aggiunge la mail all'inbox in real time
                }
            }
        } catch (IOException ignored) {
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
