package edu.utn.listenchat.handler.voice;

import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.ExitHandler;
import edu.utn.listenchat.handler.common.NewsHandler;
import edu.utn.listenchat.handler.common.SendingHandler;

import static org.apache.commons.lang3.StringUtils.replace;


public class VoiceCommandHandler {

    private ConversationHandler conversationHandler;
    private SendingHandler sendingHandler;
    private NewsHandler newsHandler;
    private ExitHandler exitHandler;


    public void handleCancel() {
    }

    public void handleCommands() {
    }

    public void handleConversationWith(String receivedCommand) {
        String contact = replace(receivedCommand, "conversación con ", "");
        this.conversationHandler.prepareConversation(contact);
    }

    public void handleFollowing() {
        this.conversationHandler.following();
    }

    public void handleFollowingDay() {
        this.conversationHandler.followingDay();
    }

    public void handleHelp() {
    }

    public void handleNewMessages() {
        this.newsHandler.sayNewMessages();
    }

    public void handleNovelties() {
        this.newsHandler.sayNovelties();
    }

    public void handlePrevious() {
        this.conversationHandler.previous();
    }

    public void handlePreviousDay() {
        this.conversationHandler.previousDay();
    }

    public void handleQuit() {
        exitHandler.handleQuit();
    }

    public void handleSendMessage(String receivedMessage) {
        String contact = replace(receivedMessage, "enviar mensaje a ", "");
        this.sendingHandler.prepareMessage(contact);
    }


    public void setConversationHandler(ConversationHandler conversationHandler) {
        this.conversationHandler = conversationHandler;
    }

    public void setSendingHandler(SendingHandler sendingHandler) {
        this.sendingHandler = sendingHandler;
    }

    public void setNewsHandler(NewsHandler newsHandler) {
        this.newsHandler = newsHandler;
    }

    public void setExitHandler(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }
}