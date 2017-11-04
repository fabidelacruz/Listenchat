package edu.utn.listenchat.model;


/**
 * It indicates the status of a massage and that determines if an incoming message is a novelty or not.
 * */
public enum MessageStatus {

    /** The incoming message is not listened for the user yet. */
    NEW,

    /** The incoming message is already listened for the user. */
    LISTENED,

    /** The incoming message is archived for the user. Initial status for outgoing messages. */
    ARCHIVED

}