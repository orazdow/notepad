package com.clemm.notepad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;
import java.util.List;

class NoteRepository extends SQLiteOpenHelper {

    private static final String[] NOTE_COLUMNS = {"rowid", "date", "title", "content", "password"};

    public NoteRepository(Context context) {
        super(context, "NotepadDB", (SQLiteDatabase.CursorFactory) null, 3);
    }

    public long countNotes() {
        long count;
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor cursorRawQuery = writableDatabase.rawQuery("SELECT count(*) FROM notes", null);
        if (cursorRawQuery.moveToFirst()) {
            do {
                count = cursorRawQuery.getLong(0);
            } while (cursorRawQuery.moveToNext());
        } else {
            count = 0;
        }
        cursorRawQuery.close();
        writableDatabase.close();
        return count;
    }

    public List<Note> getNotes(int sortMode) {
        String orderByColumn;
        LinkedList<Note> notes = new LinkedList<>();
        String sortDirection = "DESC";
        if (sortMode != 0) {
            orderByColumn = sortMode != 2 ? "rowid" : "date";
        } else {
            orderByColumn = "title";
            sortDirection = "ASC";
        }
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor cursorRawQuery = writableDatabase.rawQuery("SELECT rowid,* FROM notes ORDER BY " + orderByColumn + " " + sortDirection, null);
        if (cursorRawQuery.moveToFirst()) {
            do {
                notes.add(new Note(cursorRawQuery.getLong(0), cursorRawQuery.getString(1), cursorRawQuery.getString(2), cursorRawQuery.getString(3), cursorRawQuery.getString(4)));
            } while (cursorRawQuery.moveToNext());
        }
        cursorRawQuery.close();
        writableDatabase.close();
        return notes;
    }

    public void deleteNote(long id) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        readableDatabase.delete("notes", "rowid = ?", new String[]{String.valueOf(id)});
        readableDatabase.close();
    }

    public void insertNote(Note note) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", note.getDate());
        contentValues.put("title", note.getTitle());
        contentValues.put("content", note.getContent());
        contentValues.put("password", note.getPassword());
        writableDatabase.insert("notes", null, contentValues);
        writableDatabase.close();
    }

    public Note getNote(long id) {
        Note note;
        SQLiteDatabase readableDatabase = getReadableDatabase();
        Cursor cursorQuery = readableDatabase.query("notes", NOTE_COLUMNS, "rowid = ?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursorQuery != null) {
            cursorQuery.moveToFirst();
            note = new Note(cursorQuery.getLong(0), cursorQuery.getString(1), cursorQuery.getString(2), cursorQuery.getString(3), cursorQuery.getString(4));
            cursorQuery.close();
        } else {
            note = null;
        }
        readableDatabase.close();
        return note;
    }

    public void updateNote(Note note) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", note.getDate());
        contentValues.put("title", note.getTitle());
        contentValues.put("content", note.getContent());
        contentValues.put("password", note.getPassword());
        readableDatabase.update("notes", contentValues, "rowid = ?", new String[]{String.valueOf(note.getId())});
        readableDatabase.close();
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("CREATE TABLE notes (date TEXT, title TEXT, content TEXT, password TEXT)");
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        if (i == 2 && i2 == 3) {
            sQLiteDatabase.execSQL("ALTER TABLE notes ADD COLUMN password TEXT;");
        }
    }
}



