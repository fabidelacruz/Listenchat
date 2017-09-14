package edu.utn.listenchat.model;

/**
 * Created by fabiandelacruz on 13/9/17.
 */

public enum Substep {

    MESSAGES("Mensajes nuevos"),
    NOVELTIES("Novedades"),
    CONVERSATION("Conversación"),
    SELECT_CONTACT("Seleccione contacto"),
    READ("");

    Substep(String description) {
        this.description = description;
    }

    private String description;

    public String getDescription() {
        return description;
    }
}
