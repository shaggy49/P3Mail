module com.example._ese_provs_fxml {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.ese_fxml.controller to javafx.fxml;
    exports com.ese_fxml.controller;
    exports com.ese_fxml;
    opens com.ese_fxml to javafx.fxml;
}