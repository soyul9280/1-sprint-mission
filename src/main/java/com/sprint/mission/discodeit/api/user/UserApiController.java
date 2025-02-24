package com.sprint.mission.discodeit.api.user;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Tag(name="User Api",description = "유저 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;
    private final UserStatusService userStatusService;


    @Operation(summary = "사용자 생성",description = "사용자 생성한다.")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<User> createUser(
            @RequestPart("userCreateRequest") @Valid UserCreateRequestDto userCreateRequest,
            @RequestPart(value="profile" ,required = false) MultipartFile profile ) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        User user = userService.createUser(userCreateRequest, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "모든 사용자 조회",description = "모든 사용자 조회한다.")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAllUsers() {
        List<UserResponseDto> allUsers = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(allUsers);
    }

    @Operation(summary = "사용자 정보 수정")
    @PatchMapping(value="/{userId}",consumes = {"multipart/form-data"})
    public ResponseEntity<User> update(@PathVariable UUID userId,
                                       @RequestPart("userUpdateRequest") @Valid UserUpdateRequestDto updateUserDto,
                                       @RequestPart(value="profile",required = false) MultipartFile profile) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        User user = userService.updateUser(userId, updateUserDto, file);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "사용자 삭제")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 상태 업데이트")
    @PutMapping("/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId, @RequestBody @Valid UserStatusUpdateDto request) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(userStatus);
    }

    private BinaryContentCreateRequestDto convertToBinaryContent(MultipartFile profile) {
        if (profile.isEmpty() || profile == null) {
            return null;
        }
        try{
            return new BinaryContentCreateRequestDto(
                    profile.getName(),
                    profile.getSize(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }catch (IOException e){
            throw new RuntimeException("파일 오류",e);
        }
    }
}
