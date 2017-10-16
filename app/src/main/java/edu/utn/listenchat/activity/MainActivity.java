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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import edu.utn.listenchat.R;
import edu.utn.listenchat.connector.MessengerConnector;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.handler.ConversationHandler;
import edu.utn.listenchat.handler.NewsHandler;
import edu.utn.listenchat.handler.SendingHandler;
import edu.utn.listenchat.listener.TextToSpeechCallaback;
import edu.utn.listenchat.model.MenuStep;
import edu.utn.listenchat.model.Step;
import edu.utn.listenchat.model.Message;
import edu.utn.listenchat.model.Substep;
import edu.utn.listenchat.service.DummyLoader;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

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
import static edu.utn.listenchat.model.Step.CONVERSATION;
import static edu.utn.listenchat.model.Substep.SELECT_CONTACT;
import static edu.utn.listenchat.utils.CursorUtils.convertCursorToMap;
import static edu.utn.listenchat.utils.DateUtils.toPrettyString;
import static edu.utn.listenchat.utils.DateUtils.toStringUntilDay;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.split;

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
    private static final String DIA_SIGUIENTE = "día siguiente";
    private static final String DIA_ANTERIOR = "día anterior";

    private TextToSpeechService textToSpeechService = new TextToSpeechService();

    private List<String> comandos = newArrayList(NOVEDADES, LEER_MENSAJES_NUEVOS, CONVERSACIÓN,
            ENVIAR_MENSAJE, CANCELAR, COMANDOS, AYUDA, SALIR, ENTRAR, SIGUIENTE, ANTERIOR);

    private PersistenceService persistenceService = new PersistenceService();
    private MessageDao messageDao = new MessageDao();
    private DummyLoader dummyLoader = new DummyLoader();
    private MessengerConnector messengerConnector = new MessengerConnector();

    private SendingHandler sendingHandler;
    private NewsHandler newsHandler;
    private ConversationHandler conversationHandler;

    ListView list;
    CustomListAdapter adapter;

    private boolean sendingMessage = false;

    private MenuStep step;

    private boolean shortPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.newsHandler = new NewsHandler();
        this.newsHandler.setPersistenceService(this.persistenceService);
        this.newsHandler.setTextToSpeechService(this.textToSpeechService);

        this.conversationHandler = new ConversationHandler();
        this.conversationHandler.setTextToSpeechService(this.textToSpeechService);
        this.conversationHandler.setMessageDao(this.messageDao);

        this.sendingHandler = new SendingHandler();
        this.sendingHandler.setTextToSpeechService(this.textToSpeechService);
        this.sendingHandler.setPersistenceService(this.persistenceService);
        this.sendingHandler.setMessengerConnector(this.messengerConnector);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=(ListView)findViewById(R.id.list);

        reloadAdapter();
        checkPermissions();

        Context context = this.getApplicationContext();
        if (this.messageDao.all(context).size() == 0) {
            this.dummyLoader.load(context);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Listenchat-msg"));

        textToSpeechService.speak(getString(R.string.welcome_message), this, this.buildStartCallback());
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
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_HEADSETHOOK || keyCode == KEYCODE_MEDIA_PLAY || keyCode == KEYCODE_MEDIA_PLAY_PAUSE) {
            shortPress = false;
            this.step = null;
            this.handleOkButton();
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean processed = false;

        switch (keyCode) {
            case KEYCODE_MEDIA_NEXT:
            case KEYCODE_VOLUME_UP:
                processed = handleNextButton();
                break;
            case KEYCODE_MEDIA_PREVIOUS:
            case KEYCODE_VOLUME_DOWN:
                processed = handlePreviousButton();
                break;
            case KEYCODE_MEDIA_PLAY_PAUSE:
            case KEYCODE_MEDIA_PLAY:
            case KEYCODE_HEADSETHOOK:
                event.startTracking();
                if(event.getRepeatCount() == 0){
                    shortPress = true;
                }
                return true;
        }

        return processed || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_HEADSETHOOK || keyCode == KEYCODE_MEDIA_PLAY || keyCode == KEYCODE_MEDIA_PLAY_PAUSE) {
            if(shortPress){
                handleOkButton();
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean handlePreviousButton() {
        if (step == null) {
            return false;
        }

        textToSpeechService.stop();

        if (Step.MAIN.equals(this.step.getStep())) {
            Substep previous = Substep.previous(this.step.getSubstep());
            this.step.setSubstep(previous);
            this.textToSpeechService.speak(previous.getDescription(),this, this.buildStartCallback());
            return true;
        }

        if (CONVERSATION.equals(this.step.getStep())) {
            if (SELECT_CONTACT.equals(this.step.getSubstep())) {
                previousContact();
            } else {
                this.conversationHandler.previous(this);
            }

            return true;
        }

        return false;
    }

    private void previousContact() {
        List<String> contacts = this.persistenceService.getContacts(this);

        if (!contacts.isEmpty()) {
            if (this.step.getContact() == null) {
                step.setContact(contacts.get(0));
            } else {
                int idx = contacts.indexOf(this.step.getContact()) - 1;
                this.step.setContact(idx >= 0 ? contacts.get(idx) : contacts.get(contacts.size() - 1));
            }

            textToSpeechService.speak(this.step.getContact(), this, this.buildStartCallback());
        } else {
            textToSpeechService.speak("No hay contactos", this, this.buildStartCallback());
            this.step = null;
        }

    }

    private boolean handleNextButton() {
        if (step == null) {
            return false;
        }

        textToSpeechService.stop();

        if (Step.MAIN.equals(this.step.getStep())) {
            Substep next = Substep.next(this.step.getSubstep());
            this.step.setSubstep(next);
            this.textToSpeechService.speak(next.getDescription(),this, this.buildStartCallback());
            return true;
        }

        if (CONVERSATION.equals(this.step.getStep())) {
            if (SELECT_CONTACT.equals(this.step.getSubstep())) {
                nextContact();
            } else {
                this.conversationHandler.following(this);
            }

            return true;
        }

        return false;
    }

    private void nextContact() {
        List<String> contacts = this.persistenceService.getContacts(this);

        if (!contacts.isEmpty()) {
            if (this.step.getContact() == null) {
                step.setContact(contacts.get(0));
            } else {
                int idx = contacts.indexOf(this.step.getContact()) + 1;
                this.step.setContact(idx < contacts.size() ? contacts.get(idx) : contacts.get(0));
            }

            textToSpeechService.speak(this.step.getContact(), this, this.buildStartCallback());
        } else {
            textToSpeechService.speak("No hay contactos", this, this.buildStartCallback());
            this.step = null;
        }
    }

    private boolean handleOkButton() {
        textToSpeechService.stop();

        if (this.step == null) {
            step = new MenuStep();
            step.setStep(Step.MAIN);
            step.setSubstep(Substep.MESSAGES);
            textToSpeechService.speak("Menú principal", this, this.buildStartCallback());
            textToSpeechService.speak(step.getSubstep().getDescription(), this, this.buildStartCallback());
        } else {
            if (Step.MAIN.equals(step.getStep())) {
                switch (this.step.getSubstep()) {
                    case NOVELTIES:
                        this.newsHandler.tellTheNovelties(this);
                        this.step = null;
                        break;

                    case MESSAGES:
                        this.newsHandler.tellTheNewMessages(this);
                        this.step = null;
                        break;

                    case CONVERSATION:
                        this.step.setSubstep(SELECT_CONTACT);
                        this.step.setStep(CONVERSATION);
                        this.textToSpeechService.speak(SELECT_CONTACT.getDescription(), this, this.buildStartCallback());
                        break;
                }
            }else if (CONVERSATION.equals(step.getStep()) && SELECT_CONTACT.equals(step.getSubstep())
                    && step.getContact() != null) {
                this.step.setSubstep(Substep.READ);
                this.conversationHandler.prepareConversation(this, step.getContact());
            }

        }

        return true;
    }

    @Override
    public void processVoiceCommands(String... voiceCommands) {
        List<String> filtered = filterCommands(voiceCommands);

        if (!filtered.isEmpty()) {
            if (this.sendingMessage) {
                this.sendingHandler.sendMessage(this, filtered.get(0).toLowerCase());
            } else {
                switch (filtered.get(0).toLowerCase()) {
                    case NOVEDADES:
                        this.newsHandler.tellTheNovelties(this);
                        break;
                    case LEER_MENSAJES_NUEVOS:
                        this.newsHandler.tellTheNewMessages(this);
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
                        this.conversationHandler.following(this);
                        break;
                    case ANTERIOR:
                        this.conversationHandler.previous(this);
                        break;
                    case DIA_SIGUIENTE:
                        this.conversationHandler.followingDay(this);
                        break;
                    case DIA_ANTERIOR:
                        this.conversationHandler.previousDay(this);
                        break;
                    default:
                        this.handleDefault(filtered.get(0).toLowerCase());
                        break;
                }

            }
        }

        restartListeningService();
    }


    private void handleDefault(String command) {
        if (command.contains("conversación con")) {
            String contact = replace(command, "conversación con ", "");
            this.conversationHandler.prepareConversation(this, contact);
        } else if (command.contains("enviar mensaje a")) {
            String contact = replace(command, "enviar mensaje a ", "");
            this.sendingHandler.prepareMessage(this, contact);
        } else {
            textToSpeechService.speak("Comando desconocido", this, this.buildStartCallback());
        }
    }


    private List<String> filterCommands(String[] voiceCommands) {
        if (this.sendingMessage) {
            return newArrayList(voiceCommands);
        }

        return newArrayList(filter(Arrays.asList(voiceCommands), new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                boolean valid = false;

                for (String command : comandos) {
                    if (input != null && input.toLowerCase().contains(command) || input.toLowerCase().contains("conversación con")) {
                        valid = true;
                    }
                }
                return valid;
            }
        }));
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
        Cursor cursor = persistenceService.getAllCursor(getApplicationContext());
        if (cursor.moveToFirst()) {
            do {
                if (message.getIntentId().equals(cursor.getString(1))) {
                    return true;
                }
            } while (cursor.moveToNext());
        }
        return false;
    }

    public TextToSpeechCallaback buildStartCallback() {
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
