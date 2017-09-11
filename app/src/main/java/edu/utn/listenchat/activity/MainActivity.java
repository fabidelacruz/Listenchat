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
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Predicate;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import edu.utn.listenchat.R;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.listener.VoiceRecognitionListener;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;
import edu.utn.listenchat.utils.CursorUtils;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static android.widget.Toast.LENGTH_LONG;
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Lists.newArrayList;
import static edu.utn.listenchat.utils.CursorUtils.convertCursorToMap;
import static edu.utn.listenchat.utils.CursorUtils.messageIds;
import static edu.utn.listenchat.utils.DateUtils.toStringUntilDay;
import static edu.utn.listenchat.utils.DateUtils.toStringUntilMinute;

public class MainActivity extends ListeningActivity {

    private static final String NOVEDADES = "novedades";
    private static final String LEER_MENSAJES_NUEVOS = "leer mensajes nuevos";
    private static final String CONVERSACIÓN = "conversacion";
    private static final String ENVIAR_MENSAJE = "enviar mensaje";
    private static final String CANCELAR = "cancelar";
    private static final String COMANDOS = "comandos";
    private static final String AYUDA = "ayuda";
    private static final String SALIR = "salir";
    private static final String ENTRAR = "entrar";
    private static final String SIGUIENTE = "siguiente";
    private static final String ANTERIOR = "anterior";

    private TextToSpeechService textToSpeechService = new TextToSpeechService();

    private List<String> comandos = newArrayList(NOVEDADES, LEER_MENSAJES_NUEVOS, CONVERSACIÓN,
            ENVIAR_MENSAJE, CANCELAR, COMANDOS, AYUDA, SALIR, ENTRAR, SIGUIENTE, ANTERIOR);

    private PersistenceService persistenceService = new PersistenceService();
    private MessageDao messageDao = new MessageDao();

    ListView list;
    CustomListAdapter adapter;

    private int currentMessage;
    private boolean enabledConversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=(ListView)findViewById(R.id.list);

        reloadAdapter();
        checkPermissions();

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Listenchat-msg"));

