package com.gmail.at.boban.talevski.popularmovies.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.at.boban.talevski.popularmovies.R;
import com.gmail.at.boban.talevski.popularmovies.model.MovieReview;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    private List<MovieReview> reviewsList;
    private Context context;

    public MovieReviewsAdapter(Context context, List<MovieReview> reviewsList) {
        this.context = context;
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public MovieReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int layoutResourceId = R.layout.review_video_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutResourceId, viewGroup, false);
        MovieReviewsViewHolder holder = new MovieReviewsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public class MovieReviewsViewHolder extends RecyclerView.ViewHolder {
        private TextView reviewText;

        public MovieReviewsViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewText = itemView.findViewById(R.id.tv_review);
        }

        public void bind(int position) {
            MovieReview review = reviewsList.get(position);
            // Enable <a> links in the text from the review
            reviewText.setMovementMethod(LinkMovementMethod.getInstance());
            reviewText.setText(Html.fromHtml(review.getContent()));
        }
    }
}
