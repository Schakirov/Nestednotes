package com.example.nestednotes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HEADING = "heading";
    public static final String COLUMN_DETAILS = "details";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_HEADING + " TEXT, " +
                    COLUMN_DETAILS + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public long addNote(String heading, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADING, heading);
        values.put(COLUMN_DETAILS, details);

        long newRowId = -1;
        try {
            newRowId = db.insert(TABLE_NOTES, null, values);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return newRowId;
    }

    public Note getNoteById(long noteId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Note note = null;

        try {
            Cursor cursor = db.query(
                    TABLE_NOTES,
                    new String[]{COLUMN_ID, COLUMN_HEADING, COLUMN_DETAILS},
                    COLUMN_ID + " = ?",
                    new String[]{String.valueOf(noteId)},
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String heading = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADING));
                String details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS));

                note = new Note(id, heading, details);
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return note;
    }

    public boolean updateNote(long noteId, String heading, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HEADING, heading);
        values.put(COLUMN_DETAILS, details);

        int rowsAffected = -1;

        try {
            rowsAffected = db.update(TABLE_NOTES, values, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(noteId)});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return rowsAffected > 0;
    }

    public boolean deleteNoteById(long noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = -1;

        try {
            rowsDeleted = db.delete(TABLE_NOTES, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(noteId)});
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return rowsDeleted > 0;
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.query(
                    TABLE_NOTES,
                    new String[]{COLUMN_ID, COLUMN_HEADING, COLUMN_DETAILS},
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String heading = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADING));
                    String details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS));

                    notes.add(new Note(id, heading, details));
                } while (cursor.moveToNext());

                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        return notes;
    }

    // Note class representing a single note
    public static class Note {
        private long id;
        private String heading;
        private String details;

        public Note(long id, String heading, String details) {
            this.id = id;
            this.heading = heading;
            this.details = details;
        }

        public long getId() {
            return id;
        }

        public String getHeading() {
            return heading;
        }

        public String getDetails() {
            return details;
        }
    }
}
