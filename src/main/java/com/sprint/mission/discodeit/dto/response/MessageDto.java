package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageDto {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String content;
    private UUID channelId;
    private UserDto author;
    private List<BinaryContentDto> attchments;
}
