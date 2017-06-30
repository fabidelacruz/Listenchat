package edu.utn.listenchat.activity;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
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
        List<String> comandos = new ArrayList<String>();
        comandos.add("escuchar mensajes");
        comandos.add("enviar mensaje");
        comandos.add("cancelar");
        comandos.add("comandos");
        comandos.add("ayuda");
        comandos.add("salir");
        comandos.add("entrar");
        comandos.add("siguiente");
        comandos.add("atras");

        TextView tv = (TextView) findViewById(R.id.tv);
        boolean esComando = false;
        for (String command : voiceCommands) {
          //  Toast.makeText(this, command, Toast.LENGTH_LONG).show();
         //   tv.append(command + "\n");
            for(String com : comandos) {
                if (com.toLowerCase().contains(command)) {
                    Toast.makeText(this, command, Toast.LENGTH_LONG).show();
                    esComando = true;
                }
            }
        }
        if(esComando){
            textToSpeechService.speak("Como usted diga", buildStartCallback(), this);
        }else{
            textToSpeechService.speak("Comando desconocido", buildStartCallback(), this);
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
