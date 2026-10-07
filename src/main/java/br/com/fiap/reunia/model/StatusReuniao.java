package br.com.fiap.reunia.model;

/**
 * Ciclo de vida da reuniao.
 * AGENDADA -> REALIZADA | CANCELADA ; CANCELADA -> AGENDADA (reagendamento) ; REALIZADA e estado final.
 */
public enum StatusReuniao {
    AGENDADA,
    REALIZADA,
    CANCELADA;

    public boolean podeMudarPara(StatusReuniao novo) {
        return switch (this) {
            case AGENDADA -> novo == REALIZADA || novo == CANCELADA;
            case CANCELADA -> novo == AGENDADA;
            case REALIZADA -> false;
        };
    }
}
