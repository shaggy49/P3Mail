module com.example._soluzione_prof {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example._soluzione_prof to javafx.fxml;
    exports com.example._soluzione_prof;
}