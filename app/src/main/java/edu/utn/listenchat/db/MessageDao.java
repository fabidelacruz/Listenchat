package edu.utn.listenchat.db;

import android.content.Context;
import android.database.Cursor;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.PersistenceService;

import static com.google.common.collect.Lists.newArrayList;
import static edu.utn.listenchat.utils.DateUtils.toDate;
import static edu.utn.listenchat.utils.DateUtils.toStringUntilDay;
import static edu.utn.listenchat.utils.StringUtils.safeEquals;


public class MessageDao {

    private PersistenceService persistenceService;


    public List<Message> all() {
        Cursor cursor = this.persistenceService.getAllCursor();
        List<Message> messages = newArrayList();

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();

                message.setIntentId(cursor.getString(1));
                message.setName(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setLeido(cursor.getString(4));
                message.setReceivedDate(toDate(cursor.getString(5)));
                message.setDirection(cursor.getString(6));

                messages.add(message);
            } while(cursor.moveToNext());
        }
        return messages;
    }

    public List<Message> allFromContact(String contact) {
        List<Message> allFromContact = newArrayList();

        for (Message message : this.all()) {
            if (safeEquals(contact, message.getName())) {
                allFromContact.add(message);
            }
        }
        return allFromContact;
    }

    public Multimap<String, Message> massagesByDate(String contact) {
        Multimap<String, Message> messagesByDate = MultimapBuilder.treeKeys().linkedListValues().build();

        List<Message> messages = this.allFromContact(contact);
        for (Message message : messages) {
            messagesByDate.put(toStringUntilDay(message.getReceivedDate()), message);
        }
        return messagesByDate;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
