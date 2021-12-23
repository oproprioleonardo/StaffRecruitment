package com.leon.htools.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.inject.Singleton;
import com.leon.htools.ReportProcessing;
import com.leon.htools.utils.TemplateMessage;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.concurrent.TimeUnit;

@Singleton
public class ReportProcessingCache {

    public Cache<Long, ReportProcessing> cache =
            Caffeine.newBuilder()
                    .maximumSize(200)
                    .expireAfterWrite(90, TimeUnit.SECONDS)
                    .removalListener((Long key, ReportProcessing value, RemovalCause removalCause) -> {
                        if (value != null && removalCause == RemovalCause.EXPIRED) {
                            final MessageChannel channel = value.message.getChannel();
                            value.message.delete().queue();
                            channel.sendMessageEmbeds(TemplateMessage.EXPIRED.embed()).complete().delete()
                                   .queueAfter(30, TimeUnit.SECONDS);
                        }
                    })
                    .build();


    public boolean exists(Long userId) {
        return this.cache.asMap().containsKey(userId);
    }

    public ReportProcessing get(Long userId) {
        return this.cache.getIfPresent(userId);
    }

    public void remove(Long userId) {
        this.cache.invalidate(userId);
    }

    public void put(Long userId, ReportProcessing rp) {
        this.cache.put(userId, rp);
    }


}
