package com.internpilot.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditEventService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public AuditEvent record(UUID actorId, String entityType, UUID entityId, String action, String metadata) {
        return auditEventRepository.save(AuditEvent.builder()
                .actorId(actorId)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .metadata(metadata)
                .build());
    }

    @Transactional(readOnly = true)
    public Page<AuditEventDto> find(UUID actorId, String entityType, Pageable pageable) {
        Page<AuditEvent> events;
        if (actorId != null) {
            events = auditEventRepository.findByActorId(actorId, pageable);
        } else if (entityType != null && !entityType.isBlank()) {
            events = auditEventRepository.findByEntityType(entityType, pageable);
        } else {
            events = auditEventRepository.findAll(pageable);
        }
        return events.map(AuditEventDto::from);
    }
}
