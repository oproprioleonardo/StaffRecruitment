package com.leon.srecruit.api.services;

import com.leon.srecruit.database.Service;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface RecruitService extends Service<RecruitmentImpl, Long> {

    Uni<List<RecruitmentImpl>> readByUserId(long id);

}
