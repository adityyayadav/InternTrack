package com.internpilot.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacultyAssignmentRepository extends JpaRepository<FacultyAssignment, UUID> {

    List<FacultyAssignment> findByFacultyId(UUID facultyId);

    boolean existsByFacultyIdAndStudentId(UUID facultyId, UUID studentId);

    Optional<FacultyAssignment> findByFacultyIdAndStudentId(UUID facultyId, UUID studentId);

    List<FacultyAssignment> findByStudentId(UUID studentId);
}
