package edu.utn.listenchat.handler.common;

import edu.utn.listenchat.handler.AbstractHandler;

public class ExitHandler extends AbstractHandler {

    public void handleExit() {
        activity.finish();
    }

}
