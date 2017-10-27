package edu.utn.listenchat.service;

import android.content.Context;

import com.google.common.collect.Lists;

import java.util.List;

import edu.utn.listenchat.model.Message;

import static edu.utn.listenchat.utils.DateUtils.toDate;


public class DummyLoader {

    private PersistenceService persistenceService = new PersistenceService();

    public void load() {


        List<Message> messages = Lists.newArrayList();

        messages.add(Message.create("Fabián Cardaci", "Hola 1", toDate("2017-09-05T00:11:22"), "I"));
        messages.add(Message.create("Fabián Cardaci", "Hola 2", toDate("2017-09-05T00:11:22"), "I"));
        messages.add(Message.create("Fabián Cardaci", "Hola 3", toDate("2017-09-05T00:11:22"), "I"));

        messages.add(Message.create("Fabián Cardaci", "Hola 11", toDate("2017-09-06T00:11:22"), "I"));
        messages.add(Message.create("Fabián Cardaci", "Hola 12", toDate("2017-09-06T00:11:22"), "I"));
        messages.add(Message.create("Fabián Cardaci", "Hola 13", toDate("2017-09-06T00:11:22"), "I"));

        messages.add(Message.create("Fabi De La Cruz", "Chau 1", toDate("2017-09-05T00:11:22"), "I"));
        messages.add(Message.create("Fabi De La Cruz", "Chau 2", toDate("2017-09-05T00:11:22"), "I"));
        messages.add(Message.create("Fabi De La Cruz", "Chau 3", toDate("2017-09-05T00:11:22"), "I"));

        messages.add(Message.create("Fabi De La Cruz", "Chau 11", toDate("2017-09-06T00:11:22"), "I"));
        messages.add(Message.create("Fabi De La Cruz", "Chau 12", toDate("2017-09-06T00:11:22"), "I"));
        messages.add(Message.create("Fabi De La Cruz", "Chau 13", toDate("2017-09-06T00:11:22"), "I"));

        messages.add(Message.create("Maximiliano Bolia Morales", "Hola soy Maxi", toDate("2017-09-06T00:11:22"), "I"));

        persistenceService.insert(messages);
    }


    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
