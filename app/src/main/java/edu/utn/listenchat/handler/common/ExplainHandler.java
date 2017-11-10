package edu.utn.listenchat.handler.common;

import com.google.common.collect.Lists;

import java.util.List;

import edu.utn.listenchat.handler.AbstractHandler;
import edu.utn.listenchat.handler.voice.NotFoundHandler;
import edu.utn.listenchat.model.VoiceCommand;

import static edu.utn.listenchat.model.VoiceCommand.CLEAR;
import static edu.utn.listenchat.model.VoiceCommand.COMMANDS;
import static edu.utn.listenchat.model.VoiceCommand.CONVERSATION_WITH;
import static edu.utn.listenchat.model.VoiceCommand.EXIT;
import static edu.utn.listenchat.model.VoiceCommand.EXPLAIN;
import static edu.utn.listenchat.model.VoiceCommand.HELP;
import static edu.utn.listenchat.model.VoiceCommand.NEW_MESSAGES;
import static edu.utn.listenchat.model.VoiceCommand.NOVELTIES;
import static edu.utn.listenchat.model.VoiceCommand.SEND_MESSAGE;
import static java.lang.String.format;

public class ExplainHandler extends AbstractHandler {

    public static final List<VoiceCommand> AVAILABLE_COMMAND = Lists.newArrayList(NOVELTIES,
            NEW_MESSAGES, CONVERSATION_WITH, SEND_MESSAGE, COMMANDS, EXIT, HELP);

    private NotFoundHandler notFoundHandler;

    public void handleHelp(String command) {
        stopListening();

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
                        "los comandos. Siguiente. y. Anterior. Para repetir el mensaje diga. De nuevo. " +
                        "Para saltar de día de conversación dicte los comandos. Día siguiente. y. Día anterior");
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
            case CLEAR:
                this.say(CLEAR, "Permite limpiar la bandeja de mensajes nuevos en caso de ya " +
                        "haber sido escuchados.");
                break;
            default:
                this.textToSpeechService.speak("Comando no explicado", getResumeCallback());
        }
    }

    private void say(VoiceCommand command, String text) {
        this.textToSpeechService.speak(format("%s: %s", command.getText(), text), getResumeCallback());
    }

    public void setNotFoundHandler(NotFoundHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }
}
