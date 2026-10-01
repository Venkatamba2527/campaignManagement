package com.campaignPlatform.backend.service;

import com.campaignPlatform.backend.domain.CampaignStatus;
import com.campaignPlatform.backend.dto.request.CampaignRequestDto;
import com.campaignPlatform.backend.dto.response.CampaignResponseDto;
import com.campaignPlatform.backend.repository.CampaignRepository;
import com.campaignPlatform.backend.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final ChannelRepository channelRepository;

    public CampaignService(CampaignRepository campaignRepository,
                           ChannelRepository channelRepository) {
        this.campaignRepository = campaignRepository;
        this.channelRepository  = channelRepository;
    }

    // -------------------------------------------------------
    // READ
    // -------------------------------------------------------

    public List<CampaignResponseDto> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    public CampaignResponseDto getCampaignById(Long id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));
    }

    public List<CampaignResponseDto> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }

    public List<CampaignResponseDto> getCampaignsByChannel(Long channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + channelId));
        return campaignRepository.findByChannelId(channelId);
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public CampaignResponseDto createCampaign(CampaignRequestDto dto) {
        validateChannelIds(dto.channelIds());

        if (dto.startAt() != null && dto.endAt() != null
                && dto.endAt().isBefore(dto.startAt())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return campaignRepository.create(dto);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    public CampaignResponseDto updateCampaign(Long id, CampaignRequestDto dto) {
        campaignRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));

        validateChannelIds(dto.channelIds());

        if (dto.startAt() != null && dto.endAt() != null
                && dto.endAt().isBefore(dto.startAt())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        return campaignRepository.update(id, dto)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));
    }

    public CampaignResponseDto updateCampaignStatus(Long id, CampaignStatus newStatus) {
        CampaignResponseDto existing = campaignRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));

        validateStatusTransition(existing.status(), newStatus);

        return campaignRepository.updateStatus(id, newStatus)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    public void deleteCampaign(Long id) {
        CampaignResponseDto existing = campaignRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Campaign not found with id: " + id));

        if (existing.status() != CampaignStatus.DRAFT
                && existing.status() != CampaignStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Only DRAFT or CANCELLED campaigns can be deleted. Current status: " + existing.status());
        }

        campaignRepository.delete(id);
    }

    // -------------------------------------------------------
    // PRIVATE HELPERS
    // -------------------------------------------------------

    private void validateChannelIds(List<Long> channelIds) {
        if (channelIds == null || channelIds.isEmpty()) return;
        for (Long channelId : channelIds) {
            channelRepository.findById(channelId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Channel not found with id: " + channelId));
        }
    }

    private void validateStatusTransition(CampaignStatus current, CampaignStatus next) {
        boolean valid = switch (current) {
            case DRAFT      -> next == CampaignStatus.SCHEDULED || next == CampaignStatus.CANCELLED;
            case SCHEDULED  -> next == CampaignStatus.RUNNING   || next == CampaignStatus.CANCELLED;
            case RUNNING    -> next == CampaignStatus.PAUSED     || next == CampaignStatus.COMPLETED
                                                                 || next == CampaignStatus.CANCELLED;
            case PAUSED     -> next == CampaignStatus.RUNNING    || next == CampaignStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " → " + next);
        }
    }
}
