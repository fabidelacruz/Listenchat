package edu.utn.listenchat.handler.button;


import android.view.KeyEvent;

import edu.utn.listenchat.activity.MainActivity;

import static android.view.KeyEvent.KEYCODE_HEADSETHOOK;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static edu.utn.listenchat.activity.State.getState;
import static java.lang.Boolean.FALSE;

public class ButtonUpHandler {

    private MainActivity mainActivity;
    private ButtonOkHandler buttonOkHandler;

    public boolean handle(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_HEADSETHOOK || keyCode == KEYCODE_MEDIA_PLAY || keyCode == KEYCODE_MEDIA_PLAY_PAUSE) {
            if(getState().isShortPress()){
                this.buttonOkHandler.handle();
            }
            getState().setShortPress(FALSE);
            return true;
        }
        return mainActivity.superOnKeyUp(keyCode, event);
    }


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setButtonOkHandler(ButtonOkHandler buttonOkHandler) {
        this.buttonOkHandler = buttonOkHandler;
    }

}
