package edu.utn.listenchat.activity;


import edu.utn.listenchat.connector.MessengerConnector;
import edu.utn.listenchat.db.MessageDao;
import edu.utn.listenchat.handler.button.ButtonDownHandler;
import edu.utn.listenchat.handler.button.ButtonOkHandler;
import edu.utn.listenchat.handler.button.ButtonUpHandler;
import edu.utn.listenchat.handler.button.LongPressHandler;
import edu.utn.listenchat.handler.common.CommandsHandler;
import edu.utn.listenchat.handler.common.ConversationHandler;
import edu.utn.listenchat.handler.common.ExitHandler;
import edu.utn.listenchat.handler.common.NewsHandler;
import edu.utn.listenchat.handler.common.SendingHandler;
import edu.utn.listenchat.handler.voice.NotFoundHandler;
import edu.utn.listenchat.handler.voice.VoiceCommandHandler;
import edu.utn.listenchat.service.DummyLoader;
import edu.utn.listenchat.service.PersistenceService;
import edu.utn.listenchat.service.TextToSpeechService;

public class Injector {

    public static void injectDependencies(MainActivity mainActivity) {
        PersistenceService persistenceService = new PersistenceService();
        persistenceService.setMainActivity(mainActivity);

        MessengerConnector messengerConnector = new MessengerConnector();
        messengerConnector.setMainActivity(mainActivity);
        messengerConnector.setPersistenceService(persistenceService);

        TextToSpeechService textToSpeechService = new TextToSpeechService();
        textToSpeechService.setMainActivity(mainActivity);

        DummyLoader dummyLoader = new DummyLoader();
        dummyLoader.setPersistenceService(persistenceService);

        MessageDao messageDao = new MessageDao();
        messageDao.setPersistenceService(persistenceService);

        NewsHandler newsHandler = new NewsHandler();
        newsHandler.setPersistenceService(persistenceService);
        newsHandler.setTextToSpeechService(textToSpeechService);

        ConversationHandler conversationHandler = new ConversationHandler();
        conversationHandler.setTextToSpeechService(textToSpeechService);
        conversationHandler.setMessageDao(messageDao);

        SendingHandler sendingHandler = new SendingHandler();
        sendingHandler.setMainActivity(mainActivity);
        sendingHandler.setTextToSpeechService(textToSpeechService);
        sendingHandler.setPersistenceService(persistenceService);
        sendingHandler.setMessengerConnector(messengerConnector);

        ExitHandler exitHandler = new ExitHandler();
        exitHandler.setActivity(mainActivity);

        CommandsHandler commandsHandler = new CommandsHandler();
        commandsHandler.setTextToSpeechService(textToSpeechService);

        VoiceCommandHandler voiceCommandHandler = new VoiceCommandHandler();
        voiceCommandHandler.setConversationHandler(conversationHandler);
        voiceCommandHandler.setSendingHandler(sendingHandler);
        voiceCommandHandler.setNewsHandler(newsHandler);
        voiceCommandHandler.setExitHandler(exitHandler);
        voiceCommandHandler.setCommandsHandler(commandsHandler);

        NotFoundHandler notFoundHandler = new NotFoundHandler();
        notFoundHandler.setTextToSpeechService(textToSpeechService);

        ButtonOkHandler buttonOkHandler = new ButtonOkHandler();
        buttonOkHandler.setTextToSpeechService(textToSpeechService);
        buttonOkHandler.setConversationHandler(conversationHandler);
        buttonOkHandler.setNewsHandler(newsHandler);

        ButtonUpHandler buttonUpHandler = new ButtonUpHandler();
        buttonUpHandler.setMainActivity(mainActivity);
        buttonUpHandler.setButtonOkHandler(buttonOkHandler);

        ButtonDownHandler buttonDownHandler = new ButtonDownHandler();
        buttonDownHandler.setMainActivity(mainActivity);
        buttonDownHandler.setConversationHandler(conversationHandler);
        buttonDownHandler.setTextToSpeechService(textToSpeechService);
        buttonDownHandler.setPersistenceService(persistenceService);

        LongPressHandler longPressHandler = new LongPressHandler();
        longPressHandler.setButtonOkHandler(buttonOkHandler);

        mainActivity.setPersistenceService(persistenceService);
        mainActivity.setTextToSpeechService(textToSpeechService);
        mainActivity.setButtonDownHandler(buttonDownHandler);
        mainActivity.setButtonUpHandler(buttonUpHandler);
        mainActivity.setDummyLoader(dummyLoader);
        mainActivity.setLongPressHandler(longPressHandler);
        mainActivity.setMessageDao(messageDao);
        mainActivity.setSendingHandler(sendingHandler);
        mainActivity.setVoiceCommandHandler(voiceCommandHandler);
        mainActivity.setNotFoundHandler(notFoundHandler);
    }

}
