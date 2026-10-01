package com.campaignPlatform.backend.service;

import com.campaignPlatform.backend.dto.request.ChannelRequestDto;
import com.campaignPlatform.backend.dto.response.ChannelResponseDto;
import com.campaignPlatform.backend.repository.ChannelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChannelService {

    private final ChannelRepository channelRepository;

    public ChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // -------------------------------------------------------
    // READ
    // -------------------------------------------------------

    public List<ChannelResponseDto> getAllChannels() {
        return channelRepository.findAll();
    }

    public ChannelResponseDto getChannelById(Long id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + id));
    }

    public List<ChannelResponseDto> getActiveChannels() {
        return channelRepository.findAllActive();
    }

    // -------------------------------------------------------
    // CREATE
    // -------------------------------------------------------

    public ChannelResponseDto createChannel(ChannelRequestDto dto) {
        boolean nameExists = channelRepository.findAll()
                .stream()
                .anyMatch(c -> c.name().equalsIgnoreCase(dto.name()));
        if (nameExists) {
            throw new IllegalArgumentException("Channel with name '" + dto.name() + "' already exists");
        }
        return channelRepository.create(dto);
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------

    public ChannelResponseDto updateChannel(Long id, ChannelRequestDto dto) {
        return channelRepository.update(id, dto)
                .orElseThrow(() -> new NoSuchElementException("Channel not found with id: " + id));
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------

    public void deleteChannel(Long id) {
        boolean deleted = channelRepository.delete(id);
        if (!deleted) {
            throw new NoSuchElementException("Channel not found with id: " + id);
        }
    }
}
