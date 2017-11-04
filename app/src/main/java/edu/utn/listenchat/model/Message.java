package edu.utn.listenchat.model;

import java.util.Date;

import edu.utn.listenchat.utils.StringUtils;

import static edu.utn.listenchat.model.MessageDirection.INCOMING;
import static edu.utn.listenchat.model.MessageDirection.OUTGOING;
import static edu.utn.listenchat.model.MessageStatus.ARCHIVED;
import static edu.utn.listenchat.model.MessageStatus.NEW;
import static edu.utn.listenchat.utils.DateUtils.toStringUntilMinute;
import static java.lang.String.format;

public class Message {

    private Long id;
    private String intentId;
    private String contact;
    private String text;
    private MessageStatus status;
    private Date date;
    private MessageDirection direction;

    public static Message createIncomingMessage(String contact, String text) {
        Message message = create(contact, text);
        message.setDirection(INCOMING);
        message.setStatus(NEW);

        return message;
    }

    public static Message createOutgoingMessage(String contact, String text) {
        Message message = create(contact, text);
        message.setDirection(OUTGOING);
        message.setStatus(ARCHIVED);

        return message;
    }

    private static Message create(String contact, String text) {
        Message message = new Message();

        Date date = new Date();
        message.setIntentId(format("%s-%s-%s", toStringUntilMinute(date), contact, text));
        message.setContact(StringUtils.normalized(contact));
        message.setText(text);
        message.setDate(date);

        return message;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MessageDirection getDirection() {
        return direction;
    }

    public void setDirection(MessageDirection direction) {
        this.direction = direction;
    }

}

