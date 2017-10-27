package edu.utn.listenchat.handler.button;

import android.view.KeyEvent;

import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static edu.utn.listenchat.activity.State.getState;
import static java.lang.Boolean.FALSE;


public class LongPressHandler {

    private ButtonOkHandler buttonOkHandler;

    public boolean handle(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_HEADSETHOOK || keyCode == KEYCODE_MEDIA_PLAY || keyCode == KEYCODE_MEDIA_PLAY_PAUSE) {
            getState().setShortPress(FALSE);
            getState().setMenuStep(null);
            this.buttonOkHandler.handle();
            return true;
        }
        return false;

    }

    public void setButtonOkHandler(ButtonOkHandler buttonOkHandler) {
        this.buttonOkHandler = buttonOkHandler;
    }

}
