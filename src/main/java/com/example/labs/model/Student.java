package com.example.labs.model;

import com.example.labs.core.Habitat;
import com.google.gson.annotations.Expose;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;



public abstract class Student implements IBehaviour {
    @Expose
    protected float x;
    @Expose
    protected float y;
    @Expose
    protected Image image;
    public static final int SIZE = 50;

    @Expose
    protected float creationTime = 0;
    @Expose
    protected long creationTimeNanos = 0;
    @Expose
    protected float lifeTime;
    @Expose
    protected int id;
//    private final Object positionLock = new Object();
    private transient volatile Object positionLock;

    public Student(int id,float x, float y, Image image, float lifeTime, float creationTime, long creationTimeNanos) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.image = image;
        this.lifeTime = lifeTime;
        this.creationTime = creationTime;
        this.creationTimeNanos = creationTimeNanos;
    }

//    public float getX() {
//        synchronized (positionLock) {
//            return x;
//        }
//    }
//    public float getY() {
//        synchronized (positionLock) {
//            return y;
//        }
//    }
    public abstract void initImage();

    private Object getLock() {
        if (positionLock == null) {
            synchronized (this) {
                if (positionLock == null) {
                    positionLock = new Object();
                }
            }
        }
        return positionLock;
    }
    public void setCreationTime(float time) {
        this.creationTime = time;
    }
    public void setTimeOfLife(float time) {
        this.lifeTime = time;
    }
    public void setCreationTimeNanos(long time) {
        this.creationTimeNanos = time;
    }
//    @Override
//    public void render(GraphicsContext gc) {
//        synchronized (positionLock) {
//            gc.drawImage(image,x,y,SIZE,SIZE);
//        }
//    }

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


//    public void setPosition(float newX, float newY) {
//        synchronized (positionLock) {
//            this.x = newX;
//            this.y = newY;
//        }
//    }
public float getX() {
    synchronized (getLock()) { return x; }
}
    public float getY() {
        synchronized (getLock()) { return y; }
    }
    public void render(GraphicsContext gc) {
        synchronized (getLock()) {
            gc.drawImage(image, x, y, SIZE, SIZE);
        }
    }
    public void setPosition(float newX, float newY) {
        synchronized (getLock()) {
            this.x = newX;
            this.y = newY;
        }
    }
}
