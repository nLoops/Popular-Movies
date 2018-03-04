package com.example.android.moviestation.Utils;

import android.util.Log;

import com.example.android.moviestation.ContentClasses.Movie;
import com.example.android.moviestation.ContentClasses.MovieVideos;
import com.example.android.moviestation.ContentClasses.ReviewModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * This class will contains helper Methods that
 * Convert String Url to Object Url
 * Pass the Url to HttpConnection to get response from server
 * Convert the Stream to String.
 * Parse String response using JSON to convert it to good formatted data.
 */

public class NetworkUtils {

    /**
     * Class Tag That we will use into LOG.
     */
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /////////////////////////////////// FOR MOVIE DB URLS /////////////////////////

    /**
     * Public Base URL (MovieDB)
     */
    public static final String BASE_URL = "https://api.themoviedb.org/3";
    /**
     * Popular Movies Search Key Word
     */
    public static final String POPULAR_ENDPOINT = "/movie/popular";
    /**
     * High Rated Movies Search Key Word
     */
    public static final String HIGHRATED_ENDPOINT = "/movie/top_rated";

    /**
     * Default Search Language
     */
    public static final String DEFAULT_LANGUAGE = "en_US";

    /**
     * Default Query response Pages.
     */
    public static final String DEFAULT_PAGES = "1";

    /**
     * Uri PARAMS
     * API_KEY
     * DEFAULT LANGUAGE
     * DEFAULT PAGES.
     * USE THEM TO BUILD UP URI QUERY.
     */
    public static final String PARAM_API_KEY = "api_key";
    public static final String PARAM_DEF_LANGUAGE = "language";
    public static final String PARAM_DEF_PAGES = "page";


    /**
     * Base Url for Movie Video Url
     */
    public static final String BASE_VIDEO_URL = "https://api.themoviedb.org/3/movie/";

    /**
     * Video filter Path URL
     */
    public static final String VIDEO_URL_PATH = "/videos";

    /**
     * Reviews filter Path URL
     */

    public static final String REVIEW_URL_PATH = "/reviews";

    /////////////////////////////////////////// END OF MOVIE DB Vars ///////////////////////

    /**
     * Server Request Method String
     */
    private static final String CONNECTION_METHOD = "GET";
    /**
     * Server ReadTimeOut (In Milisecods)
     */
    private static final int CONNECTION_READTIME = 10000;
    /**
     * Server Connect TimeOut (In Milisecods)
     */
    private static final int CONNECTION_CONNECTTIME = 15000;

    /**
     * JSON Objects & Array Names For Movie Data
     */
    private static final String JSON_RESULTS_ARRAY = "results";
    private static final String JSON_RATE = "vote_average";
    private static final String JSON_TITLE = "title";
    private static final String JSON_POSTER = "poster_path";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_MOVIE_ID = "id";

    /**
     * JSON Objects & Arrays Names for Movie Video
     */
    private static final String JSON_VIDEO_RESULT_ARRAY = "results";
    private static final String JSON_VIDEO_KEY = "key";
    private static final String JSON_VIDEO_NAME = "name";
    private static final String JSON_VIDEO_SITE = "site";
    private static final String JSON_VIDEO_TYPE = "type";

    /**
     * JSON Objects & Arrays Names For Movie Reviews.
     */

    private static final String JSON_REVIEW_RESULT_ARRAY = "results";
    private static final String JSON_REVIEW_ID = "id";
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";
    private static final String JSON_REVIEW_URL = "url";

    public static ArrayList<Movie> executeMovieDataOpr(String stringUrl) {
        URL response = createURL(stringUrl);
        String result = makeHttpsConnection(response);
        return fetchJSONData(result);
    }

    public static ArrayList<MovieVideos> executeMovieVideosOpr(String stringUrl) {
        URL response = createURL(stringUrl);
        String result = makeHttpsConnection(response);
        return fetchJSONVideos(result);
    }

    public static ArrayList<ReviewModel> executeMovieReviewsOpr(String stringUrl) {
        URL response = createURL(stringUrl);
        String result = makeHttpsConnection(response);
        return fetchJSONReviews(result);
    }


    /**
     * Helper Method that accept urlString and Return URL Object
     *
     * @param urlString
     * @return URL Object.
     */
    private static URL createURL(String urlString) {
        // if passed string is empty we return null
        if (urlString == null || urlString.equals("")) {
            return null;
        }

        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.d(TAG, "Cannot Create URL Object because of " + e);
        }

