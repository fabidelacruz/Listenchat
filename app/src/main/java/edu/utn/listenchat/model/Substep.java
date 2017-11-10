package edu.utn.listenchat.model;

/**
 * Created by fabiandelacruz on 13/9/17.
 */

public enum Substep {

    MESSAGES("Mensajes nuevos", Step.MAIN),
    SEND_MESSAGE("Enviar mensaje", Step.MAIN),
    NOVELTIES("Novedades", Step.MAIN),
    CLEAR("Limpiar", Step.MAIN),
    CONVERSATION("Conversación", Step.MAIN),
    COMMANDS("Comandos", Step.MAIN),
    HELP("Ayuda", Step.MAIN),
    EXPLAIN("Explicar comando", Step.MAIN),
    EXIT("Salir", Step.MAIN),
    SELECT_CONTACT("Seleccione contacto", Step.CONVERSATION),
    SELECT_COMMAND("Seleccione comando", Step.EXPLAIN),
    READ("", Step.CONVERSATION);


    Substep(String description, Step step) {
        this.description = description;
        this.step = step;
    }

    private String description;
    private Step step;

    public String getDescription() {
        return description;
    }

    public static Substep previous(Substep substep) {
        Substep selected = null;
        for (Substep value: values()) {
            if (value.step.equals(substep.step) && value.ordinal() <= substep.ordinal() - 1) {
                if (selected == null || selected.ordinal() < value.ordinal()) {
                    selected = value;
                }
            }
        }

        return selected != null ? selected : substep;
    }

    public static Substep next(Substep substep) {
        for (Substep value: values()) {
            if (value.step.equals(substep.step) && value.ordinal() >= substep.ordinal() + 1) {
                return value;
            }
        }

        return substep;
    }
}
