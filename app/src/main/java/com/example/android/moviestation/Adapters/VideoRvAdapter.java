package com.example.android.moviestation.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviestation.ContentClasses.MovieVideos;
import com.example.android.moviestation.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Recycle View Adapter.
 */

public class VideoRvAdapter extends RecyclerView.Adapter<VideoRvAdapter.VideoViewHolder> {



    public interface VideoOnClickListener {
        void onClick(String videoUrl);
    }

    /**
     * Ref of Listener InterFace
     */
    private final VideoOnClickListener mClickListener;

    /**
     * Passed Context.
     */
    private final Context mContext;

    /**
     * private ArrayList of Videos that will set onBackground Thread
     */
    private ArrayList<MovieVideos> mMovieVideos;

    public VideoRvAdapter(Context context, VideoOnClickListener listener) {
        mContext = context;
        mClickListener = listener;
        mMovieVideos = new ArrayList<>();

    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutID = R.layout.single_video_layout;
        boolean shouldAttachedToParent = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, viewGroup, shouldAttachedToParent);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String imgUrl = mMovieVideos.get(position).getVideoImagePath();
        String vidName = mMovieVideos.get(position).getVideoName();
        Picasso.with(mContext)
                .load(imgUrl)
                .into(holder.mVideoImageView);
        holder.mNameTextView.setText(vidName);

    }

    /**
     * Helper Method that clear the data set and notify the changes.
     */
    public void clear() {
        mMovieVideos.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mMovieVideos) return 0;
        return mMovieVideos.size();
    }

    public void setMovieVideos(ArrayList<MovieVideos> listVideos) {
        this.mMovieVideos = listVideos;
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        final ImageView mVideoImageView;
        final TextView mNameTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoImageView = itemView.findViewById(R.id.iv_video_img);
            mNameTextView = itemView.findViewById(R.id.tv_video_name);
            Typeface fontType = Typeface.createFromAsset(mContext.getAssets(), "fonts/font_bold.ttf");
            mNameTextView.setTypeface(fontType);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = getAdapterPosition();
            String videoPath = mMovieVideos.get(id).getVideoPath();
            mClickListener.onClick(videoPath);
        }
    }
}
