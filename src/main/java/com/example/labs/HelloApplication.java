package com.example.labs;

import com.example.labs.core.AppConfig;
import com.example.labs.core.Habitat;
import com.example.labs.core.TimerService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.imageio.IIOException;
import java.io.IOException;

public class HelloApplication extends Application {
    private AppConfig config;
    private HelloController controller;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        this.controller = fxmlLoader.getController();

        Habitat habitat = Habitat.getHabitat(1280,920);
        TimerService timer = new TimerService(habitat);

        controller.setHabitat(habitat);
        controller.setTimer(timer);
        controller.setConsoleWriter(habitat.getConsoleWriter());
        controller.setStage(stage);
        this.config = new AppConfig();
        config.loadConfig();
        controller.setConfig(this.config);

        stage.setScene(scene);
        stage.show();
    }

    private void saveConfigBeforeExit() {
        if(config != null) {
            config.saveConfig();
        }
    }
    @Override
    public void stop() {
        if (config != null) {
            controller.updateConfigFromUI();
            config.saveConfig();
            System.out.println("Конфиг сохранен");
        }
    }
}

