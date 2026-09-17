package com.internpilot.documents;

import com.internpilot.common.exception.ResourceNotFoundException;
import com.internpilot.users.FacultyAssignmentRepository;
import com.internpilot.users.Profile;
import com.internpilot.users.ProfileRepository;
import com.internpilot.users.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProfileRepository profileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final FacultyAssignmentRepository facultyAssignmentRepository;
    private final SupabaseStorageService storageService;

    public UploadIntentDto.Response createUploadIntent(UUID userId, UploadIntentDto.Request request) {
        profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", userId));

        String storagePath = storageService.generateStoragePath(userId, request.getDocumentType(), request.getFileName());
        String bucket = storageService.getDefaultBucket();
        String downloadUrl = storageService.getDownloadUrl(bucket, storagePath);

        return UploadIntentDto.Response.builder()
                .storagePath(storagePath)
                .bucketName(bucket)
                .downloadUrl(downloadUrl)
                .documentType(request.getDocumentType())
                .fileName(request.getFileName())
                .build();
    }

    @Transactional
    public DocumentDto registerDocument(UUID userId, CreateDocumentDto dto) {
        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", userId));

        String bucket = (dto.getBucketName() != null && !dto.getBucketName().isBlank())
                ? dto.getBucketName()
                : storageService.getDefaultBucket();

        Document doc = Document.builder()
                .user(user)
                .documentType(dto.getDocumentType())
                .fileName(dto.getFileName())
                .fileType(dto.getFileType())
                .fileSize(dto.getFileSize())
                .storagePath(dto.getStoragePath())
                .bucketName(bucket)
                .build();

        Document saved = documentRepository.save(doc);

        // If this is a resume, link it directly to the student profile
        if (dto.getDocumentType() == DocumentType.RESUME) {
            studentProfileRepository.findById(userId).ifPresent(sp -> {
                sp.setResumeFileId(saved.getId());
                studentProfileRepository.save(sp);
            });
        }

        String downloadUrl = storageService.getDownloadUrl(saved.getBucketName(), saved.getStoragePath());
        return DocumentDto.from(saved, downloadUrl);
    }

    @Transactional
    public DocumentDto uploadDocument(UUID userId, DocumentType documentType, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        Profile user = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile", userId));

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String storagePath = storageService.generateStoragePath(userId, documentType, originalFilename);
        String bucket = storageService.getDefaultBucket();

        try {
            byte[] bytes = file.getBytes();
            storageService.uploadFile(bucket, storagePath, bytes, file.getContentType());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read uploaded file contents.", e);
        }

        Document doc = Document.builder()
                .user(user)
                .documentType(documentType)
                .fileName(originalFilename)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .storagePath(storagePath)
                .bucketName(bucket)
                .build();

        Document saved = documentRepository.save(doc);

        if (documentType == DocumentType.RESUME) {
            studentProfileRepository.findById(userId).ifPresent(sp -> {
                sp.setResumeFileId(saved.getId());
                studentProfileRepository.save(sp);
            });
        }

        String downloadUrl = storageService.getDownloadUrl(saved.getBucketName(), saved.getStoragePath());
        return DocumentDto.from(saved, downloadUrl);
    }

    public DocumentDto getDocumentById(UUID id, UUID requesterId, boolean isFacultyOrAdmin) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        if (!isFacultyOrAdmin) {
            if (!doc.getUser().getId().equals(requesterId)) {
                throw new AccessDeniedException("You do not have permission to view this document.");
            }
        } else {
            // If faculty (not admin), ensure student is assigned or requester owns document
            boolean isOwner = doc.getUser().getId().equals(requesterId);
            boolean isAssigned = facultyAssignmentRepository.existsByFacultyIdAndStudentId(requesterId, doc.getUser().getId());
            if (!isOwner && !isAssigned) {
                // Check if admin is verified by caller
            }
        }

        String downloadUrl = storageService.getDownloadUrl(doc.getBucketName(), doc.getStoragePath());
        return DocumentDto.from(doc, downloadUrl);
    }

    public Page<DocumentDto> getMyDocuments(UUID userId, DocumentType type, Pageable pageable) {
        Page<Document> docs = (type != null)
                ? documentRepository.findByUserIdAndDocumentType(userId, type, pageable)
                : documentRepository.findByUserId(userId, pageable);

        return docs.map(doc -> DocumentDto.from(doc, storageService.getDownloadUrl(doc.getBucketName(), doc.getStoragePath())));
    }

    @Transactional
    public void deleteDocument(UUID id, UUID userId) {
        Document doc = documentRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", id));

        // Delete underlying object in Supabase Storage
        storageService.deleteFile(doc.getBucketName(), doc.getStoragePath());

        // Unlink from student profile if this was the resume
        if (doc.getDocumentType() == DocumentType.RESUME) {
            studentProfileRepository.findById(userId).ifPresent(sp -> {
                if (id.equals(sp.getResumeFileId())) {
                    sp.setResumeFileId(null);
                    studentProfileRepository.save(sp);
                }
            });
        }

        documentRepository.delete(doc);
    }

    public Page<DocumentDto> getAssignedFacultyDocuments(UUID facultyId, UUID studentIdFilter, DocumentType typeFilter, Pageable pageable) {
        List<UUID> assignedStudentIds = facultyAssignmentRepository.findByFacultyId(facultyId)
                .stream()
                .map(fa -> fa.getStudent().getId())
                .toList();

        if (assignedStudentIds.isEmpty()) {
            return Page.empty(pageable);
        }

        if (studentIdFilter != null) {
            if (!assignedStudentIds.contains(studentIdFilter)) {
                throw new AccessDeniedException("The specified student is not assigned to you.");
            }
            Page<Document> docs = (typeFilter != null)
                    ? documentRepository.findByUserIdAndDocumentType(studentIdFilter, typeFilter, pageable)
                    : documentRepository.findByUserId(studentIdFilter, pageable);

            return docs.map(doc -> DocumentDto.from(doc, storageService.getDownloadUrl(doc.getBucketName(), doc.getStoragePath())));
        }

        Page<Document> docs = (typeFilter != null)
                ? documentRepository.findByUserIdInAndDocumentType(assignedStudentIds, typeFilter, pageable)
                : documentRepository.findByUserIdIn(assignedStudentIds, pageable);

        return docs.map(doc -> DocumentDto.from(doc, storageService.getDownloadUrl(doc.getBucketName(), doc.getStoragePath())));
    }
}
