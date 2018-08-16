package com.gmail.at.boban.talevski.popularmovies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

        MovieDbApi api = RetrofitClientInstance.getRetrofitInstance().create(MovieDbApi.class);
        Call<MovieDbResponse> call = api.getPopularMovies(Constants.API_KEY);
        call.enqueue(new Callback<MovieDbResponse>() {
            @Override
            public void onResponse(Call<MovieDbResponse> call, Response<MovieDbResponse> response) {
                Log.d(TAG, "call successful");
//                adapter = new MovieAdapter(this, response);
            }

            @Override
            public void onFailure(Call<MovieDbResponse> call, Throwable t) {
                Log.d(TAG, "call unsuccessful");
            }
        });
    }
}
