package edu.utn.listenchat.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.listener.TextToSpeechCallaback;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;

public class TextToSpeechService {

    private MainActivity mainActivity;
    private TextToSpeech textToSpeech;

    private boolean started = false;

    synchronized public void speak(final String message) {
        final TextToSpeechCallaback conversionCallback = this.buildStartCallback();

        if (textToSpeech != null) {
            speak(message, conversionCallback);
        } else {
            textToSpeech = new TextToSpeech(mainActivity, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        textToSpeech.setLanguage(new Locale("es", "AR"));
                        textToSpeech.setPitch(1f);
                        textToSpeech.setSpeechRate(1f);

                        started = true;
                        TextToSpeechService.this.speak(message, conversionCallback);
                    } else {
                        conversionCallback.onErrorOccured(-1);
                    }
                }
            });
        }
    }

    public void speak(String message, TextToSpeechCallaback conversionCallback) {
        if (started) {
            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                ttsGreater21(message, conversionCallback);
            } else {
                ttsUnder20(message, conversionCallback);
            }
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
                Log.e(TAG, "onError: ", null);
            } 
 
            @Override 
            public void onDone(String utteranceId) {
                textToSpeechCallaback.onCompletion();
            } 
        }); 
        textToSpeech.speak(text, QUEUE_ADD, map);
    }
 
    @TargetApi(LOLLIPOP)
    private void ttsGreater21(String text, TextToSpeechCallaback textToSpeechCallaback) {
        String utteranceId = text.hashCode() + "";
        textToSpeech.speak(text, QUEUE_ADD, null, utteranceId);
        textToSpeechCallaback.onCompletion();
    }

    public void stop() {
        textToSpeech.stop();
    }

    private TextToSpeechCallaback buildStartCallback() {
        return new TextToSpeechCallaback() {

            @Override
            public void onCompletion() {
            }

            @Override
            public void onErrorOccured(int errorCode) {
                Log.e("", errorCode + "");
            }

        };
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}