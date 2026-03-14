package com.example.labs.core;

import java.io.PrintWriter;
import java.io.PipedWriter;

public class ResponseWriter {
    private final PrintWriter writer;

    public ResponseWriter(PipedWriter pipedWriter) {
        this.writer = new PrintWriter(pipedWriter, true);
    }

    public void sendResponse(String response) {
        writer.println(response);
    }
}