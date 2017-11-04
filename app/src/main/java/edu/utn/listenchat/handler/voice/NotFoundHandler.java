package edu.utn.listenchat.handler.voice;

import edu.utn.listenchat.service.TextToSpeechService;


public class NotFoundHandler {

    private TextToSpeechService textToSpeechService;

    public void sayMessage() {
        textToSpeechService.speak("Comando desconocido");
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

}