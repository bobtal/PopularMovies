package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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
        }
        return super.onOptionsItemSelected(item);
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
        if (isNetworkAvailable()) {
            showProgressBar();
            call.enqueue(new Callback<MovieDbResponse>() {
                @Override
                public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                    Log.d(TAG, "call successful");
                    adapter = new MovieAdapter(MainActivity.this, response.body().getResults());
                    moviesRecyclerView.setAdapter(adapter);
                    moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    moviesRecyclerView.setHasFixedSize(true);

                    hideProgressBar();
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

    private void hideProgressBar() {
        loadingProgress.setVisibility(View.INVISIBLE);
        moviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        loadingProgress.setVisibility(View.VISIBLE);
        moviesRecyclerView.setVisibility(View.INVISIBLE);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            isAvailable = true;
        }
        return isAvailable;
    }
}
