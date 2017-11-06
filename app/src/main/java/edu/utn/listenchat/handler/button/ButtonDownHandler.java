package edu.utn.listenchat.handler.button;

import android.view.KeyEvent;

import java.util.List;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.ExplainHandler;
import edu.utn.listenchat.model.MenuStep;
import edu.utn.listenchat.model.Step;
import edu.utn.listenchat.model.Substep;
import edu.utn.listenchat.model.VoiceCommand;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static android.view.KeyEvent.KEYCODE_HOME;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;
import static edu.utn.listenchat.activity.State.getState;
import static edu.utn.listenchat.model.Step.CONVERSATION;
import static edu.utn.listenchat.model.Step.EXPLAIN;
import static edu.utn.listenchat.model.Step.SEND_MESSAGE;
import static edu.utn.listenchat.model.Substep.SELECT_COMMAND;
import static edu.utn.listenchat.model.Substep.SELECT_CONTACT;
import static java.lang.Boolean.TRUE;


public class ButtonDownHandler {

    private MainActivity mainActivity;
    private TextToSpeechService textToSpeechService;
    private ConversationHandler conversationHandler;
    private PersistenceService persistenceService;

    public boolean handle(int keyCode, KeyEvent event) {
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
            case KEYCODE_HOME:
                event.startTracking();
                if(event.getRepeatCount() == 0){
                    getState().setShortPress(TRUE);
                }
                return true;
        }

        return processed || this.mainActivity.superOnKeyDown(keyCode, event);
    }

    private boolean handlePreviousButton() {
        MenuStep step = getState().getMenuStep();

        if (step == null) {
            return false;
        }

        textToSpeechService.stop();

        if (Step.MAIN.equals(step.getStep())) {
            Substep previous = Substep.previous(step.getSubstep());
            step.setSubstep(previous);
            this.textToSpeechService.speak(previous.getDescription());
            return true;
        }

        if (CONVERSATION.equals(step.getStep())) {
            if (SELECT_CONTACT.equals(step.getSubstep())) {
                previousContact();
            } else {
                this.conversationHandler.previous();
            }

            return true;
        }

        if (EXPLAIN.equals(step.getStep())) {
            if (SELECT_COMMAND.equals(step.getSubstep())) {
                previousCommand();
            }

            return true;
        }

        return false;
    }

    private void previousContact() {
        List<String> contacts = this.persistenceService.getContacts();

        MenuStep step = getState().getMenuStep();
        if (!contacts.isEmpty()) {
            if (step.getContact() == null) {
                step.setContact(contacts.get(0));
            } else {
                int idx = contacts.indexOf(step.getContact()) - 1;
                step.setContact(idx >= 0 ? contacts.get(idx) : contacts.get(contacts.size() - 1));
            }

            textToSpeechService.speak(step.getContact());
        } else {
            textToSpeechService.speak("No hay contactos");
            step = null;
        }

    }

    private void previousCommand() {
        List<VoiceCommand> commands = ExplainHandler.AVAILABLE_COMMAND;

        if (getState().getExplainedCommand() == null) {
            getState().setExplainedCommand(commands.get(0));
        } else {
            int idx = commands.indexOf(getState().getExplainedCommand()) - 1;
            getState().setExplainedCommand(idx >= 0 ? commands.get(idx) : commands.get(commands.size() - 1));
        }

        textToSpeechService.speak(getState().getExplainedCommand().getText());

    }

    private void nextCommand() {
        List<VoiceCommand> commands = ExplainHandler.AVAILABLE_COMMAND;

        if (getState().getExplainedCommand() == null) {
            getState().setExplainedCommand(commands.get(0));
        } else {
            int idx = commands.indexOf(getState().getExplainedCommand()) + 1;
            getState().setExplainedCommand(idx <= commands.size() - 1 ? commands.get(idx) : commands.get(0));
        }

        textToSpeechService.speak(getState().getExplainedCommand().getText());

    }

    private boolean handleNextButton() {
        MenuStep step = getState().getMenuStep();

        if (step == null) {
            return false;
        }

        textToSpeechService.stop();

        if (Step.MAIN.equals(step.getStep())) {
            Substep next = Substep.next(step.getSubstep());
            step.setSubstep(next);
            this.textToSpeechService.speak(next.getDescription());
            return true;
        }

        if (CONVERSATION.equals(step.getStep())) {
            if (SELECT_CONTACT.equals(step.getSubstep())) {
                nextContact();
            } else {
                this.conversationHandler.following();
            }

            return true;
        }

        if (SEND_MESSAGE.equals(step.getStep())) {
            if (SELECT_CONTACT.equals(step.getSubstep())) {
                nextContact();
            }
            return true;
        }


        if (EXPLAIN.equals(step.getStep())) {
            if (SELECT_COMMAND.equals(step.getSubstep())) {
                nextCommand();
            }

            return true;
        }

        return false;
    }

    private void nextContact() {
        List<String> contacts = this.persistenceService.getContacts();

        MenuStep step = getState().getMenuStep();

        if (!contacts.isEmpty()) {
            if (step.getContact() == null) {
                step.setContact(contacts.get(0));
            } else {
                int idx = contacts.indexOf(step.getContact()) + 1;
                step.setContact(idx < contacts.size() ? contacts.get(idx) : contacts.get(0));
            }

            textToSpeechService.speak(step.getContact());
        } else {
            textToSpeechService.speak("No hay contactos");
            step = null;
        }
    }


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setConversationHandler(ConversationHandler conversationHandler) {
        this.conversationHandler = conversationHandler;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

}
