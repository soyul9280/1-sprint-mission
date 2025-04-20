package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String username;
    private String email;
    private BinaryContentDto profile;
    private Boolean online;

    public UserDto(String username, String email, BinaryContentDto profile, Boolean online) {
        this.username = username;
        this.email = email;
        this.profile = profile;
        this.online = online;
    }
}
