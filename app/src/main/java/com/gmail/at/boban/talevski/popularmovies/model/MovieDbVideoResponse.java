package com.gmail.at.boban.talevski.popularmovies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDbVideoResponse {
    @SerializedName("results")
    @Expose
    List<MovieVideo> results;

    public List<MovieVideo> getResults() {
        return results;
    }

    public void setResults(List<MovieVideo> results) {
        this.results = results;
    }
}
