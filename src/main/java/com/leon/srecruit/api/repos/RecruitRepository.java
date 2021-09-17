package com.leon.srecruit.api.repos;

import com.leon.srecruit.database.Repository;
import com.leon.srecruit.internal.entities.RecruitmentImpl;
import io.smallrye.mutiny.Uni;

import java.util.List;

public interface RecruitRepository extends Repository<RecruitmentImpl, Long> {

    Uni<List<RecruitmentImpl>> readByUserId(long id);

    Uni<RecruitmentImpl> readByNickname(String nickname);
}
