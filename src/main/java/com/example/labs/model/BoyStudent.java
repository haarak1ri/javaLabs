package com.example.labs.model;

import javafx.scene.image.Image;

public class BoyStudent extends Student {
    private static final Image BoyImage = new Image(BoyStudent.class.getResourceAsStream("/Images/graduated.png"));

    public BoyStudent(int id, float x, float y, float lTime,float cTime, long cTimeNanos) {
        super(id,x,y,BoyImage,lTime,cTime, cTimeNanos);
    }
}
