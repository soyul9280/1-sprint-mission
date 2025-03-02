package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message messageSave(MessageCreateRequestDto request, List<BinaryContentCreateRequestDto> binaryContents);
    Optional<Message> findMessage(UUID id);
    List<Message> findAllMessages();
    List<Message> findAllByChannelId(UUID channelId);
    Message updateMessage(UUID id, MessageUpdateDto messageParam);
    void deleteMessage(UUID id);
}
