package com.example.labs.core;
import com.example.labs.model.BoyStudent;
import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.example.labs.core.TimerService;
import javafx.scene.canvas.GraphicsContext;
import com.example.labs.model.Student;
import java.util.Random;
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


    private Set<Integer> activeIds = new HashSet<>();

    private long treeMapTimeNanos = 0;
    private TreeMap<Long,Integer> birthToId = new TreeMap<>();
    private float n1TimeOfLife = 1.0f;
    private float n2TimeOfLife = 1.0f;
    private Random random = new Random();


    private List<IBehaviour> objects = new LinkedList<>();


    private Habitat(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void setN1TimeofLife(float timeofLife) {
        this.n1TimeOfLife = timeofLife;
    }

    public void setN2TimeOfLife(float timeofLife) {
        this.n2TimeOfLife = timeofLife;
    }



    public static Habitat getHabitat(int width, int height) {
        if(habitat == null) {
            habitat = new Habitat(width,height);
        }
        return habitat;
    }
    public void setParams(float n1, float n2, double p1, double p2, float nt1, float nt2) {
        this.N1 = n1;
        this.N2 = n2;
        this.P1 = p1;
        this.P2 = p2;
        this.n1TimeOfLife = nt1;
        this.n2TimeOfLife = nt2;
    }
    public static Habitat getHabitat() {
        if (habitat == null) {
            throw new IllegalStateException("Habitat not initialized with dimensions");
        }
        return habitat;
    }

    public void updateAll(float deltaTime) {
        simulationTime += deltaTime;
        treeMapTimeNanos += (long)(deltaTime * 1_000_000_000L);
        boyTimer += deltaTime;
        girlTimer += deltaTime;

        while (boyTimer >= N1) {
            boyTimer -= N1;
            if (Math.random() < P1) {
                float x = (float)Math.random() * (width-Student.SIZE);
                float y = (float)Math.random() * (height-Student.SIZE);
                int id = generateId();
                objects.add(new BoyStudent(id, x, y, n1TimeOfLife, simulationTime,treeMapTimeNanos));
                System.out.println("Объект с " + id + " создан в " + simulationTime );
                activeIds.add(id);
                birthToId.put(treeMapTimeNanos,id);
                boyCount++;
            }
        }
        while (girlTimer >= N2) {
            girlTimer -= N2;
            if (Math.random() < P2) {
                float x = (float)Math.random() * (width-Student.SIZE);
                float y = (float)Math.random() * (height-Student.SIZE);
                int id = generateId();
                objects.add(new GirlStudent(id, x,y,n2TimeOfLife, simulationTime,treeMapTimeNanos));
                System.out.println("Объект с " + id + " создан в " + simulationTime );
                activeIds.add(id);
                birthToId.put(treeMapTimeNanos,id);
                girlCount++;
            }
        }

        Iterator<IBehaviour> it = objects.iterator();
        while(it.hasNext()) {
            IBehaviour obj = it.next();
            obj.update(deltaTime);
            if(simulationTime >= obj.getCreationTime() + obj.getLifeTime()) {
                it.remove();
                System.out.println("Объект с " + obj.getId() + " удален в " + simulationTime );
                activeIds.remove(obj.getId());
                birthToId.remove(obj.getCreationTimeNanos());
            }
        }

    }
    public void renderAll(GraphicsContext gc) {
        for (IBehaviour obj : objects) {
            obj.render(gc);
        }
    }




    public void reset() {
        objects.clear();
        activeIds.clear();
        birthToId.clear();

        boyTimer = 0;
        girlTimer = 0;
        simulationTime = 0;
        treeMapTimeNanos = 0;
        boyCount = 0;
        girlCount = 0;
        System.out.println("Habitat reset. simulationTime = 0");


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

    public int generateId() {
        int newId = 0;
        int attempts = 0;
        do {
            newId = random.nextInt(999999) + 1;
            attempts++;
            if (attempts > 1000000) {
                throw new RuntimeException("Невозможно сгенерировать уникальный ID: все числа заняты");
        }
        } while (activeIds.contains(newId));

        return newId;
    }
    //лаба3=======================================================================================
    public TreeMap<Long,Integer> getBirthToId() {
        return birthToId;
    }
    public Set<Integer> getActiveIds() {
        return activeIds;
    }
}


