package com.campaignPlatform.backend.domain.model.channel;

import com.campaignPlatform.backend.domain.ChannelType;

import java.time.OffsetDateTime;

/**
 * Response payload representing a Channel.
 */
public record ChannelResponse(

        Long id,
        String name,
        ChannelType type,
        String description,
        Boolean isActive,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
