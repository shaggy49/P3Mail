package com.p3mail.application.client.controller;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.p3mail.application.ClientMain;
import com.p3mail.application.client.model.Client;
import com.p3mail.application.connection.model.Email;
import com.p3mail.application.connection.request.SendRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReplyController {
    Socket socketConnection = null;
    ObjectOutputStream out = null;


    @FXML
    private Label receiversLbl;

    @FXML
    private TextField receiversField;

    @FXML
    private TextArea sendText;

    @FXML
    private TextArea receivedText;

    @FXML
    private Button sendButton;

    private Client model;
    private Email email;
    private List<String> replyReceiver = new ArrayList<>();
    private Stage stage;

    /**
     * This method takes the model as parameter to save information about model.
     *
     * @param isReplyAll
     * @param model
     * @param email
     * @param stage
     */
    @FXML
    void initialize(Boolean isReplyAll, Client model, Email email, Stage stage) {
        this.stage = stage;
        this.model = model;
        this.email = email;
        this.socketConnection = model.getSocket();
        this.out = model.getOut();
        replyReceiver.add(email.getSender());
        if (isReplyAll) {
            receiversLbl.setText("Destinatari:");
            StringBuilder stringBuilder = new StringBuilder();
            for (String receiver : email.getReceivers()) {
                if (!receiver.equals(model.emailAddressProperty().get())) {
                    stringBuilder.append(", ").append(receiver);
                    replyReceiver.add(receiver);
                }
            }
            receiversField.setText(email.getSender() + stringBuilder.toString());
        } else {
            receiversLbl.setText("Destinatario:");
            receiversField.setText(email.getSender());
        }
        receivedText.setText("Data email ricevuta: " + email.getDate() + '\n' + "Oggetto: " + email.getObject() + '\n' + "Contenuto: " + email.getText());
    }


    /**
     * When send button is clicked if sendText area is empty it emits alert,
     * else it changes controller and fxml file to MainWindowController and mainWindow.fxml.
     * When cancel buttons is clicked it changes controller and fxml file to MainWindowController
     * and mainWindow.fxml.
     */
    public void handleButtons(MouseEvent mouseEvent) throws IOException {
        Button b = (Button) mouseEvent.getSource();
        if (b.getId().equals("sendButton") && sendText.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Inserisci un messaggio di risposta per inviare la mail");
            alert.showAndWait();
        } else {
            double oldHeight;
            double oldWidth;
            if (b.getId().equals("cancelButton")) {
                FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml")));
                Parent root = (Parent) loader.load();
                MainWindowController newMainWindowController = loader.getController();
                newMainWindowController.initialize(false, model, stage);

                oldWidth = stage.getWidth();
                oldHeight = stage.getHeight();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                stage.setTitle("Email client");
                stage.setScene(scene);
                stage.setWidth(oldWidth);
                stage.setHeight(oldHeight);
                stage.show();
            } else {
                String stringBuilder = sendText.getText() + '\n' + '\n' + "----------Messaggio di risposta----------" + '\n' + "Da: " + email.getSender() + '\n' + "Data email: " + email.getDate() + '\n' + "Contenuto: " + email.getText();
                String objectField = "";
				if (email.getObject().startsWith("Re: "))
					objectField = email.getObject();
				else if (email.getObject().startsWith("Fwd: "))
					objectField = "Re: " + email.getObject().substring(5);
				else
					objectField = "Re: " + email.getObject();

                Email emailToSend = new Email(
                        model.emailAddressProperty().get(),
                        replyReceiver,
                        objectField,
                        stringBuilder.toString());
                System.out.println("You want to send the email: "); //debug purpose
                System.out.println(emailToSend);
                try {
                    model.writeOut(new SendRequest(emailToSend));
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("The server doesn't seem connected");
                    alert.show();
                } finally {
                    FXMLLoader loader = new FXMLLoader((ClientMain.class.getResource("mainWindow.fxml")));
                    Parent root = (Parent) loader.load();
                    MainWindowController newMainWindowController = loader.getController();
                    newMainWindowController.initialize(false, model, stage);

                    oldWidth = stage.getWidth();
                    oldHeight = stage.getHeight();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
                    stage.setTitle("Email client");
                    stage.setScene(scene);
                    stage.setWidth(oldWidth);
                    stage.setHeight(oldHeight);
                    stage.show();
                }
            }

        }
    }
}
