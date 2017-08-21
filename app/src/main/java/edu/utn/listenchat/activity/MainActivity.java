package edu.utn.listenchat.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import edu.utn.listenchat.R;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.service.TextToSpeechService;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;
import static com.google.common.collect.Collections2.filter;

public class MainActivity extends ListeningActivity {

    private static final int PERMISSION_REQUEST = 9999;

    private TextToSpeechService textToSpeechService = new TextToSpeechService();

    private List<String> comandos = new ArrayList<>();

    private PersistenceService persistenceService = new PersistenceService();

    ListView list;
    CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        comandos.add("escuchar mensajes");
        comandos.add("enviar mensaje");
        comandos.add("cancelar");
        comandos.add("comandos");
        comandos.add("ayuda");
        comandos.add("salir");
        comandos.add("entrar");
        comandos.add("siguiente");
        comandos.add("atras");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());
        adapter = new CustomListAdapter(getApplicationContext(), cursor, 0);
        list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);

        if (!checkNotificationEnabled()) {
            Toast.makeText(this, "Por favor habilite a Listenchat para recibir notificaciones", LENGTH_LONG).show();;
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Listenchat-msg"));


        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
        }

        textToSpeechService.speak(getString(R.string.welcome_message), buildStartCallback(), this);

        /*
        VoiceRecognitionListener.getInstance().setListener(this); // Here we set the current listener
        startListening(); // starts listening*/
    }

    private boolean checkNotificationEnabled() {
        try{
            return Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(this.getPackageName());
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public void processVoiceCommands(String... voiceCommands) {
        List<String> filtered = filterCommands(voiceCommands);

        if (filtered.isEmpty()) {
            textToSpeechService.speak("Comando desconocido", buildStartCallback(), this);
        } else {
            switch (filtered.get(0).toLowerCase()) {
                case "escuchar mensajes":
                    handleListenMessages(this);
                    break;
                case "enviar mensaje":
                    break;
                case "cancelar":
                    break;
                case "comandos":
                    break;
                case "ayuda":
                    break;
                case "salir":
                    break;
                case "entrar":
                    break;
                case "siguiente":
                    break;
                case "atras":
                    break;
            }
        }

        restartListeningService();
    }

    private void handleListenMessages(Context context) {
    }

    private List<String> filterCommands(String[] voiceCommands) {
        return Lists.newArrayList(filter(Arrays.asList(voiceCommands), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                boolean valid = false;

                for (String command : comandos) {
                    if (input != null && input.toLowerCase().contains(command)) {
                        valid = true;
                    }
                }
                return valid;
            }
        }));
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


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            try {
                Message model = new Message();
                model.setName(title);
                model.setMessage(text);
                model.setReceivedDate(new Date());
                persistenceService.insert(context, model);
                adapter.changeCursor(persistenceService.getAllCursor(context));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
