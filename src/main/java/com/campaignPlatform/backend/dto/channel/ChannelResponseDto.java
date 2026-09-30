package com.campaignPlatform.backend.dto.channel;

import com.campaignPlatform.backend.domain.ChannelType;

import java.time.OffsetDateTime;

/**
 * DTO returned by the API representing a Channel.
 */
public record ChannelResponseDto(

        Long id,
        String name,
        ChannelType type,
        String description,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
