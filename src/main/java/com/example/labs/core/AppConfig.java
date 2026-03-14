package com.example.labs.core;

import javax.imageio.IIOException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private float N1 = 1.0f;
    private float N2 = 1.0f;
    private double P1 = 1;
    private double P2 = 1;
    private float n1TimeOfLife = 5.0f;
    private float n2TimeOfLife = 5.0f;

    private boolean timerVisible = true;
    private boolean showInfoOnStop = false;

    private boolean boysMoving = true;
    private boolean girlsMoving = true;
    private int boyPriority = 5;
    private int girlPriority = 5;

    public float getN1() {
        return N1;
    }
    public void setN1(float N1) {
        this.N1 = N1;
    }


    public float getN2() {
        return N2;
    }
    public void setN2(float N2) {
        this.N2 = N2;
    }


    public double getP1() {
        return P1;
    }
    public void setP1(double P1) {
        this.P1 = P1;
    }


    public double getP2() {
        return P2;
    }
    public void setP2(double P2) {
        this.P2 = P2;
    }


    public float getN1TimeOfLife() {
        return n1TimeOfLife;
    }
    public void setN1TimeOfLife(float n1TimeOfLife) {
        this.n1TimeOfLife = n1TimeOfLife;
    }


    public float getN2TimeOfLife() {
        return n2TimeOfLife;
    }
    public void setN2TimeOfLife(float n2TimeOfLife) {
        this.n2TimeOfLife = n2TimeOfLife;
    }


    public int getBoyPriority() {
        return boyPriority;
    }
    public void setBoyPriority(int boyPriority) {
        this.boyPriority = boyPriority;
    }


    public int getGirlPriority() {
        return girlPriority;
    }
    public void setGirlPriority(int girlPriority) {
        this.girlPriority = girlPriority;
    }


    public boolean isTimerVisible() {
        return timerVisible;
    }
    public void setTimerVisible(boolean timerVisible) {
        this.timerVisible = timerVisible;
    }


    public boolean isShowInfoOnStop() {
        return showInfoOnStop;
    }
    public void setShowInfoOnStop(boolean showInfoOnStop) {
        this.showInfoOnStop = showInfoOnStop;
    }


    public boolean isBoysMoving() {
        return boysMoving;
    }
    public void setBoysMoving(boolean boysMoving) {
        this.boysMoving = boysMoving;
    }


    public boolean isGirlsMoving() {
        return girlsMoving;
    }
    public void setGirlsMoving(boolean girlsMoving) {
        this.girlsMoving = girlsMoving;
    }

    public void saveConfig()  {
        Properties props = new Properties();
        props.setProperty("N1",String.valueOf(N1));
        props.setProperty("N2",String.valueOf(N2));
        props.setProperty("P1",String.valueOf(P1));
        props.setProperty("P2",String.valueOf(P2));
        props.setProperty("n1TimeOfLife",String.valueOf(n1TimeOfLife));
        props.setProperty("n2TimeOfLife",String.valueOf(n2TimeOfLife));
        props.setProperty("timerVisible",String.valueOf(timerVisible));
        props.setProperty("showInfoOnStop",String.valueOf(showInfoOnStop));
        props.setProperty("boysMoving",String.valueOf(boysMoving));
        props.setProperty("girlsMoving",String.valueOf(girlsMoving));
        props.setProperty("boyPriority",String.valueOf(boyPriority));
        props.setProperty("girlPriority",String.valueOf(girlPriority));

         try(FileWriter writer = new FileWriter("config.properties")) {
             props.store(writer,null);
         }
         catch (IOException e) {
             System.out.println("Ошибка при сохранении: " + e.getMessage());
         }
    }
    public void loadConfig()  {
        Properties props = new Properties();
        try(FileReader reader = new FileReader("config.properties")) {
            props.load(reader);
            this.N1 = Float.parseFloat(props.getProperty("N1", String.valueOf(this.N1)));
            this.N2 = Float.parseFloat(props.getProperty("N2", String.valueOf(this.N2)));
            this.P1 = Double.parseDouble(props.getProperty("P1", String.valueOf(this.P1)));
            this.P2 = Double.parseDouble(props.getProperty("P2", String.valueOf(this.P2)));
            this.n1TimeOfLife = Float.parseFloat(props.getProperty("n1TimeOfLife", String.valueOf(this.n1TimeOfLife)));
            this.n2TimeOfLife = Float.parseFloat(props.getProperty("n2TimeOfLife", String.valueOf(this.n2TimeOfLife)));
            this.timerVisible = Boolean.parseBoolean(props.getProperty("timerVisible", String.valueOf(this.timerVisible)));
            this.showInfoOnStop = Boolean.parseBoolean(props.getProperty("showInfoOnStop", String.valueOf(this.showInfoOnStop)));
            this.boysMoving = Boolean.parseBoolean(props.getProperty("boysMoving", String.valueOf(this.boysMoving)));
            this.girlsMoving = Boolean.parseBoolean(props.getProperty("girlsMoving", String.valueOf(this.girlsMoving)));
            this.boyPriority = Integer.parseInt(props.getProperty("boyPriority", String.valueOf(this.boyPriority)));
            this.girlPriority = Integer.parseInt(props.getProperty("girlPriority", String.valueOf(this.girlPriority)));
        }
        catch (FileNotFoundException e) {
            System.out.println("Файл не найден, используются значения по умолчанию");
        } catch (IOException e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
        }
    }
}
