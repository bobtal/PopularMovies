package com.gmail.at.boban.talevski.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.adapter.MovieAdapter;
import com.gmail.at.boban.talevski.popularmovies.api.MovieDbApi;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.model.MovieDbResponse;
import com.gmail.at.boban.talevski.popularmovies.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    RecyclerView moviesRecyclerView;
    MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRecyclerView = findViewById(R.id.rv_movies);

        MovieDbApi api = RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class);
        Call<MovieDbResponse> call = api.getPopularMovies(Constants.API_KEY);
        call.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                Log.d(TAG, "call successful");
                adapter = new MovieAdapter(MainActivity.this, response.body().getResults());
                moviesRecyclerView.setAdapter(adapter);
                moviesRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                moviesRecyclerView.setHasFixedSize(true);
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.d(TAG, "call unsuccessful");
                Toast.makeText(MainActivity.this, R.string.error_displaying_movies, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
