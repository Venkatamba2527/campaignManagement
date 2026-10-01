package com.campaignPlatform.backend.dto.response;

import com.campaignPlatform.backend.domain.CampaignStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO returned by the API representing a Campaign.
 */
public record CampaignResponseDto(

        Long id,
        String name,
        String description,
        CampaignStatus status,

        // Channels this campaign is running on
        List<Long> channelIds,

        BigDecimal budget,
        String currency,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
