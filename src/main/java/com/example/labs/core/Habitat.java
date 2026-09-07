package com.example.labs.core;
import com.example.labs.model.BoyStudent;
import com.example.labs.model.GirlStudent;
import com.example.labs.model.IBehaviour;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.scene.canvas.GraphicsContext;
import com.example.labs.model.Student;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Random;
import java.util.*;


public class Habitat implements Serializable {
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

    private List<IBehaviour> objects = new LinkedList<>();
    private Set<Integer> activeIds = new HashSet<>();
    private long treeMapTimeNanos = 0;
    private TreeMap<Integer,Long> birthToId = new TreeMap<>();
    private float n1TimeOfLife = 1.0f;
    private float n2TimeOfLife = 1.0f;
    private Random random = new Random();

    //лаба 4
    private final Object objectsLock = new Object();
    private final Object activeIdsLock = new Object();
    private final Object birthToIdLock = new Object();

    private BaseAIBoys boysAI;
    private BaseAIGirls girlsAI;

    private boolean isAiRunning = false;
    private float boysVelocity = 50.0f;
    private float girlsVelocity = 50.0f;

    private float circleRadius = 50.0f;
    private int boyPriority = Thread.NORM_PRIORITY;
    private int girlPriority = Thread.NORM_PRIORITY;

    // лаба 5
    //канал 1
    private PipedWriter commandWriter;
    private PipedReader commandReader;
    //канал 2
    private PipedWriter responseWriter;
    private PipedReader responseReader;

    private ConsoleWriter consoleWriter;
    private ConsoleReader consoleReader;
    private PipedReader responseReaderForUI;
    private UICommandListener pendingUIListener;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(IBehaviour.class, new IBehaviourDeserializer())
            .create();
    private FileProvider fileProvider;
    private boolean startWithFile;
    private boolean startWithDB;


    public void setFileProvider(FileProvider fileProvider) {
        this.fileProvider = fileProvider;
    }


    private Habitat(int width, int height){
        this.width = width;
        this.height = height;
    }

