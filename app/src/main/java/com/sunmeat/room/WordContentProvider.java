package com.sunmeat.room;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WordContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.sunmeat.room.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/words");

    private static final int WORDS = 1;
    private static final int WORD_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, "words", WORDS);
        uriMatcher.addURI(AUTHORITY, "words/#", WORD_ID);
    }

    private WordDao wordDao;

    @Override
    public boolean onCreate() {
        WordRoomDatabase database = WordRoomDatabase.getDatabase(getContext());
        wordDao = database.wordDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        if (uriMatcher.match(uri) == WORDS) {
            cursor = wordDao.getAllWordsCursor();
        } else {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == WORDS) {
            return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".words";
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) == WORDS) {
            String wordText = values.getAsString("word");
            if (wordText != null) {
                Word word = new Word(wordText);
                wordDao.insert(word);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(CONTENT_URI, wordText.hashCode());
            }
        }
        throw new IllegalArgumentException("Invalid URI: " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == WORDS) {
            wordDao.deleteAll();
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        throw new IllegalArgumentException("Invalid URI: " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("Update operation is not supported");
    }
}