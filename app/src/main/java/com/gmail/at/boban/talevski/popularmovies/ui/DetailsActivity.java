package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.Constants;
import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieReviewsAdapter;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieVideosAdapter;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.databinding.ActivityDetailsBinding;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieVideo;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.AppExecutors;
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
    private AppDatabase db;
    private boolean isFavoriteMovie = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(
                DetailsActivity.this, R.layout.activity_details);

        db = AppDatabase.getInstance(getApplicationContext());

        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(MainActivity.EXTRA_MOVIE)) {
            movie = startingIntent.getParcelableExtra(MainActivity.EXTRA_MOVIE);
            binding.setMovie(movie);

            initFavoriteButton();
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

    private void initFavoriteButton() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movie movieFromDatabase = db.movieDao().loadMovieById(movie.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (movieFromDatabase != null) {
                            isFavoriteMovie = true;
                            binding.favorite.setImageDrawable(ContextCompat.getDrawable(
                                    DetailsActivity.this, R.drawable.ic_star_yellow_48dp));
                        } else {
                            isFavoriteMovie = false;
                            binding.favorite.setImageDrawable(ContextCompat.getDrawable(
                                    DetailsActivity.this, R.drawable.ic_star_border_black_48dp));
                        }
                    }
                });
            }
        });
    }

    public void onToggleFavorite(View view) {
        if (isFavoriteMovie) {
            ((ImageButton)view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_border_black_48dp));
            isFavoriteMovie = false;
            removeMovieFromFavorites(movie);
        } else {
            ((ImageButton) view).setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_star_yellow_48dp));
            isFavoriteMovie = true;
            addMovieToFavorites(movie);
        }
    }

    private void removeMovieFromFavorites(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.movieDao().deleteMovie(movie);
            }
        });
    }

    private void addMovieToFavorites(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                db.movieDao().insertMovie(movie);
            }
        });
    }
}
