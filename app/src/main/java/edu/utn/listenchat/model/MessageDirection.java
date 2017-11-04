package edu.utn.listenchat.model;

public enum MessageDirection {

    INCOMING,
    OUTGOING;

    public boolean is(String direction) {
        return this.name().equals(direction);
    }

}
