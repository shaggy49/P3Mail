module com.p3mail.application.p3mail {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.p3mail.application to javafx.fxml;
    exports com.p3mail.application;
}