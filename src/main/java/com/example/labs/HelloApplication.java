package com.example.labs;

import com.example.labs.core.AppConfig;
import com.example.labs.core.Habitat;
import com.example.labs.core.TimerService;
import com.example.labs.db.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.labs.tcp.ServerCommandHandler;

import javax.imageio.IIOException;
import java.io.IOException;

public class HelloApplication extends Application {
    private AppConfig config;
    private HelloController controller;
    private ServerCommandHandler serverCommandHandler;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        this.controller = fxmlLoader.getController();

        Habitat habitat = Habitat.getHabitat(1280,920);
        TimerService timer = new TimerService(habitat);
        Database db = new Database();
        this.serverCommandHandler = new ServerCommandHandler(controller, habitat);
        controller.setHabitat(habitat);
        controller.setTimer(timer);
        controller.setConsoleWriter(habitat.getConsoleWriter());
        controller.setDatabase(db);
        controller.setStage(stage);
        controller.setSeverCommandHandler(serverCommandHandler);
        this.config = new AppConfig();
        config.loadConfig();
        controller.setConfig(this.config);

        stage.setScene(scene);
        stage.show();
        serverCommandHandler.start();
    }

    private void saveConfigBeforeExit() {
        if(config != null) {
            config.saveConfig();
        }
    }
    @Override
    public void stop() {
        if (serverCommandHandler != null) {
            serverCommandHandler.stop();
        }
        if (config != null) {
            controller.updateConfigFromUI();
            config.saveConfig();
            System.out.println("Конфиг сохранен");
        }
    }
}

