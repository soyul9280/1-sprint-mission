package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User API", description = "User Management")
public interface UserApiDocs {

    @Operation(summary = "Create")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "유저 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유저 생성 실패")
    })
    ResponseEntity<User> createUser(
            @RequestPart("userCreateRequest") @Valid UserCreateRequestDto userCreateRequest,
            @RequestPart(value = "profile", required = false) MultipartFile profile);

    @Operation(summary = "FindAllUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유저 조회 실패")
    })
    ResponseEntity<List<UserResponseDto>> findAllUsers();

    @Operation(summary = "UpdateUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "유저 업데이트 실패")
    })
    ResponseEntity<User> update(@PathVariable UUID userId,
                                @RequestPart("userUpdateRequest") @Valid UserUpdateRequestDto updateUserDto,
                                @RequestPart(value="profile",required = false) MultipartFile profile);

    @Operation(summary = "DeleteUser")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "유저 삭제 실패")
    })
    ResponseEntity<Void> delete(@PathVariable UUID userId);

    @Operation(summary = "UpdateUserStatus")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "유저 상태 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "유저 상태 업데이트 실패")
    })
    ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId, @RequestBody @Valid UserStatusUpdateDto request);
}
