package com.leon.srecruit.internal.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.srecruit.api.repos.RecruitRepository;
import com.leon.srecruit.api.services.RecruitService;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import io.smallrye.mutiny.Uni;
import lombok.Getter;

import java.util.List;

@Singleton
@Getter
public class RecruitServiceImpl implements RecruitService {

    @Inject
    private RecruitRepository repository;

    @Override
    public Uni<List<RecruitmentImpl>> readByUserId(long id) {
        return this.repository.readByUserId(id);
    }
}
