package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    MessageDto messageSave(MessageCreateRequestDto request, List<BinaryContentCreateRequestDto> binaryContents);
    MessageDto findMessageById(UUID id);
    PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page);
    MessageDto updateMessage(UUID id, MessageUpdateDto messageParam);
    void deleteMessage(UUID id);
}
