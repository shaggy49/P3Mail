module com.p3mail.application.p3mail {
    requires javafx.controls;
    requires javafx.fxml;


    exports com.p3mail.application;
    opens com.p3mail.application to javafx.fxml;
    exports com.p3mail.application.model;
    opens com.p3mail.application.model to javafx.fxml;
    exports com.p3mail.application.controller;
    opens com.p3mail.application.controller to javafx.fxml;
}