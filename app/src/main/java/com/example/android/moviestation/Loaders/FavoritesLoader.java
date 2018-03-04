package com.example.android.moviestation.Loaders;


import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.Utils.FavoritesUtils;

import java.util.List;

/**
 * Loader To load data from database into background thread.
 */

public class FavoritesLoader extends AsyncTaskLoader<List<Movie>> {

    Context mContext;

    public FavoritesLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        return FavoritesUtils.getMovieList(mContext);
    }
}
