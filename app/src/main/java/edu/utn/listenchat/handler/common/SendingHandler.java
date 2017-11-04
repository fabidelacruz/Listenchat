package edu.utn.listenchat.handler.common;


import android.widget.Toast;

import edu.utn.listenchat.connector.MessengerConnector;
import edu.utn.listenchat.handler.AbstractHandler;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.PersistenceService;

import static edu.utn.listenchat.activity.State.getState;
import static edu.utn.listenchat.model.Message.createOutgoingMessage;
import static edu.utn.listenchat.utils.StringUtils.safeEquals;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class SendingHandler extends AbstractHandler {

    private MessengerConnector messengerConnector;
    private PersistenceService persistenceService;


    public void prepareMessage(String contact) {
        stopListening();
        String msnContact = this.findMsnContact(contact);
        if (isNotBlank(msnContact)) {
            getState().setSendingMessageMode(TRUE);
            getState().setCurrentContact(msnContact);
            textToSpeechService.speak(format("Mensaje a %s. Diga", msnContact), getResumeCallback());
        } else {
            textToSpeechService.speak(format("No se ha encontrado el contacto %s", contact), getResumeCallback());
        }
    }

    public void sendMessage(String text) {
        stopListening();
        if ("cancelar".equalsIgnoreCase(text)) {
            textToSpeechService.speak("Envío cancelado", getResumeCallback());
        } else {
            Toast.makeText(activity, "Mensaje: " + text, Toast.LENGTH_LONG).show();
            this.messengerConnector.send(text, 0);
            Message message = createOutgoingMessage(getState().getCurrentContact(), text);
            persistenceService.insert(message);
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

    public void setMessengerConnector(MessengerConnector messengerConnector) {
        this.messengerConnector = messengerConnector;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}