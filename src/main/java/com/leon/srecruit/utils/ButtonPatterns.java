package com.leon.srecruit.utils;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.UUID;

@UtilityClass
public class ButtonPatterns {

    public String SEPARATOR = "XxxX";
    public String CONFIRM = "confirm";
    public String CANCEL = "cancel";
    public String ACCEPT = "accept";
    public String REFUSE = "refuse";
    public String CANCEL_RECRUIT = CANCEL + SEPARATOR + "recruitment" + SEPARATOR;
    public String CONFIRM_RECRUIT = CONFIRM + SEPARATOR + "recruitment" + SEPARATOR;
    public String CANCEL_INVTION_CONFTION = CANCEL + SEPARATOR + "invitation" + SEPARATOR + "confirmation" + SEPARATOR;
    public String CONFIRM_INVTION_CONFTION_0 =
            CONFIRM + SEPARATOR + "invitation" + SEPARATOR + "confirmation" + SEPARATOR + "false" + SEPARATOR;
    public String CONFIRM_INVTION_CONFTION_1 =
            CONFIRM + SEPARATOR + "invitation" + SEPARATOR + "confirmation" + SEPARATOR + "true" + SEPARATOR;
    public String REFUSE_INVITATION = REFUSE + SEPARATOR + "invitation" + SEPARATOR;
    public String ACCEPT_INVITATION = ACCEPT + SEPARATOR + "invitation" + SEPARATOR;
    public String NEXT_STEP = "next" + SEPARATOR + "step" + SEPARATOR;
    public String ACCEPT_TO_TEAM = ACCEPT + SEPARATOR + "team" + SEPARATOR;
    public String REFUSE_TO_TEAM = REFUSE + SEPARATOR + "team" + SEPARATOR;
    public String CONFIRM_TO_TEAM_0 = CONFIRM + SEPARATOR + "team" + SEPARATOR + "false" + SEPARATOR;
    public String CONFIRM_TO_TEAM_1 = CONFIRM + SEPARATOR + "team" + SEPARATOR + "true" + SEPARATOR;
    public String CANCEL_TO_TEAM = CANCEL + SEPARATOR + "team" + SEPARATOR;
    public String READY_TO_WORK = "alright" + SEPARATOR;

    public Component alright(long rId) {
        return Button.primary(READY_TO_WORK + rId, "Terminar recrutamento");
    }

    public Component acceptToTeam(long rId) {
        return Button.success(ACCEPT_TO_TEAM + rId, "Aprovar");
    }

    public Component refuseToTeam(long rId) {
        return Button.danger(REFUSE_TO_TEAM + rId, "Reprovar");
    }

    public Component confirmAcceptToTeam(long rId) {
        return Button.success(CONFIRM_TO_TEAM_1 + rId, "Confirmar");
    }

    public Component confirmRefuseToTeam(long rId) {
        return Button.success(CONFIRM_TO_TEAM_0 + rId, "Confirmar");
    }

    public Component cancelToTeam(long rId) {
        return Button.danger(CANCEL_TO_TEAM + rId, "Voltar atrás");
    }

    public Component cancelRecruitment(UUID uuid) {
        return Button.danger(CANCEL_RECRUIT + uuid.toString(), "Cancelar");
    }

    public Component confirmRecruitment(UUID uuid) {
        return Button.success(CONFIRM_RECRUIT + uuid.toString(), "Enviar");
    }

    public Component refuseInvitation(long rId) {
        return Button.danger(REFUSE_INVITATION + rId, "Não estou interessado");
    }

    public Component acceptInvitation(long rId) {
        return Button.success(ACCEPT_INVITATION + rId, "Quero prosseguir");
    }

    public Component cancelInvitation(long rId) {
        return Button.danger(CANCEL_INVTION_CONFTION + rId, "Voltar atrás");
    }

    public Component nextStep(long rId) {
        return Button.primary(NEXT_STEP + rId, "Entrevistado");
    }

    public Component confirmAcceptInvitation(long rId) {
        return Button.success(CONFIRM_INVTION_CONFTION_1 + rId, "Confirmar");
    }

    public Component confirmRefuseInvitation(long rId) {
        return Button.success(CONFIRM_INVTION_CONFTION_0 + rId, "Confirmar");
    }

}
