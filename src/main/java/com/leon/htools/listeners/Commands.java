package com.leon.htools.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.htools.ReportProcessing;
import com.leon.htools.cache.RecruitCacheProcess;
import com.leon.htools.cache.ReportProcessingCache;
import com.leon.htools.config.Config;
import com.leon.htools.exceptions.CommandException;
import com.leon.htools.internal.entities.RecruitmentImpl;
import com.leon.htools.internal.entities.ReportImpl;
import com.leon.htools.utils.ButtonPatterns;
import com.leon.htools.utils.TemplateMessage;
import com.leon.htools.utils.TemplateMessages;
import com.leon.htools.utils.validators.ArgsValidator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

@Singleton
public class Commands extends ListenerAdapter {

    @Inject
    private Config config;
    @Inject
    private RecruitCacheProcess recruitCacheProcess;
    @Inject
    private ReportProcessingCache reportProcessingCache;

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        if (!e.isFromGuild()) return;
        if (e.getName().equalsIgnoreCase("recrutar")) {
            final Guild guild = Objects.requireNonNull(e.getGuild());
            final Member member = e.getMember();
            if (guild.getIdLong() != config.getRecruitmentGuildId() || member == null) return;
            e.deferReply().queue();
            if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                final String id = Objects.requireNonNull(e.getOption("id")).getAsString();
                final String nickname = Objects.requireNonNull(e.getOption("nickname")).getAsString();
                final String gamemode = Objects.requireNonNull(e.getOption("gamemode")).getAsString();

                try {
                    ArgsValidator.throwIfNicknameIsWrong(nickname);
                    ArgsValidator.throwIfGamemodeIsNull(gamemode, config);
                } catch (CommandException exception) {
                    e.getHook().editOriginal("").setEmbeds(exception.getMessageEmbed()).queue();
                    return;
                }
                final User sender = e.getUser();
                e.getJDA().retrieveUserById(id).onErrorMap(throwable -> {
                    e.getHook().editOriginal("").setEmbeds(TemplateMessage.USER_NOT_FOUND.embed()).queue();
                    return sender;
                }).queue(target -> {
                    if (target.getId().equalsIgnoreCase(sender.getId())) return;
                    final RecruitmentImpl r = new RecruitmentImpl();
                    r.setApplicantId(target.getIdLong());
                    r.setOperatorId(sender.getIdLong());
                    r.setGamemode(gamemode);
                    r.setNickname(nickname);
                    final UUID uuid = recruitCacheProcess.add(r);
                    e.getHook().editOriginal(TemplateMessages.infoAttached(r, config))
                     .setActionRow(
                             ButtonPatterns.confirmRecruitment(uuid),
                             ButtonPatterns.cancelRecruitment(uuid)
                     ).queue();
                });
            } else e.getHook().deleteOriginal().queue();

        } else if (e.getName().equalsIgnoreCase("clear")) {
            final Guild guild = Objects.requireNonNull(e.getGuild());
            final Member member = e.getMember();
            if (guild.getIdLong() != config.getRecruitmentGuildId() || member == null) return;
            e.deferReply().queue();
            if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                final MessageChannel channel = e.getChannel();
                e.getHook().editOriginal("Apagando...")
                 .queue(message -> {
                     channel.getHistoryBefore(message, 100)
                            .queue(messageHistory -> e.getTextChannel()
                                                      .deleteMessages(messageHistory.getRetrievedHistory())
                                                      .queue());
                     message.delete().queue();
                 });
            } else e.getHook().deleteOriginal().queue();

        } else if (e.getName().equalsIgnoreCase("ban")) {
            final Guild guild = Objects.requireNonNull(e.getGuild());
            final Member member = e.getMember();
            if (member == null) return;
            e.deferReply().queue();
            if (member.hasPermission(Permission.BAN_MEMBERS) || member.getIdLong() == 807227822564573235L) {
                final String id = Objects.requireNonNull(e.getOption("id")).getAsString();
                final OptionMapping optReason = e.getOption("reason");
                e.getJDA().retrieveUserById(id).queue(target -> {
                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setAuthor("Membro punido");
                    eb.appendDescription("O membro " + target.getAsTag() + " foi banido do servidor com sucesso!");
                    eb.setColor(new Color(47, 49, 54));
                    final EmbedBuilder ebError = new EmbedBuilder();
                    ebError.setAuthor("Sem permissão");
                    ebError.appendDescription("O membro " + target.getAsTag() + " possui um cargo superior.");
                    ebError.setColor(new Color(47, 49, 54));
                    if (optReason != null) guild.ban(id, 0, e.getUser().getAsTag() + ": " + optReason.getAsString())
                                                .queue(
                                                        unused -> e.getHook().editOriginalEmbeds(eb.appendDescription(
                                                                "\n`Motivo`: " +
                                                                optReason.getAsString())
                                                                                                   .build())
                                                                   .queue()
                                                        ,
                                                        throwable -> e.getHook()
                                                                      .editOriginalEmbeds(ebError.build())
                                                                      .queue());
                    else guild.ban(id, 0, e.getUser().getAsTag() + ": Nenhum motivo foi especificado.")
                              .queue(
                                      unused -> e.getHook().editOriginalEmbeds(eb.build()).queue()
                                      , throwable -> e.getHook().editOriginalEmbeds(ebError.build()).queue());
                }, throwable -> {
                    final EmbedBuilder eb = new EmbedBuilder();
                    eb.setDescription("O ID informado é inválido! Lembre-se de que é aceito o ID do usuário.");
                    eb.setColor(new Color(47, 49, 54));
                    e.getHook().editOriginalEmbeds(eb.build()).queue();
                });
            } else e.getHook().deleteOriginal().queue();
        } else if (e.getName().equalsIgnoreCase("report")) {
            final MessageChannel channel = e.getChannel();
            final User sender = e.getUser();
            if (reportProcessingCache.exists(sender.getIdLong())) {
                channel.sendMessage(sender.getAsMention() +
                                    ", você já está fazendo um relatório. Aguarde alguns segundos ou complete o existente.")
                       .queue();
                return;
            }
            final String title = Objects.requireNonNull(e.getOption("title")).getAsString();
            final MessageBuilder messageBuilder = new MessageBuilder();
            final ReportImpl report = new ReportImpl();
            report.setUserId(sender.getId());
            report.setLastOperator(sender.getId());
            report.setTitle(title);
            final ReportProcessing reportProcessing = new ReportProcessing(report);
            final MessageEmbed embed = reportProcessing.buildMessage(sender);
            messageBuilder.setEmbeds(embed);
            reportProcessing.message =
                    channel
                            .sendMessage(messageBuilder.build())
                            .complete()
                            .editMessage("**" + sender.getName() +
                                         "**, obrigado por querer reportar uma falha da rede. O título de apresentação foi definido com sucesso!\n" +
                                         "Agora é necessário que você explique passo a passo como reproduzir o bug. Quando terminar de explicar clique no botão.")
                            .setActionRow(Button.success("confirm-next", "Pronto"))
                            .complete();
            reportProcessingCache.put(sender.getIdLong(), reportProcessing);
        } else if (e.getName().equalsIgnoreCase("staffhelp")) {
            e.getChannel().sendMessageEmbeds(TemplateMessage.HELP.embed()).queue();
        }
    }

}
