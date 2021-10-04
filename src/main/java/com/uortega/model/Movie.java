package com.uortega.model;

public class Movie {
    private Long movieId;
    private String title;
    private String director;
    private Integer year;

    public Movie() {

    }

    public Movie(Long movieId, String title, String director, Integer year) {
        this.movieId = movieId;
        this.title = title;
        this.director = director;
        this.year = year;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
