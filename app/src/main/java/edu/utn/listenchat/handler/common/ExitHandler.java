package edu.utn.listenchat.handler.common;

import edu.utn.listenchat.activity.MainActivity;

public class ExitHandler {

    private MainActivity activity;

    public void handleExit() {
        activity.finish();
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }
}
