package com.campaignPlatform.backend.repository;

import com.campaignPlatform.backend.domain.CampaignStatus;
import com.campaignPlatform.backend.dto.request.CampaignRequestDto;
import com.campaignPlatform.backend.dto.response.CampaignResponseDto;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Arrays;
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
                .map(this::toDto);
    }

    public Optional<CampaignResponseDto> findById(Long id) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .fetchOptional()
                .map(this::toDto);
    }

    public List<CampaignResponseDto> findByStatus(CampaignStatus status) {
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.STATUS.eq(status.name()))
                .fetch()
                .map(this::toDto);
    }

    public List<CampaignResponseDto> findByChannelId(Long channelId) {
        // Use PostgreSQL && (overlap) operator to find campaigns containing channelId
        return dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.CHANNEL_IDS.contains(new Long[]{channelId}))
                .fetch()
                .map(this::toDto);
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public CampaignResponseDto create(CampaignRequestDto dto) {
        Long[] channelIds = toArray(dto.channelIds());

        // Initial audit entry
        String audit = buildAuditEntry(null, CampaignStatus.DRAFT, dto.createdBy());

        var record = dsl.insertInto(CAMPAIGN)
                .set(CAMPAIGN.NAME,        dto.name())
                .set(CAMPAIGN.DESCRIPTION, dto.description())
                .set(CAMPAIGN.STATUS,      CampaignStatus.DRAFT.name())
                .set(CAMPAIGN.CHANNEL_IDS, channelIds)
                .set(CAMPAIGN.BUDGET,      dto.budget())
                .set(CAMPAIGN.CURRENCY,    dto.currency())
                .set(CAMPAIGN.START_AT,    dto.startAt())
                .set(CAMPAIGN.END_AT,      dto.endAt())
                .set(CAMPAIGN.CREATED_BY,  dto.createdBy())
                .set(CAMPAIGN.AUDIT,       JSONB.valueOf(audit))
                .returning()
                .fetchOne();

        return toDto(record);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    public Optional<CampaignResponseDto> update(Long id, CampaignRequestDto dto) {
        Long[] channelIds = toArray(dto.channelIds());

        int updated = dsl.update(CAMPAIGN)
                .set(CAMPAIGN.NAME,        dto.name())
                .set(CAMPAIGN.DESCRIPTION, dto.description())
                .set(CAMPAIGN.CHANNEL_IDS, channelIds)
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

    public Optional<CampaignResponseDto> updateStatus(Long id, CampaignStatus newStatus, String changedBy) {
        // Fetch current to build audit entry
        var existing = dsl.selectFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .fetchOne();
        if (existing == null) return Optional.empty();

        CampaignStatus oldStatus = CampaignStatus.valueOf(existing.getStatus());
        String currentAudit = existing.getAudit() != null ? existing.getAudit().data() : "[]";
        String newAudit = appendAuditEntry(currentAudit, oldStatus, newStatus, changedBy);

        int updated = dsl.update(CAMPAIGN)
                .set(CAMPAIGN.STATUS,     newStatus.name())
                .set(CAMPAIGN.UPDATED_AT, OffsetDateTime.now())
                .set(CAMPAIGN.AUDIT,      JSONB.valueOf(newAudit))
                .where(CAMPAIGN.ID.eq(id))
                .execute();

        if (updated == 0) return Optional.empty();
        return findById(id);
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    public boolean delete(Long id) {
        return dsl.deleteFrom(CAMPAIGN)
                .where(CAMPAIGN.ID.eq(id))
                .execute() > 0;
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    private CampaignResponseDto toDto(
            com.campaignplatform.jooq.tables.records.CampaignRecord r) {
        Long[] arr = r.getChannelIds();
        List<Long> channelIds = (arr != null) ? Arrays.asList(arr) : List.of();
        String audit = (r.getAudit() != null) ? r.getAudit().data() : "[]";

        return new CampaignResponseDto(
                r.getId(),
                r.getName(),
                r.getDescription(),
                CampaignStatus.valueOf(r.getStatus()),
                channelIds,
                r.getBudget(),
                r.getCurrency(),
                r.getStartAt(),
                r.getEndAt(),
                r.getCreatedBy(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                audit
        );
    }

    private Long[] toArray(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return new Long[0];
        return ids.toArray(new Long[0]);
    }

    private String buildAuditEntry(CampaignStatus from, CampaignStatus to, String by) {
        String ts = OffsetDateTime.now().toString();
        String fromStr = from != null ? "\"" + from.name() + "\"" : "null";
        return "[{\"from\":" + fromStr + ",\"to\":\"" + to.name()
                + "\",\"by\":\"" + (by != null ? by : "") + "\",\"at\":\"" + ts + "\"}]";
    }

    private String appendAuditEntry(String existingJson, CampaignStatus from, CampaignStatus to, String by) {
        String ts = OffsetDateTime.now().toString();
        String entry = "{\"from\":\"" + from.name() + "\",\"to\":\"" + to.name()
                + "\",\"by\":\"" + (by != null ? by : "") + "\",\"at\":\"" + ts + "\"}";
        // Append to existing JSON array
        String trimmed = existingJson.trim();
        if (trimmed.equals("[]")) {
            return "[" + entry + "]";
        }
        return trimmed.substring(0, trimmed.length() - 1) + "," + entry + "]";
    }
}
