package com.internpilot.documents;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Page<Document> findByUserId(UUID userId, Pageable pageable);

    Page<Document> findByUserIdAndDocumentType(UUID userId, DocumentType documentType, Pageable pageable);

    Optional<Document> findByIdAndUserId(UUID id, UUID userId);

    List<Document> findByUserIdAndDocumentType(UUID userId, DocumentType documentType);

    Page<Document> findByUserIdIn(Collection<UUID> userIds, Pageable pageable);

    Page<Document> findByUserIdInAndDocumentType(Collection<UUID> userIds, DocumentType documentType, Pageable pageable);
}
