package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ChannelApi;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;


import com.sprint.mission.discodeit.service.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelApiController implements ChannelApi {
    private final ChannelService channelService;


    @PostMapping("/public")
    @Override
    public ResponseEntity<ChannelDto> createPublicChannel(@Valid @RequestBody PublicChannelCreateRequestDto publicChannelParam) {
        log.info("공개 채널 생성 요청:{}",publicChannelParam);
        ChannelDto publicChannel = channelService.createPublicChannel(publicChannelParam);
        log.debug("공개 채널 생성 응답: {}", publicChannel);
        return new ResponseEntity<>(publicChannel, HttpStatus.CREATED);
    }


    @PostMapping("/private")
    @Override
    public ResponseEntity<ChannelDto> createPrivateChannel(@Valid @RequestBody PrivateChannelCreateRequestDto privateChannelParam) {
        log.info("비공개 채널 생성 요청: {}", privateChannelParam);
        ChannelDto createdChannel = channelService.createPrivateChannel(privateChannelParam);
        log.debug("비공개 채널 생성 응답: {}", createdChannel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }


    @PatchMapping("{channelId}")
    @Override
    public ResponseEntity<ChannelDto> updateChannel(@PathVariable("channelId") UUID channelId, @Valid @RequestBody ChannelUpdateRequestDto channelParam) {
        log.info("채널 수정 요청: id={}, request={}", channelId, channelParam);
        ChannelDto channel = channelService.updateChannel(channelId, channelParam);
        log.debug("채널 수정 응답: {}", channel);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }


    @DeleteMapping("{channelId}")
    @Override
    public ResponseEntity<Void> deleteChannel(@PathVariable("channelId") UUID channelId) {
        log.info("채널 삭제 요청: id={}", channelId);
        channelService.deleteChannel(channelId);
        log.debug("채널 삭제 완료");
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Override
    public ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam("userId") UUID userId) {
        log.info("사용자 채널 목록 조회 요청: userId={}", userId);
        List<ChannelDto> channels = channelService.findAllByUserId(userId);
        log.debug("사용자별 채널 목록 조회 응답: count={}", channels.size());
        return ResponseEntity.status(HttpStatus.OK).body(channels);
    }
}
