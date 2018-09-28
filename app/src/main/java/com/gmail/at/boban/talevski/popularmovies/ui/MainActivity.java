package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.gmail.at.boban.talevski.popularmovies.utils.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String EXTRA_MOVIE = "com.gmail.at.boban.talevski.popularmovies.ui.EXTRA_MOVIE";

    private RecyclerView moviesRecyclerView;
    private MovieAdapter adapter;
    private MovieDbApi api;
    ProgressBar loadingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecyclerView = findViewById(R.id.rv_movies);
        loadingProgress = findViewById(R.id.loading_progress);

        api = RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class);
        loadMostPopularMovies();
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
                loadMostPopularMovies();
                return true;

            case R.id.action_top_rated:
                loadTopRatedMovies();
                return true;

            case R.id.action_favorites:
                loadFavoriteMovies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadFavoriteMovies() {
        List<Movie> favoriteMovies = AppDatabase.getInstance(this).movieDao().loadAllMovies();
        populateGridWithMovies(favoriteMovies);
    }

    private void loadMostPopularMovies() {
        Call<MovieDbResponse> call = api.getPopularMovies(Constants.API_KEY);
        loadMovieData(call);
    }

    private void loadTopRatedMovies() {
        Call<MovieDbResponse> call = api.getTopRatedMovies(Constants.API_KEY);
        loadMovieData(call);
    }

    private void loadMovieData(Call<MovieDbResponse> call) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            showProgressBar();
            call.enqueue(new Callback<MovieDbResponse>() {
                @Override
                public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                    Log.d(TAG, "call successful");
                    List<Movie> results = response.body().getResults();

                    hideProgressBar();

                    populateGridWithMovies(results);
                }

                @Override
                public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                    Log.d(TAG, "call unsuccessful");
                    Toast.makeText(MainActivity.this, R.string.error_displaying_movies, Toast.LENGTH_LONG).show();

                    hideProgressBar();
                }
            });
        }
        else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show();
        }
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
