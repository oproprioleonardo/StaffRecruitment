package com.leon.htools.api.entities;

import com.leon.htools.ReportStatus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface Report extends Serializable {

    Long getId();

    void setId(Long id);

    String getUserId();

    void setUserId(String userId);

    Calendar getDate();

    void setDate(Calendar date);

    String getTitle();

    void setTitle(String title);

    List<String> getSteps();

    void setSteps(List<String> steps);

    Map<String, String> getAttachments();

    void setAttachments(Map<String, String> attachments);

    String getExpectedOutcome();

    void setExpectedOutcome(String expectedOutcome);

    String getActualResult();

    void setActualResult(String actualResult);

    String getServerName();

    void setServerName(String serverName);

    String getLastOperator();

    void setLastOperator(String lastOperator);

    ReportStatus getStatus();

    void setStatus(ReportStatus status);

    RestAction<User> getAuthor(JDA jda);

    void appendStep(String step);

    void attach(String description, String url);


}
