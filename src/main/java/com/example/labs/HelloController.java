package com.example.labs;

import com.example.labs.core.Habitat;
import com.example.labs.core.TimerService;
import com.example.labs.model.IBehaviour;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController  {
    @FXML
    private AnchorPane root;
    @FXML
    private Canvas gameCanvas;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;

    @FXML
    private Button showTimerButton;
    @FXML
    private Label timerLabel;
    @FXML
    private Label boyCountLabel;
    @FXML
    private Label girlCountLabel;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label totalSimulationTime;
    private GraphicsContext gc;

    private Habitat habitat;
    private TimerService timerService;

    private boolean isTimerVisible = false;
    @FXML
    public void initialize() {
        root.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);

        this.gc = gameCanvas.getGraphicsContext2D();

        updateButtonState(false);
        showTimerButton.setDisable(false);
        toggleTimer();
        hideUIStatistics();

        clearCanvas();
    }

    private void clearCanvas() {
        if (gc != null) {
            gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        }
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    public void setTimer(TimerService timerService) {
        this.timerService = timerService;
        timerService.setOnUpdate(() -> {refreshDisplay();
            updateTimerDisplay();
         });
    }
    private void updateTimerDisplay() {
        if (timerLabel != null && habitat != null) {

            float time = habitat.getSimulationTime();
            timerLabel.setText(String.format("Время: %.1f сек", time));
        }
    }
    private void handleKeyPress(KeyEvent event) {
        executeActionForKey(event.getCode());
        event.consume();
    }

    private void executeActionForKey(KeyCode code) {
        switch (code) {
            case B:
                startSimulation();
                break;
            case E:
                stopSimulation();
                break;
            case T:
                toggleTimer();
                break;
        }
    }

    private void toggleTimer() {
        if(isTimerVisible) {
            isTimerVisible = false;
            timerLabel.setVisible(false);
        }
        else {
            isTimerVisible = true;
            timerLabel.setVisible(true);
        }
    }
    private void hideUIStatistics() {
        boyCountLabel.setVisible(false);
        girlCountLabel.setVisible(false);
        totalCountLabel.setVisible(false);
        totalSimulationTime.setVisible(false);
    }
    private void showUIStatistics() {
        boyCountLabel.setVisible(true);
        girlCountLabel.setVisible(true);
        totalCountLabel.setVisible(true);
        totalSimulationTime.setVisible(true);
    }

    private void updateButtonState(boolean isRunning) {
        startButton.setDisable(isRunning);
        stopButton.setDisable(!isRunning);
    }

    private void startSimulation() {
        if(!timerService.isRunning()) {
            hideUIStatistics();
            updateButtonState(true);
            habitat.reset();
            timerService.start();
        }
    }

    private void stopSimulation() {

        if(timerService.isRunning()) {
            timerService.stop();
            showStatistics();
            updateButtonState(false);
            showUIStatistics();
        }
    }



    private void showStatistics() {
        if (habitat != null) {
            boyCountLabel.setText("Мальчики: " + habitat.getBoyCount());
            girlCountLabel.setText("Девочки: " + habitat.getGirlCount());
            totalCountLabel.setText("Всего: " + habitat.getTotalCount());
            String timeStr = String.format("%.1f", habitat.getSimulationTime());
            totalSimulationTime.setText("Время: " + timeStr + " сек");
        }
    }


    public void handleStartButton(ActionEvent actionEvent) {
        startSimulation();
    }

    public void handleStopButton(ActionEvent actionEvent) {
        stopSimulation();
    }

    public void handleShowTimerButton(ActionEvent actionEvent) {
        toggleTimer();
    }

    public void refreshDisplay() {
        if (habitat != null && gc != null) {
            clearCanvas();
            habitat.renderAll(gc);
        }
    }


}