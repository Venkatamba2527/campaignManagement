package com.campaignPlatform.backend.dto.channel;

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

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        Boolean isActive
) {
    // Default isActive to true when not provided
    public ChannelRequestDto {
        if (isActive == null) isActive = true;
    }
}
