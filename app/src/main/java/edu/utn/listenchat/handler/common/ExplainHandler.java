package edu.utn.listenchat.handler.common;

import com.google.common.collect.Lists;

import java.util.List;

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

    public static final List<VoiceCommand> AVAILABLE_COMMAND = Lists.newArrayList(NOVELTIES,
            NEW_MESSAGES, CONVERSATION_WITH, SEND_MESSAGE, COMMANDS, EXIT, HELP);

    private TextToSpeechService textToSpeechService;
    private NotFoundHandler notFoundHandler;

    public void handleHelp(String command) {

        VoiceCommand voiceCommand = VoiceCommand.findCommand(command);
        if (voiceCommand == null) {
            this.notFoundHandler.sayMessage();
            return;
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
                        "todos los mensajes enviados y recibidos con este contacto, ordenados " +
                        "cronológicamente. Se comienza a recorrer desde el primer mensaje del " +
                        "último día de conversación. Para recorrer los mensajes del mismo día dicte " +
                        "los comandos. Siguiente. Y. Anterior. Para saltar de día de conversación " +
                        "dicte los comandos. Día siguiente. Y. Día anterior");
                break;
            case SEND_MESSAGE:
                this.say(SEND_MESSAGE, ". Seguido del nombre del contacto. Permite enviar un mensaje " +
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
                this.say((HELP), "Cuenta un breve puntapié inicial para que puedas comenzar a usar " +
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