    private void initConsole() throws IOException {

        //канал для UI->logic
        this.commandWriter = new PipedWriter();
        this.commandReader = new PipedReader(this.commandWriter);

        //канал 2 для ответов
        this.responseWriter = new PipedWriter();
        this.responseReader = new PipedReader(this.responseWriter);

        this.consoleWriter = new ConsoleWriter(this.commandWriter);
        System.out.println("Каналы созданы");
        ResponseWriter respWriter = new ResponseWriter(this.responseWriter);
        this.consoleReader = new ConsoleReader(this, this.commandReader, respWriter);

        if (pendingUIListener != null) {
            this.consoleReader.setUIListener(pendingUIListener);

        }
        consoleReader.start();
        System.out.println("ConsoleReader запущен");
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
    public static Habitat getHabitat() {
        if (habitat == null) {
            throw new IllegalStateException("Habitat not initialized with dimensions");
        }
        return habitat;
    }

    public void setParams(float n1, float n2, double p1, double p2, float nt1, float nt2, int boyPriority, int girlPriority) {
        this.N1 = n1;
        this.N2 = n2;
        this.P1 = p1;
        this.P2 = p2;
        this.n1TimeOfLife = nt1;
        this.n2TimeOfLife = nt2;
        this.boyPriority = boyPriority;
        this.girlPriority = girlPriority;
        if (boysAI != null) {
            boysAI.setPriority(boyPriority);
        }
        if (girlsAI != null) {
            girlsAI.setPriority(girlPriority);
        }
    }


    public void updateAll(float deltaTime) {
        simulationTime += deltaTime;
        treeMapTimeNanos += (long)(deltaTime * 1_000_000_000L);
        boyTimer += deltaTime;
        girlTimer += deltaTime;

        synchronized (objectsLock) {
           generateObject();
        }

        removeDeadObjects();

    }

    public void generateObject() {

        synchronized (objectsLock) {
            while (boyTimer >= N1) {
                boyTimer -= N1;
                if (Math.random() < P1) {
                    float x = (float)Math.random() * (width-Student.SIZE);
                    float y = (float)Math.random() * (height-Student.SIZE);
                    int id = generateId();
                    objects.add(new BoyStudent(id, x, y, n1TimeOfLife, simulationTime, treeMapTimeNanos));
//                    System.out.println("Объект с " + id + " создан в " + simulationTime);

                    synchronized (activeIdsLock) {
                        activeIds.add(id);
                    }
                    synchronized (birthToIdLock) {
                        birthToId.put(id,treeMapTimeNanos);
                    }
                    boyCount++;
                }
            }
            while (girlTimer >= N2) {
                girlTimer -= N2;
                if (Math.random() < P2) {
                    float x = (float)Math.random() * (width-Student.SIZE);
                    float y = (float)Math.random() * (height-Student.SIZE);
                    int id = generateId();
                    objects.add(new GirlStudent(id, x, y, n2TimeOfLife, simulationTime, treeMapTimeNanos));
//                    System.out.println("Объект с " + id + " создан в " + simulationTime);
                    synchronized (activeIdsLock) {
                        activeIds.add(id);
                    }
                    synchronized (birthToIdLock) {
                        birthToId.put(id, treeMapTimeNanos);
                    }
                    girlCount++;
                }
            }
        }
    }

    private void removeDeadObjects() {
        synchronized (objectsLock) {
            Iterator<IBehaviour> it = objects.iterator();
            while(it.hasNext()) {
                IBehaviour obj = it.next();
                if(simulationTime >= obj.getCreationTime() + obj.getLifeTime()) {
                    it.remove();


                    synchronized (activeIdsLock) {
                        activeIds.remove(obj.getId());
                    }
                    synchronized (birthToIdLock) {
                        birthToId.remove(obj.getId());
                    }
                }
            }
        }
    }

    public void renderAll(GraphicsContext gc) {
        synchronized (objectsLock) {
            for (IBehaviour obj : objects) {
                obj.render(gc);
            }
        }
    }

    public void reset() {
        stopAI();
        synchronized (objectsLock) {
            synchronized (activeIdsLock) {
                synchronized (birthToIdLock) {
                    if(fileProvider.isFileExist() && startWithFile == true ) {

                        boyTimer = 0;
                        girlTimer = 0;
                        simulationTime = 0;
                        treeMapTimeNanos = 0;
                        boyCount = (int) objects.stream().filter(o -> o instanceof BoyStudent).count();
                        girlCount = (int) objects.stream().filter(o -> o instanceof GirlStudent).count();
                    } else if (startWithDB) {
                        boyTimer = 0;
                        girlTimer = 0;
                        simulationTime = 0;
                        treeMapTimeNanos = 0;
                        boyCount = (int) objects.stream().filter(o -> o instanceof BoyStudent).count();
                        girlCount = (int) objects.stream().filter(o -> o instanceof GirlStudent).count();
                    }
                    else {
                        objects.clear();
                        activeIds.clear();
                        birthToId.clear();
                        boyTimer = 0;
                        girlTimer = 0;
                        simulationTime = 0;
                        treeMapTimeNanos = 0;
                        boyCount = 0;
                        girlCount = 0;
                    }

                }
            }
        }
        this.boysAI = new BaseAIBoys(this, boyPriority, boysVelocity);
        this.girlsAI = new BaseAIGirls(this, girlPriority, girlsVelocity);
        isAiRunning = false;
        System.out.println("Habitat reset. simulationTime = 0");
    }

    public List<IBehaviour> getObjects() {
        synchronized (objectsLock) {
            return new LinkedList<>(objects);
        }

    }

    public float getSimulationTime() {
        return simulationTime;
    }

    public synchronized int getBoyCount() {
        return boyCount;
    }

    public synchronized int getGirlCount() {
        return girlCount;
    }

    public synchronized int getTotalCount() {
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
        } while (checkActiveId(newId));

        return newId;
    }
    public boolean checkActiveId(int id) {
        synchronized (activeIdsLock) {
            return activeIds.contains(id);
        }
    }
    //лаба3=======================================================================================
    public TreeMap<Integer,Long> getBirthToId() {
        synchronized (birthToIdLock) {return new TreeMap<Integer,Long>(birthToId);}
    }
    public Set<Integer> getActiveIds() {
        synchronized (activeIdsLock) {return new HashSet<>(activeIds);
        }
    }
    //лаба4======================================================================================
    public List<IBehaviour> getBoysStudents() {
        synchronized (objectsLock) {
            List<IBehaviour> boys = new LinkedList<>();
            for(IBehaviour obj : objects) {
                if(obj instanceof BoyStudent) {
                    boys.add(obj);
                }
            }
            return boys;
        }

    }
    public List<IBehaviour> getGirlStudents() {
        synchronized (objectsLock) {
            List<IBehaviour> girls = new LinkedList<>();
            for(IBehaviour obj : objects) {
                if(obj instanceof GirlStudent) {
                    girls.add(obj);
                }
            }
            return girls;
        }
    }

