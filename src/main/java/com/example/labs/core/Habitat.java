package com.example.labs.core;
import com.example.labs.model.BoyStudent;
import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.core.TimerService;
import javafx.scene.canvas.GraphicsContext;
import com.example.labs.model.Student;

import java.util.*;

public class Habitat {
    private final float N1 = 1.0f;
    private final float N2 = 1.5f;
    private final double P1 = 0.7;
    private final double P2 = 0.5;
    private static int width;
    private static int height;

    private float boyTimer = 0;
    private float girlTimer = 0;
    private float simulationTime = 0;
    private int boyCount = 0;
    private int girlCount = 0;

    List<IBehaviour> objects = new ArrayList<>();


    public Habitat(int width, int height){
        this.width = width;
        this.height = height;
    }
    public void updateAll(float deltaTime) {
        simulationTime += deltaTime;
        boyTimer += deltaTime;
        girlTimer += deltaTime;

        while (boyTimer >= N1) {
            boyTimer -= N1;
            if (Math.random() < P1) {
                float x = (float)Math.random() * (width-Student.SIZE);
                float y = (float)Math.random() * (height-Student.SIZE);
                objects.add(new BoyStudent(x, y));
                boyCount++;
            }
        }
        while (girlTimer >= N2) {
            girlTimer -= N2;
            if (Math.random() < P2) {
                float x = (float)Math.random() * (width-Student.SIZE);
                float y = (float)Math.random() * (height-Student.SIZE);
                objects.add(new GirlStudent(x,y));
                girlCount++;
            }
        }
        for (IBehaviour obj : objects) {
            obj.update(deltaTime);
        }
    }
    public void renderAll(GraphicsContext gc) {
        for (IBehaviour obj : objects) {
            obj.render(gc);
        }
    }



    public void reset() {
        objects.clear();
        boyTimer = 0;
        girlTimer = 0;
        simulationTime = 0;
        boyCount = 0;
        girlCount = 0;
    }
    public List<IBehaviour> getObjects() {
        return objects;
    }

    public float getSimulationTime() {
        return simulationTime;
    }

    public int getBoyCount() {
        return boyCount;
    }

    public int getGirlCount() {
        return girlCount;
    }

    public int getTotalCount() {
        return objects.size();
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }
}
