package com.leon.htools.config;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportConfig {

    private Set<BugCategory> categories;

    public void addRoleAtBug(String id, String roleId) {
        this.categories.stream().map(BugCategory::getBugs).reduce((bugs, bugs2) -> {
            final HashSet<Bug> bugs1 = Sets.newHashSet(bugs);
            bugs1.addAll(bugs2);
            return bugs1;
        }).flatMap(bugs -> bugs.stream()
                               .filter(bug -> bug.getId().equalsIgnoreCase(id))
                               .findFirst())
                       .ifPresent(bug -> bug.getRoles().add(roleId));
    }

    public void removeRoleAtBug(String id, String roleId) {
        this.categories.stream().map(BugCategory::getBugs).reduce((bugs, bugs2) -> {
            final HashSet<Bug> bugs1 = Sets.newHashSet(bugs);
            bugs1.addAll(bugs2);
            return bugs1;
        }).flatMap(bugs -> bugs.stream()
                               .filter(bug -> bug.getId().equalsIgnoreCase(id))
                               .findFirst())
                       .ifPresent(bug -> bug.getRoles().removeIf(s -> s.equalsIgnoreCase(roleId)));
    }

}
