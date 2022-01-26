package com.example.fall_detection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class HistoryDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FallHistory";
    private static final String TABLE_FALLS = "Falls";
    private static final String KEY_ID = "id";
    private static final String KEY_TIME = "time";
    private static final String KEY_DATE = "date";

    public HistoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_FALLS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_DATE + " TEXT,"
                + KEY_TIME + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FALLS);

        // Create tables again
        onCreate(db);
    }

    // code to add the new fall history
    public void addFall(DetectedFall detectedFall) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, detectedFall.getCurrent_date()); // date
        values.put(KEY_TIME, detectedFall.getCurrent_time()); // time

        // Inserting Row
        db.insert(TABLE_FALLS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public List<DetectedFall> getAllFalls() {
        List<DetectedFall> fallsList = new ArrayList<DetectedFall>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FALLS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        DetectedFall detectedFall;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                detectedFall = new DetectedFall(cursor.getString(1), cursor.getString(2));
//                detectedFall.setCurrent_date(cursor.getString(1)); // date
//                detectedFall.setCurrent_time(cursor.getString(2)); // time
                // Adding contact to list
                fallsList.add(detectedFall);
            } while (cursor.moveToNext());
        }

        // return contact list
        return fallsList;
    }
}
