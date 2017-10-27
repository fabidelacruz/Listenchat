package edu.utn.listenchat.handler.common;


import android.widget.Toast;

import java.util.Date;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.connector.MessengerConnector;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.activity.State.getState;
import static edu.utn.listenchat.utils.StringUtils.safeEquals;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class SendingHandler {

    private MainActivity mainActivity;
    private TextToSpeechService textToSpeechService;
    private MessengerConnector messengerConnector;
    private PersistenceService persistenceService;


    public void prepareMessage(String contact) {
        String msnContact = this.findMsnContact(contact);
        if (isNotBlank(msnContact)) {
            getState().setSendingMessageMode(TRUE);
            getState().setCurrentContact(msnContact);
            textToSpeechService.speak(format("Mensaje a %s. Diga", msnContact));
        } else {
            textToSpeechService.speak(format("No se ha encontrado el contacto %s", contact));
        }
    }

    public void sendMessage(String text) {
        if ("cancelar".equalsIgnoreCase(text)) {
            textToSpeechService.speak("Envío cancelado");
        } else {
            Toast.makeText(mainActivity, "Mensaje: " + text, Toast.LENGTH_LONG).show();
            this.messengerConnector.send(text, 0);
            Message message = Message.create(getState().getCurrentContact(), text, new Date(), "O");
            persistenceService.insert(mainActivity, message);
        }
        getState().setSendingMessageMode(FALSE);
    }

    private String findMsnContact(String receivedContact) {
        for (String msnContact : this.messengerConnector.obtainContacts()) {
            if (safeEquals(msnContact, receivedContact)) {
                return  msnContact;
            }
        }
        return EMPTY;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
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