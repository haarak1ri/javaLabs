package com.example.labs.tcp;
import com.example.labs.HelloController;
import com.example.labs.core.Habitat;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.util.Random;

public class ServerCommandHandler implements Runnable {
    private static final Random RANDOM = new Random();
    private Thread thread;
    private boolean running = false;
    private PrintWriter out;
    private Socket socket;
    private HelloController controller;
    private final Habitat habitat;
    private static String clientName;
    private static final String[] NAMES = {
            "Alice", "Bob", "Charlie", "David", "Emma",
            "Frank", "Grace", "Henry", "Ivy", "Jack"
    };
    
    public ServerCommandHandler(HelloController controller, Habitat habitat) {
        this.controller = controller;
        this.habitat = habitat;
        this.clientName = NAMES[RANDOM.nextInt(NAMES.length)];
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
            running = true;
        }

    }

    public void stop() {
        running = false;
        if(thread != null) {
            thread.interrupt();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
        System.out.println("Сетевое соединение остановлено");
    }


    @Override
    public void run() {
            try {
                socket = new Socket("localhost",8080);
                out = new PrintWriter(socket.getOutputStream(),true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                sendToServer("NAME:" + clientName);

                while(running) {
                    String message;
                    while((message = in.readLine()) != null) {
                        processServerMessage(message);
                    }

                }
            } catch (ConnectException e) {
                System.err.println("Server not lauched!");
            } catch (IOException e) {
                System.err.println("Error : " + e.getMessage());
            }

    }

    private void processServerMessage(String message) {
        if (message.startsWith("NAME_TAKEN:")) {
            regenerateName();
            sendToServer("NAME:" + clientName);
            System.out.println("Name was taken, trying new name: " + clientName);
        }
        if(message.startsWith("CLIENT_LIST:")) {
            String line = message.substring(12);
            String[] names = line.split(",");
            Platform.runLater(()-> controller.updateClientList(names));
        }
        else if(message.equals("STOP")) {
            Platform.runLater(()->controller.simulationRunning.set(false));

        }
        else if(message.equals("START")) {
            Platform.runLater(()->controller.simulationRunning.set(true));
        }
    }

    private void regenerateName() {
        this.clientName = NAMES[RANDOM.nextInt(NAMES.length)];
        System.out.println("Name regenerated: " + clientName);
    }
    public static String getName() {
        return clientName;
    }
    public void sendToServer(String message) {
        if (out != null) out.println(message);
    }


}
