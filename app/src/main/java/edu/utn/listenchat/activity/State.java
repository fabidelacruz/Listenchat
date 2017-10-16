package edu.utn.listenchat.activity;


public class State {

    private static State instance = null;

    protected State() {}

    public static State getState() {
        if (instance == null) {
            instance = new State();
        }
        return instance;
    }

    /** Mode */
    private boolean sendingMessageMode;
    private boolean conversationMode;

    /** Context */
    private String currentContact;
    private int currentMessagePosition;
    private String currentMessageDate;


    public boolean isSendingMessageMode() {
        return sendingMessageMode;
    }

    public void setSendingMessageMode(boolean sendingMessageMode) {
        this.sendingMessageMode = sendingMessageMode;
    }

    public String getCurrentContact() {
        return currentContact;
    }

    public void setCurrentContact(String currentContact) {
        this.currentContact = currentContact;
    }

    public boolean isConversationMode() {
        return conversationMode;
    }

    public void setConversationMode(boolean conversationMode) {
        this.conversationMode = conversationMode;
    }

    public int getCurrentMessagePosition() {
        return currentMessagePosition;
    }

    public void setCurrentMessagePosition(int currentMessagePosition) {
        this.currentMessagePosition = currentMessagePosition;
    }

    public String getCurrentMessageDate() {
        return currentMessageDate;
    }

    public void setCurrentMessageDate(String currentMessageDate) {
        this.currentMessageDate = currentMessageDate;
    }
}