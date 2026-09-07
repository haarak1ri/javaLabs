package com.example.labs.model;

import javafx.scene.canvas.GraphicsContext;

public interface IBehaviour {
    void update(float deltaTime);
    void render(GraphicsContext gc);
    float getCreationTime();
    long getCreationTimeNanos();
    float getLifeTime();
    int getId();
    void setCreationTime(float time);
    void setTimeOfLife(float time);
    void setCreationTimeNanos(long time);
    public IBehaviour copy();
    public IBehaviour copyForSir();
    String getType();
    float getX();
    float getY();

    void initImage();
}
