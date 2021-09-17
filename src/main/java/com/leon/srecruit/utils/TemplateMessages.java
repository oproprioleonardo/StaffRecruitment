package com.leon.srecruit.utils;

import com.leon.srecruit.api.entities.Recruitment;
import com.leon.srecruit.config.Config;
import com.leon.srecruit.config.Gamemode;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.Calendar;

@UtilityClass
public class TemplateMessages {

    public Message infoAttached(Recruitment recruitment, Config config) {
        final MessageBuilder builder = new MessageBuilder();
        final Gamemode gamemode = config.getGamemodes().stream()
                                        .filter(o -> o.getName()
                                                      .equalsIgnoreCase(recruitment.getGamemode()))
                                        .findFirst().get();
        final EmbedBuilder embedBuilder = TemplateMessage.INFO_ATTACHED_SUCCESS
                .get()
                .appendDescription("> `Nickname`: " + recruitment.getNickname() + "\n")
                .appendDescription("> `Modo de Jogo`: " + gamemode.getLabel() + "\n")
                .appendDescription("> `Discord`: " + recruitment.getApplicantAsMention() + "\n")
                .appendDescription("> `Discord ID`: " + recruitment.getApplicantId());
        builder
                .setEmbeds(embedBuilder.build())
                .allowMentions(Message.MentionType.USER, Message.MentionType.EMOTE)
                .mentionUsers(recruitment.getApplicantId());
        return builder.build();
    }

    public Message invitationReceived(Recruitment recruitment, Config config) {
        final MessageBuilder builder = new MessageBuilder();
        final Gamemode gamemode = config.getGamemodes().stream()
                                        .filter(o -> o.getName()
                                                      .equalsIgnoreCase(recruitment.getGamemode()))
                                        .findFirst().get();
        final EmbedBuilder embedBuilder = TemplateMessage.INVITATION_RECEIVED
                .get()
                .appendDescription("> **Informações recolhidas** \n")
                .appendDescription("> `Nickname`: " + recruitment.getNickname() + "\n")
                .appendDescription("> `Modo de Jogo`: " + gamemode.getLabel() + "\n")
                .appendDescription("\n")
                .appendDescription("> **NOTA**: Caso pretenda continuar com o recrutamento tudo estará explicado no")
                .appendDescription(" grupo de recrutados, por favor leia tudo com muita atenção e evite perguntas")
                .appendDescription(" desnecessárias. Inicialmente só terá acesso ao canal de verificação por cerca de")
                .appendDescription(" três minutos onde terá acesso às informações iniciais.\n")
                .appendDescription("**\n**");
        builder
                .setContent("")
                .setEmbeds(embedBuilder.build())
                .allowMentions(Message.MentionType.USER, Message.MentionType.EMOTE)
                .mentionUsers(recruitment.getApplicantId());
        return builder.build();
    }

    public Message controlPanel(Recruitment recruitment, Config config) {
        final MessageBuilder builder = new MessageBuilder();
        final Gamemode gamemode = config.getGamemode(recruitment.getGamemode());
        final EmbedBuilder embedBuilder = new EmbedBuilder()
                .setColor(new Color(47, 49, 54))
                .setTitle("Configurações - Recrutamento")
                .appendDescription(
                        "O candidato `" + recruitment.getNickname() + "` foi adicionado ao sistema de recrutamento.\n")
                .appendDescription("\n")
                .appendDescription(
                        "Todas as etapas do recrutamento são controladas a partir deste painel de controle.\n")
                .appendDescription("\n")
                .appendDescription(
                        "> **STATUS**: " + recruitment.getStage().getLabel() + " (_" + gamemode.getLabel() + "_).")
                .setFooter("Use os botões abaixo para controlar o sistema.");
        builder
                .setEmbeds(embedBuilder.build())
                .allowMentions(Message.MentionType.USER, Message.MentionType.EMOTE)
                .mentionUsers(recruitment.getApplicantId());
        return builder.build();
    }

    public Message generatedLink(Calendar calendar) {
        final EmbedBuilder builder = TemplateMessage.GENERATED_LINK.get();
        builder.setTimestamp(calendar.toInstant());
        return new MessageBuilder(builder).build();
    }

    public String accepted(User user) {
        return "Olá, " + user.getAsMention() + "!\n" +
               "\n" +
               "Você foi **aprovado** no processo seletivo do nosso recrutamento, parabéns!\n" +
               "\n" +
               "Antes de tudo, é de extrema importância que tenha o mínimo de conhecimento para entregar o melhor suporte da melhor maneira aos jogadores. Dessa forma deixámos disponível uma introdução à equipe, onde você pode encontra pequenos esclarecimentos, dúvidas frequentes e comandos da equipe...\n" +
               "\n" +
               " Consequentemente pedimos que essa introdução e as respetivas regras do servidor sejam lidas (obrigatório).\n" +
               "\n" +
               "1.  Documento introdutório da equipe - (não pode ser divulgado.) https://equipe.hylex.me/informacoes/intro;\n" +
               "2. Regras do servidor: https://hylex.net/rules.\n" +
               "\n" +
               "Ressaltamos, que é muito importante após entrar na equipe, esclarecer as suas dúvidas. Nunca, mas nunca responda a um jogador com uma informação que você não possui certeza se é a correta. Nestes casos, você deve falar com o responsável do modo de jogo a que você pertence, ou esclarecer no canal de dúvidas gerais da equipe.\n" +
               "\n" +
               "Por fim, é muito importante manter a formalidade em todos os canais, é importante passarmos uma imagem profissional para todos. Algumas regras básicas de gramática também estão no documento de introdução, então caso precise, sinta-se livre para procurar. (não é obrigatório falar formalmente no grupo oficial da equipe)\n" +
               "\n" +
               "No momento são apenas estas informações que gostaríamos que você estivesse ciente antes de entrar no grupo da equipe.\n" +
               "\n" +
               "**NOTA**: Após a sua confirmação de leitura de ambos os documentos, poderemos prosseguir.";
    }

    public String refused(User user) {
        return "Olá, " + user.getAsMention() + "!\n" +
               "\n" +
               "Lamentamos, mas você foi **negado** no processo seletivo do nosso recrutamento.\n" +
               "\n" +
               "Você infelizmente foi reprovado na última etapa do processo seletivo de equipe do Hylex. \n" +
               "Gostaríamos de agradecer a você pelo seu esforço e resiliência em chegar na última etapa, muitos não conseguem tal feito. \n" +
               "\n" +
               "**NOTA**: Ainda será possível participar do processo seletivo, o tempo de envio de um novo formulário para uma nova tentativa é de uma semana. Acreditamos que todos vocês podem ter potencial se o almejarem. Apenas é questão de esforço, reflexão e dedicação. Recomendamos sempre a leitura completa das informações de ingresso, ou seja, as habilidades / requisitos necessários, dicas de ingresso.\n" +
               "\n" +
               "Até uma próxima, :wave:.";
    }


}
