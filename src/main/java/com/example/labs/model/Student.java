package com.example.labs.model;

import com.example.labs.core.Habitat;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;



public abstract class Student implements IBehaviour {
    protected float x,y;
    protected Image image;
    public static final int SIZE = 50;
    Habitat h = Habitat.getHabitat();

    public Student(float x, float y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }
    public float getX() {return x;}
    public float getY() {return y;}



    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(image,x,y,SIZE,SIZE);
    }

    @Override
    public void update(float deltaTime) {
        move(deltaTime);
    }
    public void move(float deltaTime) {
        x += 100 * deltaTime;
        if(x > h.getWidth()) {
            x = -SIZE;
        }
    }


}