    public void startAI() {
        if(!isAiRunning) {
            boysAI.start();
            girlsAI.start();
            isAiRunning = true;
            System.out.println("AI потоки запущены");
        }
    }
    public void stopAI() {
        if(isAiRunning) {
            boysAI.stop();
            girlsAI.stop();
            isAiRunning = false;
            System.out.println("AI потоки остановлены");
        }
    }

    public void boysAIpause() {
        if (boysAI != null) {
            boysAI.pause();
            System.out.println("AI парней на паузе");
        }

    }
    public void boysAIresume() {
        if(boysAI != null) {
            boysAI.resume();
        }

    }

    public void girlsAIpause() {
        if(boysAI != null) {
            girlsAI.pause();
        }

    }
    public void girlsAIresume() {
        if(boysAI != null) {
            girlsAI.resume();
        }
    }

    public void boysAIsetPriority(int priority) {
        this.boyPriority = priority;
        if(boysAI != null ) {
            boysAI.setPriority(priority);
        }
    }
    public void setGirlsPriority(int priority) {
        this.girlPriority = priority;
        if (girlsAI != null) {
            girlsAI.setPriority(priority);
        }
    }
    public int getBoysPriority() {
        return boysAI.getPriority();
    }
    public int getGirlsPriority() {
        return girlsAI.getPriority();
    }
    public boolean isAIRunning() {
        return isAiRunning;
    }

    public void setBoysVelocity(float v ){
        this.boysVelocity = v;
        if (boysAI != null) {
            boysAI.setSpeed(v);
        }
    }
    public void setGirlsVelocity(float v) {
        this.girlsVelocity = v;
        if (girlsAI != null) {
            girlsAI.setSpeed(v);
        }
    }

