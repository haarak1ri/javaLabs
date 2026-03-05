package com.example.labs;

import com.example.labs.core.Habitat;
import com.example.labs.core.TimerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        HelloController controller = fxmlLoader.getController();

        Habitat habitat = Habitat.getHabitat(1280,920);
        TimerService timer = new TimerService(habitat);

        controller.setHabitat(habitat);
        controller.setTimer(timer);

        stage.setScene(scene);
        stage.show();
    }
}
