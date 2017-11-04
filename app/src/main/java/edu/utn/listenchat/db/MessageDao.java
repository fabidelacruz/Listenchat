package edu.utn.listenchat.db;

import android.database.Cursor;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.List;

import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.MessageDirection;
import edu.utn.listenchat.model.MessageStatus;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.utils.CursorUtils;

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
                messages.add(CursorUtils.toMessage(cursor));
            } while(cursor.moveToNext());
        }
        return messages;
    }

    public List<Message> allFromContact(String contact) {
        List<Message> allFromContact = newArrayList();

        for (Message message : this.all()) {
            if (safeEquals(contact, message.getContact())) {
                allFromContact.add(message);
            }
        }
        return allFromContact;
    }

    public Multimap<String, Message> massagesByDate(String contact) {
        Multimap<String, Message> messagesByDate = MultimapBuilder.treeKeys().linkedListValues().build();

        List<Message> messages = this.allFromContact(contact);
        for (Message message : messages) {
            messagesByDate.put(toStringUntilDay(message.getDate()), message);
        }
        return messagesByDate;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
