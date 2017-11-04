package edu.utn.listenchat.handler.common;


import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Multimap;

import java.util.Collection;

import edu.utn.listenchat.handler.AbstractHandler;
import edu.utn.listenchat.service.PersistenceService;

import static edu.utn.listenchat.utils.CursorUtils.convertCursorToMap;

public class NewsHandler extends AbstractHandler {

    private PersistenceService persistenceService;

    public void sayNovelties() {
        stopListening();
        Cursor cursor = persistenceService.getNewsCursor();

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<String> userMessages = allMessages.get(user);
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

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<String> userMessages = allMessages.get(user);
                stringBuilder.append("Mensajes recibidos de ").append(user).append(". ");
                for (String message : userMessages) {
                    stringBuilder.append(message).append(". ");
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString());
            }
            textToSpeechService.speak("", getResumeCallback());
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", getResumeCallback());
        }
    }


    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
