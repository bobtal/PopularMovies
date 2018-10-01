package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;

public class MainViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;
    private final MovieRepository.ErrorHandler errorHandler;

    public MainViewModelFactory(AppDatabase database, MovieRepository.ErrorHandler errorHandler) {
        this.database = database;
        this.errorHandler = errorHandler;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new MainViewModel(database, errorHandler);
    }
}
