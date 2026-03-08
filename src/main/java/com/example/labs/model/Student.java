package com.example.labs.model;

import com.example.labs.core.Habitat;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;



public abstract class Student implements IBehaviour {
    protected float x,y;
    protected Image image;
    public static final int SIZE = 50;


    protected float creationTime = 0;
    protected long creationTimeNanos = 0;
    protected float lifeTime;
    protected int id;
    private final Object positionLock = new Object();

    public Student(int id,float x, float y, Image image, float lifeTime, float creationTime, long creationTimeNanos) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.image = image;
        this.lifeTime = lifeTime;
        this.creationTime = creationTime;
        this.creationTimeNanos = creationTimeNanos;
    }
    public float getX() {
        synchronized (positionLock) {
            return x;
        }
    }
    public float getY() {
        synchronized (positionLock) {
            return y;
        }
    }



    @Override
    public void render(GraphicsContext gc) {
        synchronized (positionLock) {
            gc.drawImage(image,x,y,SIZE,SIZE);
        }
    }

    @Override
    public void update(float deltaTime) {
//        move(deltaTime);
    }

    @Override
    public float getLifeTime() {
        return this.lifeTime;
    }
    @Override
    public float getCreationTime() {
        return this.creationTime;
    }
    @Override
    public long getCreationTimeNanos() {
        return this.creationTimeNanos;
    }
    @Override
    public int getId() {
        return this.id;
    }

//    public void move(float deltaTime) {
////        x += 100 * deltaTime;
////        if(x > h.getWidth()) {
////            x = -SIZE;
////        }
//    }


    public void setPosition(float newX, float newY) {
        synchronized (positionLock) {
            this.x = newX;
            this.y = newY;
        }
    }
}
