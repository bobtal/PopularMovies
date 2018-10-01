package com.gmail.at.boban.talevski.popularmovies.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;

public class DetailsViewModel extends ViewModel {

    private final MovieRepository movieRepository;
    private LiveData<MovieDbVideoReviewResponse> movieVideosAndReviews;
    private int movieId;

    public DetailsViewModel(AppDatabase database, int movieId,
                            MovieRepository.ErrorHandlerActivity errorHandler) {
        movieRepository = new MovieRepository(
                RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class),
                database.movieDao(),
                errorHandler);
        this.movieId = movieId;
    }

    public LiveData<MovieDbVideoReviewResponse> getVideosAndReviewsForMovie() {
        if (movieVideosAndReviews == null || movieVideosAndReviews.getValue() == null) {
            movieVideosAndReviews = movieRepository.getMovieVideosAndReviews(movieId);
        }
        return movieVideosAndReviews;
    }
}
