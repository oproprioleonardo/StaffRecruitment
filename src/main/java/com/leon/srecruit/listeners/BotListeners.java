package com.leon.srecruit.listeners;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.srecruit.Dispatcher;
import com.leon.srecruit.RecruitStatus;
import com.leon.srecruit.api.services.RecruitService;
import com.leon.srecruit.cache.RecruitCacheProcess;
import com.leon.srecruit.config.Config;
import com.leon.srecruit.config.Gamemode;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import com.leon.srecruit.log.LogManager;
import com.leon.srecruit.utils.ButtonPatterns;
import com.leon.srecruit.utils.TemplateMessage;
import com.leon.srecruit.utils.TemplateMessages;
import io.smallrye.mutiny.Uni;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import net.dv8tion.jda.api.managers.ChannelManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Singleton
public class BotListeners extends ListenerAdapter {

    @Inject
    private RecruitCacheProcess cache;
    @Inject
    private RecruitService service;
    @Inject
    private Config config;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private LogManager logManager;
    @Inject
    private JDA jda;

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton() == null) return;
        if (event.getMessage() == null) return;

        final Message message = event.getMessage();
        final Button button = event.getButton();
        final ButtonInteraction inter = event.getInteraction();
        final String buttonId = Objects.requireNonNull(button.getId());
        final MessageChannel channel = event.getChannel();

        if (!buttonId.contains(ButtonPatterns.SEPARATOR)) return;

        final String[] strings = buttonId.split(ButtonPatterns.SEPARATOR);
        if (buttonId.startsWith(ButtonPatterns.CANCEL_RECRUIT)) {
            final Guild guild = Objects.requireNonNull(event.getGuild());
            final UUID uuid = UUID.fromString(strings[2]);
            inter.deferEdit().queue();
            guild.retrieveMember(event.getUser()).queue(member -> {
                if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                    cache.remove(uuid);
                    message.delete().queue();
                    channel.sendMessageEmbeds(TemplateMessage.INFO_CANCELED.embed())
                           .allowedMentions(Lists.newArrayList(Message.MentionType.EMOTE))
                           .queue((rs) -> rs.delete().queueAfter(30, TimeUnit.SECONDS));
                }
            });
        } else if (buttonId.startsWith(ButtonPatterns.CONFIRM_RECRUIT)) {
            final Guild guild = Objects.requireNonNull(event.getGuild());
            final UUID uuid = UUID.fromString(strings[2]);
            inter.deferEdit().queue();
            guild.retrieveMember(event.getUser()).queue(member -> {
                if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                    final RecruitmentImpl r = ((RecruitmentImpl) cache.get(uuid));
                    message.delete().queue();
                    final Guild refGuild = Objects.requireNonNull(jda.getGuildById(config.getReferenceGuildId()));
                    jda.retrieveUserById(r.getApplicantId())
                       .queue(
                               user -> refGuild.retrieveMember(user).queue(target -> {
                                   if (target.getRoles()
                                             .stream()
                                             .anyMatch(role -> role.getIdLong() == config.getVerifiedRoleId())) {
                                       dispatcher.sendInvitation(user, r, channel);
                                   } else logManager.logFailInvite(r, channel, true);
                               }, throwable -> logManager.logFailInvite(r, channel, false)),
                               throwable -> logManager.logFailInvite(r, channel, false)
                       );
                    this.cache.remove(uuid);
                }
            });
        } else if (buttonId.startsWith(ButtonPatterns.ACCEPT_INVITATION)) {
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.confirmAcceptInvitation(id),
                    ButtonPatterns.cancelInvitation(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.REFUSE_INVITATION)) {
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.confirmRefuseInvitation(id),
                    ButtonPatterns.cancelInvitation(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.CANCEL_INVTION_CONFTION)) {
            final long id = Long.parseLong(strings[3]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.acceptInvitation(id),
                    ButtonPatterns.refuseInvitation(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.CONFIRM_INVTION_CONFTION_1)) {
            final long id = Long.parseLong(strings[4]);
            inter.deferEdit().queue();
            message.delete().queue();
            channel.sendMessageEmbeds(TemplateMessage.GENERATING_LINK.embed())
                   .queue(oldMsg -> {
                       oldMsg.delete().queue();
                       service.read(id)
                              .call((r) -> {
                                  if (System.currentTimeMillis() >
                                      r.getDate().getTime().getTime() + config.getTimeToAliveInvite() * 60 * 1000) {
                                      r.setStage(RecruitStatus.EXPIRED);
                                      r.setFinalized(true);
                                      channel.sendMessageEmbeds(TemplateMessage.INVITATION_EXPIRE.embed()).queue();
                                      logManager.logInviteExpired(r, event.getUser());
                                  } else {
                                      channel.sendMessage("|| " + r.getInvitationUrl().replace("https://", "") + " ||").queue();
                                      channel.sendMessage(TemplateMessages.generatedLink(r.getDate())).queue();
                                      r.setStage(RecruitStatus.WAIT_CHECKING);
                                  }
                                  return service.update(r);
                              }).await().indefinitely();
                   });
        } else if (buttonId.startsWith(ButtonPatterns.CONFIRM_INVTION_CONFTION_0)) {
            final long id = Long.parseLong(strings[4]);
            inter.deferEdit().queue();
            message.delete().queue();
            channel.sendMessageEmbeds(TemplateMessage.REFUSE_INVITATION.embed()).queue();
            service.read(id).call((r) -> {
                r.setStage(RecruitStatus.INVITATION_REFUSED);
                r.setFinalized(true);
                logManager.logInviteRefused(r, event.getUser());
                return service.update(r);
            }).await().indefinitely();
        } else if (buttonId.startsWith(ButtonPatterns.NEXT_STEP)) {
            final Guild guild = Objects.requireNonNull(event.getGuild());
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().queue();
            guild.retrieveMember(event.getUser()).queue(member -> {
                if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                    service.read(id).call((r) -> {
                        r.setStage(RecruitStatus.INTERVIEW_DONE);
                        final Gamemode gamemode = config.getGamemode(r.getGamemode());
                        guild.removeRoleFromMember(
                                r.getApplicantId(),
                                Objects.requireNonNull(guild.getRoleById(gamemode.getRoleId()))
                        )
                             .queue(unused -> guild.addRoleToMember(
                                     r.getApplicantId(),
                                     Objects.requireNonNull(guild.getRoleById(config.getIntervieweeRoleId()))
                             )
                                                   .queue());

                        event.getHook().editOriginal(TemplateMessages.controlPanel(r, config))
                             .setActionRow(
                                     ButtonPatterns.acceptToTeam(id),
                                     ButtonPatterns.refuseToTeam(id)
                             ).queue();
                        return service.update(r);
                    }).await().indefinitely();
                }
            });
        } else if (buttonId.startsWith(ButtonPatterns.ACCEPT_TO_TEAM)) {
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.confirmAcceptToTeam(id),
                    ButtonPatterns.cancelToTeam(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.REFUSE_TO_TEAM)) {
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.confirmRefuseToTeam(id),
                    ButtonPatterns.cancelToTeam(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.CANCEL_TO_TEAM)) {
            final long id = Long.parseLong(strings[2]);
            inter.deferEdit().setActionRow(
                    ButtonPatterns.acceptToTeam(id),
                    ButtonPatterns.refuseToTeam(id)
            ).queue();
        } else if (buttonId.startsWith(ButtonPatterns.CONFIRM_TO_TEAM_1)) {
            final long id = Long.parseLong(strings[3]);
            final Guild guild = Objects.requireNonNull(event.getGuild());
            inter.deferEdit().queue();
            message.delete().queue();
            service.read(id).call((r) -> {
                r.setStage(RecruitStatus.ACCEPTED);
                jda.retrieveUserById(r.getApplicantId()).queue(user -> {
                    final ChannelManager channelManager =
                            Objects.requireNonNull(guild.getGuildChannelById(channel.getId()))
                                   .getManager();
                    guild.removeRoleFromMember(r.getApplicantId(),
                                               Objects.requireNonNull(guild.getRoleById(config.getIntervieweeRoleId())))
                         .queue(unused -> guild.addRoleToMember(r.getApplicantId(),
                                                                Objects.requireNonNull(guild.getRoleById(config.getAcceptedRoleId()))
                         ).queue());
                    guild.retrieveMember(user)
                         .queue(member -> channelManager.putPermissionOverride(member, Lists.newArrayList(
                                 Permission.VIEW_CHANNEL,
                                 Permission.MESSAGE_HISTORY,
                                 Permission.MESSAGE_READ,
                                 Permission.MESSAGE_WRITE
                         ), Lists.newArrayList()).queue(unused -> channel.sendMessage(
                                 TemplateMessages.accepted(user)
                         ).setActionRow(ButtonPatterns.alright(r.getId())).queue()));
                });
                return service.update(r);
            }).await().indefinitely();
        } else if (buttonId.startsWith(ButtonPatterns.CONFIRM_TO_TEAM_0)) {
            final long id = Long.parseLong(strings[3]);
            final Guild guild = Objects.requireNonNull(event.getGuild());
            inter.deferEdit().queue();
            Objects.requireNonNull(guild.getGuildChannelById(channel.getId())).delete().queue();
            service.read(id).call((r) -> {
                jda.retrieveUserById(r.getApplicantId())
                   .queue(user -> user.openPrivateChannel()
                                      .queue(privateChannel -> privateChannel.sendMessage(
                                              TemplateMessages.refused(user)
                                      ).allowedMentions(Collections.singleton(Message.MentionType.USER))
                                                                             .queue(message1 -> guild.kick(r.getApplicantId()
                                                                                                            .toString())
                                                                                                     .queue())));
                r.setStage(RecruitStatus.FORCED_REFUSED);
                r.setFinalized(true);
                return service.update(r);
            }).await().indefinitely();
        } else if (buttonId.startsWith(ButtonPatterns.READY_TO_WORK)) {
            final long id = Long.parseLong(strings[1]);
            final Guild guild = Objects.requireNonNull(event.getGuild());
            inter.deferEdit().queue();
            guild.retrieveMember(event.getUser()).queue(member -> {
                if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                    Objects.requireNonNull(guild.getGuildChannelById(channel.getId())).delete().queue();
                    service.read(id).call((r) -> {
                        jda.retrieveUserById(r.getApplicantId())
                           .queue(user -> Objects.requireNonNull(jda.getGuildChannelById(config.getAlrightChannelInviteId()))
                                                 .createInvite()
                                                 .setMaxUses(1)
                                                 .setMaxAge(config.getTimeToAliveInvite(), TimeUnit.MINUTES)
                                                 .queue(invite -> user.openPrivateChannel()
                                                                      .queue(privateChannel ->
                                                                                     privateChannel
                                                                                             .sendMessage(
                                                                                                     "|| " +
                                                                                                     invite.getUrl()
                                                                                                           .replace(
                                                                                                                   "https://",
                                                                                                                   ""
                                                                                                           ) + " ||")
                                                                                             .queue()
                                                                      )));

                        r.setStage(RecruitStatus.READY_TO_WORK);
                        r.setFinalized(true);
                        return service.update(r);
                    }).invoke((r) -> guild.kick(r.getApplicantId().toString()).queue()).await().indefinitely();
                }
            });
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        final User user = event.getUser();
        final Guild guild = event.getGuild();
        if (guild.getIdLong() != config.getRecruitmentGuildId()) return;
        logManager.logLeaveEvent(user);
        this.service
                .readByUserId(user.getIdLong())
                .onItem().ifNotNull()
                .call(recruitmentList -> {
                    final RecruitmentImpl r = recruitmentList.stream().findFirst().get();
                    final Gamemode gamemode = config.getGamemode(r.getGamemode());
                    final Category category = Objects.requireNonNull(guild.getCategoryById(gamemode.getCategoryId()));
                    if (!r.isFinalized()) {
                        category.getTextChannels()
                                .stream()
                                .filter(channel -> channel.getName()
                                                          .equalsIgnoreCase(r.getNickname() + "-" + r.getId()))
                                .findFirst()
                                .ifPresent(channel -> channel.delete().queue());
                        r.setStage(RecruitStatus.ABANDONED);
                        r.setFinalized(true);
                    }
                    return Uni.createFrom().item(Lists.newArrayList(r));
                })
                .onFailure().recoverWithNull()
                .chain(recruitmentList -> {
                    if (recruitmentList != null) return service.update(recruitmentList.get(0));
                    else return Uni.createFrom().voidItem();
                })
                .await().indefinitely();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        final User user = event.getUser();
        final Member member = event.getMember();
        final Guild guild = event.getGuild();
        if (guild.getIdLong() != config.getRecruitmentGuildId()) return;
        logManager.logJoinEvent(user);
        this.service
                .readByUserId(user.getIdLong())
                .onItem().ifNotNull()
                .call(recruitmentList -> {
                    final RecruitmentImpl r = recruitmentList.stream().findFirst().get();
                    final Gamemode gamemode = config.getGamemode(r.getGamemode());
                    final Category category = Objects.requireNonNull(guild.getCategoryById(gamemode.getCategoryId()));
                    guild.addRoleToMember(member, Objects.requireNonNull(jda.getRoleById(gamemode.getRoleId())))
                         .queueAfter(3, TimeUnit.MINUTES);
                    member.modifyNickname(r.getNickname()).queue();
                    category
                            .createTextChannel(r.getNickname() + "-" + r.getId())
                            .queue(channel -> channel
                                    .sendMessage(TemplateMessages.controlPanel(r, config))
                                    .setActionRow(ButtonPatterns.nextStep(r.getId()))
                                    .queue()
                            );
                    r.setStage(RecruitStatus.CHECKED);
                    return Uni.createFrom().item(Lists.newArrayList(r));
                })
                .onFailure().recoverWithNull()
                .chain(recruitmentList -> {
                    if (recruitmentList != null) return service.update(recruitmentList.get(0));
                    else return Uni.createFrom().voidItem();
                })
                .await().indefinitely();

    }
}
