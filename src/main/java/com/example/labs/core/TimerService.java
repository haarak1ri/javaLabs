package com.example.labs.core;
import javafx.animation.AnimationTimer;

public class TimerService {
    private Runnable onUpdate; // контейнер хранящий какой то метод
    private AnimationTimer timer;
    private Habitat habitat;
    private long lastTime = 0;
    private boolean isRunning = false;
    private boolean onPause = false;



    public TimerService(Habitat habitat) {
        this.habitat = habitat;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                float deltaTime = (now - lastTime) / 1e9f;
                lastTime = now;
                habitat.updateAll(deltaTime);

                if(onUpdate != null) { //запускает переданный метод через лямбду
                    onUpdate.run();
                }

            }
        };
    }

    public void start() {
        lastTime = 0;
        timer.start();
        isRunning = true;
    }

    public void stop() {
        timer.stop();
        isRunning = false;
        onPause = false;
        lastTime = 0;
    }

    public void pause() {
        timer.stop();
        isRunning = false;
        onPause = true;
    }


    public boolean isRunning() {
        return isRunning;
    }
    public boolean isOnPause() {
        return onPause;
    }

    public void setOnUpdate(Runnable someAction) {
        this.onUpdate = someAction;
    }

    public void setIsRunning(boolean state) {
        this.isRunning = state;
    }
    public void setIsOnPause(boolean state) {
        this.onPause = state;
    }
}