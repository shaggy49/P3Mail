module com.example._ese_provs_fxml {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.ese_fxml.controller to javafx.fxml;
    exports com.ese_fxml.controller;
}