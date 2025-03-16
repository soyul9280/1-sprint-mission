package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.ChannelApiDocs;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;


import com.sprint.mission.discodeit.mapper.ChannelMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelApiController implements ChannelApiDocs {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;


    @PostMapping("/public")
    @Override
    public ResponseEntity<Channel> createPublicChannel(@RequestBody @Valid PublicChannelCreateRequestDto publicChannelParam) {
        Channel createdChannel = channelService.createPublicChannel(publicChannelParam);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }


    @PostMapping("/private")
    @Override
    public ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateRequestDto privateChannelParam) {
        Channel createdChannel = channelService.createPrivateChannel(privateChannelParam);
        ChannelDto dto = channelMapper.toDto(createdChannel);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PatchMapping("{channelId}")
    @Override
    public ResponseEntity<Channel> updateChannel(@PathVariable("channelId") UUID channelId, @RequestBody ChannelUpdateRequestDto channelParam) {
        Channel channel = channelService.updateChannel(channelId, channelParam);
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
        List<Channel> channels = channelService.findAllByUserId(userId);
        List<ChannelDto> channelList = new ArrayList<>();
        for (Channel channel : channels) {
            ChannelDto channelDto = channelMapper.toDto(channel);
            channelList.add(channelDto);
        }
        return ResponseEntity.status(HttpStatus.OK).body(channelList);
    }
}
