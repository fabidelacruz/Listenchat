package edu.utn.listenchat.model;

import edu.utn.listenchat.handler.voice.VoiceCommandHandler;

import static edu.utn.listenchat.utils.StringUtils.safeContains;
import static edu.utn.listenchat.utils.StringUtils.safeEquals;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public enum VoiceCommand {

    COMMANDS(TRUE, "comandos") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleCommands();
        }
    },

    CONVERSATION_WITH(FALSE, "conversación con") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleConversationWith(receivedCommand);
        }
    },

    FOLLOWING(TRUE, "siguiente") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleFollowing();
        }
    },

    FOLLOWING_DAY(TRUE, "día siguiente") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleFollowingDay();
        }
    },

    HELP(TRUE, "ayuda") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleHelp();
        }
    },

    NEW_MESSAGES(TRUE, "leer mensajes nuevos") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleNewMessages();
        }
    },

    NOVELTIES(TRUE, "novedades") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleNovelties();
        }
    },

    PREVIOUS(TRUE, "anterior") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handlePrevious();
        }
    },

    PREVIOUS_DAY(TRUE, "día anterior") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handlePreviousDay();
        }
    },

    EXIT(TRUE, "salir") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleExit();
        }
    },

    EXPLAIN(FALSE, "explicar comando") {
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleExplain(receivedCommand);
        }
    },

    SEND_MESSAGE(FALSE, "enviar mensaje a") {Ine
        @Override
        public void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand) {
            voiceCommandHandler.handleSendMessage(receivedCommand);
        }
    };


    private boolean fullMatch;
    private String text;


    VoiceCommand(boolean fullMatch, String text) {
        this.fullMatch = fullMatch;
        this.text = text;
    }

    public static VoiceCommand findCommand(String receivedMessage) {
        for (VoiceCommand voiceCommand : values()) {
            if (voiceCommand.match(receivedMessage)) {
                return voiceCommand;
            }
        }
        return null;
    }

    private boolean match(String receivedCommand) {
        return this.fullMatch ? safeEquals(receivedCommand, this.text) : safeContains(receivedCommand, this.text);
    }

    public abstract void execute(VoiceCommandHandler voiceCommandHandler, String receivedCommand);

    public String getText() {
        return text;
    }

}
