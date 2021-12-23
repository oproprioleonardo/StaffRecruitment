package com.leon.htools.internal.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.leon.htools.api.repos.ReportRepository;
import com.leon.htools.api.services.ReportService;
import com.leon.htools.database.Repository;
import com.leon.htools.internal.entities.ReportImpl;
import io.smallrye.mutiny.Uni;

@Singleton
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository repository;

    @Override
    public Repository<ReportImpl, Long> getRepository() {
        return this.repository;
    }

    @Override
    public Uni<Void> create(ReportImpl object) {
        return this.repository.commit(object);
    }

    @Override
    public Uni<ReportImpl> read(Long id) {
        return this.repository.read(id);
    }

    @Override
    public Uni<ReportImpl> update(ReportImpl object) {
        return this.repository.update(object);
    }

    @Override
    public Uni<Void> delete(ReportImpl object) {
        return this.repository.delete(object);
    }

    @Override
    public Uni<ReportImpl> deleteById(Long id) {
        return this.repository.deleteById(id);
    }

    @Override
    public Uni<Boolean> exists(Long id) {
        return this.repository.exists(id);
    }
}
