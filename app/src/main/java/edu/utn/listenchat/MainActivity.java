package edu.utn.listenchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.utn.listenchat.service.TextToSpeechService;

public class MainActivity extends AppCompatActivity {
    
    private TextToSpeechService textToSpeechService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textToSpeechService = new TextToSpeechService(this);
        textToSpeechService.speak(getString(R.string.welcome_message));
    }


}
