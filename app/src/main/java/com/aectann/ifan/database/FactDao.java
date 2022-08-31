package com.aectann.ifan.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.aectann.ifan.models.Fact;

import java.util.List;

@Dao
public interface FactDao {
    @Query("SELECT * FROM facts")
    List<Fact> getFactList();
    @Insert
    void insertFact(Fact fact);
}