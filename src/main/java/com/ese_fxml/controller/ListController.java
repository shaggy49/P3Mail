package com.ese_fxml.controller;

import com.ese_fxml.model.DataModel;
import javafx.fxml.FXML;
import com.ese_fxml.model.Email;
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

        model.loadData(null);//  aggiunto per far caricare i dati delle person

        listView.setItems(model.getPersonList());

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                model.setCurrentPerson(newSelection)); //aggiunge il listener di changed


        model.currentPersonProperty().addListener((obs, oldPerson, newPerson) -> {
            if (newPerson == null) {
                listView.getSelectionModel().clearSelection();
            } else {
                listView.getSelectionModel().select(newPerson);
            }
        });

        listView.setCellFactory((ListView<Person> lv) -> new ListCell<Person>() {
            @Override
            public void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(person.getFirstName() + " , " + person.getLastName());
                }
            }
        });
    }
}