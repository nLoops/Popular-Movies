package com.example.android.moviestation;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.android.moviestation.Adapters.MovieRvAdapter;
import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.Loaders.FavoritesLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity implements
        MovieRvAdapter.MovieOnClickListener,
        LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final int LOADER_ID = 55;
    private RecyclerView mRecycleView;
    private MovieRvAdapter mAdapter;
    private TextView mEmptyView;

    private static final String RECYCLE_VIEW_STATE = "recycle_state";
    private int mScrollState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mEmptyView = (TextView) findViewById(R.id.favorite_layout_emptyView);
        mRecycleView = (RecyclerView) findViewById(R.id.rv_favorite_layout);
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

        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setHasFixedSize(true);
        mAdapter = new MovieRvAdapter(this, this, true);
        mRecycleView.setAdapter(mAdapter);

        loadData();


    }

    private void loadData() {
        LoaderManager loaderManager = getLoaderManager();
        if (loaderManager == null) {
            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(LOADER_ID, null, this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RecyclerView.LayoutManager layoutManager = mRecycleView.getLayoutManager();
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
    public void onClick(Movie movie) {
        String moviePoster = movie.getmPosterPath();
        String movieTitle = movie.getTitle();
        String movieDate = movie.getReleaseDate();
        String movieOverview = movie.getOverView();
        double movieRate = movie.getVoteRate();
        int movieID = movie.getMovieID();

        Intent detailIntent = new Intent(FavoriteActivity.this, DetailActivity.class);
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
        return new FavoritesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies.size() > 0) {
            mAdapter.setMoviesArrayList((ArrayList<Movie>) movies);
            mRecycleView.scrollToPosition(mScrollState);
        } else {
            /*
            show no data View
             */
            mEmptyView.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.clear();
    }

}
