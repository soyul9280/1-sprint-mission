package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ChannelApi;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;


import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelApiController implements ChannelApi {
    private final ChannelService channelService;


    @PostMapping("/public")
    @Override
    public ResponseEntity<ChannelDto> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequestDto publicChannelParam) {
        ChannelDto publicChannel = channelService.createPublicChannel(publicChannelParam);
        return new ResponseEntity<>(publicChannel, HttpStatus.CREATED);
    }


    @PostMapping("/private")
    @Override
    public ResponseEntity<ChannelDto> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequestDto privateChannelParam) {
        ChannelDto createdChannel = channelService.createPrivateChannel(privateChannelParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }


    @PatchMapping("{channelId}")
    @Override
    public ResponseEntity<ChannelDto> updateChannel(@PathVariable("channelId") UUID channelId, @Valid @RequestBody ChannelUpdateRequestDto channelParam) {
        ChannelDto channel = channelService.updateChannel(channelId, channelParam);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }


    @DeleteMapping("{channelId}")
    @Override
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Override
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
