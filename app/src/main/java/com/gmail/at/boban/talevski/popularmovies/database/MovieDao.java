package com.gmail.at.boban.talevski.popularmovies.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies")
    List<MovieEntity> loadAllMovies();

    @Insert
    void insertMovie(MovieEntity movieEntity);

    @Delete
    void deleteMovie(MovieEntity movieEntity);
}
