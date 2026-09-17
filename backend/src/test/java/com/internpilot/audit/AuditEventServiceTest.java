package com.internpilot.audit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditEventServiceTest {

    @Mock
    private AuditEventRepository auditEventRepository;

    @InjectMocks
    private AuditEventService auditEventService;

    @Test
    void recordPersistsActorAndEventDetails() {
        UUID actorId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        AuditEvent saved = AuditEvent.builder()
                .id(UUID.randomUUID())
                .actorId(actorId)
                .entityType("APPLICATION")
                .entityId(entityId)
                .action("SUBMITTED")
                .metadata("{}")
                .build();
        when(auditEventRepository.save(any(AuditEvent.class))).thenReturn(saved);

        AuditEvent result = auditEventService.record(actorId, "APPLICATION", entityId, "SUBMITTED", "{}");

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(captor.capture());
        assertThat(captor.getValue().getActorId()).isEqualTo(actorId);
        assertThat(captor.getValue().getEntityId()).isEqualTo(entityId);
        assertThat(captor.getValue().getAction()).isEqualTo("SUBMITTED");
        assertThat(result).isSameAs(saved);
    }
}
