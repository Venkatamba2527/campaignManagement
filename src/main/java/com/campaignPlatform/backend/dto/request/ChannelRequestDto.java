package com.campaignPlatform.backend.dto.request;

import com.campaignPlatform.backend.domain.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating or updating a Channel.
 */
public record ChannelRequestDto(

        @NotBlank(message = "Channel name is required")
        @Size(max = 100, message = "Channel name must not exceed 100 characters")
        String name,

        @NotNull(message = "Channel type is required")
        ChannelType type,

        String description,

        Boolean isActive
) {}
