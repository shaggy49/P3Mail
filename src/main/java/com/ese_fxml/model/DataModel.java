package com.ese_fxml.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.io.File;

/**
 * @author fede, nina, matto
 */
public class DataModel {

    private final ObservableList<Email> emailList = FXCollections.observableArrayList(new Callback<Email, Observable[]>() {
        @Override
        public Observable[] call(Email email) {
            return new Observable[] {email.mailSenderProperty(), email.mailReceiverProperty()}; //StringProperty implementa Observable, quindi può essere inserito in un array di Observable per up-casting
        }
    });

    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

    public ObjectProperty<Email> currentEmailProperty() {
        return currentEmail;
    }

    public final Email getCurrentEmail() {
        return currentEmailProperty().get();
    }

    public final void setCurrentEmail(Email email) {
        currentEmailProperty().set(email);
    }

    public ObservableList<Email> getEmailList() {
        return emailList;
    }

    public void loadData(File file) {
        // mock...
        emailList.setAll(
                new Email(1, "Jacob", "Smith", "mail1", "text1", true),
                new Email(2, "Isabella", "Johnson", "mail2", "text2", false),
                new Email(3, "Ethan", "Williams", "mail3", "text3", true),
                new Email(4, "Emma", "Jones", "mail4", "text4", false),
                new Email(5, "Michael", "Brown", "mail5", "text5", true)
        );
    }

    public void saveData(File file) { }
}

