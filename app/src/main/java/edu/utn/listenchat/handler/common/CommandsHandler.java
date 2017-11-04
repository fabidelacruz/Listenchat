package edu.utn.listenchat.handler.common;


import edu.utn.listenchat.handler.AbstractHandler;

import static edu.utn.listenchat.model.VoiceCommand.COMMANDS;
import static edu.utn.listenchat.model.VoiceCommand.CONVERSATION_WITH;
import static edu.utn.listenchat.model.VoiceCommand.EXIT;
import static edu.utn.listenchat.model.VoiceCommand.HELP;
import static edu.utn.listenchat.model.VoiceCommand.NEW_MESSAGES;
import static edu.utn.listenchat.model.VoiceCommand.NOVELTIES;
import static edu.utn.listenchat.model.VoiceCommand.SEND_MESSAGE;

public class CommandsHandler extends AbstractHandler {

    public void handleCommands() {
        this.stopListening();
        this.textToSpeechService.speak("Comandos");
        this.textToSpeechService.speak(NOVELTIES.getText());
        this.textToSpeechService.speak(NEW_MESSAGES.getText());
        this.textToSpeechService.speak(CONVERSATION_WITH.getText());
        this.textToSpeechService.speak(SEND_MESSAGE.getText());
        this.textToSpeechService.speak(COMMANDS.getText());
        this.textToSpeechService.speak(EXIT.getText());
        this.textToSpeechService.speak(HELP.getText(), getResumeCallback());
    }

}
