package com.example.labs.core;

import java.io.PipedWriter;
import java.io.PrintWriter;

public class ConsoleWriter {
    private final PrintWriter writer;

    public ConsoleWriter(PipedWriter pipedWriter ){
        this.writer = new PrintWriter(pipedWriter,true);

    }

    public void sendCommand(String command) {

        writer.println(command);

    }
}
