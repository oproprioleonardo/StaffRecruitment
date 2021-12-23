package com.leon.htools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.leon.htools.api.repos.RecruitRepository;
import com.leon.htools.api.repos.ReportRepository;
import com.leon.htools.api.services.RecruitService;
import com.leon.htools.api.services.ReportService;
import com.leon.htools.cache.RecruitCacheProcess;
import com.leon.htools.cache.ReportProcessingCache;
import com.leon.htools.config.Config;
import com.leon.htools.internal.repos.RecruitRepositoryImpl;
import com.leon.htools.internal.repos.ReportRepositoryImpl;
import com.leon.htools.internal.services.RecruitServiceImpl;
import com.leon.htools.internal.services.ReportServiceImpl;
import com.leon.htools.log.Dispatcher;
import com.leon.htools.log.LogManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.hibernate.reactive.mutiny.Mutiny;

import javax.persistence.Persistence;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

@AllArgsConstructor(staticName = "of")
public class Module extends AbstractModule {

    private final Bot bot;

    protected void configure() {
        bind(Bot.class).toInstance(this.bot);
        bind(RecruitCacheProcess.class);
        bind(Dispatcher.class);
        bind(LogManager.class);
        bind(ReportProcessingCache.class);
        bind(ReportService.class).to(ReportServiceImpl.class);
        bind(ReportRepository.class).to(ReportRepositoryImpl.class);
        bind(RecruitRepository.class).to(RecruitRepositoryImpl.class);
        bind(RecruitService.class).to(RecruitServiceImpl.class);
    }

    @Provides
    @Singleton
    @SneakyThrows
    public Config providesConfig() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final File file = new File("config.json");
        if (!file.exists())
            Files.copy(this.getClass().getResourceAsStream("/config.json"), file.toPath());
        return gson.fromJson(new FileReader(file.getAbsolutePath()), Config.class);
    }

    @Provides
    @Singleton
    public Mutiny.SessionFactory providesSessionFactory() {
        return Persistence.createEntityManagerFactory("recruitment_processor").unwrap(Mutiny.SessionFactory.class);
    }

    @Provides
    @Singleton
    public Dotenv providesDotenv() {
        return Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().directory("./").load();
    }

    @SneakyThrows
    @Provides
    @Singleton
    public JDA providesJDA(Dotenv dotenv) {
        final JDABuilder jdaBuilder = JDABuilder.createDefault(dotenv.get("AUTH_SECRET"));
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE);
        jdaBuilder.setActivity(Activity.watching("Hylex Equipe"));
        jdaBuilder.setAutoReconnect(true);
        jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        final JDA jda = jdaBuilder.build();
        jda.updateCommands()
           .addCommands(
                   new CommandData("recrutar", "Inicia o processo de admissão à equipe.")
                           .addOption(OptionType.STRING, "id",
                                      "ID do Discord do candidato para admissão à equipe.", true
                           )
                           .addOption(OptionType.STRING, "nickname", "Nickname do canditado no Minecraft.", true)
                           .addOption(OptionType.STRING, "gamemode", "Modo de jogo desejado.", true),
                   new CommandData("clear", "Apaga mensagens do chat."),
                   new CommandData("ban", "Banir um usuário do grupo.")
                           .addOption(
                                   OptionType.STRING,
                                   "id",
                                   "ID do Discord do infrator.",
                                   true
                           )
                           .addOption(OptionType.STRING, "reason", "O motivo do banimento.", false),
                   new CommandData("report", "Reportar um bug")
                           .addOption(
                                   OptionType.STRING,
                                   "title",
                                   "Assunto sobre o que se refere.",
                                   true
                           ),
                   new CommandData("staffhelp", "Help command to HylexUtils system.")

           ).queue();
        return jda;
    }


}
