package edu.utn.listenchat.handler.common;

import edu.utn.listenchat.handler.voice.NotFoundHandler;
import edu.utn.listenchat.model.VoiceCommand;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.model.VoiceCommand.COMMANDS;
import static edu.utn.listenchat.model.VoiceCommand.CONVERSATION_WITH;
import static edu.utn.listenchat.model.VoiceCommand.EXIT;
import static edu.utn.listenchat.model.VoiceCommand.HELP;
import static edu.utn.listenchat.model.VoiceCommand.NEW_MESSAGES;
import static edu.utn.listenchat.model.VoiceCommand.NOVELTIES;
import static edu.utn.listenchat.model.VoiceCommand.SEND_MESSAGE;
import static java.lang.String.format;

public class ExplainHandler {

    private TextToSpeechService textToSpeechService;
    private NotFoundHandler notFoundHandler;

    public void handleHelp(String command) {

        VoiceCommand voiceCommand = VoiceCommand.findCommand(command);
        if (voiceCommand == null) {
            this.notFoundHandler.sayMessage();
        }

        switch (voiceCommand) {
            case NOVELTIES:
                this.say(NOVELTIES, "Lista la cantidad de mensajes nuevos por contacto");
                break;
            case NEW_MESSAGES:
                this.say(NEW_MESSAGES, "Lista todos los mensajes nuevos");
                break;
            case CONVERSATION_WITH:
                this.say(CONVERSATION_WITH, ". Seguido del nombre del contacto. Permite recorrer " +
                        "todos los mensajes enviados y recibidos con éste, ordenados cronolóogiamente." +
                        "Por defecto se comienza a recorrer desde el primer mensaje del último día" +
                        "de conversación. Para recorrer los mensajes del mismo día diga los comandos " +
                        "siguiente y anterior. Para saltar de un día hacia otro diga dia siguiente" +
                        "y dia anterior");
                break;
            case SEND_MESSAGE:
                this.say(SEND_MESSAGE, ". Seguido del nombre del contacto. Permite enviar un mensaje" +
                        "de voz para un contacto. Luego del dictado, se abrirá la aplicación de " +
                        "messenger esperando que el usuario confirme el envío.");
                break;
            case COMMANDS:
                this.say(COMMANDS, "Lista el conjunto de comandos con los que cuenta el usuario");
                break;
            case EXIT:
                this.say(EXIT, "Permite cerrar la aplicación");
                break;
            case HELP:
                this.say((HELP), "Cuenta un breve puntapié inicial para que puedas comenzar a usar" +
                        "Listenchat");
                break;
            default:
                this.textToSpeechService.speak("Comando no explicado");
        }
    }

    private void say(VoiceCommand command, String text) {
        this.textToSpeechService.speak(format("%s: %s", command.getText(), text));
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setNotFoundHandler(NotFoundHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }
}
