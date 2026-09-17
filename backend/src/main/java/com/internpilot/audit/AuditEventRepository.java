package com.internpilot.audit;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    Page<AuditEvent> findByActorId(UUID actorId, Pageable pageable);
    Page<AuditEvent> findByEntityType(String entityType, Pageable pageable);
}
