package edu.utn.listenchat.handler.button;

import edu.utn.listenchat.activity.State;
import edu.utn.listenchat.handler.common.CommandsHandler;
import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.ExitHandler;
import edu.utn.listenchat.handler.common.ExplainHandler;
import edu.utn.listenchat.handler.common.HelpHandler;
import edu.utn.listenchat.handler.common.NewsHandler;
import edu.utn.listenchat.model.MenuStep;
import edu.utn.listenchat.model.Step;
import edu.utn.listenchat.model.Substep;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.model.Step.CONVERSATION;
import static edu.utn.listenchat.model.Step.EXPLAIN;
import static edu.utn.listenchat.model.Substep.SELECT_COMMAND;
import static edu.utn.listenchat.model.Substep.SELECT_CONTACT;


public class ButtonOkHandler {

    private NewsHandler newsHandler;
    private TextToSpeechService textToSpeechService;
    private ConversationHandler conversationHandler;
    private CommandsHandler commandsHandler;
    private HelpHandler helpHandler;
    private ExitHandler exitHandler;
    private ExplainHandler explainHandler;

    public boolean handle() {
        textToSpeechService.stop();

        MenuStep step = State.getState().getMenuStep();
        if (step == null) {
            step = new MenuStep();
            step.setStep(Step.MAIN);
            step.setSubstep(Substep.MESSAGES);
            State.getState().setMenuStep(step);
            textToSpeechService.speak("Menú principal");
            textToSpeechService.speak(step.getSubstep().getDescription());
        } else {
            if (Step.MAIN.equals(step.getStep())) {
                switch (step.getSubstep()) {
                    case NOVELTIES:
                        this.newsHandler.sayNovelties();
                        State.getState().setMenuStep(null);
                        break;

                    case MESSAGES:
                        this.newsHandler.sayNewMessages();
                        State.getState().setMenuStep(null);
                        break;

                    case CONVERSATION:
                    case SEND_MESSAGE:
                        step.setSubstep(SELECT_CONTACT);
                        step.setStep(CONVERSATION);
                        this.textToSpeechService.speak(SELECT_CONTACT.getDescription());
                        break;

                    case HELP:
                        helpHandler.handleHelp();
                        State.getState().setMenuStep(null);
                        break;

                    case COMMANDS:
                        commandsHandler.handleCommands();
                        State.getState().setMenuStep(null);
                        break;

                    case EXIT:
                        exitHandler.handleExit();
                        break;

                    case EXPLAIN:
                        step.setStep(EXPLAIN);
                        step.setSubstep(SELECT_COMMAND);
                        this.textToSpeechService.speak(SELECT_COMMAND.getDescription());
                        break;
                }
            } else if (CONVERSATION.equals(step.getStep()) && SELECT_CONTACT.equals(step.getSubstep())
                    && step.getContact() != null) {
                step.setSubstep(Substep.READ);
                this.conversationHandler.prepareConversation(step.getContact());
            }  else if (EXPLAIN.equals(step.getStep()) && SELECT_COMMAND.equals(step.getSubstep())
                    && step.getContact() != null) {
                this.explainHandler.handleHelp(State.getState().getExplainedCommand().getText());
                State.getState().setMenuStep(null);
            }

        }

        return true;
    }


    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

    public void setNewsHandler(NewsHandler newsHandler) {
        this.newsHandler = newsHandler;
    }

    public void setConversationHandler(ConversationHandler conversationHandler) {
        this.conversationHandler = conversationHandler;
    }

    public void setCommandsHandler(CommandsHandler commandsHandler) {
        this.commandsHandler = commandsHandler;
    }

    public void setHelpHandler(HelpHandler helpHandler) {
        this.helpHandler = helpHandler;
    }

    public void setExitHandler(ExitHandler exitHandler) {
        this.exitHandler = exitHandler;
    }

    public void setExplainHandler(ExplainHandler explainHandler) {
        this.explainHandler = explainHandler;
    }
}