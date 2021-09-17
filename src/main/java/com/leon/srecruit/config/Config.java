package com.leon.srecruit.config;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {

    private long adminRole = 0;
    private long alrightChannelInviteId = 0;
    private long recruitmentGuildId = 0;
    private long teamGuildId = 0;
    private long referenceGuildId = 0;
    private long timeToAliveInvite = 10080;
    private long intervieweeRoleId = 0;
    private long verifiedRoleId = 0;
    private long channelInviteId = 0;
    private long logInviteChannelId = 0;
    private long logChannelId = 0;
    private long acceptedRoleId = 0;
    private Set<Gamemode> gamemodes = Sets.newHashSet();

    public Gamemode getGamemode(String name) {
        return gamemodes.stream().filter(gamemode -> gamemode.getName().equalsIgnoreCase(name)).findFirst().get();
    }

}
