package edu.utn.listenchat.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.R;
import edu.utn.listenchat.listener.VoiceRecognitionListener;
import edu.utn.listenchat.service.TextToSpeechService;

public class MainActivity extends ListeningActivity {

    private TextToSpeechService textToSpeechService = new TextToSpeechService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechService.speak(getString(R.string.welcome_message), buildStartCallback(), this);

        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening
    }

    // Here is where the magic happens
    @Override
    public void processVoiceCommands(String... voiceCommands) {
        for (String command : voiceCommands) {
            Toast.makeText(this, command, Toast.LENGTH_LONG).show();
        }
        restartListeningService();
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

}
