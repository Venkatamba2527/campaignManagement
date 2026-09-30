package com.campaignPlatform.backend.dto.campaign;

import com.campaignPlatform.backend.domain.CampaignStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO returned by the API representing a Campaign.
 */
public record CampaignResponseDto(

        Long id,
        String name,
        String description,
        CampaignStatus status,
        BigDecimal budget,
        String currency,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
