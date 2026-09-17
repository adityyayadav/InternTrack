package com.internpilot.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, UUID> {

    Page<WeeklyReport> findByStudentId(UUID studentId, Pageable pageable);

    Page<WeeklyReport> findByApplicationId(UUID applicationId, Pageable pageable);

    Optional<WeeklyReport> findByIdAndStudentId(UUID id, UUID studentId);

    boolean existsByApplicationIdAndWeekNumber(UUID applicationId, Integer weekNumber);

    Page<WeeklyReport> findByStudentIdIn(Collection<UUID> studentIds, Pageable pageable);

    Page<WeeklyReport> findByStudentIdInAndStatus(Collection<UUID> studentIds, ReportStatus status, Pageable pageable);

    Page<WeeklyReport> findByStatus(ReportStatus status, Pageable pageable);

    long countByApplicationIdAndStatus(UUID applicationId, ReportStatus status);

    long countByStudentId(UUID studentId);

    long countByStudentIdAndStatus(UUID studentId, ReportStatus status);

    long countByStudentIdInAndStatusIn(Collection<UUID> studentIds, Collection<ReportStatus> statuses);

    java.util.List<WeeklyReport> findByStudentIdIn(Collection<UUID> studentIds);

    java.util.List<WeeklyReport> findByStudentId(UUID studentId);
}
