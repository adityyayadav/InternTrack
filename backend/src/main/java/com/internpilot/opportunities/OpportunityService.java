package com.internpilot.opportunities;

import com.internpilot.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;

    public Page<OpportunityDto> getAllActiveOpportunities(Pageable pageable) {
        return opportunityRepository.findByIsActiveTrue(pageable)
                .map(OpportunityDto::from);
    }

    public OpportunityDto getOpportunityById(UUID id) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", id));
        return OpportunityDto.from(opportunity);
    }

    @Transactional
    public OpportunityDto createOpportunity(OpportunityDto dto, UUID adminId) {
        Opportunity opp = Opportunity.builder()
                .title(dto.getTitle())
                .organization(dto.getOrganization())
                .description(dto.getDescription())
                .requiredSkills(dto.getRequiredSkills() != null ? dto.getRequiredSkills() : "[]")
                .location(dto.getLocation())
                .workMode(dto.getWorkMode())
                .duration(dto.getDuration())
                .applicationDeadline(dto.getApplicationDeadline())
                .isActive(true)
                .createdBy(adminId)
                .build();

        return OpportunityDto.from(opportunityRepository.save(opp));
    }

    @Transactional
    public OpportunityDto updateOpportunity(UUID id, OpportunityDto dto) {
        Opportunity opp = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", id));

        opp.setTitle(dto.getTitle());
        opp.setOrganization(dto.getOrganization());
        opp.setDescription(dto.getDescription());
        if (dto.getRequiredSkills() != null)
            opp.setRequiredSkills(dto.getRequiredSkills());
        opp.setLocation(dto.getLocation());
        opp.setWorkMode(dto.getWorkMode());
        opp.setDuration(dto.getDuration());
        opp.setApplicationDeadline(dto.getApplicationDeadline());
        if (dto.getIsActive() != null)
            opp.setActive(dto.getIsActive());

        return OpportunityDto.from(opportunityRepository.save(opp));
    }
}
