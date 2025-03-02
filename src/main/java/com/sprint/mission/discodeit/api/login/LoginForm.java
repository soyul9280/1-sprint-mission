package com.sprint.mission.discodeit.api.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LoginForm {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
