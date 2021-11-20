package com.ese_fxml.controller;

import com.ese_fxml.model.DataModel;
import javafx.fxml.FXML;
import com.ese_fxml.model.Email;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * @author fede, nina, matto
 */
public class ListController {

    @FXML
    private ListView<Email> listView;

    private DataModel model;

    /*
     * Questo metodo è una sorta di costruttore, che associa ad ogni controller il model
     * */
    public void initModel(DataModel model) {
        // ensure model is only set once:
        if (this.model != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }

        this.model = model;

        model.loadData(null);//  aggiunto per far caricare i dati delle email

        listView.setItems(model.getEmailList());

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                model.setCurrentEmail(newSelection)); //aggiunge il listener di changed


        model.currentEmailProperty().addListener((obs, oldEmail, newEmail) -> {
            if (newEmail == null) {
                listView.getSelectionModel().clearSelection();
            } else {
                listView.getSelectionModel().select(newEmail);
            }
        });

        listView.setCellFactory((ListView<Email> lv) -> new ListCell<Email>() {
            @Override
            public void updateItem(Email email, boolean empty) {
                super.updateItem(email, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(email.getMailSender() + " , " + email.getMailReceiver());
                }
            }
        });
    }
}