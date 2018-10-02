package com.gmail.at.boban.talevski.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieReviewsAdapter;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieVideosAdapter;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.databinding.ActivityDetailsBinding;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbVideoReviewResponse;
import com.gmail.at.boban.talevski.popularmovies.model.MovieVideo;
import com.gmail.at.boban.talevski.popularmovies.utils.AppExecutors;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.DetailsViewModel;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.DetailsViewModelFactory;

public class DetailsActivity extends AppCompatActivity
        implements MovieVideosAdapter.MovieVideosAdapterClickHandler,
        MovieRepository.ErrorHandlerActivity {

    private static final String TAG = DetailsActivity.class.getSimpleName();
    private static final String YOUTUBE_BASE_URL_V = "https://www.youtube.com/watch?v=";

    private Movie movie;
    private MovieVideosAdapter movieVideosAdapter;
    private MovieReviewsAdapter movieReviewsAdapter;
    private ActivityDetailsBinding binding;
    private AppDatabase db;
    private boolean isFavoriteMovie = false;
    // a key to be used for sharing of the first trailer of movie
    private String firstTrailerKey;

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

        setupViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        switch (menuItemId) {
            case R.id.action_share:
                if (firstTrailerKey != null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, YOUTUBE_BASE_URL_V + firstTrailerKey);
                    Intent.createChooser(shareIntent, getString(R.string.share_trailer));
                    startActivity(shareIntent);
                } else {
                    Snackbar.make(binding.flMovieReviews, R.string.no_videos_to_share, Snackbar.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewModel() {
        showProgressBarVideos();
        showProgressBarReviews();
        DetailsViewModelFactory factory =
                new DetailsViewModelFactory(AppDatabase.getInstance(this), movie.getId(),this);
        final DetailsViewModel viewModel =
                ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.getVideosAndReviewsForMovie().observe(this, new Observer<MovieDbVideoReviewResponse>() {
            @Override
            public void onChanged(@Nullable MovieDbVideoReviewResponse movieDbVideoReviewResponse) {
                Log.d(TAG, "Updating movie videos and reviews from LiveData in ViewModel");
                populateMovieVideosAndReviewsRecyclerViews(movieDbVideoReviewResponse);
            }
        });
    }

    private void populateMovieVideosAndReviewsRecyclerViews(MovieDbVideoReviewResponse movieDbVideoReviewResponse) {
        if (movieDbVideoReviewResponse.getVideos() != null) {
            // get the videos from the response and pass them
            // to the MovieVideosAdapter constructor
            movieVideosAdapter = new MovieVideosAdapter(
                    DetailsActivity.this,
                    DetailsActivity.this,
                    movieDbVideoReviewResponse.getVideos().getResults());
            // extract the first trailer key to be used in the share intent
            if (movieDbVideoReviewResponse.getVideos().getResults().size() != 0) {
                firstTrailerKey = movieDbVideoReviewResponse.getVideos().getResults().get(0).getKey();
            } else {
                firstTrailerKey = null;
            }
            binding.rvMovieVideos.setAdapter(movieVideosAdapter);
            binding.rvMovieVideos.setLayoutManager(
                    new LinearLayoutManager(DetailsActivity.this));
        }
        hideProgressBarVideos();

        if (movieDbVideoReviewResponse.getReviews() != null) {
            // get the reviews from the response and pass them
            // to the MovieReviewsAdapter constructor
            movieReviewsAdapter = new MovieReviewsAdapter(
                    DetailsActivity.this,
                    movieDbVideoReviewResponse.getReviews().getResults());
            binding.rvMovieReviews.setAdapter(movieReviewsAdapter);
            binding.rvMovieReviews.setLayoutManager(
                    new LinearLayoutManager(DetailsActivity.this));
        }
        hideProgressBarReviews();
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
        Uri uri = Uri.parse(YOUTUBE_BASE_URL_V + movieVideo.getKey());
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

    @Override
    public void handleError(String errorMessage) {
        Snackbar.make(binding.flMovieReviews, errorMessage, Snackbar.LENGTH_SHORT).show();
        binding.loadingProgressReviews.setVisibility(View.INVISIBLE);
        binding.loadingProgressVideos.setVisibility(View.INVISIBLE);
    }
}
