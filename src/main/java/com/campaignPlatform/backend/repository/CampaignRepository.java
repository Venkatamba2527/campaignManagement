package com.campaignPlatform.backend.repository;

import com.campaignPlatform.backend.domain.CampaignStatus;
import com.campaignPlatform.backend.dto.request.CampaignRequestDto;
import com.campaignPlatform.backend.dto.response.CampaignResponseDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.campaignplatform.jooq.Tables.CAMPAIGN;
import static com.campaignplatform.jooq.Tables.CAMPAIGN_CHANNEL;

@Repository
public class CampaignRepository {

    private final DSLContext dsl;

    public CampaignRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // -------------------------------------------------------
    // READ
    // -------------------------------------------------------

    public List<CampaignResponseDto> findAll() {
        return dsl.selectFrom(CAMPAIGN)
                .fetch()
                .map(r -> toDto(r.getId(), r.getName(), r.getDescription(),
                        r.getStatus(), r.getBudget(), r.getCurrency(),
                        r.getStartAt(), r.getEndAt(), r.getCreatedBy(),
                        r.getCreatedAt(), r.getUpdatedAt()));
    }

    public Optional<CampaignResponseDto> findById(Long id) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .fetchOptional()
                .map(r -> toDto(r.getId(), r.getName(), r.getDescription(),
                        r.getStatus(), r.getBudget(), r.getCurrency(),
                        r.getStartAt(), r.getEndAt(), r.getCreatedBy(),
                        r.getCreatedAt(), r.getUpdatedAt()));
    }

    public List<CampaignResponseDto> findByStatus(CampaignStatus status) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.STATUS.eq(status.name()))
                .fetch()
                .map(r -> toDto(r.getId(), r.getName(), r.getDescription(),
                        r.getStatus(), r.getBudget(), r.getCurrency(),
                        r.getStartAt(), r.getEndAt(), r.getCreatedBy(),
                        r.getCreatedAt(), r.getUpdatedAt()));
    }

    public List<CampaignResponseDto> findByChannelId(Long channelId) {
        return dsl.select(CAMPAIGN.fields())
                .from(CAMPAIGN)
                .join(CAMPAIGN_CHANNEL).on(CAMPAIGN_CHANNEL.CAMPAIGN_ID.eq(CAMPAIGN.ID))
                .where(CAMPAIGN_CHANNEL.CHANNEL_ID.eq(channelId))
                .fetchInto(CAMPAIGN)
                .map(r -> toDto(r.getId(), r.getName(), r.getDescription(),
                        r.getStatus(), r.getBudget(), r.getCurrency(),
                        r.getStartAt(), r.getEndAt(), r.getCreatedBy(),
                        r.getCreatedAt(), r.getUpdatedAt()));
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public CampaignResponseDto create(CampaignRequestDto dto) {
        // Insert campaign
        var record = dsl.insertInto(CAMPAIGN)
                .set(CAMPAIGN.NAME,        dto.name())
                .set(CAMPAIGN.DESCRIPTION, dto.description())
                .set(CAMPAIGN.STATUS,      CampaignStatus.DRAFT.name())
                .set(CAMPAIGN.BUDGET,      dto.budget())
                .set(CAMPAIGN.CURRENCY,    dto.currency())
                .set(CAMPAIGN.START_AT,    dto.startAt())
                .set(CAMPAIGN.END_AT,      dto.endAt())
                .set(CAMPAIGN.CREATED_BY,  dto.createdBy())
                .returning()
                .fetchOne();

        Long campaignId = record.getId();

        // Insert channel associations
        if (dto.channelIds() != null && !dto.channelIds().isEmpty()) {
            for (Long channelId : dto.channelIds()) {
                dsl.insertInto(CAMPAIGN_CHANNEL)
                        .set(CAMPAIGN_CHANNEL.CAMPAIGN_ID, campaignId)
                        .set(CAMPAIGN_CHANNEL.CHANNEL_ID,  channelId)
                        .execute();
            }
        }

        return toDto(record.getId(), record.getName(), record.getDescription(),
                record.getStatus(), record.getBudget(), record.getCurrency(),
                record.getStartAt(), record.getEndAt(), record.getCreatedBy(),
                record.getCreatedAt(), record.getUpdatedAt());
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    public Optional<CampaignResponseDto> update(Long id, CampaignRequestDto dto) {
        int updated = dsl.update(CAMPAIGN)
                .set(CAMPAIGN.NAME,        dto.name())
                .set(CAMPAIGN.DESCRIPTION, dto.description())
                .set(CAMPAIGN.BUDGET,      dto.budget())
                .set(CAMPAIGN.CURRENCY,    dto.currency())
                .set(CAMPAIGN.START_AT,    dto.startAt())
                .set(CAMPAIGN.END_AT,      dto.endAt())
                .set(CAMPAIGN.UPDATED_AT,  OffsetDateTime.now())
                .where(CAMPAIGN.ID.eq(id))
                .execute();

        if (updated == 0) return Optional.empty();

        // Replace channel associations
        if (dto.channelIds() != null) {
            dsl.deleteFrom(CAMPAIGN_CHANNEL)
                    .where(CAMPAIGN_CHANNEL.CAMPAIGN_ID.eq(id))
                    .execute();
            for (Long channelId : dto.channelIds()) {
                dsl.insertInto(CAMPAIGN_CHANNEL)
                        .set(CAMPAIGN_CHANNEL.CAMPAIGN_ID, id)
                        .set(CAMPAIGN_CHANNEL.CHANNEL_ID,  channelId)
                        .execute();
            }
        }

        return findById(id);
    }

    public Optional<CampaignResponseDto> updateStatus(Long id, CampaignStatus status) {
        int updated = dsl.update(CAMPAIGN)
                .set(CAMPAIGN.STATUS,     status.name())
                .set(CAMPAIGN.UPDATED_AT, OffsetDateTime.now())
                .where(CAMPAIGN.ID.eq(id))
                .execute();

        if (updated == 0) return Optional.empty();
        return findById(id);
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    public boolean delete(Long id) {
        // campaign_channel rows deleted via ON DELETE CASCADE
        return dsl.deleteFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .execute() > 0;
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    private List<Long> fetchChannelIds(Long campaignId) {
        return dsl.select(CAMPAIGN_CHANNEL.CHANNEL_ID)
                .from(CAMPAIGN_CHANNEL)
                .where(CAMPAIGN_CHANNEL.CAMPAIGN_ID.eq(campaignId))
                .fetch(CAMPAIGN_CHANNEL.CHANNEL_ID);
    }

    private CampaignResponseDto toDto(Long id, String name, String description,
                                       String status, java.math.BigDecimal budget,
                                       String currency, OffsetDateTime startAt,
                                       OffsetDateTime endAt, String createdBy,
                                       OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new CampaignResponseDto(
                id, name, description,
                CampaignStatus.valueOf(status),
                fetchChannelIds(id),
                budget, currency,
                startAt, endAt,
                createdBy, createdAt, updatedAt
        );
    }
}
