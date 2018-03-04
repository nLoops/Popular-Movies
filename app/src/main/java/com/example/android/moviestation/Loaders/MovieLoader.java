package com.example.android.moviestation.Loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.Utils.NetworkUtils;

import java.util.List;

/**
 * Loader to handle background working.
 * Decide to use instead of AsyncTask to handle Activity Life Cycle.
 */

public class MovieLoader extends AsyncTaskLoader<List<Movie>> {
    /**
     * Log Tag
     */
    private static final String TAG = MovieLoader.class.getSimpleName();
    /**
     * Passed String Url
     */
    private final String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        // if passed Url not null or Empty we execute the method.
        if (mUrl != null || !mUrl.equals("")) {
            return NetworkUtils.executeMovieDataOpr(mUrl);
        } else {
            return null;
        }
    }
}
