package edu.utn.listenchat.model;

import java.util.Date;

import edu.utn.listenchat.utils.StringUtils;

import static edu.utn.listenchat.utils.DateUtils.toStringUntilMinute;
import static java.lang.String.format;

public class Message {

    private String intentId;
    private String name;
    private String message;
    private String leido;
    private Date receivedDate;
    private String direction;

    public static Message create(String contact, String text, Date date, String direction) {
        Message message = new Message();

        message.setIntentId(format("%s-%s-%s", toStringUntilMinute(date), contact, text));
        message.setName(StringUtils.normalized(contact));
        message.setMessage(text);
        message.setReceivedDate(date);
        message.setLeido("N");
        message.setDirection(direction);

        return message;
    }

    public String getIntentId() {
        return intentId;
    }

    public void setIntentId(String intentId) {
        this.intentId = intentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLeido() {
        return leido;
    }

    public void setLeido(String leido) {
        this.leido = leido;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}

