package com.gmail.at.boban.talevski.popularmovies.model;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MovieVideos {
    @Expose
    private List<MovieVideo> results;

    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }
}
