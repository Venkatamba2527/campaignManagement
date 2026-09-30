package com.campaignPlatform.backend.repository;

import com.campaignPlatform.backend.domain.CampaignStatus;
import com.campaignPlatform.backend.dto.request.CampaignRequestDto;
import com.campaignPlatform.backend.dto.response.CampaignResponseDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.campaignplatform.jooq.Tables.CAMPAIGN;

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
                .map(r -> new CampaignResponseDto(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        CampaignStatus.valueOf(r.getStatus()),
                        r.getBudget(),
                        r.getCurrency(),
                        r.getStartAt(),
                        r.getEndAt(),
                        r.getCreatedBy(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    public Optional<CampaignResponseDto> findById(Long id) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .fetchOptional()
                .map(r -> new CampaignResponseDto(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        CampaignStatus.valueOf(r.getStatus()),
                        r.getBudget(),
                        r.getCurrency(),
                        r.getStartAt(),
                        r.getEndAt(),
                        r.getCreatedBy(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    public List<CampaignResponseDto> findByStatus(CampaignStatus status) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.STATUS.eq(status.name()))
                .fetch()
                .map(r -> new CampaignResponseDto(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        CampaignStatus.valueOf(r.getStatus()),
                        r.getBudget(),
                        r.getCurrency(),
                        r.getStartAt(),
                        r.getEndAt(),
                        r.getCreatedBy(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public CampaignResponseDto create(CampaignRequestDto dto) {
        return dsl.insertInto(CAMPAIGN)
                .set(CAMPAIGN.NAME,        dto.name())
                .set(CAMPAIGN.DESCRIPTION, dto.description())
                .set(CAMPAIGN.STATUS,      CampaignStatus.DRAFT.name())
                .set(CAMPAIGN.BUDGET,      dto.budget())
                .set(CAMPAIGN.CURRENCY,    dto.currency())
                .set(CAMPAIGN.START_AT,    dto.startAt())
                .set(CAMPAIGN.END_AT,      dto.endAt())
                .set(CAMPAIGN.CREATED_BY,  dto.createdBy())
                .returning()
                .fetchOne(r -> new CampaignResponseDto(
                        r.getId(),
                        r.getName(),
                        r.getDescription(),
                        CampaignStatus.valueOf(r.getStatus()),
                        r.getBudget(),
                        r.getCurrency(),
                        r.getStartAt(),
                        r.getEndAt(),
                        r.getCreatedBy(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
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
        int deleted = dsl.deleteFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .execute();
        return deleted > 0;
    }
}