        return url;

    }

    /**
     * @param url
     * @return String of Server Response.
     */
    private static String makeHttpsConnection(URL url) {
        // String that will hold JSON response.
        String response = "";
        // if passed URL is empty we return empty result.
        if (url == null) {
            return response;
        }
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(CONNECTION_METHOD);
            urlConnection.setReadTimeout(CONNECTION_READTIME);
            urlConnection.setConnectTimeout(CONNECTION_CONNECTTIME);
            urlConnection.connect();
            /*
               if we got a successful connection then we will read the data.
               Code 200 = Successful Connection.
               Code 401 = unvaild API_KEY.
               Code 404 = Page trying to access not found.
             */

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                response = readFromStream(inputStream);
            } else {
                Log.d(TAG, "Unsuccessful connection " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // if the InputStream or HttpConnection Still Here We Close them
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.d(TAG, "Cannot Close The Stream Because of " + e);
                }
            }

        }

        return response;
    }


    /**
     * Accepts the Stream From Server as Param
     *
     * @param in
     * @return readable string using InputStreamReader , BufferReader.
     */
    private static String readFromStream(InputStream in) {

        StringBuilder builder = new StringBuilder();

        if (in != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            try {

                String line = reader.readLine();
                while (line != null) {
                    builder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(TAG, "Cannot read from stream because of " + e);
            }
        }

        return builder.toString();

    }

    /**
     * Using String ServerResponse to parse JSON and return ArrayList of Movies.
     *
     * @param serverResponse
     * @return
     */
    private static ArrayList<Movie> fetchJSONData(String serverResponse) {
        ArrayList<Movie> moviesList = new ArrayList<>();
        if (serverResponse != null && !serverResponse.equals("")) {

            try {
                JSONObject rootObject = new JSONObject(serverResponse);
                if (rootObject.has(JSON_RESULTS_ARRAY)) {
                    JSONArray resultsArray = rootObject.getJSONArray(JSON_RESULTS_ARRAY);
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject currentMovie = resultsArray.getJSONObject(i);
                        double movieRate = currentMovie.getDouble(JSON_RATE);
                        String movieTitle = currentMovie.getString(JSON_TITLE);
                        String moviePoster = currentMovie.getString(JSON_POSTER);
                        String movieOverView = currentMovie.getString(JSON_OVERVIEW);
                        String movieReleaseDate = currentMovie.getString(JSON_RELEASE_DATE);
                        int movieID = currentMovie.getInt(JSON_MOVIE_ID);
                        moviesList.add(new Movie(movieTitle, moviePoster, movieOverView, movieReleaseDate, movieRate, movieID));
                    }

                } else {
                    Log.d(TAG, "There's no Result Array Attached to this JSON response");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Cannot Parse Server Response Because of " + e);
            }

        }

        return moviesList;
    }

    private static ArrayList<MovieVideos> fetchJSONVideos(String serverResponse) {
        ArrayList<MovieVideos> videosList = new ArrayList<>();
        if (serverResponse != null && !serverResponse.equals("")) {

            try {
                JSONObject root = new JSONObject(serverResponse);
                if (root.has(JSON_VIDEO_RESULT_ARRAY)) {
                    JSONArray resultsArray = root.getJSONArray(JSON_VIDEO_RESULT_ARRAY);
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject currentObject = resultsArray.getJSONObject(i);
                        String vidKey = currentObject.getString(JSON_VIDEO_KEY);
                        String vidName = currentObject.getString(JSON_VIDEO_NAME);
                        String vidSite = currentObject.getString(JSON_VIDEO_SITE);
                        String vidType = currentObject.getString(JSON_VIDEO_TYPE);
                        MovieVideos movieVideos = new MovieVideos(vidKey, vidName,
                                vidSite, vidType);
                        videosList.add(movieVideos);
                    }

                } else {
                    Log.d(TAG, "There's no Result Array Attached to this JSON response");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Cannot Parse Server Response Because of " + e);
            }
        }
        return videosList;
    }

    private static ArrayList<ReviewModel> fetchJSONReviews(String serverResponse) {
        ArrayList<ReviewModel> reviewList = new ArrayList<>();
        if (serverResponse != null && !serverResponse.equals("")) {

            try {
                JSONObject root = new JSONObject(serverResponse);
                if (root.has(JSON_REVIEW_RESULT_ARRAY)) {
                    JSONArray resultsArray = root.getJSONArray(JSON_REVIEW_RESULT_ARRAY);
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject currentObject = resultsArray.getJSONObject(i);
                        String reviewID = currentObject.getString(JSON_REVIEW_ID);
                        String reviewAuthor = currentObject.getString(JSON_REVIEW_AUTHOR);
                        String reviewContent = currentObject.getString(JSON_REVIEW_CONTENT);
                        String reviewUrl = currentObject.getString(JSON_REVIEW_URL);
                        ReviewModel reviewModel = new ReviewModel(reviewID, reviewAuthor,
                                reviewContent, reviewUrl);
                        reviewList.add(reviewModel);
                    }

                } else {
                    Log.d(TAG, "There's no Result Array Attached to this JSON response");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Cannot Parse Server Response Because of " + e);
            }
        }
        return reviewList;
    }


}
