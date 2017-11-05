package edu.utn.listenchat.handler.voice;

import edu.utn.listenchat.handler.AbstractHandler;
import edu.utn.listenchat.service.TextToSpeechService;


public class NotFoundHandler extends AbstractHandler {

    public void sayMessage() {
        textToSpeechService.speak("Comando desconocido", getResumeCallback());
    }

}