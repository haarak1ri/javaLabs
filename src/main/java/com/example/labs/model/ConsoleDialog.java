package com.example.labs.model;

import com.example.labs.core.ConsoleWriter;
import com.example.labs.core.ResponseReader;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.PipedReader;

public class ConsoleDialog extends Stage {
    private final ConsoleWriter consoleWriter;
    private final ResponseReader responseReader;
    private final TextArea outputArea;
    private final TextField inputField;

    public ConsoleDialog(ConsoleWriter consoleWriter, PipedReader responseReaderPipe) {
        this.consoleWriter = consoleWriter;
        setTitle("Console");
        setWidth(600);
        setHeight(400);

        setMinWidth(400);
        setMinHeight(300);

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: monospace;");

        inputField = new TextField();
        inputField.setPromptText("enter the command...");

        inputField.setOnAction(e -> {
            String command = inputField.getText().trim();
            if (!command.isEmpty()) {

                outputArea.appendText("> " + command + "\n");


                consoleWriter.sendCommand(command);


                inputField.clear();
            }
        });
        this.responseReader = new ResponseReader(responseReaderPipe, outputArea);
        this.responseReader.start();

        setOnCloseRequest(e -> {
            responseReader.stop();
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(outputArea, inputField);
        root.setStyle("-fx-padding: 10;");
        VBox.setVgrow(outputArea, Priority.ALWAYS);

        Scene scene = new Scene(root);
        setScene(scene);
    }
    public void appendText(String text) {
        Platform.runLater(() -> outputArea.appendText(text + "\n"));
    }
    public void clearOutput() {
        Platform.runLater(() -> outputArea.clear());
    }
    @Override
    public void hide() {
        responseReader.stop();
        super.hide();
    }
}
