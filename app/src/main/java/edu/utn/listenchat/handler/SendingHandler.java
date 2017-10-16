package edu.utn.listenchat.handler;


import java.util.Date;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.connector.MessengerConnector;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.activity.State.getState;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class SendingHandler {

    private TextToSpeechService textToSpeechService;
    private MessengerConnector messengerConnector;
    private PersistenceService persistenceService;


    public void prepareMessage(MainActivity activity, String contact) {
        getState().setSendingMessageMode(TRUE);
        getState().setCurrentContact(contact);
        textToSpeechService.speak("Diga", activity, activity.buildStartCallback());
    }

    public void sendMessage(MainActivity activity, String text) {
        if ("cancelar".equalsIgnoreCase(text)) {
            textToSpeechService.speak("Envío cancelado", activity, activity.buildStartCallback());
        } else {
            this.messengerConnector.send(activity, text, 0);
            Message message = Message.create(getState().getCurrentContact(), text, new Date(), "O");
            persistenceService.insert(activity.getApplicationContext(), message);
        }
        getState().setSendingMessageMode(FALSE);
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setMessengerConnector(MessengerConnector messengerConnector) {
        this.messengerConnector = messengerConnector;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }
}