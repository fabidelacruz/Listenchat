package edu.utn.listenchat.handler;


import android.util.Log;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.TextToSpeechService;

import static com.google.common.collect.Lists.newArrayList;
import static edu.utn.listenchat.activity.State.getState;
import static edu.utn.listenchat.utils.DateUtils.toPrettyString;
import static java.lang.Boolean.TRUE;
import static org.apache.commons.lang3.StringUtils.split;


public class ConversationHandler {

    private TextToSpeechService textToSpeechService;
    private MessageDao messageDao;


    public void prepareConversation(MainActivity activity, String contact) {
        Multimap<String, Message> messages = this.messageDao.massagesByDate(activity, contact);
        List<String> dates = Lists.newArrayList(messages.keySet());

        if (dates.size() > 0) {
            getState().setCurrentContact(contact);
            getState().setCurrentMessagePosition(-1);
            getState().setConversationMode(TRUE);
            String lastDate = dates.get(dates.size()-1);
            getState().setCurrentMessageDate(lastDate);
            textToSpeechService.speak("Conversación con " + contact, activity, activity.buildStartCallback());
            textToSpeechService.speak(toPrettyString(lastDate), activity, activity.buildStartCallback());
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje de " + contact, activity, activity.buildStartCallback());
            getState().setConversationMode(TRUE);
        }

    }


    public void following(MainActivity activity) {
        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(activity, getState().getCurrentContact());

        if (getState().isConversationMode()) {
            if (messagesByDate.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                List<Message> dateMessages = newArrayList(messagesByDate.get(getState().getCurrentMessageDate()));
                int currentMessagePosition = getState().getCurrentMessagePosition();
                if (currentMessagePosition + 1 < dateMessages.size()) {
                    currentMessagePosition += 1;
                    getState().setCurrentMessagePosition(currentMessagePosition);
                    Message message = dateMessages.get(currentMessagePosition);
                    stringBuilder.append(this.prettyContact(message) + ". ").append(". ");
                    stringBuilder.append(message.getMessage()).append(". ");
                } else {
                    stringBuilder.append("No hay más mensajes siguientes del " + toPrettyString(getState().getCurrentMessageDate()));
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), activity, activity.buildStartCallback());
            } else {
                textToSpeechService.speak("Situación no esperada", activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("El comando siguiente sólo puede usarse en modo conversación", activity, activity.buildStartCallback());
        }
    }


    public void previous(MainActivity activity) {
        Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(activity, getState().getCurrentContact());

        if (getState().isConversationMode()) {
            if (messagesByDate.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                List<Message> dateMessages = newArrayList(messagesByDate.get(getState().getCurrentMessageDate()));
                int currentMessagePosition = getState().getCurrentMessagePosition();
                if (currentMessagePosition - 1 >= 0) {
                    currentMessagePosition -= 1;
                    getState().setCurrentMessagePosition(currentMessagePosition);
                    Message message = dateMessages.get(currentMessagePosition);
                    stringBuilder.append(this.prettyContact(message) + ". ").append(". ");
                    stringBuilder.append(message.getMessage()).append(". ");
                } else {
                    stringBuilder.append("No hay más mensajes anteriores del " + toPrettyString(getState().getCurrentMessageDate()));
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), activity, activity.buildStartCallback());
            } else {
                textToSpeechService.speak("Situación no esperada", activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("El comando anterior sólo puede usarse en modo conversación", activity, activity.buildStartCallback());
        }
    }

    public void followingDay(MainActivity activity) {
        if (getState().isConversationMode()) {
            Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(activity, getState().getCurrentContact());
            List<String> dates = Lists.newArrayList(messagesByDate.keySet());
            int datePosition = dates.indexOf(getState().getCurrentMessageDate());
            if (datePosition + 1 < dates.size()) {
                getState().setCurrentMessageDate(dates.get(datePosition + 1));
                getState().setCurrentMessagePosition(-1);
                textToSpeechService.speak(toPrettyString(getState().getCurrentMessageDate()), activity, activity.buildStartCallback());
            } else {
                textToSpeechService.speak("Ya no hay más días de conversación con "+ getState().getCurrentContact(), activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("El comando dia siguiente sólo puede usarse en modo conversación", activity, activity.buildStartCallback());
        }
    }

    public void previousDay(MainActivity activity) {
        if (getState().isConversationMode()) {
            Multimap<String, Message> messagesByDate = this.messageDao.massagesByDate(activity, getState().getCurrentContact());
            List<String> dates = Lists.newArrayList(messagesByDate.keySet());
            int datePosition = dates.indexOf(getState().getCurrentMessageDate());
            if (datePosition > 0) {
                getState().setCurrentMessageDate(dates.get(datePosition - 1));
                getState().setCurrentMessagePosition(-1);
                textToSpeechService.speak(toPrettyString(getState().getCurrentMessageDate()), activity, activity.buildStartCallback());
            } else {
                textToSpeechService.speak("Ya no hay días de conversación anteriores con "+ getState().getCurrentContact(), activity, activity.buildStartCallback());
            }
        } else {
            textToSpeechService.speak("El comando dia anterior sólo puede usarse en modo conversación", activity, activity.buildStartCallback());
        }
    }

    private String prettyContact(Message message) {
        String direction = message.getDirection();
        if ("O".equals(direction)) {
            return "YO";
        } else if ("I".equals(direction)) {
            return newArrayList(split(message.getName(), " ")).get(0);
        }
        return "";
    }


    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }
}
