package com.leon.htools.internal.repos;

import com.google.inject.Singleton;
import com.leon.htools.api.repos.ReportRepository;
import com.leon.htools.database.JpaRepository;
import com.leon.htools.internal.entities.ReportImpl;

@Singleton
public class ReportRepositoryImpl extends JpaRepository<ReportImpl, Long> implements ReportRepository {

    public ReportRepositoryImpl() {
        super(ReportImpl.class);
    }
}