    public ConsoleWriter getConsoleWriter() {
        try {
            if (consoleWriter == null) {
                initConsole();
            }
            return consoleWriter;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PipedReader getResponseReader() {
        try {
            if (responseReader == null) {
                initConsole();
            }
            return responseReader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setUIListener(UICommandListener listener) {
        this.pendingUIListener = listener;
        if (consoleReader != null) {
            consoleReader.setUIListener(listener);
        }
    }

    @Override
    public void saveObjects(String path) throws IOException {
        try(ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
            ArrayList<IBehaviour> objectsCopy = new ArrayList<>();
            for(IBehaviour o : objects) {
                objectsCopy.add(o.copyForSir());
            }
            for(IBehaviour o : objectsCopy) {
                System.out.println(o.getType());
            }

            oos.writeFloat(getSimulationTime());
            oos.writeObject(objectsCopy);
        }
    }
    public void setObjects(ArrayList<IBehaviour> o) {
        this.objects = o;
    }
    public void setActiveIds(Set<Integer> s ) {this.activeIds = s;}
    public void setBirthToId(TreeMap<Integer,Long> b) {this.birthToId = b;}

    @Override
    @SuppressWarnings("unchecked")
    public void loadObjects(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            float savedSimTime = ois.readFloat();
            Object readedObj = ois.readObject();
            if(readedObj instanceof ArrayList<?>) {
                    ArrayList<IBehaviour> obj = (ArrayList<IBehaviour>)readedObj;
                    fixTimes(obj,savedSimTime);

                    for(IBehaviour o : obj) {
                    if(o instanceof IBehaviour s) {
                        s.initImage();
                        }
                    }


                    Set<Integer> ids = new HashSet<>();
                    TreeMap<Integer,Long> btoid = new TreeMap<>();

                    for(IBehaviour o : obj) {
                        ids.add(o.getId());
                        btoid.put(o.getId(), o.getCreationTimeNanos());
                    }


                    this.objects = obj;
                    this.activeIds = ids;
                    this.birthToId = btoid;


            } else {
                throw new IOException("File contains smth bad");
            }
        }
    }

    @Override
    public void fixTimes(ArrayList<IBehaviour> obj, float time) {
        for(IBehaviour o : obj) {
            o.setTimeOfLife(time - o.getCreationTime());
            o.setCreationTime(0);
            o.setCreationTimeNanos(0);
        }
    }

    public List<IBehaviour> getStudentsByType(String type) {
        List<IBehaviour> st;
        if(type.equals("boy")) {
            st =  getBoysStudents();
        }
        else {
            st = getGirlStudents();
        }
        return st;
    }

    private static class IBehaviourDeserializer implements JsonDeserializer<IBehaviour> {
        @Override
        public IBehaviour deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext ctx)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            if ("boy".equals(type)) {
                return ctx.deserialize(obj, BoyStudent.class);
            } else {
                return ctx.deserialize(obj, GirlStudent.class);
            }
        }
    }


    public String getSavedToJson(float timeOfCall) {
        String json = null;
        try {

            List<IBehaviour> objCopy = new ArrayList<>();
            for(IBehaviour obj : objects) {
                IBehaviour copy = obj.copy();
                float livedTime = timeOfCall - obj.getCreationTime();
                float remainingLife = obj.getLifeTime() - livedTime;
                copy.setTimeOfLife(remainingLife);
                copy.setCreationTime(0);
                copy.setCreationTimeNanos(0);
                objCopy.add(copy);
            }


            Set<Integer> activeIdsCopy = new HashSet<>(activeIds);


            TreeMap<Integer,Long> birthToIdCopy = new TreeMap<>();
            for(Map.Entry<Integer,Long> entry : birthToId.entrySet()) {
                birthToIdCopy.put(entry.getKey(), 0L);
            }

            Map<String, Object> allCollections = new HashMap<>();
            allCollections.put("objects", objCopy);
            allCollections.put("activeIds", activeIdsCopy);
            allCollections.put("birthToId", birthToIdCopy);
            json = gson.toJson(allCollections);
        } catch (Exception e) {
            System.err.println("Ошибка перевода в json" + e.getMessage());
        }
        return json;
    }
    public void setFromJson(JsonObject json) {
        List<IBehaviour> obj = gson.fromJson(json.get("objects"), new TypeToken<List<IBehaviour>>(){}.getType());
        for(IBehaviour o : obj) {
            if(o instanceof Student s) {
                s.initImage();
            }
        }

        Set<Integer> ids = gson.fromJson(json.get("activeIds"), new TypeToken<Set<Integer>>(){}.getType());
        TreeMap<Integer,Long> btoid = gson.fromJson(json.get("birthToId"), new TypeToken<TreeMap<Integer,Long> >(){}.getType());

        this.objects = obj;
        this.activeIds = ids;
        this.birthToId = btoid;
    }

    public void startWithFile() {
        this.startWithFile = true;
    }
    public void startWithoutFile() {
        this.startWithFile = false;
    }

    public void startWithDB() {
        this.startWithDB = true;
    }
    public void startWithoutDB() {
        this.startWithDB = false;
    }


    public void updateN1Params(float n) {
        synchronized (this) {
            this.N1 = n;
        }
    }
    public void updateN2Params(float n) {
        synchronized (this) {
            this.N2 = n;
        }
    }

    public void updateP1Params(double p) {
        synchronized (this) {
            this.P1 = p;
        }
    }
    public void updateP2Params(double p) {
        synchronized (this) {
            this.P2 = p;
        }
    }
    public void updateTimesOfLifeN1Params(float s) {
        synchronized (this) {
            this.n1TimeOfLife = s;
        }
    }
    public void updateTimesOfLifeN2Params(float s) {
        synchronized (this) {
            this.n2TimeOfLife = s;
        }
    }
}


