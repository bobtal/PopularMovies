package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.Constants;
import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieReviewsAdapter;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieVideosAdapter;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.databinding.ActivityDetailsBinding;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieVideo;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.NetworkUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity
        implements MovieVideosAdapter.MovieVideosAdapterClickHandler {

    private static final String TAG = DetailsActivity.class.getSimpleName();

    private MovieDbApi api;
    private Movie movie;
    private MovieVideosAdapter movieVideosAdapter;
    private MovieReviewsAdapter movieReviewsAdapter;
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                DetailsActivity.this, R.layout.activity_details);

        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(MainActivity.EXTRA_MOVIE)) {
            movie = startingIntent.getParcelableExtra(MainActivity.EXTRA_MOVIE);
            binding.setMovie(movie);
        } else {
            closeOnError();
        }

        api = RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class);
        if (NetworkUtils.isNetworkAvailable(this)) {
            showProgressBarReviews();
            showProgressBarVideos();
            Call<MovieDbVideoReviewResponse> call =
                    api.getVideosAndReviewsForMovie(movie.getId(), Constants.API_KEY, NetworkUtils.APPEND_TO_RESPONSE);
            call.enqueue(new Callback<MovieDbVideoReviewResponse>() {
                @Override
                public void onResponse(Call<MovieDbVideoReviewResponse> call, Response<MovieDbVideoReviewResponse> response) {
                    Log.d(TAG, "video call success");

                    // get the videos from the response and pass them
                    // to the MovieVideosAdapter constructor
                    movieVideosAdapter = new MovieVideosAdapter(
                            DetailsActivity.this,
                            DetailsActivity.this,
                            response.body().getVideos().getResults());
                    binding.rvMovieVideos.setAdapter(movieVideosAdapter);
                    binding.rvMovieVideos.setLayoutManager(
                            new LinearLayoutManager(DetailsActivity.this));
                    hideProgressBarVideos();

                    // get the reviews from the response and pass them
                    // to the MovieReviewsAdapter constructor
                    movieReviewsAdapter = new MovieReviewsAdapter(
                            DetailsActivity.this,
                            response.body().getReviews().getResults());
                    binding.rvMovieReviews.setAdapter(movieReviewsAdapter);
                    binding.rvMovieReviews.setLayoutManager(
                            new LinearLayoutManager(DetailsActivity.this));
                    hideProgressBarReviews();
                }

                @Override
                public void onFailure(Call<MovieDbVideoReviewResponse> call, Throwable t) {
                    Log.d(TAG, "video call failure");
                }
            });
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.movie_data_not_available, Toast.LENGTH_LONG).show();
    }

    private void hideProgressBarVideos() {
        binding.loadingProgressVideos.setVisibility(View.INVISIBLE);
        binding.rvMovieVideos.setVisibility(View.VISIBLE);
    }

    private void showProgressBarVideos() {
        binding.loadingProgressVideos.setVisibility(View.VISIBLE);
        binding.rvMovieVideos.setVisibility(View.INVISIBLE);
    }

    private void hideProgressBarReviews() {
        binding.loadingProgressReviews.setVisibility(View.INVISIBLE);
        binding.rvMovieReviews.setVisibility(View.VISIBLE);
    }

    private void showProgressBarReviews() {
        binding.loadingProgressReviews.setVisibility(View.VISIBLE);
        binding.rvMovieReviews.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(MovieVideo movieVideo) {
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + movieVideo.getKey());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, R.string.no_app_for_video, Toast.LENGTH_LONG).show();
        }
    }
}
