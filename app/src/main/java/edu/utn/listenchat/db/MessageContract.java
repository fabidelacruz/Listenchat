package edu.utn.listenchat.db;

import android.provider.BaseColumns;

import java.util.Date;

public final class MessageContract {

    private MessageContract() {}

    /* Inner class that defines the table contents */
    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_NAME_CONTACT = "contact";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_DATE = "date";
    }



}