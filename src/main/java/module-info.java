module com.p3mail.application.p3mail {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.p3mail.application;
    opens com.p3mail.application to javafx.fxml;
    exports com.p3mail.application.client.model;
    opens com.p3mail.application.client.model to javafx.fxml;
    exports com.p3mail.application.client.controller;
    opens com.p3mail.application.client.controller to javafx.fxml;
    exports com.p3mail.application.connection.model;
    opens com.p3mail.application.connection.model to javafx.fxml;
}