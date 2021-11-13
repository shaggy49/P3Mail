package com.ese_fxml.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.io.File;

/**
 *
 * @author liliana
 */
public class DataModel {

    private final ObservableList<Email> emailList = FXCollections.observableArrayList(new Callback<Email, Observable[]>() {
        @Override
        public Observable[] call(Email Email) {
            return new Observable[] {com.ese_fxml.model.Email.firstNameProperty(), com.ese_fxml.model.Email.lastNameProperty()}; //StringProperty implementa Observable, quindi può essere inserito in un array di Observable per up-casting
        }
    });

    private final ObjectProperty<Email> currentEmail = new SimpleObjectProperty<>(null);

    public ObjectProperty<Email> currentEmailProperty() {
        return Email;
    }

    public final Email getCurrentEmail() {
        return EmailProperty().get();
    }

    public final void setCurrentEmail(Email Email) {
        com.ese_fxml.model.EmailProperty().set(com.ese_fxml.model.Email);
    }

    public ObservableList<Email> getEmailList() {
        return EmailList ;
    }

    public void loadData(File file) {
        // mock...
        EmailList.setAll(
                new Email("Jacob", "Smith", "jacob.smith@example.com"),
                new Email("Isabella", "Johnson","isabella.johnson@example.com"),
                new Email("Ethan", "Williams", "ethan.williams@example.com"),
                new Email("Emma", "Jones", "emma.jones@example.com"),
                new Email("Michael", "Brown", "michael.brown@example.com")
        );
    }

    public void saveData(File file) { }
}

