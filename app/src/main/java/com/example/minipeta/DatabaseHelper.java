package com.example.minipeta;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "minipeta.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_LOST_ITEMS = "lost_items";
    public static final String COL_ID = "id";
    public static final String COL_PRODUCT_NAME = "product_name";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_IMAGE_URI = "image_uri";
    public static final String COL_STATUS = "status";
    public static final String COL_DATE_REPORTED = "date_reported";

    private static final String CREATE_LOST_ITEMS =
            "CREATE TABLE " + TABLE_LOST_ITEMS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PRODUCT_NAME + " TEXT NOT NULL, " +
                    COL_DESCRIPTION + " TEXT, " +
                    COL_IMAGE_URI + " TEXT, " +
                    COL_STATUS + " TEXT NOT NULL DEFAULT 'REPORTED', " +
                    COL_DATE_REPORTED + " TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOST_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add migration steps here if the database structure changes.
    }

    public long insertLostItem(String productName, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRODUCT_NAME, productName.trim());
        values.put(COL_DESCRIPTION, description == null ? "" : description.trim());
        values.put(COL_STATUS, "REPORTED");
        return db.insert(TABLE_LOST_ITEMS, null, values);
    }

    public Cursor getAllLostItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_LOST_ITEMS,
                null,
                null,
                null,
                null,
                null,
                COL_DATE_REPORTED + " DESC"
        );
    }

    public int updateLostItemStatus(long id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        return db.update(
                TABLE_LOST_ITEMS,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteLostItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                TABLE_LOST_ITEMS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }
}