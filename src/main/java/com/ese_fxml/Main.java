package com.ese_fxml;

import com.ese_fxml.controller.ListController;
import com.ese_fxml.model.DataModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author fede, nina, matto
 */
public class Main extends Application {
    /*
     * It creates components and insert them into the panel.
     */

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();
        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("resources/list.fxml"));
        root.setCenter(listLoader.load());
        ListController listController = listLoader.getController();

        DataModel model = new DataModel();
        listController.initModel(model);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
