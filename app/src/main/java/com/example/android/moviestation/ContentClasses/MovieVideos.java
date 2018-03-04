package com.example.android.moviestation.ContentClasses;

/**
 * Class That Holding MovieVideo ( Trailer , Teaser , Clips ).
 * by passing to query the Movie Id.
 */

public class MovieVideos {

    /**
     * Log Tag
     */
    private static final String TAG = MovieVideos.class.getSimpleName();

    /**
     * Base YouTube Thumbnail URL
     */

    private static final String BASE_YOUTUBE_IMG_URL = "http://img.youtube.com/vi/";

    /**
     * YouTube Image Resolution
     */
    private static final String YOUTUBE_HIGH_RESOLUTION = "/maxresdefault.jpg";

    /**
     * YouTube Image Resolution
     */
    private static final String YOUTUBE_LOW_RESOLUTION = "/mqdefault.jpg";

    /**
     * YouTube Image Resolution
     */
    private static final String YOUTUBE_MED_RESOLUTION = "/0.jpg";

    /**
     * Base Url for Movie Videos YOUTUBE
     */
    private static final String BASE_YOUTUBE_URL = "http://www.youtube.com/watch?v=";


    private final String mVideoKey;
    private final String mVideoName;
    private final String mVideoSite;
    private final String mVideoType;

    public MovieVideos(String videoKey, String videoName, String videoSite, String videoType) {
        this.mVideoKey = videoKey;
        this.mVideoName = videoName;
        this.mVideoSite = videoSite;
        this.mVideoType = videoType;
    }

    public String getVideoKey() {
        return mVideoKey;
    }

    public String getVideoName() {
        return mVideoName;
    }

    public String getVideoSite() {
        return mVideoSite;
    }

    public String getVideoType() {
        return mVideoType;
    }

    /**
     * Return a full url of image we can display using Picasso
     */
    public String getVideoImagePath() {
        return BASE_YOUTUBE_IMG_URL + mVideoKey + YOUTUBE_MED_RESOLUTION;
    }

    /**
     * Helper Method that generate the video url to allow
     * to pass within intent to play into youtube
     */
    public String getVideoPath() {
        return BASE_YOUTUBE_URL + mVideoKey;
    }

    @Override
    public String toString() {
        return "MovieVideos{" +
                "mVideoKey='" + mVideoKey + '\'' +
                ", mVideoName='" + mVideoName + '\'' +
                ", mVideoSite='" + mVideoSite + '\'' +
                ", mVideoType='" + mVideoType + '\'' +
                '}';
    }
}
