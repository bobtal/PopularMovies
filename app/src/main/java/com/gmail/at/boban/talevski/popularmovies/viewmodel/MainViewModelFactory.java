package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieType;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;
    private final MovieType movieType;

    public MainViewModelFactory(AppDatabase database, MovieType movieType) {
        this.database = database;
        this.movieType = movieType;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainViewModel(database, movieType);
    }
}
