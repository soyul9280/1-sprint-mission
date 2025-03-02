package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.User;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserResponseDto {
    private UUID id;
    private Instant createAt;
    private Instant updateAt;

    private String loginId;
    private String userName;
    private String userEmail;
    private UUID profileId;
    private boolean isOnline;

    public UserResponseDto(User user, boolean userOnline) {
        this.id = user.getId();
        this.createAt = user.getCreatedAt();
        this.updateAt = user.getUpdatedAt();
        this.loginId = user.getLoginId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.profileId = user.getProfileId();
        this.isOnline = userOnline;
    }
}
