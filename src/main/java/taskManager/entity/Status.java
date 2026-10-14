package taskManager.entity;

import java.util.EnumSet;
import java.util.Set;

public enum Status {
    FAZER,
    EM_PROGRESSO,
    CONCLUIDA;

    /**
     * Fluxo permitido:
     *   FAZER -> EM_PROGRESSO            (iniciar)
     *   EM_PROGRESSO -> CONCLUIDA        (concluir)
     *   EM_PROGRESSO -> FAZER            (pausar)
     *   CONCLUIDA -> EM_PROGRESSO        (reabrir)
     */
    public Set<Status> proximosPermitidos() {
        return switch (this) {
            case FAZER -> EnumSet.of(EM_PROGRESSO);
            case EM_PROGRESSO -> EnumSet.of(CONCLUIDA, FAZER);
            case CONCLUIDA -> EnumSet.of(EM_PROGRESSO);
        };
    }

    public boolean podeIrPara(Status destino) {
        return proximosPermitidos().contains(destino);
    }
}
