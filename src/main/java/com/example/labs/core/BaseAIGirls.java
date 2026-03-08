package com.example.labs.core;

import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BaseAIGirls extends BaseAI {
    private static final float RADIUS = 50.0f;
    private Map<Integer, Float> angles = new HashMap<>();
    private Map<Integer, float[]> centers = new HashMap<>();

    public BaseAIGirls(Habitat habitat, int priority, float v) {
        super(habitat, priority, v);
    }

    @Override
    protected void moveObjects(float deltTime) {

        List<IBehaviour> girls = habitat.getGirlStudents();


        angles.keySet().retainAll(habitat.getActiveIds());
        centers.keySet().retainAll(habitat.getActiveIds());

        for (IBehaviour obj : girls) {
            GirlStudent girl = (GirlStudent) obj;
            int id = girl.getId();


            if (!centers.containsKey(id)) {
                // Центр окружности = текущая позиция
                centers.put(id, new float[]{girl.getX(), girl.getY()});
                angles.put(id, 0.0f); // начинаем с угла 0
            }

            // Получаем центр и текущий угол
            float[] center = centers.get(id);
            float angle = angles.get(id);

            // Увеличиваем угол (движение по окружности)
            // угловая скорость = velocity / radius
            angle += (velocity / RADIUS) * deltTime;

            // Если угол больше 360 градусов (2π радиан) - оборачиваем
            if (angle > 2 * Math.PI) {
                angle -= 2 * Math.PI;
            }

            // Сохраняем новый угол
            angles.put(id, angle);

            // Вычисляем новую позицию на окружности
            float newX = center[0] + RADIUS * (float) Math.cos(angle);
            float newY = center[1] + RADIUS * (float) Math.sin(angle);


            if (newX < 0) newX = 0;
            if (newX > habitat.getWidth() - Student.SIZE) newX = habitat.getWidth() - Student.SIZE;
            if (newY < 0) newY = 0;
            if (newY > habitat.getHeight() - Student.SIZE) newY = habitat.getHeight() - Student.SIZE;

            // Устанавливаем новую позицию
            girl.setPosition(newX, newY);
        }
    }
}