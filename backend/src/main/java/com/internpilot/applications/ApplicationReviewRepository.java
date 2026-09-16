package com.internpilot.applications;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, UUID> {
}
