package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private BinaryContentDto profile;
    private Boolean online;
}
