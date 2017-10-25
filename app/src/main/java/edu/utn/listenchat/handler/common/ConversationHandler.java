package edu.utn.listenchat.handler.common;


import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.TextToSpeechService;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;
import static edu.utn.listenchat.activity.State.getState;
import static edu.utn.listenchat.utils.DateUtils.toPrettyString;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.split;


public class ConversationHandler {

    private TextToSpeechService textToSpeechService;
    private MessageDao messageDao;


    public void prepareConversation(String contact) {
        Multimap<String, Message> messages = this.messageDao.massagesByDate(contact);
        List<String> dates = Lists.newArrayList(messages.keySet());

        if (dates.size() > 0) {
            getState().setCurrentContact(contact);
            getState().setCurrentMessagePosition(-1);
            getState().setConversationMode(TRUE);
            String lastDate = dates.get(dates.size()-1);
            getState().setCurrentMessageDate(lastDate);
            textToSpeechService.speak("Conversación con " + contact);
            textToSpeechService.speak(toPrettyString(lastDate));
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje de " + contact);
            getState().setConversationMode(FALSE);
        }

    }


    public void following() {
        checkState(getState().isConversationMode(), "El comando siguiente sólo puede usarse en modo conversación");

        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(getState().getCurrentContact());

        StringBuilder stringBuilder = new StringBuilder();
        List<Message> dateMessages = newArrayList(messagesByDate.get(getState().getCurrentMessageDate()));
        int currentMessagePosition = getState().getCurrentMessagePosition();
        if (currentMessagePosition + 1 < dateMessages.size()) {
            currentMessagePosition += 1;
            getState().setCurrentMessagePosition(currentMessagePosition);
            Message message = dateMessages.get(currentMessagePosition);
            stringBuilder.append(this.prettyContact(message)).append(": ");
            stringBuilder.append(message.getMessage()).append(". ");
        } else {
            stringBuilder.append("No hay más mensajes siguientes del " + toPrettyString(getState().getCurrentMessageDate()));
        }
        textToSpeechService.speak(stringBuilder.toString());
    }


    public void previous() {
        checkState(getState().isConversationMode(), "El comando anterior sólo puede usarse en modo conversación");

        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(getState().getCurrentContact());

        StringBuilder stringBuilder = new StringBuilder();
        List<Message> dateMessages = newArrayList(messagesByDate.get(getState().getCurrentMessageDate()));
        int currentMessagePosition = getState().getCurrentMessagePosition();
        if (currentMessagePosition - 1 >= 0) {
            currentMessagePosition -= 1;
            getState().setCurrentMessagePosition(currentMessagePosition);
            Message message = dateMessages.get(currentMessagePosition);
            stringBuilder.append(this.prettyContact(message)).append(": ");
            stringBuilder.append(message.getMessage()).append(". ");
        } else {
            stringBuilder.append("No hay más mensajes anteriores del " + toPrettyString(getState().getCurrentMessageDate()));
        }
        textToSpeechService.speak(stringBuilder.toString());
    }

    public void again() {
        checkState(getState().isConversationMode(), "El comando de nuevo sólo puede usarse en modo conversación");
        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(getState().getCurrentContact());
        List<Message> dateMessages = newArrayList(messagesByDate.get(getState().getCurrentMessageDate()));
        int currentMessagePosition = getState().getCurrentMessagePosition();
        StringBuilder stringBuilder = new StringBuilder();
        if (currentMessagePosition >= 0) {
            Message message = dateMessages.get(currentMessagePosition);
            stringBuilder.append(this.prettyContact(message)).append(": ");
            stringBuilder.append(message.getMessage()).append(". ");
        } else {
            stringBuilder.append(toPrettyString(getState().getCurrentMessageDate()));
        }
        textToSpeechService.speak(stringBuilder.toString());
    }

    public void followingDay() {
        checkState(getState().isConversationMode(), "El comando día siguiente sólo puede usarse en modo conversación");

        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(getState().getCurrentContact());
        List<String> dates = Lists.newArrayList(messagesByDate.keySet());
        int datePosition = dates.indexOf(getState().getCurrentMessageDate());
        if (datePosition + 1 < dates.size()) {
            getState().setCurrentMessageDate(dates.get(datePosition + 1));
            getState().setCurrentMessagePosition(-1);
            textToSpeechService.speak(toPrettyString(getState().getCurrentMessageDate()));
        } else {
            textToSpeechService.speak("Ya no hay más días de conversación con "+ getState().getCurrentContact());
        }
    }

    public void previousDay() {
        checkState(getState().isConversationMode(), "El comando día anterior sólo puede usarse en modo conversación");

        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(getState().getCurrentContact());
        List<String> dates = Lists.newArrayList(messagesByDate.keySet());
        int datePosition = dates.indexOf(getState().getCurrentMessageDate());
        if (datePosition > 0) {
            getState().setCurrentMessageDate(dates.get(datePosition - 1));
            getState().setCurrentMessagePosition(-1);
            textToSpeechService.speak(toPrettyString(getState().getCurrentMessageDate()));
        } else {
            textToSpeechService.speak("Ya no hay días de conversación anteriores con "+ getState().getCurrentContact());
        }
    }

    private String prettyContact(Message message) {
        switch (message.getDirection()) {
            case "O":
                return "Yo";
            case "I":
                return newArrayList(split(message.getName(), " ")).get(0);
            default:
                return "";
        }
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }
}
