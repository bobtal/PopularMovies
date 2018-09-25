package com.gmail.at.boban.talevski.popularmovies.api;

import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieDbApi {

    @GET("movie/popular")
    Call<MovieDbResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MovieDbResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Call<MovieDbVideoReviewResponse> getVideosAndReviewsForMovie(
            @Path("id") int movieId,
            @Query("api_key") String apiKey,
            @Query("append_to_response") String appendToResponse);
}
