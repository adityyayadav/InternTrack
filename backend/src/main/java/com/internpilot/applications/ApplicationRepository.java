package com.internpilot.applications;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    Page<Application> findByStudentId(UUID studentId, Pageable pageable);

    // Ensure one active application or draft per opportunity per student
    boolean existsByStudentIdAndOpportunityId(UUID studentId, UUID opportunityId);

    Optional<Application> findByIdAndStudentId(UUID id, UUID studentId);

    long countByStudentId(UUID studentId);

    long countByStudentIdAndStatus(UUID studentId, ApplicationStatus status);

    java.util.List<Application> findByStudentIdAndStatus(UUID studentId, ApplicationStatus status);

    long countByStudentIdInAndStatusIn(java.util.Collection<UUID> studentIds, java.util.Collection<ApplicationStatus> statuses);

    long countByStatus(ApplicationStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(DISTINCT a.student.id) FROM Application a WHERE a.status = com.internpilot.applications.ApplicationStatus.APPROVED")
    long countDistinctPlacedStudents();
}
