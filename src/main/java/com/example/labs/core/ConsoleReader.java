package com.example.labs.core;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PrintWriter;

public class ConsoleReader implements Runnable{
    private boolean running;
    private Habitat habitat;
    private final BufferedReader reader;
    private Thread thread;
    private UICommandListener UIListener; // ссылка на контроллер
    private final ResponseWriter responseWriter;

    public void start() {
        if(thread == null) {
            thread = new Thread(this);
            thread.start();
            running = true;
        }

    }
    public void setUIListener(UICommandListener listener) {
        this.UIListener = listener;
    }
    public ConsoleReader(Habitat habitat, PipedReader pipedReader, ResponseWriter responseWriter) {
        this.reader = new BufferedReader(pipedReader);
        this.responseWriter = responseWriter;
        this.habitat = habitat;
    }

    public void stop() {
        running = false;
        if(thread != null) {
            thread.interrupt();
        }
    }


    @Override
    public void run() {

        while (running) {
            try {

                if (reader.ready()) {

                    String com = reader.readLine();
                    System.out.println("Прочитано: " + com);
                    if (com != null) {
                        executeCommand(com);
                    }
                } else {

                }
                Thread.sleep(5);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
                break;

            } catch (IOException e) {

                e.printStackTrace();
                break;
            }
        }
    }

    private void executeCommand(String command) {

        String response = "";
        if(command.equals("showtimer")) {

            if( UIListener != null && UIListener.getTimerState().equals("hide")) {
                UIListener.onToggleTimer();
                response = "the timer is shown";
            } else {
                response = "timer has already shown";
            }
        }
        else if(command.equals("hidetimer")) {
            if(UIListener != null && UIListener.getTimerState().equals("show")) {
                UIListener.onToggleTimer();
                response = "the timer is hidden";
            } else {
                response = "timer has already hidden";
            }
        }
        else {
            response = "Unknown command " + command;
        }
        if(!response.isEmpty()) {

            responseWriter.sendResponse(response);
        }
    }
}
