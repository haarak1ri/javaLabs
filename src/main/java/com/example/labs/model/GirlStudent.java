package com.example.labs.model;

import javafx.scene.image.Image;

public class GirlStudent extends Student{
    private static final Image GirlImage = new Image(GirlStudent.class.getResourceAsStream("/Images/student.png"));
    public GirlStudent(float x, float y) {
        super(x,y,GirlImage);
    }

}
