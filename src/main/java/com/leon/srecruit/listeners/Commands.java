package com.leon.srecruit.listeners;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.srecruit.cache.RecruitCacheProcess;
import com.leon.srecruit.config.Config;
import com.leon.srecruit.exceptions.CommandException;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import com.leon.srecruit.utils.ButtonPatterns;
import com.leon.srecruit.utils.TemplateMessage;
import com.leon.srecruit.utils.TemplateMessages;
import com.leon.srecruit.utils.validators.ArgsValidator;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@Singleton
public class Commands extends ListenerAdapter {

    @Inject
    private Config config;
    @Inject
    private RecruitCacheProcess cache;

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        if (e.getName().equalsIgnoreCase("recrutar")) {
            e.deferReply().queue();
            if (!e.isFromGuild()) return;
            final Guild guild = Objects.requireNonNull(e.getGuild());
            if (guild.getIdLong() != config.getRecruitmentGuildId()) return;
            guild.retrieveMember(e.getUser()).queue(member -> {
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
                        final UUID uuid = cache.add(r);
                        e.getHook().editOriginal(TemplateMessages.infoAttached(r, config))
                         .setActionRow(
                                 ButtonPatterns.confirmRecruitment(uuid),
                                 ButtonPatterns.cancelRecruitment(uuid)
                         ).queue();
                    });
                } else e.getHook().deleteOriginal().queue();
            });

        } else if (e.getName().equalsIgnoreCase("clear")) {
            e.deferReply().queue();
            if (!e.isFromGuild()) return;
            final Guild guild = Objects.requireNonNull(e.getGuild());
            if (guild.getIdLong() != config.getRecruitmentGuildId()) return;
            guild.retrieveMember(e.getUser()).queue(member -> {
                if (member.getRoles().stream().anyMatch(role -> config.getAdminRole() == role.getIdLong())) {
                    final MessageChannel channel = e.getChannel();
                    e.getHook().editOriginal("Apagando...")
                     .queue(message -> {
                         channel.getHistoryBefore(message, 100)
                                .queue(messageHistory -> e.getTextChannel().deleteMessages(messageHistory.getRetrievedHistory()).queue());
                         message.delete().queue();
                     });
                } else e.getHook().deleteOriginal().queue();
            });
        }
    }

}
