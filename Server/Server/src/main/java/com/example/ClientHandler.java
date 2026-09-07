package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private String name = "Unknown";
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            MultiThreadServer.clients.add(this);
            this.broadcastCLients();

            String line;
            while((line = in.readLine()) != null) {
                this.processMessage(line);
            }
        } catch (IOException var8) {
            System.out.println(this.name + " disconected");
        } finally {
            MultiThreadServer.clients.remove(this);
            this.broadcastCLients();
        }

        try {
            this.socket.close();
        } catch (IOException var7) {
        }

    }

    public String getName() {
        return this.name;
    }

    public void send(String msg) {
        if (this.out != null) {
            this.out.println(msg);
        }

    }

    private void processMessage(String message) {
        if (message.startsWith("NAME:")) {
            this.name = message.substring(5).trim();
            System.out.println("Client called himself " + this.name);
            this.broadcastCLients();
        } else if (message.startsWith("STOP_SYNC:")) {
            String target = message.substring(10).trim();
            this.sendToClient(target, "STOP");
            this.broadcastCLients();
        } else if (message.startsWith("START_SYNC:")) {
            String target = message.substring(11).trim();
            this.sendToClient(target, "START");
            this.broadcastCLients();
        } else {
            System.out.println("[" + this.name + "] " + message);
        }

    }

    private void sendToClient(String target, String command) {
        for(ClientHandler c : MultiThreadServer.clients) {
            if (c.getName().equals(target)) {
                c.send(command);
                return;
            }
        }

    }

    private void broadcastCLients() {
        StringBuilder sb = new StringBuilder();

        for(ClientHandler c : MultiThreadServer.clients) {
            if (sb.length() > 0) {
                sb.append(",");
            }

            sb.append(c.getName());
        }

        String msg = "CLIENT_LIST:" + String.valueOf(sb);

        for(ClientHandler c : MultiThreadServer.clients) {
            c.send(msg);
        }

    }
}