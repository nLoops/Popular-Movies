package com.example.android.moviestation.ContentClasses;

/**
 * This Class will have the data comes from Server regarding to
 * review content.
 */

public class ReviewModel {
    private final String mReviewID;
    private final String mReviewAuthor;
    private final String mReviewContent;
    private final String mReviewUrl;

    public ReviewModel(String reviewID, String reviewAuthor, String reviewContent, String reviewUrl) {
        this.mReviewID = reviewID;
        this.mReviewAuthor = reviewAuthor;
        this.mReviewContent = reviewContent;
        this.mReviewUrl = reviewUrl;
    }

    public String getReviewID() {
        return mReviewID;
    }

    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

    public String getReviewUrl() {
        return mReviewUrl;
    }

    @Override
    public String toString() {
        return "ReviewModel{" +
                "mReviewID='" + mReviewID + '\'' +
                ", mReviewAuthor='" + mReviewAuthor + '\'' +
                ", mReviewContent='" + mReviewContent + '\'' +
                ", mReviewUrl='" + mReviewUrl + '\'' +
                '}';
    }
}
