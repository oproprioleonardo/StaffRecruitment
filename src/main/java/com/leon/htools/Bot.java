package com.leon.htools;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.leon.htools.listeners.Commands;
import com.leon.htools.listeners.RecruitmentListeners;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.apache.logging.log4j.LogManager;

@Getter
public class Bot {

    private static Bot instance;
    private static Injector injector;

    @Inject
    private JDA jda;

    public Bot() {
        instance = this;
        try {
            injector = Guice.createInjector(Module.of(this));
            injector.injectMembers(this);
        } catch (Exception e) {
            e.printStackTrace();
            LogManager.getRootLogger().warn("Configure seus dados no arquivo .env e config.json");
            System.exit(0);
            return;
        }
        this.jda.addEventListener(
                getInstance(Commands.class),
                getInstance(RecruitmentListeners.class)
        );
    }

    public static <O> O getInstance(Class<O> clazz) {
        return injector.getInstance(clazz);
    }

}

