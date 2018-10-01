package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;

public class DetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase database;
    private int movieId;
    private final MovieRepository.ErrorHandlerActivity errorHandler;

    public DetailsViewModelFactory(AppDatabase database, int movieId,
                                   MovieRepository.ErrorHandlerActivity errorHandler) {
        this.database = database;
        this.movieId = movieId;
        this.errorHandler = errorHandler;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DetailsViewModel(database, movieId, errorHandler);
    }
}
