package com.internpilot.audit;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
public class AuditEventDto {
    UUID id;
    UUID actorId;
    String entityType;
    UUID entityId;
    String action;
    String metadata;
    Instant createdAt;

    public static AuditEventDto from(AuditEvent event) {
        return AuditEventDto.builder()
                .id(event.getId())
                .actorId(event.getActorId())
                .entityType(event.getEntityType())
                .entityId(event.getEntityId())
                .action(event.getAction())
                .metadata(event.getMetadata())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
