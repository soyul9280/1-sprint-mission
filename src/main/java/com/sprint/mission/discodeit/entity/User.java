package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
public class User implements Serializable {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String userName;
    private String userEmail;
    private transient String password;
    private String loginId;

    private UUID profileId;


    public User(String loginId, String password, String userName, String userEmail,UUID profileId) {
        this.id=UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;

        this.loginId = loginId;
        this.password = password;
        this.userName = userName;
        this.userEmail = userEmail;
        this.profileId = profileId;
    }

    public void updateUser(String newLoginId, String newPassword, String newUserName, String newUserEmail, UUID newProfileId) {
        this.updatedAt = Instant.now();
        this.loginId = newLoginId;
        this.password = newPassword;
        this.userName = newUserName;
        this.userEmail = newUserEmail;
        this.profileId = newProfileId;
    }
}
