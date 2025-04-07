package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.UserApi;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController implements UserApi {
    private final UserService userService;
    private final UserStatusService userStatusService;

    @PostMapping(consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<UserDto> createUser(
            @RequestPart("userCreateRequest") @Valid UserCreateRequestDto userCreateRequest,
            @RequestPart(value="profile" ,required = false) MultipartFile profile ) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        UserDto user = userService.createUser(userCreateRequest, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<UserDto> result = userService.findAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping(value="/{userId}",consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<UserDto> update(@PathVariable("userId") UUID userId,
                                       @RequestPart("userUpdateRequest") @Valid UserUpdateRequestDto updateUserDto,
                                       @RequestPart(value="profile",required = false) MultipartFile profile) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        UserDto user = userService.updateUser(userId, updateUserDto, file);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @DeleteMapping("/{userId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("userId") UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/userStatus")
    @Override //유저서비스에서 작업하면 유저서비스를 거쳐서 status서비스 작업하니까 컨트롤러에 책임을 주었는데 타당한지 고민해보기
    public ResponseEntity<UserStatusDto> updateUserStatus(@PathVariable("userId") UUID userId,@Valid @RequestBody UserStatusUpdateDto request) {
        UserStatusDto updateUserStatus = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updateUserStatus);
    }

    private BinaryContentCreateRequestDto convertToBinaryContent(MultipartFile profile) {
        if (profile == null||profile.isEmpty()) {
            return null;
        }
        try{
            return new BinaryContentCreateRequestDto(
                    profile.getOriginalFilename(),
                    profile.getSize(),
                    profile.getContentType(),
                    profile.getBytes()
            );
        }catch (IOException e){
            throw new RuntimeException("파일 변환 과정 오류",e);//파일 변환 에러도 따로 만들어야할지 고민하기
        }
    }
}
