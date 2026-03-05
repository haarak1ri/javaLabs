package com.example.labs.model;

import javafx.scene.image.Image;

public class GirlStudent extends Student{
    private static final Image GirlImage = new Image(GirlStudent.class.getResourceAsStream("/Images/student.png"));
    public GirlStudent(int id,float x, float y, float lTime,float cTime, long cTimeNanos) {
        super(id,x,y,GirlImage,lTime,cTime, cTimeNanos);
    }

}
