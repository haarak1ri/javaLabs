package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class MultiThreadServer {
    public static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started");

        while(true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client: " + String.valueOf(socket.getInetAddress()));
            ClientHandler hander = new ClientHandler(socket);
            (new Thread(hander)).start();
        }
    }
}