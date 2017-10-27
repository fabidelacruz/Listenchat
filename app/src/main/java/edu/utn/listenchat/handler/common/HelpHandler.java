package edu.utn.listenchat.handler.common;

import edu.utn.listenchat.service.TextToSpeechService;

public class HelpHandler {

    private TextToSpeechService textToSpeechService;

    public void handleHelp() {
        this.textToSpeechService.speak("Hola amigo. Di la palabra comandos para listar todos los " +
                "comandos de voz con los que cuentas para interactuar con Listenchat. Para conocer " +
                "más acerca de la funcionalidad de un comando di: Explicar comando, seguido del " +
                "nombre del comando que se desea detallar. También puedes usar el modo auricular, " +
                "para activarlo, presiona el botón medio de tus auriculares y usa los botones " +
                "siguiente y atrás para navegar por el menú. Tú puedes amigo");
    }

    public void setTextToSpeechService(TextToSpeechService textToSpeechService) {
        this.textToSpeechService = textToSpeechService;
    }

}
