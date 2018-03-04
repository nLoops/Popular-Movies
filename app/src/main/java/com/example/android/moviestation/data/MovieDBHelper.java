package com.example.android.moviestation.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.moviestation.data.MovieSqlContract.FavoriteEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * This class will be the responsible of Creating DB if not Existing
 * OnUpgrade or Edit into DB
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDBHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "favorites.db";
    public static final int DATABASE_VERSION = 1;

    private static final String CREATE_ENTRIES_TABLES_SQL = "CREATE TABLE " +
            FavoriteEntry.TABLE_NAME + " ( " +
            FavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FavoriteEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
            FavoriteEntry.MOVIE_POSTER + " TEXT NOT NULL, " +
            FavoriteEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, " +
            FavoriteEntry.MOVIE_DATE + " TEXT NOT NULL,  " +
            FavoriteEntry.MOVIE_RATE + " REAL NOT NULL, " +
            FavoriteEntry.MOVIE_ID + " INTEGER NOT NULL );";

    /**
     * Declare local context to get Constructor Class Context.
     */
    private final Context context;


    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ENTRIES_TABLES_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        // You will not need to modify this unless you need to do some android specific things.
        // When upgrading the database, all you need to do is add a file to the assets folder and name it:
        // from_1_to_2.sql with the version that you are upgrading to as the last version.
        try {

            for (int i = oldVer; i < newVer; i++) {
                // Looking if there's any new added files with new SQL scripts.
                String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
                Log.d(LOG_TAG, "Looking for migrationFile:  " + migrationName);
                readAndExcuteSQlScriptDB(db, context, migrationName);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "cannot upgrade the Database because of " + e);
        }
    }

    /**
     * The Main void Job is to get the file name that passed from OnUpgrade Method and try to open it
     * and get the data using inputStream and BufferReader
     * also we will pass the BufferReader to another HelperMethod excuteSQLScripts.
     *
     * @param database
     * @param context
     * @param fileName
     */
    private void readAndExcuteSQlScriptDB(SQLiteDatabase database, Context context, String fileName) {
        // check if the passed file is empty
        // if true we return early.
        if (TextUtils.isEmpty(fileName)) {
            Log.d(LOG_TAG, "File name is empty: " + fileName);
            return;
        }

        Log.e(LOG_TAG, "Script Found and Start Upgrading");
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = null;
        try {

            InputStream in = assetManager.open(fileName);
            InputStreamReader inReader = new InputStreamReader(in);
            reader = new BufferedReader(inReader);
            excuteSQlScript(database, reader);

        } catch (IOException e) {

            Log.e(LOG_TAG, "IOException: " + e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "BufferReader IOException: " + e);
                }
            }
        }

    }

    /**
     * Helper Method will get the DB , BufferReader and try to read the data into String using StringBuilder
     * if we got full Statement we will execute it and continue in our loop.
     *
     * @param database
     * @param reader
     * @throws IOException
     */
    private void excuteSQlScript(SQLiteDatabase database, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                database.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }
}
