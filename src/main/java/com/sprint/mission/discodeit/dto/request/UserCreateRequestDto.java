package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserCreateRequestDto {

    @NotBlank
    private String loginId;
    @NotBlank
    private String userName;
    @NotBlank
    private String userEmail;
    @NotBlank
    private String password;
}
