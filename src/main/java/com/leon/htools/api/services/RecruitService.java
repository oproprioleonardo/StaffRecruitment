package com.leon.htools.api.services;

import com.leon.htools.database.Service;
import com.leon.htools.internal.entities.RecruitmentImpl;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface RecruitService extends Service<RecruitmentImpl, Long> {

    Uni<List<RecruitmentImpl>> readByUserId(long id);

}
