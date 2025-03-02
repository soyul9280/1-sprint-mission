package com.sprint.mission.discodeit.api.channel;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;


import com.sprint.mission.discodeit.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel API",description = "채널 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelApiController {
    private final ChannelService channelService;

    @Operation(summary = "Public 채널 생성")
    @PostMapping("/public")
    public ResponseEntity<Channel> createPublicChannel(@RequestBody @Valid PublicChannelCreateRequestDto publicChannelParam) {
        Channel createdChannel = channelService.createPublicChannel(publicChannelParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @Operation(summary = "Private 채널 생성")
    @PostMapping("/private")
    public ResponseEntity<Channel> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateRequestDto privateChannelParam) {
        Channel createdChannel = channelService.createPrivateChannel(privateChannelParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }

    @Operation(summary = "채널 업데이트")
    @PatchMapping("{channelId}")
    public ResponseEntity<Channel> updateChannel(@PathVariable UUID channelId, @RequestBody @Valid ChannelUpdateRequestDto channelParam) {
        Channel channel = channelService.updateChannel(channelId, channelParam);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @Operation(summary = "채널 삭제")
    @DeleteMapping("{channelId}")
    public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
        channelService.deleteChannel(channelId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채널 전체 조회")
    @GetMapping
    public ResponseEntity<List<ChannelResponseDto>> findAll() {
        List<ChannelResponseDto> channels = channelService.findAllChannels();
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }

    @Operation(summary = "유저가 참여한 채널 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<List<ChannelResponseDto>> findAllByUserId(@PathVariable UUID userId) {
        List<ChannelResponseDto> channels = channelService.findAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }


}
