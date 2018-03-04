package com.example.android.moviestation.Utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.moviestation.data.MovieSqlContract.FavoriteEntry;

import com.example.android.moviestation.ContentClasses.Movie;

import java.util.ArrayList;

/**
 * Helper Methods for Favorite Database Operations
 */

public class FavoritesUtils {

    public static ArrayList<Movie> getMovieList(Context context) {
        ArrayList<Movie> movieList = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(FavoriteEntry.CONTENT_URI,
                null, null, null, null);

        try {
            while (cursor.moveToNext()) {
                double movieRate = cursor.getDouble(cursor.getColumnIndex(FavoriteEntry.MOVIE_RATE));
                String movieTitle = cursor.getString(cursor.getColumnIndex(FavoriteEntry.MOVIE_TITLE));
                String moviePoster = cursor.getString(cursor.getColumnIndex(FavoriteEntry.MOVIE_POSTER));
                String movieOverView = cursor.getString(cursor.getColumnIndex(FavoriteEntry.MOVIE_OVERVIEW));
                String movieReleaseDate = cursor.getString(cursor.getColumnIndex(FavoriteEntry.MOVIE_DATE));
                int movieID = cursor.getInt(cursor.getColumnIndex(FavoriteEntry.MOVIE_ID));

                Movie movie = new Movie(movieTitle, moviePoster, movieOverView, movieReleaseDate, movieRate, movieID);
                movieList.add(movie);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return movieList;

    }


}
