package edu.utn.listenchat.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

import edu.utn.listenchat.listener.TextToSpeechCallaback;

public class TextToSpeechService {

    private TextToSpeech textToSpeech;

    public void speak(final String message, final TextToSpeechCallaback conversionCallback, Activity appContext) {
        if (textToSpeech != null) {
            speak(message, conversionCallback);
        } else {
            textToSpeech = new TextToSpeech(appContext, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(new Locale("es", "ES"));
                        textToSpeech.setPitch(1.3f);
                        textToSpeech.setSpeechRate(1f);

                        TextToSpeechService.this.speak(message, conversionCallback);
                    } else {
                        conversionCallback.onErrorOccured(-1);
                    }
                }
            });
        }
    }

    private void speak(String message, TextToSpeechCallaback conversionCallback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(message, conversionCallback);
        } else {
            ttsUnder20(message, conversionCallback);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.finalize();
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text, final TextToSpeechCallaback textToSpeechCallaback) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
 
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
 
            @Override 
            public void onStart(String utteranceId) {
                // TODO Auto-generated method stub 
 
            } 
 
            @Override 
            public void onError(String utteranceId) {
                // TODO Auto-generated method stub 
 
            } 
 
            @Override 
            public void onDone(String utteranceId) {
                //do some work here 

                textToSpeechCallaback.onCompletion();
            } 
        }); 
 
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }
 
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text, TextToSpeechCallaback textToSpeechCallaback) {
        String utteranceId = this.hashCode() + "";
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        textToSpeechCallaback.onCompletion();
    } 
 
} 