package com.example.contentproviderexample;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {

    static final String PROVIDER = "com.example.contentproviderexample.MyContentProvider";

    static final String _ID = "_id";
    static final String URL = "content://" + PROVIDER + "/students";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String FIRSTNAME = "firstName";
    static final String LASTNAME = "lastName";
    static final String GENDER = "gender";
    static final String PLACE = "place";
    static final String SCHOLARSHIP = "scholarship";


    private static HashMap<String, String> STUDENTS_PROJECTION_MAP;
    static final int STUDENTS = 1;
    static final int STUDENTS_ID = 2;

    static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "students", STUDENTS);
        uriMatcher.addURI(PROVIDER, "students/#", STUDENTS_ID);
    }

    private SQLiteDatabase database;
    static final String DATABASE_NAME = "StudentInfo";
    static final String STUDENTS_TABLE_NAME = "Students";
    static final int DATABASE_VER = 1;
    static final String CREATE_DB_TABLE = " CREATE TABLE " + STUDENTS_TABLE_NAME +
            " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            " firstName TEXT NOT NULL, " +
            " lastName TEXT NOT NULL, " +
            " gender TEXT NOT NULL, " +
            " place TEXT NOT NULL, " +
            " scholarship TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper{

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VER);
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }



    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return (database == null)? false:true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(STUDENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case STUDENTS:
                queryBuilder.setProjectionMap(STUDENTS_PROJECTION_MAP);
                break;

            case STUDENTS_ID:
                queryBuilder.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (s1 == null || s1 == ""){
            s1 = FIRSTNAME;
        }

        Cursor cursor = queryBuilder.query(database, strings, s, strings1, null, null, s1);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)){
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd.example.students";

            case STUDENTS_ID:
                return "vnd.android.cursor.item/vnd.example.students";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowId = database.insert( STUDENTS_TABLE_NAME, "", contentValues);
        if (rowId > 0) {
            Uri uri1 = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(uri1, null);
            return uri1;
        }

        throw new SQLException("Failed to add " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case STUDENTS:
                count = database.delete(STUDENTS_TABLE_NAME, s, strings);
                break;

            case STUDENTS_ID:
                String id = uri.getPathSegments().get(1);
                count = database.delete( STUDENTS_TABLE_NAME, _ID + " = " + id +
                        (!TextUtils.isEmpty(s) ? " AND (" + s + ')' : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case STUDENTS:
                count = database.update(STUDENTS_TABLE_NAME, contentValues, s, strings);
                break;

            case STUDENTS_ID:
                count = database.update(STUDENTS_TABLE_NAME, contentValues,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(s) ? " AND (" + s + ')' : ""), strings);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
