package com.example.labs.model;

import com.google.gson.annotations.Expose;
import javafx.scene.image.Image;

public class GirlStudent extends Student{
    private static final Image GirlImage = new Image(GirlStudent.class.getResourceAsStream("/Images/student.png"));
    @Expose
    private final String type = "girl";

    public IBehaviour copyForSir() {
        return new GirlStudent(
                this.id,
                this.x,
                this.y,
                this.lifeTime,
                this.creationTime,
                this.creationTimeNanos
        );
    }
    public IBehaviour copy() {
        return new GirlStudent(
                this.id,
                this.x,
                this.y,
                this.lifeTime,
                0,
                0
        );
    }
    public GirlStudent(int id,float x, float y, float lTime,float cTime, long cTimeNanos) {
        super(id,x,y,GirlImage,lTime,cTime, cTimeNanos);
    }
    public String getType() {
        return this.type;
    }
    public void initImage() {
        this.image = GirlImage;
    }
}
