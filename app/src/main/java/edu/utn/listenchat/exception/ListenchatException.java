package edu.utn.listenchat.exception;

public class ListenchatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ListenchatException(String message) {
        super(message);
    }

    public ListenchatException(Throwable throwable) {
        super(throwable);
    }

    public ListenchatException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
