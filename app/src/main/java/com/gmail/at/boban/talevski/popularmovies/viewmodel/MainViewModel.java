package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieType;

import java.util.List;

public class MainViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<Movie>> movies;

    public MainViewModel(AppDatabase database, MovieType movieType) {
        Log.d(TAG, "Actively retrieving tasks from the Database");

        MovieRepository movieRepository = new MovieRepository(
                RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class),
                database.movieDao());

        switch (movieType) {
            case POPULAR:
                movies = movieRepository.getPopularMovies();
                break;

            case TOP_RATED:
                movies = movieRepository.getTopRatedMovies();
                break;

            case FAVORITES:
                movies = movieRepository.getFavoriteMovies();
                break;
        }

    }

    public LiveData<List<Movie>> getMovies() {
        return movies;
    }
}
