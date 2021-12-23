package com.leon.htools.internal.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leon.htools.ReportStatus;
import com.leon.htools.api.entities.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportImpl implements Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String userId;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar date;
    private String title;
    @ElementCollection
    @LazyCollection(value = LazyCollectionOption.FALSE)
    @Column(length = 600)
    private List<String> steps = Lists.newArrayList();
    @ElementCollection
    @CollectionTable(name = "attachment_mapping",
                     joinColumns = {@JoinColumn(name = "attachment_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "attachment_name", length = 80)
    @Column(name = "attachment_url")
    @LazyCollection(value = LazyCollectionOption.FALSE)
    private Map<String, String> attachments = Maps.newHashMap();
    private String expectedOutcome;
    private String actualResult;
    private String serverName;
    private String lastOperator;
    @Enumerated
    private ReportStatus status = ReportStatus.ACTIVATED;

    public RestAction<User> getAuthor(JDA jda) {
        return jda.retrieveUserById(userId);
    }

    public void appendStep(String step) {
        this.steps.add(step);
    }

    public void attach(String description, String url) {
        this.attachments.put(description, url);
    }

}
