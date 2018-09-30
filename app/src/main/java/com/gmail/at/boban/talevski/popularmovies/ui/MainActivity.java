package com.gmail.at.boban.talevski.popularmovies.ui;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.Constants;
import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieAdapter;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.popularmovies.utils.AppExecutors;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieType;
import com.gmail.at.boban.talevski.popularmovies.utils.NetworkUtils;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.MainViewModel;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.MainViewModelFactory;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_MOVIE";
    public static final String EXTRA_MOVIE_TYPE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_MOVIE_TYPE";

    private MovieAdapter adapter;

    private MovieType movieType;

    private RecyclerView moviesRecyclerView;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecyclerView = findViewById(R.id.rv_movies);
        loadingProgress = findViewById(R.id.loading_progress);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_MOVIE_TYPE)) {
            movieType = (MovieType) savedInstanceState.getSerializable(EXTRA_MOVIE_TYPE);
        } else {
            // show popular movies by default
            movieType = MovieType.FAVORITES;
        }

        setupViewModel();
    }

    private void setupViewModel() {
        MainViewModelFactory factory =
                new MainViewModelFactory(AppDatabase.getInstance(this), movieType);
        final MainViewModel viewModel =
                ViewModelProviders.of(this, factory).get(MainViewModel.class);
        viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
//                viewModel.getMovies().removeObserver(this);
                Log.d(TAG, "Updating list of movies from LiveData in ViewModel");
                populateGridWithMovies(movies);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        switch (menuItemId) {
            case R.id.action_popular:
                movieType = MovieType.POPULAR;
                setupViewModel();
                return true;

            case R.id.action_top_rated:
                movieType = MovieType.TOP_RATED;
                setupViewModel();
                return true;

            case R.id.action_favorites:
                movieType = MovieType.FAVORITES;
                setupViewModel();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateGridWithMovies(List<Movie> results) {
        adapter = new MovieAdapter(
                MainActivity.this,
                MainActivity.this,
                results);
        moviesRecyclerView.setAdapter(adapter);
        int numberOfColumns = getResources().getInteger(R.integer.columns);
        moviesRecyclerView.setLayoutManager(
                new GridLayoutManager(MainActivity.this, numberOfColumns));
        moviesRecyclerView.setHasFixedSize(true);
    }

    private void hideProgressBar() {
        loadingProgress.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        loadingProgress.setVisibility(View.VISIBLE);
        moviesRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(Movie movie) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(EXTRA_MOVIE, movie);
        startActivity(intent);
    }
}
