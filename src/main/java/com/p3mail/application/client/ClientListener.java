package com.p3mail.application.client;

import com.p3mail.application.client.controller.MainWindowController;
import com.p3mail.application.connection.MailNotFoundException;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.response.DeleteEmailNotification;
import com.p3mail.application.connection.response.NewEmailNotification;
import com.p3mail.application.connection.response.DeleteResponse;
import com.p3mail.application.connection.response.SendResponse;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;

public class ClientListener implements Runnable{
    MainWindowController controller;
    private Socket socket;
    ObjectInputStream in;

    public ClientListener(MainWindowController controller, Socket socket) throws IOException {
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
                        alert.setTitle("Errore");
                        alert.setHeaderText("L'indirizzo mail non è registrato");
                        alert.show();
                    });
                }
                else if (serverResponse instanceof List) {
                    List<Email> userEMail = (List<Email>) serverResponse;
                    Platform.runLater(() -> {
                        for (Email email : userEMail) {
                            controller.addEmailToInbox(email);
                        }
                    });
                }
                else if (serverResponse instanceof DeleteResponse) {
                    DeleteResponse response = (DeleteResponse) serverResponse;
                    boolean result = response.isResult();
                    if(result) {
                        Platform.runLater(() -> {
                            controller.deleteAndUpdateView();
                        });
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
                else if (serverResponse instanceof SendResponse) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Success");
                        alert.setHeaderText("L'email è stata correttamente consegnata");
                        alert.show();
                    });
                }
                else if (serverResponse instanceof NewEmailNotification) {
                    Email newEmail = ((NewEmailNotification) serverResponse).getNewEmail();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Nuova mail");
                        alert.setHeaderText("E' arrivata una nuova mail da: " + newEmail.getSender());
                        alert.show();
                        controller.addEmailToInbox(newEmail);
                    });
                }
                else if (serverResponse instanceof DeleteEmailNotification) {
                    Email emailToDelete = ((DeleteEmailNotification) serverResponse).getDeletedEmail();
                    Platform.runLater(() -> {
                        controller.deleteAndUpdateView(emailToDelete);
                    });
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("The server doesn't seem connected");
                alert.show();
            });
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
