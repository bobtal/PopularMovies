package com.gmail.at.boban.talevski.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDbVideoReviewResponse {
    @Expose
    private MovieVideos videos;

    @Expose
    private MovieReviews reviews;

    public MovieVideos getVideos() {
        return videos;
    }

    public void setVideos(MovieVideos videos) {
        this.videos = videos;
    }

    public MovieReviews getReviews() {
        return reviews;
    }

    public void setReviews(MovieReviews reviews) {
        this.reviews = reviews;
    }
}