        textToSpeechService.speak(getString(R.string.welcome_message), buildStartCallback(), this);
    }

    private void checkPermissions() {
        if (!checkNotificationEnabled()) {
            Toast.makeText(this, "Por favor habilite a Listenchat para recibir notificaciones", LENGTH_LONG).show();
            ;
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST);
        }
    }

    private void reloadAdapter() {
        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());
        adapter = new CustomListAdapter(getApplicationContext(), cursor);
        list.setAdapter(adapter);
    }

    private boolean checkNotificationEnabled() {
        try{
            return Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(this.getPackageName());
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KEYCODE_MEDIA_NEXT:
            case KEYCODE_VOLUME_UP:
                handleFollowing(this);
                break;
            case KEYCODE_MEDIA_PREVIOUS:
            case KEYCODE_VOLUME_DOWN:
                handlePrevious(this);
                break;
            case KEYCODE_MEDIA_PLAY_PAUSE:
            case KEYCODE_MEDIA_PLAY:
            case KEYCODE_HEADSETHOOK:
                handleNewMessages(this);
                break;
        }

        return true;
    }

    @Override
    public void processVoiceCommands(String... voiceCommands) {
        List<String> filtered = filterCommands(voiceCommands);

        if (!filtered.isEmpty()) {
            switch (filtered.get(0).toLowerCase()) {
                case NOVEDADES:
                    handleNovelties(this);
                    break;
                case LEER_MENSAJES_NUEVOS:
                    handleNewMessages(this);
                    break;
                case CONVERSACIÓN:
                    handleConversation(this);
                    break;
                case ENVIAR_MENSAJE:
                    break;
                case CANCELAR:
                    break;
                case COMANDOS:
                    break;
                case AYUDA:
                    break;
                case SALIR:
                    break;
                case ENTRAR:
                    break;
                case SIGUIENTE:
                    handleFollowing(this);
                    break;
                case ANTERIOR:
                    this.handlePrevious(this);
                    break;
                default:
                    textToSpeechService.speak("Comando desconocido", buildStartCallback(), this);
                    break;
            }
        }

        restartListeningService();
    }

    private void handleNewMessages(Context context) {
        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<String> userMessages = allMessages.get(user);
                stringBuilder.append("Mensajes recibidos de ").append(user).append(". ");
                for (String message : userMessages) {
                    stringBuilder.append(message).append(". ");
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), buildReadCallback(messageIds(cursor), this), this);
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", buildStartCallback(), this);
        }
    }

    private TextToSpeechCallaback buildReadCallback(final List<Integer> integers, final Context context) {
        return new TextToSpeechCallaback() {
            @Override
            public void onCompletion() {
                persistenceService.markNotified(integers, context);
            }

            @Override
            public void onErrorOccured(int errorCode) {
                //Do nothing
            }
        };
    }

    private void handleNovelties(Context context) {
        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            for (String user : allMessages.keySet()) {
                StringBuilder stringBuilder = new StringBuilder();
                Collection<String> userMessages = allMessages.get(user);
                stringBuilder.append(userMessages.size()).append(" mensajes recibidos de ").append(user).append(". ");
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), buildStartCallback(), this);
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje nuevo", buildStartCallback(), this);
        }
    }

    private void handleFollowing(Context context) {
        String user = "Fabián Cardaci";

        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (this.enabledConversation) {
            if (allMessages.keySet().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                List<String> userMessages = newArrayList(allMessages.get(user));
                if (currentMessage + 1 < userMessages.size()) {
                    currentMessage += 1;
                    stringBuilder.append(userMessages.get(currentMessage)).append(". ");
                } else {
                    stringBuilder.append("No hay más mensajes siguientes");
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), buildStartCallback(), this);
            } else {
                textToSpeechService.speak("Situación no esperada", buildStartCallback(), this);
            }
        } else {
            textToSpeechService.speak("El comando siguiente sólo puede usarse en modo conversación", buildStartCallback(), this);
        }
    }

    private void handlePrevious(Context context) {
        String user = "Fabián Cardaci";

        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (this.enabledConversation) {
            if (allMessages.keySet().size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                List<String> userMessages = newArrayList(allMessages.get(user));
                if (currentMessage - 1 >= 0) {
                    currentMessage -= 1;
                    stringBuilder.append(userMessages.get(currentMessage)).append(". ");
                } else {
                    stringBuilder.append("No hay más mensajes anteriores");
                }
                Log.i("MENSAJES", stringBuilder.toString());
                textToSpeechService.speak(stringBuilder.toString(), buildStartCallback(), this);
            } else {
                textToSpeechService.speak("Situación no esperada", buildStartCallback(), this);
            }
        } else {
            textToSpeechService.speak("El comando anterior sólo puede usarse en modo conversación", buildStartCallback(), this);
        }
    }


    private void handleConversation(Context context) {
        String user = "Fabián Cardaci";

        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());

        Multimap<String, String> allMessages = convertCursorToMap(cursor);

        if (allMessages.keySet().size() > 0) {
            List<String> userMessages = newArrayList(allMessages.get(user));
            if (userMessages.size() > 0) {
                textToSpeechService.speak("Conversación con " + user, buildStartCallback(), this);
                currentMessage = -1;
                this.enabledConversation = true;
            } else {
                textToSpeechService.speak("Usted no ha recibido mensajes de " + user, buildStartCallback(), this);
                enabledConversation = false;
            }
        } else {
            textToSpeechService.speak("Usted no ha recibido ningún mensaje", buildStartCallback(), this);
            enabledConversation = false;
        }

    }


    private List<String> filterCommands(String[] voiceCommands) {
        return newArrayList(filter(Arrays.asList(voiceCommands), new Predicate<String>() {
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

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            if (isMessengerNotification(intent)) {
                try {
                    Date date = new Date();
                    String intentId = toStringUntilMinute(date) + "-" + title + "-" + text;

                    Log.i("CONTROL", "New Intent id: " + intentId);

                    if (!isDuplicated(intentId)) {
                        Message model = new Message();
                        model.setIntentId(intentId);
                        model.setName(title);
                        model.setMessage(text);
                        model.setReceivedDate(date);
                        model.setLeido("N");
                        persistenceService.insert(context, model);
                        if (list != null) {
                            reloadAdapter();
                        }
                        Log.i("CONTROL", "Saved Intent id: " + intentId);
                    } else {
                        Log.i("CONTROL", "Duplicated Intent id: " + intentId);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static boolean isMessengerNotification(Intent intent) {
        return "com.facebook.orca".equals(intent.getStringExtra("package"));
    }

    private Multimap<String, Message> massagesByDate(String contact) {
        Multimap<String, Message> messagesByDate = MultimapBuilder.treeKeys().linkedListValues().build();

        List<Message> messages = this.messageDao.allFromContact(this.getApplicationContext(), contact);
        for (Message message : messages) {
            messagesByDate.put(toStringUntilDay(message.getReceivedDate()), message);
        }
        return messagesByDate;
    }

    private boolean isDuplicated(String intentId) {
        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());
        if (cursor.moveToFirst()) {
            do {
                if (intentId.equals(cursor.getString(1))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

}
