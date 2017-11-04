package edu.utn.listenchat.handler;

import android.support.annotation.NonNull;

import edu.utn.listenchat.activity.ListeningActivity;
import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.service.TextToSpeechService;

public abstract class AbstractHandler {

    protected ListeningActivity activity;
    protected TextToSpeechService textToSpeechService;

    public void stopListening() {
        activity.stopListening();
    }

    public void resumeListening() {
        activity.resumeListener();
    }

    @NonNull
    protected TextToSpeechCallaback getResumeCallback() {
        return activity;
    }

    public void setActivity(ListeningActivity activity) {
        this.activity = activity;
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }
}
