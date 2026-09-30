package com.campaignPlatform.backend.repository;

import com.campaignPlatform.backend.domain.ChannelType;
import com.campaignPlatform.backend.dto.request.ChannelRequestDto;
import com.campaignPlatform.backend.dto.response.ChannelResponseDto;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.campaignplatform.jooq.Tables.CHANNEL;

@Repository
public class ChannelRepository {

    private final DSLContext dsl;

    public ChannelRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    // -------------------------------------------------------
    // READ
    // -------------------------------------------------------

    public List<ChannelResponseDto> findAll() {
        return dsl.selectFrom(CHANNEL)
                .fetch()
                .map(r -> new ChannelResponseDto(
                        r.getId(),
                        r.getName(),
                        ChannelType.valueOf(r.getType()),
                        r.getDescription(),
                        r.getIsActive(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    public Optional<ChannelResponseDto> findById(Long id) {
        return dsl.selectFrom(CHANNEL)
                .where(CHANNEL.ID.eq(id))
                .fetchOptional()
                .map(r -> new ChannelResponseDto(
                        r.getId(),
                        r.getName(),
                        ChannelType.valueOf(r.getType()),
                        r.getDescription(),
                        r.getIsActive(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    public List<ChannelResponseDto> findByType(ChannelType type) {
        return dsl.selectFrom(CHANNEL)
                .where(CHANNEL.TYPE.eq(type.name()))
                .fetch()
                .map(r -> new ChannelResponseDto(
                        r.getId(),
                        r.getName(),
                        ChannelType.valueOf(r.getType()),
                        r.getDescription(),
                        r.getIsActive(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    public List<ChannelResponseDto> findAllActive() {
        return dsl.selectFrom(CHANNEL)
                .where(CHANNEL.IS_ACTIVE.isTrue())
                .fetch()
                .map(r -> new ChannelResponseDto(
                        r.getId(),
                        r.getName(),
                        ChannelType.valueOf(r.getType()),
                        r.getDescription(),
                        r.getIsActive(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public ChannelResponseDto create(ChannelRequestDto dto) {
        return dsl.insertInto(CHANNEL)
                .set(CHANNEL.NAME,        dto.name())
                .set(CHANNEL.TYPE,        dto.type().name())
                .set(CHANNEL.DESCRIPTION, dto.description())
                .set(CHANNEL.IS_ACTIVE,   dto.isActive())
                .returning()
                .fetchOne(r -> new ChannelResponseDto(
                        r.getId(),
                        r.getName(),
                        ChannelType.valueOf(r.getType()),
                        r.getDescription(),
                        r.getIsActive(),
                        r.getCreatedAt(),
                        r.getUpdatedAt()
                ));
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    public Optional<ChannelResponseDto> update(Long id, ChannelRequestDto dto) {
        int updated = dsl.update(CHANNEL)
                .set(CHANNEL.NAME,        dto.name())
                .set(CHANNEL.TYPE,        dto.type().name())
                .set(CHANNEL.DESCRIPTION, dto.description())
                .set(CHANNEL.IS_ACTIVE,   dto.isActive())
                .set(CHANNEL.UPDATED_AT,  OffsetDateTime.now())
                .where(CHANNEL.ID.eq(id))
                .execute();

        if (updated == 0) return Optional.empty();
        return findById(id);
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    public boolean delete(Long id) {
        int deleted = dsl.deleteFrom(CHANNEL)
                .where(CHANNEL.ID.eq(id))
                .execute();
        return deleted > 0;
    }
}
