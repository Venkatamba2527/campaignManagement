package com.campaignPlatform.backend.dto.response;

import com.campaignPlatform.backend.domain.CampaignStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO returned by the API representing a Campaign.
 * channelIds maps to the BIGINT[] column on the campaign table.
 * audit maps to the JSONB audit log column.
 */
public record CampaignResponseDto(

        Long id,
        String name,
        String description,
        CampaignStatus status,
        List<Long> channelIds,
        BigDecimal budget,
        String currency,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        String createdBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String audit
) {}
