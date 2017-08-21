package edu.utn.listenchat.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import edu.utn.listenchat.db.ListenchatDbHelper;
import edu.utn.listenchat.db.MessageContract.MessageEntry;
import edu.utn.listenchat.model.Message;

import static edu.utn.listenchat.db.MessageContract.MessageEntry.TABLE_NAME;

/**
 * Created by fabian on 20/08/17.
 */

public class PersistenceService {

    public void insert(Context context, Message model) {
        java.text.DateFormat formatter = DateFormat.getDateFormat(context);

        ContentValues values = new ContentValues();
        values.put(MessageEntry.COLUMN_NAME_CONTACT, model.getName());
        values.put(MessageEntry.COLUMN_NAME_CONTENT, model.getMessage());
        values.put(MessageEntry.COLUMN_NAME_DATE, formatter.format(model.getReceivedDate()));

        SQLiteDatabase writableDatabase = new ListenchatDbHelper(context).getWritableDatabase();
        writableDatabase.insert(TABLE_NAME, null, values);
        writableDatabase.close();
    }

    public Cursor getAllCursor(Context context) {
        return new ListenchatDbHelper(context).getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
