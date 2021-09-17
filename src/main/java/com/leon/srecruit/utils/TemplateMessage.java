package com.leon.srecruit.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public enum TemplateMessage {

    NICKNAME_LENGTH_NOT_SUPPORTED(
            new EmbedBuilder()
                    .setColor(new Color(212, 105, 105))
                    .addField("Erro:", "A quantidade de caracteres do nickname não é suportada.",
                              false
                    )),
    SERVER_NOT_FOUND(
            new EmbedBuilder()
                    .setColor(new Color(212, 105, 105))
                    .addField("Erro:", "O nome do servidor não corresponde à nenhum configurado.",
                              false
                    )),
    USER_NOT_FOUND(
            new EmbedBuilder()
                    .setColor(new Color(212, 105, 105))
                    .addField("Erro:", "Não foi encontrado nenhum usuário com este Discord ID.",
                              false
                    )),
    INTERNAL_ERROR(
            new EmbedBuilder()
                    .setColor(new Color(212, 105, 105))
                    .addField("Erro:", "Ocorreu um erro interno, por favor verifique o console.",
                              false
                    )),
    INFO_ATTACHED_SUCCESS(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .appendDescription("<:aprovado:882018256657985587> **Informações indexadas com sucesso!**\n")
                    .appendDescription("\n")
                    .appendDescription("Confirma as informações reconhecidas pelo nosso sistema de recrutamento.\n")
                    .appendDescription("Caso queira prosseguir com o processo basta selecionar em \"Enviar\".\n")
                    .appendDescription("\n")
                    .appendDescription("> **Informações recolhidas** \n")),
    INFO_CANCELED(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .appendDescription("<:negado:882018256519577671> **Processo cancelado com sucesso!**\n")
    ),
    INVITATION_ERROR(
            new EmbedBuilder()
                    .setColor(new Color(212, 105, 105))
                    .appendDescription("<:negado:882018256519577671> **Processo cancelado...**\n")
                    .appendDescription("\n")
                    .appendDescription("Oops, não consegui enviar a mensagem para o candidato.\n")
                    .appendDescription("\n")
                    .appendDescription("> É possível que o mesmo não esteja no discord / mensagens privadas abertas.")
    ),
    INVITATION_EXPIRE(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .setTitle("Convite de ingresso expirado")
                    .appendDescription("Olá, lamentamos informar que o seu recrutamento foi\n")
                    .appendDescription("cancelado devido ao prazo de resposta excedido.\n")
                    .appendDescription("\n")
                    .appendDescription("> Pedimos que esteja mais atento da próxima vez em")
                    .appendDescription(" todos os canais e mensagens privadas.\n")
                    .appendDescription("\n")
                    .appendDescription("Caso ainda esteja interessado para ingressar na nossa\n")
                    .appendDescription("equipe, sinta-se à vontade para refazer o formulário.\n")
                    .appendDescription("\n")
                    .appendDescription("Agradecemos pelo seu interesse em se candidatar.\n")
                    .appendDescription("Espero que entenda o motivo de tal decisão.\n")
                    .appendDescription("\n")
                    .appendDescription("Com os melhores cumprimentos,\n")
                    .appendDescription("Equipe de recrutamento.")
                    .setImage("https://minecraftskinstealer.com/achievement/2/Convite+expirado/Prazo+excedido.")
    ),
    INVITATION_RECEIVED(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .appendDescription("Olá, sou o responsável pelo processo de todos os recrutamentos no Hylex.\n")
                    .appendDescription("\n")
                    .appendDescription(
                            "<:aprovado:882018256657985587> **O seu formulário foi analisado e decidimos aceitar sua candidatura!**\n")
                    .appendDescription("\n")
                    .appendDescription(
                            "Você foi aceite na primeira etapa do processo seletivo de integração à equipe.\n")
                    .appendDescription("\n")
                    .appendDescription(
                            "Antes de tudo, esteja ciente de que todas as informações compartilhadas dentro do\n")
                    .appendDescription(
                            "processo seletivo e dentro da equipe nunca deverão ser divulgadas para o público.\n")
                    .appendDescription("\n")
                    .appendDescription(
                            "A partir do momento que aceitar iniciar a próxima etapa do processo seletivo, é\n")
                    .appendDescription("necessário que daqui em diante cumpra algumas responsabilidades e que tenha\n")
                    .appendDescription("total maturidade para agir de forma correta com todos os membros.\n")
                    .appendDescription("\n")
                    .setFooter("Caso decida dar continuidade basta selecionar no botão que indica sim. \n" +
                               "Porém, caso no momento já não esteja interessado clique em não.")
    ),
    REFUSE_INVITATION(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .setTitle("Convite de ingresso cancelado")
                    .appendDescription("Você cancelou a sua entrada na nossa equipe e\n")
                    .appendDescription("respeitamos a sua decisão plenamente.\n")
                    .appendDescription("\n")
                    .appendDescription("> Caso tenha recusado mas o motivo esteja")
                    .appendDescription(" relacionado com falta de tempo nas próximas")
                    .appendDescription(" semanas ou até dias contate o responsável pelo")
                    .appendDescription(" modo de jogo ao qual havia se candidatado.\n")
                    .appendDescription("\n")
                    .appendDescription("Agradecemos pelo seu interesse em se candidatar.\n")
                    .appendDescription("\n")
                    .appendDescription("Com os melhores cumprimentos,\n")
                    .appendDescription("Hylex Management Team.")
                    .setImage("https://minecraftskinstealer.com/achievement/2/Convite+cancelado/Recrutamento+negado")
    ),
    GENERATING_LINK(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .setThumbnail("https://cdn.discordapp.com/emojis/653399136737165323.gif?v=1")
                    .appendDescription("Recuperando o link do grupo de recrutamento para você.")
                    .setFooter("Aguarde uns instantes até o link ser recuperado no banco de dados.")
    ),
    GENERATED_LINK(
            new EmbedBuilder()
                    .setColor(new Color(47, 49, 54))
                    .appendDescription(
                            "<:aprovado:882018256657985587> **┆ Convite para o grupo de recrutamentos gerado com sucesso!**")
                    .setFooter("Entre no grupo assim que possível, o link é válido até 7 dias.")
    );

    private final EmbedBuilder embedBuilder;

    TemplateMessage(EmbedBuilder embedBuilder) {
        this.embedBuilder = embedBuilder;
    }

    public EmbedBuilder get() {
        return new EmbedBuilder(embedBuilder);
    }

    public MessageEmbed embed() {
        return this.embedBuilder.build();
    }

}
