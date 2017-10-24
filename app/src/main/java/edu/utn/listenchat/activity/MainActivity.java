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

import java.util.Date;

import edu.utn.listenchat.R;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.handler.button.ButtonDownHandler;
import edu.utn.listenchat.handler.button.ButtonUpHandler;
import edu.utn.listenchat.handler.button.LongPressHandler;
import edu.utn.listenchat.handler.common.SendingHandler;
import edu.utn.listenchat.handler.voice.NotFoundHandler;
import edu.utn.listenchat.handler.voice.VoiceCommandHandler;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.VoiceCommand;
import edu.utn.listenchat.service.DummyLoader;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.widget.Toast.LENGTH_LONG;
import static edu.utn.listenchat.activity.Injector.injectDependencies;
import static edu.utn.listenchat.activity.State.getState;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MainActivity extends ListeningActivity {

    private TextToSpeechService textToSpeechService;
    private PersistenceService persistenceService;
    private MessageDao messageDao;
    private DummyLoader dummyLoader;
    private SendingHandler sendingHandler;
    private VoiceCommandHandler voiceCommandHandler;
    private NotFoundHandler notFoundHandler;
    private ButtonUpHandler buttonUpHandler;
    private ButtonDownHandler buttonDownHandler;
    private LongPressHandler longPressHandler;

    ListView list;
    CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectDependencies(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=(ListView)findViewById(R.id.list);

        reloadAdapter();
        checkPermissions();

        Context context = this.getApplicationContext();
        if (this.messageDao.all().size() == 0) {
            this.dummyLoader.load();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Listenchat-msg"));

        textToSpeechService.speak(getString(R.string.welcome_message));
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
        Cursor cursor = persistenceService.getAllCursor();
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
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return this.longPressHandler.handle(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return this.buttonDownHandler.handle(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return this.buttonUpHandler.handle(keyCode, event);
    }

    public boolean superOnKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public boolean superOnKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }


    @Override
    public void processVoiceCommands(String... voiceCommands) {
        String receivedCommand = this.findReceivedCommand(voiceCommands);

        if (isNotBlank(receivedCommand)) {
            if (getState().isSendingMessageMode()) {
                this.sendingHandler.sendMessage(receivedCommand);
            } else {
                processCommand(receivedCommand);
            }
        }
        this.restartListeningService();
    }

    private void processCommand(String receivedCommand) {
        VoiceCommand voiceCommand = VoiceCommand.findCommand(receivedCommand);
        if (voiceCommand != null) {
            voiceCommand.execute(this.voiceCommandHandler, receivedCommand);
        } else {
            this.notFoundHandler.sayMessage();
        }
    }


    private String findReceivedCommand(String[] sounds) {
        for (String sound : sounds) {
            if (isNotBlank(sound) && (VoiceCommand.findCommand(sound) != null || getState().isSendingMessageMode())) {
                return sound.toLowerCase();
            }
        }
        return EMPTY;
    }

    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String source = intent.getStringExtra("package");
            String contact = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");

            if (!isMessengerNotification(intent)) {
                Log.i("CONTROL", format("%s is not a Messenger notification", source));
                return;
            }

            Message message = Message.create(contact, text, new Date(), "I");

            if (isDuplicated(message)) {
                Log.i("CONTROL", format("Duplicated intentId %s", message.getIntentId()));
                return;
            }

            persistenceService.insert(context, message);
            if (list != null) {
                reloadAdapter();
            }
            Log.i("CONTROL", "Saved Intent id: " + message.getIntentId());
        }
    };


    private boolean isMessengerNotification(Intent intent) {
        return "com.facebook.orca".equals(intent.getStringExtra("package"));
    }

    private boolean isDuplicated(Message message) {
        Cursor cursor = persistenceService.getAllCursor();
        if (cursor.moveToFirst()) {
            do {
                if (message.getIntentId().equals(cursor.getString(1))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }


    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    public void setDummyLoader(DummyLoader dummyLoader) {
        this.dummyLoader = dummyLoader;
    }

    public void setSendingHandler(SendingHandler sendingHandler) {
        this.sendingHandler = sendingHandler;
    }

    public void setVoiceCommandHandler(VoiceCommandHandler voiceCommandHandler) {
        this.voiceCommandHandler = voiceCommandHandler;
    }

    public void setNotFoundHandler(NotFoundHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }

    public void setButtonUpHandler(ButtonUpHandler buttonUpHandler) {
        this.buttonUpHandler = buttonUpHandler;
    }

    public void setButtonDownHandler(ButtonDownHandler buttonDownHandler) {
        this.buttonDownHandler = buttonDownHandler;
    }

    public void setLongPressHandler(LongPressHandler longPressHandler) {
        this.longPressHandler = longPressHandler;
    }

}
