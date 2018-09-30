package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
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

    private LiveData<List<Movie>> popularMovies;
    private LiveData<List<Movie>> topRatedMovies;
    private LiveData<List<Movie>> favoriteMovies;
    private final MovieRepository movieRepository;

    public MainViewModel(AppDatabase database) {
        Log.d(TAG, "Actively retrieving tasks from the Database");

        movieRepository = new MovieRepository(
                RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class),
                database.movieDao());

    }

    public LiveData<List<Movie>> getMovies(MovieType movieType) {
        switch (movieType) {
            case POPULAR:
                if (popularMovies == null) {
                    popularMovies = movieRepository.getPopularMovies();
                }
                return popularMovies;

            case TOP_RATED:
                if (topRatedMovies == null) {
                    topRatedMovies = movieRepository.getTopRatedMovies();
                }
                return topRatedMovies;

            default: // case FAVORITES:
                if (favoriteMovies == null) {
                    favoriteMovies = movieRepository.getFavoriteMovies();
                }
                return favoriteMovies;
        }
    }
}
