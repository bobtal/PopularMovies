package com.gmail.at.boban.talevski.popularmovies.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.gmail.at.boban.talevski.popularmovies.Constants;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.MovieDao;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    private MovieDbApi movieDbApi;
    private MovieDao movieDao;

    public MovieRepository(MovieDbApi movieDbApi, MovieDao movieDao) {
        this.movieDbApi = movieDbApi;
        this.movieDao = movieDao;
    }

    public LiveData<List<Movie>> getPopularMovies() {
        final MutableLiveData<List<Movie>> results = new MutableLiveData<>();
        movieDbApi.getPopularMovies(Constants.API_KEY).enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                Log.d(TAG, "Fetched network response for popular movies");
                results.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.d(TAG, "Error fetching popular movie data from network");
            }
        });
        return results;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        final MutableLiveData<List<Movie>> results = new MutableLiveData<>();
        movieDbApi.getTopRatedMovies(Constants.API_KEY).enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                Log.d(TAG, "Fetched network response for top rated movies");
                results.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.d(TAG, "Error fetching top rated movie data from network");
            }
        });
        return results;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        Log.d(TAG, "Fetched database response for favorite movies");
        return movieDao.loadAllMovies();
    }

}
