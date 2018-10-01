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

    public MainViewModel(AppDatabase database, MovieRepository.ErrorHandler errorHandler) {
        Log.d(TAG, "Actively retrieving tasks from the Database");

        movieRepository = new MovieRepository(
                RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class),
                database.movieDao(),
                errorHandler);

    }

    public LiveData<List<Movie>> getMovies(MovieType movieType) {
        switch (movieType) {
            // need to check both xxxMovies and xxxMovies.getValue() for null
            // because after a first failed attempt to get data (due to no internet f.e.),
            // xxxMovies is no longer null but they still don't have a valid movie list
            // and checking for xxxMovies == null prevents NPE on xxxMovies.getValue()
            case POPULAR:
                if (popularMovies == null || popularMovies.getValue() == null) {
                    popularMovies = movieRepository.getPopularMovies();
                }
                return popularMovies;

            case TOP_RATED:
                if (topRatedMovies == null || topRatedMovies.getValue() == null) {
                    topRatedMovies = movieRepository.getTopRatedMovies();
                }
                return topRatedMovies;

            default: // case FAVORITES:
                if (favoriteMovies == null || favoriteMovies.getValue() == null) {
                    favoriteMovies = movieRepository.getFavoriteMovies();
                }
                return favoriteMovies;
        }
    }
}
