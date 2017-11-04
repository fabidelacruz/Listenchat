package edu.utn.listenchat.handler.common;


import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;

import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.MessageStatus;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;
import edu.utn.listenchat.utils.CursorUtils;

import static edu.utn.listenchat.model.MessageStatus.ARCHIVED;
import static edu.utn.listenchat.model.MessageStatus.LISTENED;
import static edu.utn.listenchat.utils.CursorUtils.messagesByContact;
import static java.lang.String.format;


public class NewsHandler {

    private PersistenceService persistenceService;
    private TextToSpeechService textToSpeechService;


    public void sayNovelties() {
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
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo");
        }
    }

    public void sayNewMessages() {
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
                this.updateStatus(userMessages, LISTENED);
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo");
        }
    }

    public void handleClear() {
        Cursor cursor = this.persistenceService.getListenedCursor();
        List<Message> messages = CursorUtils.toMessages(cursor);
        if (messages.isEmpty()) {
            textToSpeechService.speak(format("No hay mensajes para limpiar"));
        } else {
            this.updateStatus(messages, ARCHIVED);
            textToSpeechService.speak(format("%s mensajes limpiados"));
        }
    }

    private void updateStatus(Collection<Message> messages, MessageStatus status) {
        for (Message message : messages) {
            message.setStatus(status);
        }
        this.persistenceService.update(messages);
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

}
