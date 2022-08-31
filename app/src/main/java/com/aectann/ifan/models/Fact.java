package com.aectann.ifan.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "facts")
public class Fact {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fact;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFact() {
        return fact;
    }

    public void setFact(String fact) {
        this.fact = fact;
    }
}