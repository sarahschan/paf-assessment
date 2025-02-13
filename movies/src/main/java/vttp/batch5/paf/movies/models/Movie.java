package vttp.batch5.paf.movies.models;

import java.sql.Date;

public class Movie {
    
    private String imdbId;
    private float voteAverage;
    private int voteCount;
    private Date releaseDate;
    private float revenue;
    private float budget;
    private int runtime;
    private String title;
    private String directors;
    private String overview;
    private String tagline;
    private String genres;
    private int imdbRating;
    private int imdbVotes;


    public Movie() {
    }


    @Override
    public String toString() {
        return "Movie [imdbId=" + imdbId + ", voteAverage=" + voteAverage + ", voteCount=" + voteCount
                + ", releaseDate=" + releaseDate + ", revenue=" + revenue + ", budget=" + budget + ", runtime="
                + runtime + ", title=" + title + ", directors=" + directors + ", overview=" + overview + ", tagline="
                + tagline + ", genres=" + genres + ", imdbRating=" + imdbRating + ", imdbVotes=" + imdbVotes + "]";
    }

    
    public String getImdbId() {
        return imdbId;
    }
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
    public float getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
    public int getVoteCount() {
        return voteCount;
    }
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }
    public Date getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    public float getRevenue() {
        return revenue;
    }
    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }
    public float getBudget() {
        return budget;
    }
    public void setBudget(float budget) {
        this.budget = budget;
    }
    public int getRuntime() {
        return runtime;
    }
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDirectors() {
        return directors;
    }
    public void setDirectors(String directors) {
        this.directors = directors;
    }
    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }
    public String getTagline() {
        return tagline;
    }
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
    public String getGenres() {
        return genres;
    }
    public void setGenres(String genres) {
        this.genres = genres;
    }
    public int getImdbRating() {
        return imdbRating;
    }
    public void setImdbRating(int imdbRating) {
        this.imdbRating = imdbRating;
    }
    public int getImdbVotes() {
        return imdbVotes;
    }
    public void setImdbVotes(int imdbVotes) {
        this.imdbVotes = imdbVotes;
    }

    
    
}
