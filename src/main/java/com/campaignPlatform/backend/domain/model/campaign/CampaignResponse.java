package com.campaignPlatform.backend.domain.model.campaign;

import com.campaignPlatform.backend.domain.CampaignStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Response payload representing a Campaign.
 */
public record CampaignResponse(

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
