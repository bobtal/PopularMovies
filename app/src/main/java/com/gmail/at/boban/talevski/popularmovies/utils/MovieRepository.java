package com.gmail.at.boban.talevski.popularmovies.utils;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.MovieDao;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieRepository {

    private static final String TAG = MovieRepository.class.getSimpleName();

    private MovieDbApi movieDbApi;
    private MovieDao movieDao;
    private ErrorHandlerActivity errorHandler;

    // should only be implemented by an Activity as it's also used
    // to access string resources using its context
    public interface ErrorHandlerActivity {
        void handleError(String errorMessage);
    }

    public MovieRepository(MovieDbApi movieDbApi, MovieDao movieDao, ErrorHandlerActivity errorHandler) {
        this.movieDbApi = movieDbApi;
        this.movieDao = movieDao;
        this.errorHandler = errorHandler;
    }

    public LiveData<List<Movie>> getPopularMovies() {
        final MutableLiveData<List<Movie>> results = new MutableLiveData<>();
        if (NetworkUtils.isNetworkAvailable((Context)errorHandler)) {
            movieDbApi.getPopularMovies(NetworkUtils.API_KEY).enqueue(new Callback<MovieDbResponse>() {
                @Override
                public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                    Log.d(TAG, "Fetched network response for popular movies");
                    results.setValue(response.body().getResults());
                }

                @Override
                public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                    Log.d(TAG, "Error fetching popular movie data from network");
                    errorHandler.handleError(((Activity) errorHandler).getString(R.string.error_displaying_movies));
                }
            });
        } else {
            errorHandler.handleError(((Activity) errorHandler).getString(R.string.no_network));
            // returning an empty list to avoid NPE on the observe call
            List<Movie> emptyList = new ArrayList<>();
            results.setValue(emptyList);
        }
        return results;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        final MutableLiveData<List<Movie>> results = new MutableLiveData<>();
        if (NetworkUtils.isNetworkAvailable((Context)errorHandler)) {
            movieDbApi.getTopRatedMovies(NetworkUtils.API_KEY).enqueue(new Callback<MovieDbResponse>() {
                @Override
                public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                    Log.d(TAG, "Fetched network response for top rated movies");
                    results.setValue(response.body().getResults());
                }

                @Override
                public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                    Log.d(TAG, "Error fetching top rated movie data from network");
                    errorHandler.handleError(((Activity) errorHandler).getString(R.string.error_displaying_movies));
                }
            });
        } else {
            errorHandler.handleError(((Activity) errorHandler).getString(R.string.no_network));
            // returning an empty list to avoid NPE on the observe call
            List<Movie> emptyList = new ArrayList<>();
            results.setValue(emptyList);
        }
        return results;
    }

    public LiveData<MovieDbVideoReviewResponse> getMovieVideosAndReviews(int movieId) {
        final MutableLiveData<MovieDbVideoReviewResponse> results = new MutableLiveData<>();
        if (NetworkUtils.isNetworkAvailable((Context)errorHandler)) {
            movieDbApi.getVideosAndReviewsForMovie(movieId, NetworkUtils.API_KEY,
                    NetworkUtils.APPEND_TO_RESPONSE).enqueue(new Callback<MovieDbVideoReviewResponse>() {
                @Override
                public void onResponse(Call<MovieDbVideoReviewResponse> call, Response<MovieDbVideoReviewResponse> response) {
                    Log.d(TAG, "Fetched network response for movie videos and reviews");
                    results.setValue(response.body());
                }

                @Override
                public void onFailure(Call<MovieDbVideoReviewResponse> call, Throwable t) {
                    errorHandler.handleError(((Activity) errorHandler).getString(R.string.movie_data_not_available));
                }
            });
        } else {
            errorHandler.handleError(((Activity) errorHandler).getString(R.string.no_network));
            // returning an empty response to avoid NPE on the observe call
            MovieDbVideoReviewResponse emptyResponse = new MovieDbVideoReviewResponse();
            results.setValue(emptyResponse);
        }
        return results;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        Log.d(TAG, "Fetched database response for favorite movies");
        return movieDao.loadAllMovies();
    }

}
