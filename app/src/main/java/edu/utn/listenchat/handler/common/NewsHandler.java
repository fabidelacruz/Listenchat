package edu.utn.listenchat.handler.common;


import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Multimap;

import java.util.Collection;

import edu.utn.listenchat.handler.AbstractHandler;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.MessageStatus;
import edu.utn.listenchat.service.PersistenceService;

import static edu.utn.listenchat.model.MessageStatus.LISTENED;
import static edu.utn.listenchat.utils.CursorUtils.messagesByContact;

public class NewsHandler extends AbstractHandler {

    private PersistenceService persistenceService;

    public void sayNovelties() {
        stopListening();
        Cursor cursor = persistenceService.getNewsCursor();

        Multimap<String, Message> allMessages = messagesByContact(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<Message> userMessages = allMessages.get(user);
                stringBuilder.append(userMessages.size()).append(" mensajes recibidos de ").append(user).append(". ");
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString());
            }
            textToSpeechService.speak("", getResumeCallback());
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", getResumeCallback());
        }
    }

    public void sayNewMessages() {
        stopListening();
        Cursor cursor = persistenceService.getNewsCursor();

        Multimap<String, Message> allMessages = messagesByContact(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<Message> userMessages = allMessages.get(user);
                stringBuilder.append("Mensajes recibidos de ").append(user).append(". ");
                for (Message message : userMessages) {
                    stringBuilder.append(message.getText()).append(". ");
                }
                this.textToSpeechService.speak(stringBuilder.toString());
                this.markMessagesAsListened(userMessages);
            }
            textToSpeechService.speak("", getResumeCallback());
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", getResumeCallback());
        }
    }

    private void markMessagesAsListened(Collection<Message> messages) {
        for (Message message : messages) {
            message.setStatus(LISTENED);
        }
        this.persistenceService.update(messages);
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
