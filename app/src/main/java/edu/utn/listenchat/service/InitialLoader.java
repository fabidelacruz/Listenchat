package edu.utn.listenchat.service;

import com.google.common.collect.Lists;

import java.util.List;

import edu.utn.listenchat.model.Message;

import static edu.utn.listenchat.utils.DateUtils.toDate;


public class InitialLoader {

    private PersistenceService persistenceService = new PersistenceService();

    public void load() {
        List<Message> messages = Lists.newArrayList();

        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 1", "2017-09-05T00:11:22"));
        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 2", "2017-09-05T00:11:22"));
        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 3", "2017-09-05T00:11:22"));

        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 11", "2017-09-06T00:11:22"));
        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 12", "2017-09-06T00:11:22"));
        messages.add(this.incomingMessage("Fabián Cardaci", "Hola 13", "2017-09-06T00:11:22"));

        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 1", "2017-09-05T00:11:22"));
        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 2", "2017-09-05T00:11:22"));
        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 3", "2017-09-05T00:11:22"));

        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 11", "2017-09-06T00:11:22"));
        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 12", "2017-09-06T00:11:22"));
        messages.add(this.incomingMessage("Fabi De La Cruz", "Chau 13", "2017-09-06T00:11:22"));

        messages.add(this.incomingMessage("Maximiliano Bolia Morales", "Hola soy Maxi", "2017-09-06T00:11:22"));

        persistenceService.insert(messages);
    }

    private Message incomingMessage(String contact, String text, String date) {
        Message message = Message.createIncomingMessage(contact, text);
        message.setDate(toDate(date));
        return message;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
