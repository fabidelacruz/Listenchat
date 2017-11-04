package edu.utn.listenchat.utils;

import android.database.Cursor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;

import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.MessageDirection;
import edu.utn.listenchat.model.MessageStatus;

import static edu.utn.listenchat.model.MessageDirection.INCOMING;
import static edu.utn.listenchat.utils.DateUtils.toDate;

public class CursorUtils {

    private CursorUtils() {

    }

    public static Multimap<String, Message> messagesByContact(Cursor cursor) {
        Multimap<String, Message> map = ArrayListMultimap.create();
        if (cursor.moveToFirst()) {
            do {
                Message message = toMessage(cursor);
                if (INCOMING.equals(message.getDirection())) {
                    map.put(message.getContact(), toMessage(cursor));
                }
            } while(cursor.moveToNext());
        }
        return map;
    }

    public static List<Integer> messageIds(Cursor cursor) {
        List<Integer> ids = Lists.newArrayList();

        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while(cursor.moveToNext());
        }
        return ids;
    }

    public static Message toMessage(Cursor cursor) {
        Message message = new Message();

        message.setId(Long.valueOf(cursor.getString(0)));
        message.setIntentId(cursor.getString(1));
        message.setContact(cursor.getString(2));
        message.setText(cursor.getString(3));
        message.setStatus(MessageStatus.valueOf(cursor.getString(4)));
        message.setDate(toDate(cursor.getString(5)));
        message.setDirection(MessageDirection.valueOf(cursor.getString(6)));

        return message;
    }

}
