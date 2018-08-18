package com.gmail.at.boban.talevski.popularmovies.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.adapter.MovieAdapter;
import com.gmail.at.boban.talevski.popularmovies.databinding.ActivityDetailsBinding;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(
                DetailsActivity.this, R.layout.activity_details);

        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(MovieAdapter.EXTRA_MOVIE)) {
            Movie movie = startingIntent.getParcelableExtra(MovieAdapter.EXTRA_MOVIE);
            binding.setMovie(movie);
        } else {
            closeOnError();
        }

    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.movie_data_not_available, Toast.LENGTH_LONG).show();
    }
}
