package com.example.labs.core;
import com.example.labs.model.BoyStudent;
import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.core.TimerService;
import javafx.scene.canvas.GraphicsContext;
import com.example.labs.model.Student;

import java.util.*;

public class Habitat {
    private static Habitat habitat;
    private float N1 = 1.0f;
    private float N2 = 1.5f;
    private double P1 = 0.7;
    private double P2 = 0.5;
    private int width;
    private int height;

    private float boyTimer = 0;
    private float girlTimer = 0;
    private float simulationTime = 0;
    private int boyCount = 0;
    private int girlCount = 0;

    private List<IBehaviour> objects = new ArrayList<>();


    private Habitat(int width, int height){
        this.width = width;
        this.height = height;
    }

    public static Habitat getHabitat(int width, int height) {
        if(habitat == null) {
            habitat = new Habitat(width,height);
        }
        return habitat;
    }
    public void setParams(float n1, float n2, double p1, double p2) {
        this.N1 = n1;
        this.N2 = n2;
        this.P1 = p1;
        this.P2 = p2;
    }
    public static Habitat getHabitat() {
        if (habitat == null) {
            throw new IllegalStateException("Habitat not initialized with dimensions");
        }
        return habitat;
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

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }


}
