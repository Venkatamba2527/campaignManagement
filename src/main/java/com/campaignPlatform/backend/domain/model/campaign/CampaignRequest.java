package com.campaignPlatform.backend.domain.model.campaign;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request payload for creating or updating a Campaign.
 */
public record CampaignRequest(

        @NotBlank(message = "Campaign name is required")
        @Size(max = 200, message = "Campaign name must not exceed 200 characters")
        String name,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
        BigDecimal budget,

        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
        String currency,

        OffsetDateTime startAt,

        OffsetDateTime endAt,

        String createdBy
) {
    // Default currency to USD when not provided
    public CampaignRequest {
        if (currency == null || currency.isBlank()) currency = "USD";
    }
}
