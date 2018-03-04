package com.example.android.moviestation.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviestation.ContentClasses.ReviewModel;
import com.example.android.moviestation.R;

import java.util.ArrayList;

/**
 * Adapter of Movie Review Data RecycleView.
 */

public class ReviewRvAdapter extends RecyclerView.Adapter<ReviewRvAdapter.ReviewViewHolder> {

    private ArrayList<ReviewModel> mReviewArrayList;

    public ReviewRvAdapter(Context context) {
        mReviewArrayList = new ArrayList<>();
    }


    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutID = R.layout.single_review_item;
        Context context = parent.getContext();
        boolean shouldAttachedToParent = false;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutID, parent, shouldAttachedToParent);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        String author = mReviewArrayList.get(position).getReviewAuthor();
        String content = mReviewArrayList.get(position).getReviewContent();
        holder.tvAuthor.setText(author);
        holder.tvContent.setText(content);
    }

    @Override
    public int getItemCount() {
        if (null == mReviewArrayList) return 0;
        return mReviewArrayList.size();
    }

    public void setReviewArrayList(ArrayList<ReviewModel> listReviews) {
        this.mReviewArrayList = listReviews;
        notifyDataSetChanged();
    }

    public void clear() {
        mReviewArrayList.clear();
        notifyDataSetChanged();
    }


    class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAuthor;
        private final TextView tvContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.txt_review_head);
            tvContent = (TextView) itemView.findViewById(R.id.txt_review_body);
        }
    }
}
