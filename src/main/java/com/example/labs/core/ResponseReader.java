package com.example.labs.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class ResponseReader implements Runnable {
    private boolean running;
    private final BufferedReader reader;
    private final TextArea outputArea;
    private Thread thread;

    public ResponseReader(PipedReader pipedReader, TextArea outputArea) {
        this.reader = new BufferedReader(pipedReader);
        this.outputArea = outputArea;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
            running = true;
        }
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (reader.ready()) {
                    String response = reader.readLine();
                    if (response != null) {
                        Platform.runLater(() ->
                                outputArea.appendText(response + "\n")
                        );
                    }
                }
                Thread.sleep(50);
            } catch (IOException | InterruptedException e) {
                break;
            }
        }
    }
}