package edu.utn.listenchat.handler;


import android.database.Cursor;
import android.util.Log;

import com.google.common.collect.Multimap;

import java.util.Collection;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.utils.CursorUtils.convertCursorToMap;


public class NewsHandler {

    private PersistenceService persistenceService;
    private TextToSpeechService textToSpeechService;


    public void tellTheNovelties(MainActivity activity) {
        Cursor cursor = persistenceService.getNewsCursor(activity.getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<String> userMessages = allMessages.get(user);
                stringBuilder.append(userMessages.size()).append(" mensajes recibidos de ").append(user).append(". ");
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", activity, activity.buildStartCallback());
        }
    }

    public void tellTheNewMessages(MainActivity activity) {
        Cursor cursor = persistenceService.getNewsCursor(activity.getApplicationContext());

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
                textToSpeechService.speak(stringBuilder.toString(), activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", activity, activity.buildStartCallback());
        }
    }


    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

}
