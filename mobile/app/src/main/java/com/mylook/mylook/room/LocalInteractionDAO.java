package com.mylook.mylook.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocalInteractionDAO {

    @Query("SELECT * FROM localinteractions")
    List<LocalInteraction> getAll();

    @Query("SELECT * FROM localinteractions WHERE userId = (:currentUser)")
    List<LocalInteraction> getAllByUser(String currentUser);

    @Insert
    void insert(LocalInteraction localInteraction);

    @Insert
    void insertAll(LocalInteraction... localInteractions);

    @Delete
    void delete(LocalInteraction localInteraction);

}
