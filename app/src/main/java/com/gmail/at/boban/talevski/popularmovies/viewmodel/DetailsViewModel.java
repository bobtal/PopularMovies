package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;

public class DetailsViewModel extends ViewModel {

    private final MovieRepository movieRepository;

    public DetailsViewModel(AppDatabase database, int movieId,
                            MovieRepository.ErrorHandler errorHandler) {
        movieRepository = new MovieRepository(
                RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class),
                database.movieDao(),
                errorHandler);
    }
}
