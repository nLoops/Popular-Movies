package com.example.android.moviestation;

import android.content.AsyncTaskLoader;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviestation.Adapters.ReviewRvAdapter;
import com.example.android.moviestation.ContentClasses.ReviewModel;
import com.example.android.moviestation.data.MovieSqlContract.FavoriteEntry;

import com.example.android.moviestation.Adapters.VideoRvAdapter;
import com.example.android.moviestation.ContentClasses.MovieVideos;
import com.example.android.moviestation.Utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity implements VideoRvAdapter.VideoOnClickListener {

    /**
     * Log Tag
     */
    private static final String TAG = DetailActivity.class.getSimpleName();
    private ImageView mPosterImageView;
    private TextView mDetailTitle;
    private TextView mDetailDate;
    private TextView mDetailOverview;
    private ImageView mYoutubeImageView;
    private ImageView mPlayTrailerImageView;
    private ImageView mFavouriteImageView;
    private TextView mNoVideoTextView;
    private RatingBar mMovieRatingBar;
    private int mMovieID;

    /**
     * Ref of Video Adapter
     */
    private VideoRvAdapter mAdapter;

    /**
     * Ref of Review Adapter
     */
    private ReviewRvAdapter mReviewAdapter;

    /**
     * Ref of Result Array List.
     */

    private ArrayList<MovieVideos> mVideosArrayList;

    /**
     * Ref of Review Array List.
     */
    private ArrayList<ReviewModel> mReviewArrayList;

    /**
     * Var to hold Video URL String.
     */
    private String videoUrl = "";

    /***
     * Flag to check Img Favourite status.
     */
    private boolean isFavChecked = false;
    /**
     * Flag to set if the Movie Saved to Favorite Or Not
     */
    private boolean isFavorite = false;

    private final int emptyFav = R.drawable.ic_favorite_border_white_24dp;
    private final int fullFav = R.drawable.ic_favorite_white_24dp;

    /**
     * Vars that will holding Movie Data.
     */
    private String mMovieTitle;
    private String mMovieDate;
    private String mMovieOverview;
    private String mMoviePoster;
    private double mMovieRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Get Ref of UI Componnets.
        mPosterImageView = findViewById(R.id.iv_detail_poster);
        mDetailTitle = findViewById(R.id.tv_detail_title);
        mDetailDate = findViewById(R.id.tv_detail_release_date);
        mDetailOverview = findViewById(R.id.tv_detail_overview);
        mYoutubeImageView = findViewById(R.id.iv_trailer_img);
        mPlayTrailerImageView = findViewById(R.id.iv_youtube_play);
        ImageView mShareImageView = findViewById(R.id.iv_share);
        mFavouriteImageView = findViewById(R.id.iv_favorite);
        mNoVideoTextView = findViewById(R.id.tv_no_video);
        mMovieRatingBar = findViewById(R.id.rb_detail_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mVideosArrayList = new ArrayList<>();


        // Setup Recycle View.
        /*
      Ref of Recycle View
     */
        RecyclerView mVideosRecycleView = findViewById(R.id.rv_detail_layout);
        mAdapter = new VideoRvAdapter(this, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mVideosRecycleView.setLayoutManager(layoutManager);
        mVideosRecycleView.setHasFixedSize(true);
        mVideosRecycleView.setAdapter(mAdapter);
        // Help to make Sliding by one full page
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mVideosRecycleView);

        /*
        Review Recycle View Setup.
         */
        RecyclerView mReviewRecycleView = findViewById(R.id.rv_review);
        mReviewAdapter = new ReviewRvAdapter(this);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReviewRecycleView.setLayoutManager(reviewLayoutManager);
        mReviewRecycleView.setHasFixedSize(true);
        mReviewRecycleView.setAdapter(mReviewAdapter);

        // Define Font
        Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/font_bold.ttf");
        mDetailTitle.setTypeface(fontType);
        mDetailDate.setTypeface(fontType);
        // get Created Intent.
        Intent createdIntent = getIntent();

        setComponentsValues(createdIntent);
        // setPlayImage OnclickListener.
        mPlayTrailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayClick(videoUrl);
            }
        });
        mYoutubeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayClick(videoUrl);
            }
        });

        mShareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareText = mDetailTitle.getText().toString() + "\n\n" + videoUrl;
                shareAction(shareText);
            }
        });

        if (isAlreadySavedToFavorites()) {
            isFavChecked = true;
            isFavorite = true;
            mFavouriteImageView.setImageResource(fullFav);
        }


        String url = buildVideoUri().toString();
        new VideoAsyncTask().execute(url);

        String reviewUrl = buildReviewUri().toString();
        new ReviewAsyncTask().execute(reviewUrl);

    }

    /***
     * Set Data to UI components depends on passed intent data.
     * @param intent
     */
    private void setComponentsValues(Intent intent) {

        if (intent.hasExtra(getString(R.string.detail_poster))) {
            Picasso.with(this)
                    .load(intent.getStringExtra(getString(R.string.detail_poster)))
                    .into(mPosterImageView);

            mMoviePoster = intent.getStringExtra(getString(R.string.detail_poster));
        }

        if (intent.hasExtra(getString(R.string.detail_title))) {
            mDetailTitle.setText(intent.getStringExtra(getString(R.string.detail_title)));
            setTitle(intent.getStringExtra(getString(R.string.detail_title)));

            mMovieTitle = intent.getStringExtra(getString(R.string.detail_title));
        }

        if (intent.hasExtra(getString(R.string.detail_date))) {
            mDetailDate.setText(intent.getStringExtra(getString(R.string.detail_date)));
            mMovieDate = intent.getStringExtra(getString(R.string.detail_date));
        }

        if (intent.hasExtra(getString(R.string.detail_overview))) {
            mDetailOverview.setText(intent.getStringExtra(getString(R.string.detail_overview)));
            mMovieOverview = intent.getStringExtra(getString(R.string.detail_overview));

        }

        if (intent.hasExtra(getString(R.string.detail_ID))) {
            mMovieID = intent.getIntExtra(getString(R.string.detail_ID), 0);
        }

        if (intent.hasExtra(getString(R.string.detail_vote))) {
            mMovieRate = intent.getDoubleExtra(getString(R.string.detail_vote), 0);
            mMovieRatingBar.setRating((float) mMovieRate / 2);
        }


    }

    /**
     * Helper Method Will check and Add the Movie to Database As Favorite
     */
    private void addToFavorite() {

        if (isFavorite) {
            Toast.makeText(getApplicationContext()
                    , getString(R.string.movie_already_in_list), Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(FavoriteEntry.MOVIE_DATE, mMovieDate);
        values.put(FavoriteEntry.MOVIE_ID, mMovieID);
        values.put(FavoriteEntry.MOVIE_OVERVIEW, mMovieOverview);
        values.put(FavoriteEntry.MOVIE_POSTER, mMoviePoster);
        values.put(FavoriteEntry.MOVIE_RATE, mMovieRate);
        values.put(FavoriteEntry.MOVIE_TITLE, mMovieTitle);
        Uri id = getContentResolver().insert(FavoriteEntry.CONTENT_URI, values);
        if (id != null) {
            Toast.makeText(getApplicationContext()
                    , getString(R.string.movie_saved_favorite), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Helper Method to easily remove Movie from List.
     */
    private void removeFromFavorite() {
        int id = getMovieSqlID();
        Uri contentUri = ContentUris.withAppendedId(FavoriteEntry.CONTENT_URI, id);
        if (id > 0) {
            int rawDeleted = getContentResolver().delete(contentUri, null, null);
            if (rawDeleted > 0) {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_deleted_favorite), Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * @return ID of current Movie.
     */
    private int getMovieSqlID() {
        int movieID = 0;
        String selection = FavoriteEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMovieID)};
        String[] projections = new String[]{FavoriteEntry._ID};
        Uri contentUri = FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(mMovieID)).build();
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(contentUri, projections, selection,
                    selectionArgs, null);
            if (cursor.moveToNext()) {
                movieID = cursor.getInt(cursor.getColumnIndex(FavoriteEntry._ID));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return movieID;
    }

    /**
     * @return boolen to check if the film is already saved or not.
     */
    private boolean isAlreadySavedToFavorites() {
        boolean isSaved;
        String selection = FavoriteEntry.MOVIE_ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mMovieID)};
        String[] projections = new String[]{FavoriteEntry._ID};
        Uri contentUri = FavoriteEntry.CONTENT_URI.buildUpon()
                .appendPath(String.valueOf(mMovieID)).build();

        Cursor cursor = getContentResolver().query(contentUri, projections, selection,
                selectionArgs, null);
        try {
            if (cursor.getCount() > 0 && cursor.getCount() == 1) {
                return isSaved = true;
            } else {
                return isSaved = false;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }


    }

    /**
     * Trigger the Favorite Status when clicked on.
     *
     * @param view
     */
    public void triggerFavorite(View view) {
        if (!isFavChecked) {
            mFavouriteImageView.setImageResource(fullFav);
            addToFavorite();
            isFavChecked = true;
        } else {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.alert_favorite_dialog_title))
                    .setPositiveButton(getString(R.string.alert_favorite_remove), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mFavouriteImageView.setImageResource(emptyFav);
                            removeFromFavorite();
                            isFavChecked = false;
                        }
                    })
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.alert_favorite_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (dialogInterface != null) {
                                dialogInterface.cancel();
                            }

                        }
                    });

            AlertDialog alertDialog = alertBuilder.create();
            alertDialog.show();


        }
    }

    /**
     * This method will build Movie database Get Videos using Movie ID
     * that passed with created Intent.
     */
    private Uri buildVideoUri() {
        if (mMovieID != 0) {
            String id = String.valueOf(mMovieID);
            String vidUrl = NetworkUtils.BASE_VIDEO_URL + id + NetworkUtils.VIDEO_URL_PATH;
            Uri.Builder builder = Uri.parse(vidUrl).buildUpon()
                    .appendQueryParameter(NetworkUtils.PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    .appendQueryParameter(NetworkUtils.PARAM_DEF_LANGUAGE, NetworkUtils.DEFAULT_LANGUAGE);
            return builder.build();
        } else {
            return null;
        }
    }

    /**
     * This method will build Movie database Get Reviews using Movie ID
     * that passed with created Intent.
     */
    private Uri buildReviewUri() {
        if (mMovieID != 0) {
            String id = String.valueOf(mMovieID);
            String reviewUrl = NetworkUtils.BASE_VIDEO_URL + id + NetworkUtils.REVIEW_URL_PATH;
            Uri.Builder builder = Uri.parse(reviewUrl).buildUpon()
                    .appendQueryParameter(NetworkUtils.PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_TOKEN)
                    .appendQueryParameter(NetworkUtils.PARAM_DEF_LANGUAGE, NetworkUtils.DEFAULT_LANGUAGE);
            return builder.build();
        } else {
            return null;
        }
    }

    /**
     * This Method will help to get and display official trailer image into primary image of
     * activity.
     */
    private void setYouTubeImage() {
        if (mVideosArrayList != null) {
            for (int i = 0; i < mVideosArrayList.size(); i++) {
                if (mVideosArrayList.get(i).getVideoType().equals("Trailer")) {
                    String imgUrl = mVideosArrayList.get(i).getVideoImagePath();
                    mNoVideoTextView.setVisibility(View.INVISIBLE);
                    Picasso.with(this)
                            .load(imgUrl)
                            .into(mYoutubeImageView);
                    videoUrl = mVideosArrayList.get(i).getVideoPath();
                }

            }
        }
    }

    /**
     * This helper method will help to create onClickListener for primary image
     * by passing uri to play trailer on YouTube.
     */
    private void setPlayClick(String urlString) {
        if (urlString != null || !urlString.equals("")) {
            Uri videoUri = Uri.parse(urlString);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, videoUri);
            if (videoIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(videoIntent);
            }
        }

    }

    @Override
    public void onClick(String videoUrl) {

        setPlayClick(videoUrl);
    }

    private void shareAction(String shareText) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(getString(R.string.app_name))
                .setText(shareText)
                .getIntent();

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }

    }

    class VideoAsyncTask extends AsyncTask<String, Void, ArrayList<MovieVideos>> {

        @Override
        protected ArrayList<MovieVideos> doInBackground(String... strings) {
            if (strings[0] != null || !strings[0].equals("")) {
                return NetworkUtils
                        .executeMovieVideosOpr(strings[0]);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<MovieVideos> movieVideos) {
            if (movieVideos != null && movieVideos.size() > 0) {
                mAdapter.setMovieVideos(movieVideos);
                mVideosArrayList = movieVideos;
                setYouTubeImage();
            } else {
                mPlayTrailerImageView.setVisibility(View.INVISIBLE);
                mNoVideoTextView.setVisibility(View.VISIBLE);
            }

        }
    }

    class ReviewAsyncTask extends AsyncTask<String, Void, ArrayList<ReviewModel>> {
        @Override
        protected ArrayList<ReviewModel> doInBackground(String... strings) {
            if (strings[0] != null || !strings[0].equals("")) {
                return NetworkUtils
                        .executeMovieReviewsOpr(strings[0]);
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewModel> reviewModels) {
            if (reviewModels != null && reviewModels.size() > 0) {
                mReviewAdapter.setReviewArrayList(reviewModels);
                mReviewArrayList = reviewModels;
            }
        }
    }
}

