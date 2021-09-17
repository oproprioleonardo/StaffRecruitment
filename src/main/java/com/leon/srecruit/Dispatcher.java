package com.leon.srecruit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.srecruit.api.services.RecruitService;
import com.leon.srecruit.config.Config;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import com.leon.srecruit.log.LogManager;
import com.leon.srecruit.utils.ButtonPatterns;
import com.leon.srecruit.utils.TemplateMessage;
import com.leon.srecruit.utils.TemplateMessages;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;
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

    public void sendInvitation(
            User user, RecruitmentImpl rec,
            MessageChannel originalCallChannel) {
        user.openPrivateChannel()
            .queue(channel -> Objects
                    .requireNonNull(jda.getGuildChannelById(config.getChannelInviteId()))
                    .createInvite()
                    .setMaxUses(1)
                    .setMaxAge(config.getTimeToAliveInvite(), TimeUnit.MINUTES)
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
                                                              .onItem().invoke(() -> message
                                                       .editMessage(TemplateMessages.invitationReceived(rec, config))
                                                       .setActionRow(
                                                               ButtonPatterns.acceptInvitation(rec.getId()),
                                                               ButtonPatterns.refuseInvitation(rec.getId())
                                                       )
                                                       .queue(
                                                               (ignored) -> logManager
                                                                       .logSucessInvite(rec, originalCallChannel)
                                                       )).await().indefinitely(),
                                       throwable -> logManager.logFailInvite(rec, originalCallChannel, false)
                               );
                    }));
    }

}
