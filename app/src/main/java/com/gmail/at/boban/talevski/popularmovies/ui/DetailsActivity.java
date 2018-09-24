package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.Constants;
import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieVideosAdapter;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.databinding.ActivityDetailsBinding;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieVideo;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity
        implements MovieVideosAdapter.MovieVideosAdapterClickHandler {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private MovieDbApi api;
    private Movie movie;
    private MovieVideosAdapter movieVideosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDetailsBinding binding = DataBindingUtil.setContentView(
                DetailsActivity.this, R.layout.activity_details);

        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(MainActivity.EXTRA_MOVIE)) {
            movie = startingIntent.getParcelableExtra(MainActivity.EXTRA_MOVIE);
            binding.setMovie(movie);
        } else {
            closeOnError();
        }

        api = RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class);
        api.getVideosForMovie(movie.getId(), Constants.API_KEY).enqueue(new Callback<MovieDbVideoResponse>() {
            @Override
            public void onResponse(Call<MovieDbVideoResponse> call, Response<MovieDbVideoResponse> response) {
                Log.d(TAG, "video call success");
                movieVideosAdapter = new MovieVideosAdapter(
                        DetailsActivity.this,
                        DetailsActivity.this,
                        response.body().getResults());
                binding.rvMovieVideos.setAdapter(movieVideosAdapter);
                binding.rvMovieVideos.setLayoutManager(
                        new LinearLayoutManager(DetailsActivity.this));
            }

            @Override
            public void onFailure(Call<MovieDbVideoResponse> call, Throwable t) {
                Log.d(TAG, "video call failure");
            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.movie_data_not_available, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onListItemClick(MovieVideo movieVideo) {

    }
}
