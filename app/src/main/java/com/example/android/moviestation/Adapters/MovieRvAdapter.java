package com.example.android.moviestation.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Recycle View Adapter.
 */

public class MovieRvAdapter extends RecyclerView.Adapter<MovieRvAdapter.MovieViewHolder> {

    /**
     * This is a flag in case we called the Adapter from FavoriteActivity
     * we have full image path we will call another method instead of getPosterPath.
     */
    private boolean isFavoriteActivityCall;

    /**
     * Handle OnClick Listener Interface
     */
    public interface MovieOnClickListener {
        void onClick(Movie movie);
    }

    private final MovieOnClickListener mOnClickListener;
    private ArrayList<Movie> mMoviesArrayList;
    private final Context mContext;

    public MovieRvAdapter(Context context, MovieOnClickListener onClickListener
            , boolean favoriteActivity) {
        mContext = context;
        mOnClickListener = onClickListener;
        mMoviesArrayList = new ArrayList<>();
        isFavoriteActivityCall = favoriteActivity;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        boolean shouldAttachedToParent = false;
        int layoutID = R.layout.single_movie_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, viewGroup, shouldAttachedToParent);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (!isFavoriteActivityCall) {
            Picasso.with(mContext)
                    .load(mMoviesArrayList.get(position).getPosterPath())
                    .into(holder.mPosterImageView);
        } else {
            Picasso.with(mContext)
                    .load(mMoviesArrayList.get(position).getmPosterPath())
                    .into(holder.mPosterImageView);
        }
        holder.mTitleTextView.setText(mMoviesArrayList.get(position).getTitle());
        float movieRate = (float) mMoviesArrayList.get(position).getVoteRate() / 2;
        String rateText = String.valueOf(mMoviesArrayList.get(position).getVoteRate()) + "/10";
        holder.mMovieRatingBar.setRating(movieRate);
        holder.mRatingTextView.setText(rateText);
    }

    @Override
    public int getItemCount() {
        if (null == mMoviesArrayList) return 0;
        return mMoviesArrayList.size();
    }

    public void setMoviesArrayList(ArrayList<Movie> movies) {
        mMoviesArrayList = movies;
        notifyDataSetChanged();
    }

    /**
     * Clear data set
     */
    public void clear() {
        mMoviesArrayList.clear();
        notifyDataSetChanged();
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView mPosterImageView;
        final TextView mTitleTextView;
        final RatingBar mMovieRatingBar;
        final TextView mRatingTextView;

        MovieViewHolder(View itemView) {
            super(itemView);
            Typeface txtFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_bold.ttf");
            mPosterImageView = itemView.findViewById(R.id.iv_poster_img);
            mMovieRatingBar = itemView.findViewById(R.id.rb_movie_rate);
            mRatingTextView = itemView.findViewById(R.id.tv_rating_txt);
            mTitleTextView = itemView.findViewById(R.id.tv_title_movie);
            mMovieRatingBar.setNumStars(5);
            mTitleTextView.setTypeface(txtFont);
            mRatingTextView.setTypeface(txtFont);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Movie movie = mMoviesArrayList.get(pos);
            mOnClickListener.onClick(movie);
        }


    }
}
