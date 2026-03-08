package com.example.labs.core;

import com.example.labs.model.BoyStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseAIBoys extends BaseAI {
    private static final float DIR_CHANGE_TIME = 2.0f;
    private float timeSinceLastChange = 0;
    private Random random = new Random();
    private Map<Integer, float[]> directions = new HashMap<>();

    public BaseAIBoys(Habitat habitat, int priority,float v) {
        super(habitat,priority,v);
    }

    @Override
    protected void moveObjects(float deltaTime) {
        timeSinceLastChange += deltaTime;
        List<IBehaviour> boys = habitat.getBoysStudents();
        directions.keySet().retainAll(habitat.getActiveIds());

        for(IBehaviour obj : boys) {
            BoyStudent boy = (BoyStudent) obj;
            int id = boy.getId();

            if(!directions.containsKey(id)) {
                float angle = (float) (random.nextDouble() * 2 * Math.PI);
                float dx = (float) Math.cos(angle);
                float dy = (float) Math.sin(angle);
                directions.put(id, new float[]{dx, dy});
            }

            if (timeSinceLastChange >= DIR_CHANGE_TIME) {
                float angle = (float) (random.nextDouble() * 2 * Math.PI);
                float dx = (float) Math.cos(angle);
                float dy = (float) Math.sin(angle);
                directions.put(id, new float[]{dx, dy});
            }

            // Получаем текущее направление
            float[] dir = directions.get(id);


            float newX = boy.getX() + dir[0] * velocity * deltaTime;
            float newY = boy.getY() + dir[1] * velocity * deltaTime;

            boolean bounced = false;
            if (newX < 0) {
                dir[0] = -dir[0];
                newX = 0;
                bounced = true;
            }
            if (newX > habitat.getWidth() - Student.SIZE) {
                dir[0] = -dir[0];
                newX = habitat.getWidth() - Student.SIZE;
                bounced = true;
            }
            if (newY < 0) {
                dir[1] = -dir[1];
                newY = 0;
                bounced = true;
            }
            if (newY > habitat.getHeight() - Student.SIZE) {
                dir[1] = -dir[1];
                newY = habitat.getHeight() - Student.SIZE;
                bounced = true;
            }
            boy.setPosition(newX, newY);
        }
        if (timeSinceLastChange >= DIR_CHANGE_TIME) {
            timeSinceLastChange = 0;
        }
    }
}
