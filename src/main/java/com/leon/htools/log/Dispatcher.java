package com.leon.htools.log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.htools.api.entities.Report;
import com.leon.htools.api.services.RecruitService;
import com.leon.htools.config.Bug;
import com.leon.htools.config.Config;
import com.leon.htools.config.RecruitmentConfig;
import com.leon.htools.internal.entities.RecruitmentImpl;
import com.leon.htools.utils.ButtonPatterns;
import com.leon.htools.utils.TemplateMessage;
import com.leon.htools.utils.TemplateMessages;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Singleton
public class Dispatcher {

    @Inject
    private Config config;
    @Inject
    private LogManager logManager;
    @Inject
    private JDA jda;
    @Inject
    private RecruitService service;

    @Inject
    private void loadDispatchers(Dotenv dotenv) {
        Arrays.stream(ReportDispatch.values())
              .forEach(reportDispatch -> reportDispatch.getInfo().load(dotenv));
    }

    public void sendInvitation(
            User user, RecruitmentImpl rec,
            MessageChannel originalCallChannel) {
        final RecruitmentConfig rCfg = config.getRecruitmentSystem();
        user.openPrivateChannel()
            .queue(channel -> Objects
                    .requireNonNull(jda.getGuildChannelById(rCfg.getChannelInviteId()))
                    .createInvite()
                    .setUnique(true)
                    .setMaxUses(1)
                    .setMaxAge(rCfg.getTimeToAliveInvite(), TimeUnit.MINUTES)
                    .queue(invite -> {
                        rec.setInvitationUrl(invite.getUrl());
                        channel.sendMessage(TemplateMessages.invitationReceived(rec, config))
                               .queue(
                                       message -> this.service.create(rec)
                                                              .onFailure()
                                                              .invoke(() -> {
                                                                  invite.delete().queue();
                                                                  channel.sendMessageEmbeds(
                                                                          TemplateMessage.INTERNAL_ERROR.embed())
                                                                         .queue();
                                                              })
                                                              .onItem()
                                                              .invoke(() -> message
                                                                      .editMessage(TemplateMessages.invitationReceived(
                                                                              rec,
                                                                              config
                                                                      ))
                                                                      .setActionRow(
                                                                              ButtonPatterns.acceptInvitation(rec.getId()),
                                                                              ButtonPatterns.refuseInvitation(rec.getId())
                                                                      )
                                                                      .queue(
                                                                              (ignored) -> logManager
                                                                                      .logSucessInvite(
                                                                                              rec,
                                                                                              originalCallChannel
                                                                                      )
                                                                      ))
                                                              .subscribe()
                                                              .with(value -> System.out.println("success: " + value)),
                                       throwable -> logManager.logFailInvite(rec, originalCallChannel, false)
                               );
                    }));
    }

    public void dispatch(ReportDispatch dispatchTarget, Report report, Bug... bugs) {
        final ReportDispatchInfo destination = dispatchTarget.getInfo();
        report.getAuthor(jda).and(jda.retrieveUserById(report.getLastOperator()), (user, user2) -> {
            final TextChannel channel = Optional.ofNullable(jda.getTextChannelById(destination.getChannelId())).get();
            final EmbedBuilder builder =
                    TemplateMessages.buildInfoMsgFrom(report, user, user2, destination.getColorMessage());
            final MessageBuilder messageBuilder = new MessageBuilder(builder);
            messageBuilder.allowMentions(Message.MentionType.USER, Message.MentionType.ROLE);
            if (report.getLastOperator() != null) messageBuilder.mentionUsers(report.getLastOperator());
            if (bugs.length > 0) {
                final Bug bug = bugs[0];
                final String[] roles = bug.getRoles().toArray(new String[]{});
                final Optional<String> optional =
                        Arrays.stream(roles).map(s -> Objects
                                .requireNonNull(jda.getRoleById(s)).getAsMention())
                              .reduce((s, s2) -> s + " " + s2);
                optional.ifPresent(s -> messageBuilder
                        .mentionRoles(roles)
                        .setContent("Etiqueta de " + s + ", " + bug.getTag()));
            }
            return channel
                    .sendMessage(messageBuilder.build())
                    .setActionRow(
                            Button.success("update-report-" + report.getId(), "Atualizar"),
                            Button.secondary("update-report-status-" + report.getId(), "Editar status")
                    );
        }).queue(RestAction::queue);

    }

}
