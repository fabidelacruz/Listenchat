package edu.utn.listenchat.service;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by fabian on 23/06/17.
 */

public class TextToSpeechService implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;

    private Boolean started;

    public TextToSpeechService(Context context) {
        textToSpeech = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            started = true;
            textToSpeech.setLanguage(new Locale("es", "ES"));
        } else {
            started = false;
        }
    }

    public FutureTask<Integer> speak(final String text) {
        final TextToSpeechService service = this;

        FutureTask<Integer> task = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                service.waitStarted();
                return textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        Executors.newSingleThreadExecutor().execute(task);

        return task;
    }

    private void waitStarted() {
        while (started == null) {
            //wait for listener
        }
    }
}
