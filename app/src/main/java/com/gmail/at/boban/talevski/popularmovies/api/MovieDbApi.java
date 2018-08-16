package com.gmail.at.boban.talevski.popularmovies.api;

import com.gmail.at.boban.talevski.popularmovies.model.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieDbApi {

    @GET("/movie/popular")
    Call<List<Movie>> getPopularMovies(@Query("api_key") String apiKey);

    @GET("/movie/top_rated")
    Call<List<Movie>> getTopRatedMovies(@Query("api_key") String apiKey);
}
