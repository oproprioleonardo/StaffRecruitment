package com.leon.htools.listeners;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.leon.htools.ReportProcessing;
import com.leon.htools.ReportStatus;
import com.leon.htools.api.entities.Report;
import com.leon.htools.api.services.ReportService;
import com.leon.htools.cache.ReportProcessingCache;
import com.leon.htools.config.Bug;
import com.leon.htools.config.BugCategory;
import com.leon.htools.config.ReportConfig;
import com.leon.htools.log.Dispatcher;
import com.leon.htools.log.ReportDispatch;
import com.leon.htools.log.ReportDispatchInfo;
import com.leon.htools.utils.TemplateMessage;
import com.leon.htools.utils.TemplateMessages;
import com.leon.htools.utils.validators.ArgsValidator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReportListeners extends ListenerAdapter {

    private static final String separator = "XXXX";
    @Inject
    private ReportProcessingCache cache;
    @Inject
    private ReportService service;
    @Inject
    private Dispatcher dispatcher;
    @Inject
    private ReportConfig botConfig;


}
