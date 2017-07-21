package edu.utn.listenchat.activity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;
import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.R;
import edu.utn.listenchat.listener.VoiceRecognitionListener;
import edu.utn.listenchat.service.TextToSpeechService;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends ListeningActivity {

    private static final int PERMISSION_REQUEST = 9999;

    private TextToSpeechService textToSpeechService = new TextToSpeechService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
        }

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
                if (command.toLowerCase().contains(com.toLowerCase())) {
                    Toast.makeText(this, command, LENGTH_LONG).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults.length == 0 || grantResults[0] != PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permisos denegados. Saliendo..", LENGTH_LONG).show();
                    this.finish();
                }
                break;
            }

        }
    }

}
