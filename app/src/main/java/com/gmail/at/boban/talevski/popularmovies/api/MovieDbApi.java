package com.gmail.at.boban.talevski.popularmovies.api;

import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbApi {

    @GET("/3/movie/popular")
    Call<MovieDbResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("/3/movie/top_rated")
    Call<MovieDbResponse> getTopRatedMovies(@Query("api_key") String apiKey);
}
