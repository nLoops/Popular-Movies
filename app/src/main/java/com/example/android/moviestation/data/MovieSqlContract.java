package com.example.android.moviestation.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract we will define all Sqlite database tables in and
 * Content Uri.
 */

public class MovieSqlContract {
    /*
    The Authority of the access to content provider in our case only our app package because
    we've chosen exported = false into Manifest declaration.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.moviestation";
    /*
    the Base Uri Schema + Authority
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
    this step will accept on all tables.
     */
    public static final String FAVORITES_PATH = "tblFavorites";

    public static final class FavoriteEntry implements BaseColumns {

        public static final String TABLE_NAME = "tblFavorites";

        public static final String MOVIE_TITLE = "mTitle";
        public static final String MOVIE_POSTER = "mPoster";
        public static final String MOVIE_OVERVIEW = "mOverview";
        public static final String MOVIE_DATE = "mDate";
        public static final String MOVIE_RATE = "mRate";
        public static final String MOVIE_ID = "mId";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(FAVORITES_PATH)
                .build();


    }
}
