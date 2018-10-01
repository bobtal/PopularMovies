package com.gmail.at.boban.talevski.popularmovies.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieAdapter;
import com.gmail.at.boban.talevski.popularmovies.database.AppDatabase;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieRepository;
import com.gmail.at.boban.talevski.popularmovies.utils.MovieType;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.MainViewModel;
import com.gmail.at.boban.talevski.popularmovies.viewmodel.MainViewModelFactory;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler,
        MovieRepository.ErrorHandlerActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_MOVIE";
    public static final String EXTRA_MOVIE_TYPE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_MOVIE_TYPE";
    private static final String EXTRA_RECYCLER_VIEW_STATE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_RECYCLER_VIEW_STATE";;

    private MovieAdapter adapter;

    private MovieType movieType;

    private Parcelable recyclerViewState;

    private RecyclerView moviesRecyclerView;
    private ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecyclerView = findViewById(R.id.rv_movies);
        loadingProgress = findViewById(R.id.loading_progress);

        // if it's a fresh activity creation, show favorite movies by default
        if (savedInstanceState == null) {
            movieType = MovieType.POPULAR;
            setupViewModel();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(EXTRA_MOVIE_TYPE, movieType);
        recyclerViewState = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(EXTRA_RECYCLER_VIEW_STATE, recyclerViewState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movieType = (MovieType) savedInstanceState.getSerializable(EXTRA_MOVIE_TYPE);
        recyclerViewState = savedInstanceState.getParcelable(EXTRA_RECYCLER_VIEW_STATE);
        setupViewModel();
    }

    private void setupViewModel() {
        showProgressBar();
        MainViewModelFactory factory =
                new MainViewModelFactory(AppDatabase.getInstance(this), this);
        final MainViewModel viewModel =
                ViewModelProviders.of(this, factory).get(MainViewModel.class);
        viewModel.getMovies(movieType).observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
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

        // clears recyclerViewState so it doesn't keep state when switching between
        // different movie lists, but shows them from the top by default
        recyclerViewState = null;

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
        moviesRecyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        moviesRecyclerView.setHasFixedSize(true);
        hideProgressBar();
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

    @Override
    public void handleError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        loadingProgress.setVisibility(View.INVISIBLE);
    }
}
