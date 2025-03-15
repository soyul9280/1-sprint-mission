package com.sprint.mission.discodeit.api.docs;

import com.sprint.mission.discodeit.api.LoginForm;
import com.sprint.mission.discodeit.dto.response.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestBody;



@Tag(name = "Auth", description = "인증 API")
public interface LoginApiDocs {

    @Operation(summary = "로그인",responses = {
            @ApiResponse(responseCode = "200",description = "로그인 성공"),
            @ApiResponse(responseCode = "400",description = "비밀번호가 일치하지 않음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",description = "사용자를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

    })
    ResponseEntity<UserDto> login(@Valid @RequestBody LoginForm loginForm);
}
