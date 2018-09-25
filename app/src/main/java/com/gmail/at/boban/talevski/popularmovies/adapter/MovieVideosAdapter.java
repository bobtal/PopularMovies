package com.gmail.at.boban.talevski.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.model.MovieVideo;

import java.util.List;

public class MovieVideosAdapter
        extends RecyclerView.Adapter<MovieVideosAdapter.MovieVideosViewHolder> {

    private List<MovieVideo> videoList;
    private Context context;
    private MovieVideosAdapterClickHandler clickHandler;

    public interface MovieVideosAdapterClickHandler {
        void onListItemClick(MovieVideo movieVideo);
    }

    public MovieVideosAdapter(Context context, MovieVideosAdapterClickHandler clickHandler,
                              List<MovieVideo> videoList) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public MovieVideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        MovieVideosViewHolder holder = new MovieVideosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieVideosViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class MovieVideosViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView videoName;

        public MovieVideosViewHolder(View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.tv_video_name);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            MovieVideo video = videoList.get(position);
            videoName.setText(video.getName());
        }

        @Override
        public void onClick(View view) {
            clickHandler.onListItemClick(videoList.get(getAdapterPosition()));
        }
    }
}
