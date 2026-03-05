package com.example.labs.model;

import javafx.scene.canvas.GraphicsContext;

public interface IBehaviour {
    void update(float deltaTime);
    void render(GraphicsContext gc);
    float getCreationTime();
    long getCreationTimeNanos();
    float getLifeTime();
    int getId();
}
