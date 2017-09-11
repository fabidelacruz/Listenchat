package edu.utn.listenchat.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import edu.utn.listenchat.db.ListenchatDbHelper;
import edu.utn.listenchat.db.MessageContract.MessageEntry;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.utils.DateUtils;

import static edu.utn.listenchat.db.MessageContract.MessageEntry.TABLE_NAME;

/**
 * Created by fabian on 20/08/17.
 */

public class PersistenceService {

    public void insert(Context context, Message model) {
        ContentValues values = new ContentValues();
        values.put(MessageEntry.COLUMN_NAME_INTENT_ID, model.getIntentId());
        values.put(MessageEntry.COLUMN_NAME_CONTACT, model.getName());
        values.put(MessageEntry.COLUMN_NAME_CONTENT, model.getMessage());
        values.put(MessageEntry.COLUMN_NAME_LEIDO, model.getLeido());
        values.put(MessageEntry.COLUMN_NAME_DATE, DateUtils.toStringUntilSecond(model.getReceivedDate()));

        SQLiteDatabase writableDatabase = new ListenchatDbHelper(context).getWritableDatabase();
        writableDatabase.insert(TABLE_NAME, null, values);
        writableDatabase.close();
    }

    public void insert(Context context, List<Message> messages) {
        for (Message message : messages) {
            this.insert(context, message);
        }
    }

    public Cursor getAllCursor(Context context) {
        return new ListenchatDbHelper(context).getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public void markNotified(List<Integer> integers, Context context) {
        new ListenchatDbHelper(context).getWritableDatabase().rawQuery("UPDATE " + TABLE_NAME + "SET leido = 'Y' WHERE _id IN (" + StringUtils.join(integers, ",") + ")", null);
    }
}
