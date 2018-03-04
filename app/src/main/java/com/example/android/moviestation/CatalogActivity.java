package com.example.android.moviestation;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.moviestation.Adapters.MovieRvAdapter;
import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.Loaders.MovieLoader;
import com.example.android.moviestation.Utils.NetworkUtils;


import java.util.ArrayList;
import java.util.List;

import static com.example.android.moviestation.Utils.NetworkUtils.DEFAULT_LANGUAGE;
import static com.example.android.moviestation.Utils.NetworkUtils.DEFAULT_PAGES;
import static com.example.android.moviestation.Utils.NetworkUtils.PARAM_API_KEY;
import static com.example.android.moviestation.Utils.NetworkUtils.PARAM_DEF_LANGUAGE;
import static com.example.android.moviestation.Utils.NetworkUtils.PARAM_DEF_PAGES;


public class CatalogActivity extends AppCompatActivity implements
        MovieRvAdapter.MovieOnClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    /**
     * Log Tag.
     */
    private static final String TAG = CatalogActivity.class.getSimpleName();

    /**
     * Unique ID for Loader
     */
    private static final int LOADER_ID = 1;

    /**
     * Ref of RecycleView
     */
    private RecyclerView mRecycleView;
    private static final String RECYCLE_VIEW_STATE = "recycle_state";
    private int mScrollState;

    /**
     * Ref of our Custom Adapter to fill RecycleView
     */
    private MovieRvAdapter mAdapter;
    /**
     * Ref of error message Textview
     */
    private TextView mNoDataTextView;
    /**
     * Ref of Progress Bar Indicator
     */
    private ProgressBar mLoadingProgressBar;
    /**
     * Ref of SwipeRefresh View
     * we decide to use it instead of button into menu
     */
    private SwipeRefreshLayout mRefreshLayout;

    /*
    a flag to check if the sort Order changed or not.
     */
    private static boolean sortChanged = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        /*Link ref with our Layout Ui Components*/
        mRecycleView = findViewById(R.id.rv_catalog_layout);
        mNoDataTextView = findViewById(R.id.tv_error_message_display);
        mLoadingProgressBar = findViewById(R.id.pb_loading_indicator);
        mRefreshLayout = findViewById(R.id.srl_refresh);
        GridLayoutManager layoutManager;


        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        /*
        if we are in portrait mode we set the grid columns to 2
          */
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            /*
            if we are in landscape mode we set the grid columns to 4
             */
            layoutManager = new GridLayoutManager(this, 4);
        }
        /*Set Recycle View
          LayoutManager
          hasFixedSize to increase recycle view performance
          finally : the recycle view adapter.
          */
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mAdapter = new MovieRvAdapter(this, this, false);
        mRecycleView.setAdapter(mAdapter);
        /*call helper method loadData to start our AsyncLoader and
          start fill the recycle view adapter with data*/

        loadMovieData();

        /*Set Refresh Layout Listener*/
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                loadMovieData();
                mRefreshLayout.setRefreshing(false);
            }
        });


        /*Set Color Schema of Refresh UI*/
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


    }


    @Override
    protected void onResume() {
        super.onResume();

             /*
        here we check if the sortOrder of film categories has changed
        if true we LoadTheData
         */
        if (sortChanged || mAdapter == null) {
            sortChanged = false;
            loadMovieData();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
        mScrollState = 0;
        mScrollState = ((GridLayoutManager) layoutManager)
                .findFirstCompletelyVisibleItemPosition();
        outState.putInt(RECYCLE_VIEW_STATE, mScrollState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mScrollState = savedInstanceState.getInt(RECYCLE_VIEW_STATE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingIntent = new Intent(CatalogActivity.this, SettingActivity.class);
                startActivity(settingIntent);
                return true;

            case R.id.favorite_layout:
                Intent favoriteIntent = new Intent(CatalogActivity.this,
                        FavoriteActivity.class);
                startActivity(favoriteIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Helper method to Start the AsyncLoader with his id
     * and grab the data from Http operation as well
     * fill the adapter to populate data into recycle view
     */
    private void loadMovieData() {
        if (isNetworkConnected()) {
            showMovieDataView();
            // Get ref of Loader Manager
            /*
      Ref of LoaderManager
     */
            LoaderManager loaderManager = getLoaderManager();

            if (loaderManager == null) {
                // Start Our Loader With Unique ID
                loaderManager.initLoader(LOADER_ID, null, CatalogActivity.this);
            } else {
                loaderManager.restartLoader(LOADER_ID, null, CatalogActivity.this);
            }

        } else {
            showNoDataView();
        }

    }


    /*
    setter to update sortChange
     */
    public static void setSortChanged(boolean sortChanged) {
        CatalogActivity.sortChanged = sortChanged;
    }

    /**
     * Ui controlling
     */
    private void showMovieDataView() {

        mNoDataTextView.setVisibility(View.INVISIBLE);
        mRecycleView.setVisibility(View.VISIBLE);
    }

    /**
     * Ui controlling
     */
    private void showNoDataView() {
        if (isNetworkConnected()) {
            mNoDataTextView.setText(getText(R.string.error_message));
        } else {
            mNoDataTextView.setText(getText(R.string.no_internet));
        }
        mNoDataTextView.setVisibility(View.VISIBLE);
        mRecycleView.setVisibility(View.INVISIBLE);
    }

    /**
     * @return state of current network True if internet available , false if not.
     */
    private Boolean isNetworkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo networkInfo = conMgr != null ? conMgr.getActiveNetworkInfo() : null;

        return networkInfo != null && networkInfo.isConnected();

    }


    /**
     * Adapter interface that handle recycle view items click
     * we will make an intent to DetailActivity passing some required data filtered by clicked movie
     * by position at adapter.
     *
     * @param movie
     */
    @Override
    public void onClick(Movie movie) {
        String moviePoster = movie.getPosterPath();
        String movieTitle = movie.getTitle();
        String movieDate = movie.getReleaseDate();
        String movieOverview = movie.getOverView();
        double movieRate = movie.getVoteRate();
        int movieID = movie.getMovieID();

        Intent detailIntent = new Intent(CatalogActivity.this, DetailActivity.class);
        detailIntent.putExtra(getString(R.string.detail_poster), moviePoster);
        detailIntent.putExtra(getString(R.string.detail_title), movieTitle);
        detailIntent.putExtra(getString(R.string.detail_date), movieDate);
        detailIntent.putExtra(getString(R.string.detail_overview), movieOverview);
        detailIntent.putExtra(getString(R.string.detail_ID), movieID);
        detailIntent.putExtra(getString(R.string.detail_vote), movieRate);
        startActivity(detailIntent);
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        /*get stored Preferences.*/
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = preferences.getString(
                getString(R.string.setting_key),
                getString(R.string.setting_default)
        );

        /*String that will hold movie display filter
          default will the Popular Movies.
          */

        String listDisplay = "";
        if (sortBy.equals(getString(R.string.setting_popular_value))) {
            listDisplay = NetworkUtils.POPULAR_ENDPOINT;
        } else if (sortBy.equals(getString(R.string.setting_toprated_value))) {
            listDisplay = NetworkUtils.HIGHRATED_ENDPOINT;
        }
        String urlString = NetworkUtils.BASE_URL + listDisplay;

        /*Build Url Depends on User Prefs*/
        Uri.Builder builder = Uri.parse(urlString).buildUpon()
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                .appendQueryParameter(PARAM_DEF_LANGUAGE, DEFAULT_LANGUAGE)
                .appendQueryParameter(PARAM_DEF_PAGES, DEFAULT_PAGES);
        Uri requestUri = builder.build();

        return new MovieLoader(this, requestUri.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        if (movies != null && movies.size() > 0) {
            showMovieDataView();
            mAdapter.setMoviesArrayList((ArrayList<Movie>) movies);
            mRecycleView.scrollToPosition(mScrollState);
        } else {
            showNoDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.clear();
    }


}
