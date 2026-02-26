package com.example.labs.model;

import javafx.scene.image.Image;

public class BoyStudent extends Student {
    private static final Image BoyImage = new Image(BoyStudent.class.getResourceAsStream("/Images/graduated.png"));

    public BoyStudent(float x, float y) {
        super(x,y,BoyImage);
    }
}
