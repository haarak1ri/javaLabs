package com.example.labs.core;

import static com.example.labs.core.Habitat.getHabitat;

public abstract class BaseAI implements Runnable {
    protected Thread thread;
    protected boolean running = false;
    protected boolean paused = false;
    protected Habitat habitat;
    protected int priority;
    protected float velocity;
    private long lastTime = 0;

    public BaseAI(Habitat habitat, int pr, float v) {
        this.habitat = habitat;
        this.priority = pr;
        this.velocity = v;
    }

    public void start() {
        if(thread == null)  {
            thread = new Thread(this);
            thread.setPriority(priority);
            thread.start();
            running = true;
        }
    }

    public void pause() {
        paused = true;
    }

    public synchronized void resume() {
        if (paused) {
            lastTime = System.nanoTime();
            paused = false;
            notify();
            System.out.println(getClass().getSimpleName() + " восстановлен");
        }
    }

    public void setLastTime(long l) {
        this.lastTime = lastTime;
    }

    public int getPriority() {
        return thread.getPriority();
    }

    public void stop() {
        running = false;
        if(thread != null) {
            thread.interrupt();
        }
    }

    public void setSpeed(float v) {
        this.velocity = v;
    }
    public void setPriority(int priority) {
        this.priority = priority;
        if (thread != null) {
            thread.setPriority(priority);
        }
    }
    @Override
    public void run() {
        lastTime = System.nanoTime();
        while(running) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }

            }
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;
            moveObjects(deltaTime);

            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    protected abstract void moveObjects(float deltaTime);
}
