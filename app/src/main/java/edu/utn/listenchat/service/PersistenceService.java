package edu.utn.listenchat.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.collect.Lists;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.db.ListenchatDbHelper;
import edu.utn.listenchat.db.MessageContract.MessageEntry;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.utils.DateUtils;

import static edu.utn.listenchat.db.MessageContract.MessageEntry.COLUMN_NAME_CONTACT;
import static edu.utn.listenchat.db.MessageContract.MessageEntry.COLUMN_NAME_STATUS;
import static edu.utn.listenchat.db.MessageContract.MessageEntry.TABLE_NAME;


public class PersistenceService {

    private MainActivity mainActivity;

    public void insert(Context context, Message message) {
        ContentValues values = new ContentValues();
        values.put(MessageEntry.COLUMN_NAME_INTENT_ID, message.getIntentId());
        values.put(MessageEntry.COLUMN_NAME_CONTACT, message.getName());
        values.put(MessageEntry.COLUMN_NAME_CONTENT, message.getMessage());
        values.put(MessageEntry.COLUMN_NAME_STATUS, message.getStatus().name());
        values.put(MessageEntry.COLUMN_NAME_DATE, DateUtils.toStringUntilSecond(message.getDate()));
        values.put(MessageEntry.COLUMN_NAME_DIRECTION, message.getDirection().name());

        SQLiteDatabase writableDatabase = new ListenchatDbHelper(context).getWritableDatabase();
        writableDatabase.insert(TABLE_NAME, null, values);
        writableDatabase.close();
    }

    public void insert(List<Message> messages) {
        for (Message message : messages) {
            this.insert(this.mainActivity, message);
        }
    }

    public Cursor getAllCursor() {
        return new ListenchatDbHelper(this.mainActivity).getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public Cursor getNewsCursor() {
        return new ListenchatDbHelper(this.mainActivity).getWritableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_STATUS + " <> 'ARCHIVED'", null);
    }

    public List<String> getContacts() {
        List<String> contacts = Lists.newArrayList();

        Cursor cursor = new ListenchatDbHelper(this.mainActivity).getWritableDatabase().rawQuery("SELECT distinct + " + COLUMN_NAME_CONTACT + " FROM " + TABLE_NAME, null);

        if (cursor.getCount() > 0) {
            cursor.moveToNext();

            do {
                contacts.add(cursor.getString(0));
            }while(cursor.moveToNext());
        }

        cursor.close();

        return contacts;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
