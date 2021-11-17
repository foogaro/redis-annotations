package com.foogaro.data.redis.fts.models;

import com.foogaro.data.redis.fts.annotations.Numeric;
import com.foogaro.data.redis.fts.annotations.Searchable;

@Searchable
public class Movie {

    private long id;
    private String title;
    private int rating;
    @Numeric private int year;

    public Movie() {
    }

    public Movie(String title, int rating, int year) {
        this.title = title;
        this.rating = rating;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", year=" + year +
                '}';
    }
}
