package com.example.android.moviestation.ContentClasses;

/**
 * A Class That Contains Single Movie Data
 * including getters / setters
 */

public class Movie {

    private final String mTitle;
    private final String mPosterPath;
    private final String mOverView;
    private final String mReleaseDate;
    private final double mVoteRate;
    private final int mMovieID;
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_POSTER_SIZE = "w500";


    /**
     * Public Constructor.
     *
     * @param title
     * @param posterPath
     * @param overView
     * @param releaseDate
     * @param voteRate
     * @param movieID
     */
    public Movie(String title, String posterPath, String overView, String releaseDate, double voteRate, int movieID) {
        this.mTitle = title;
        this.mPosterPath = posterPath;
        this.mOverView = overView;
        this.mReleaseDate = releaseDate;
        this.mVoteRate = voteRate;
        this.mMovieID = movieID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getOverView() {
        return mOverView;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public double getVoteRate() {
        return mVoteRate;
    }

    public int getMovieID() {
        return mMovieID;
    }

    public String getmPosterPath() {
        return mPosterPath;
    }

    /**
     * As Movie Database APi Docs
     * Each poster need three parts to get full Poster Url
     * 1- Base Url.
     * 2- Size of Image.
     * 3- PosterPath.
     *
     * @return fullPosterPath
     */
    public String getPosterPath() {
        return POSTER_BASE_URL + DEFAULT_POSTER_SIZE + mPosterPath;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "mTitle='" + mTitle + '\'' +
                ", mPosterPath='" + mPosterPath + '\'' +
                ", mOverView='" + mOverView + '\'' +
                ", mReleaseDate='" + mReleaseDate + '\'' +
                ", mVoteRate=" + mVoteRate +
                ", mMovieID=" + mMovieID +
                '}';
    }


}
