package com.example.wheelfortune;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "spin_history.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SPIN_HISTORY = "spin_history";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE_TIME = "date_time";
    public static final String COLUMN_PRIZE = "prize";

    private static final String CREATE_TABLE_SPIN_HISTORY =
            "CREATE TABLE " + TABLE_SPIN_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DATE_TIME + " TEXT NOT NULL, " +
                    COLUMN_PRIZE + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper", "DatabaseHelper constructed");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SPIN_HISTORY);
        Log.d("DatabaseHelper", "Table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPIN_HISTORY);
        onCreate(db);
    }

    public long addSpin(String prize) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String now = dateFormat.format(new Date());

        values.put(COLUMN_DATE_TIME, now);
        values.put(COLUMN_PRIZE, prize);

        long newRowId = db.insert(TABLE_SPIN_HISTORY, null, values);
        db.close();
        Log.d("DatabaseHelper", "Spin added to history. Row ID: " + newRowId + ", Prize: " + prize);
        return newRowId;
    }

    public List<SpinHistoryItem> getAllSpins() {
        List<SpinHistoryItem> spinHistory = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_DATE_TIME,
                COLUMN_PRIZE
        };

        String sortOrder = COLUMN_DATE_TIME + " DESC";

        Cursor cursor = db.query(
                TABLE_SPIN_HISTORY,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME));
                String prize = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIZE));
                SpinHistoryItem spin = new SpinHistoryItem(dateTime, prize);
                spinHistory.add(spin);
                Log.d("DatabaseHelper", "Retrieved spin: " + spin);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return spinHistory;
    }
}