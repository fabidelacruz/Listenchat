package edu.utn.listenchat.handler.button;

import edu.utn.listenchat.activity.MainActivity;
import edu.utn.listenchat.activity.State;
import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.NewsHandler;
import edu.utn.listenchat.model.MenuStep;
import edu.utn.listenchat.model.Step;
import edu.utn.listenchat.model.Substep;
import edu.utn.listenchat.service.TextToSpeechService;

import static edu.utn.listenchat.model.Step.CONVERSATION;
import static edu.utn.listenchat.model.Substep.SELECT_CONTACT;


public class ButtonOkHandler {

    private NewsHandler newsHandler;
    private TextToSpeechService textToSpeechService;
    private ConversationHandler conversationHandler;

    public boolean handle() {
        textToSpeechService.stop();

        MenuStep step = State.getState().getMenuStep();
        if (step == null) {
            step = new MenuStep();
            step.setStep(Step.MAIN);
            step.setSubstep(Substep.MESSAGES);
            textToSpeechService.speak("Menú principal");
            textToSpeechService.speak(step.getSubstep().getDescription());
        } else {
            if (Step.MAIN.equals(step.getStep())) {
                switch (step.getSubstep()) {
                    case NOVELTIES:
                        this.newsHandler.sayNovelties();
                        step = null;
                        break;

                    case MESSAGES:
                        this.newsHandler.sayNewMessages();
                        step = null;
                        break;

                    case CONVERSATION:
                        step.setSubstep(SELECT_CONTACT);
                        step.setStep(CONVERSATION);
                        this.textToSpeechService.speak(SELECT_CONTACT.getDescription());
                        break;
                }
            } else if (CONVERSATION.equals(step.getStep()) && SELECT_CONTACT.equals(step.getSubstep())
                    && step.getContact() != null) {
                step.setSubstep(Substep.READ);
                this.conversationHandler.prepareConversation(step.getContact());
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

}