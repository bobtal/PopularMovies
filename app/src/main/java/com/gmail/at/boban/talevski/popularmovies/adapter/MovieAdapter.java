package com.gmail.at.boban.talevski.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.model.Movie;
import com.gmail.at.boban.talevski.popularmovies.ui.DetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> movieList;
    private Context context;

    final private MovieAdapterOnClickHandler clickHandler;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler, List<Movie> movieList) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.movieList = movieList;
    }

    public interface MovieAdapterOnClickHandler {
        void onListItemClick(Movie movie);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MovieViewHolder holder = new MovieViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView moviePosterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePosterImageView = itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Movie movie = movieList.get(position);
            String posterImageUrl = Movie.POSTER_IMAGE_BASE_URL + movie.getPosterPath();
            Picasso.get()
                    .load(posterImageUrl)
                    .error(R.drawable.no_image)
                    .into(moviePosterImageView);
        }

        @Override
        public void onClick(View view) {
            clickHandler.onListItemClick(movieList.get(getAdapterPosition()));
        }
    }
}
