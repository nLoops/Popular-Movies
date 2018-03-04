package com.example.android.moviestation.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.moviestation.data.MovieSqlContract.FavoriteEntry;

/**
 * This Class act like a layer between data (SqlDatabase , files .. etc).
 * and the UI
 */

public class MovieContentProvider extends ContentProvider {

    private static final int FAVORITES_CODE = 100;
    private static final int FAVORITES_ID_CODE = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieSqlContract.CONTENT_AUTHORITY,
                MovieSqlContract.FAVORITES_PATH, FAVORITES_CODE);
        matcher.addURI(MovieSqlContract.CONTENT_AUTHORITY,
                MovieSqlContract.FAVORITES_PATH + "/#", FAVORITES_ID_CODE);
        return matcher;
    }


    private MovieDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MovieDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor resultCursor = null;

        switch (match) {
            case FAVORITES_CODE:
                resultCursor = db.query(FavoriteEntry.TABLE_NAME, null, null,
                        null, null, null, null);
                break;
            case FAVORITES_ID_CODE:
                resultCursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (resultCursor != null) {
            resultCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return resultCursor;
    }


    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long resultID;
        switch (match) {
            case FAVORITES_CODE:
                resultID = db.insert(FavoriteEntry.TABLE_NAME, null,
                        contentValues);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (resultID > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return ContentUris.withAppendedId(uri, resultID);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int deletedRaws = 0;
        switch (match) {
            case FAVORITES_ID_CODE:
                String args = uri.getLastPathSegment();
                String mSelection = FavoriteEntry._ID + "=?";
                String[] mSelectionArgs = new String[]{args};
                deletedRaws = db.delete(FavoriteEntry.TABLE_NAME, mSelection, mSelectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (deletedRaws > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return deletedRaws;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        throw new RuntimeException(" We don't need update method into Favorite Table");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException(" We don't need getType method into Favorite Table");
    }
}
