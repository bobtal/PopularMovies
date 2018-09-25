package com.gmail.at.boban.talevski.popularmovies.model;

import com.google.gson.annotations.Expose;

public class MovieReview {
    @Expose
    private String content;

    @Expose
    private String url;

    @Expose
    private String author;

    public String getContent() {
        if (content.length() <= 200) {
            return content;
        }
        // Add url to the full review in the review text
        String result = content.substring(0, 200) + "..." +
                "<a href=\"" + url + "\">Read full review</a>";
        return result;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
