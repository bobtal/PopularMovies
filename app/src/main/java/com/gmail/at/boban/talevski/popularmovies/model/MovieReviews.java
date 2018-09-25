package com.gmail.at.boban.talevski.popularmovies.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MovieReviews {
    @Expose
    private List<MovieReview> results;

    public List<MovieReview> getResults() {
        return results;
    }

    public void setResults(List<MovieReview> results) {
        this.results = results;
    }
}
