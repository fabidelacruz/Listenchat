package edu.utn.listenchat.handler.voice;

import edu.utn.listenchat.handler.common.CommandsHandler;
import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.ExitHandler;
import edu.utn.listenchat.handler.common.ExplainHandler;
import edu.utn.listenchat.handler.common.HelpHandler;
import edu.utn.listenchat.handler.common.NewsHandler;
import edu.utn.listenchat.handler.common.SendingHandler;

import static org.apache.commons.lang3.StringUtils.replace;


public class VoiceCommandHandler {

    private ConversationHandler conversationHandler;
    private SendingHandler sendingHandler;
    private NewsHandler newsHandler;
    private ExitHandler exitHandler;
    private CommandsHandler commandsHandler;
    private HelpHandler helpHandler;
    private ExplainHandler explainHandler;

    public void handleAgain() {
        this.conversationHandler.again();
    }

    public void handleClear() {
        this.newsHandler.handleClear();
    }

    public void handleCommands() {
        this.commandsHandler.handleCommands();
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
        this.helpHandler.handleHelp();
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

    public void handleExit() {
        exitHandler.handleExit();
    }

    public void handleExplain(String receivedCommand) {
        String contact = replace(receivedCommand, "explicar comando ", "");
        this.explainHandler.handleHelp(contact);
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

    public void setCommandsHandler(CommandsHandler commandsHandler) {
        this.commandsHandler = commandsHandler;
    }

    public void setHelpHandler(HelpHandler helpHandler) {
        this.helpHandler = helpHandler;
    }

    public void setExplainHandler(ExplainHandler explainHandler) {
        this.explainHandler = explainHandler;
    }
}