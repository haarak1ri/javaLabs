package com.example.labs.model;

import com.google.gson.annotations.Expose;
import javafx.scene.image.Image;

public class BoyStudent extends Student {
    private static final Image BoyImage = new Image(BoyStudent.class.getResourceAsStream("/Images/graduated.png"));
    @Expose
    private final String type = "boy";
    @Override
    public IBehaviour copy() {
        return new BoyStudent(
                this.id,
                this.x,
                this.y,
                this.lifeTime,
                0,
                0
        );
    }
    public IBehaviour copyForSir() {
        return new BoyStudent(
                this.id,
                this.x,
                this.y,
                this.lifeTime,
                this.creationTime,
                this.creationTimeNanos
        );
    }
    public void initImage() {
        this.image = BoyImage;
    }

    public BoyStudent(int id, float x, float y, float lTime,float cTime, long cTimeNanos) {
        super(id,x,y,BoyImage,lTime,cTime, cTimeNanos);
    }

    public String getType() {
        return this.type;
    }
}
