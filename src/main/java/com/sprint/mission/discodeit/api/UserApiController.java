package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.UserApiDocs;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController implements UserApiDocs {
    private final UserService userService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;
    private final UserStatusMapper userStatusMapper;

    @PostMapping(consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<UserDto> createUser(
            @RequestPart("userCreateRequest") @Valid UserCreateRequestDto userCreateRequest,
            @RequestPart(value="profile" ,required = false) MultipartFile profile ) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        User user = userService.createUser(userCreateRequest, file);
        UserDto dto = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDto>> findAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        List<UserDto> result= new ArrayList<>();
        for (User user : allUsers) {
            result.add(userMapper.toDto(user));
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping(value="/{userId}",consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<UserDto> update(@PathVariable UUID userId,
                                       @RequestPart("userUpdateRequest") @Valid UserUpdateRequestDto updateUserDto,
                                       @RequestPart(value="profile",required = false) MultipartFile profile) {
        BinaryContentCreateRequestDto file = convertToBinaryContent(profile);
        User user = userService.updateUser(userId, updateUserDto, file);
        UserDto dto = userMapper.toDto(user);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @DeleteMapping("/{userId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/userStatus")
    @Override
    public ResponseEntity<UserStatusDto> updateUserStatus(@PathVariable UUID userId, @RequestBody @Valid UserStatusUpdateDto request) {
        UserStatus userStatus = userStatusService.updateByUserId(userId, request);
        UserStatusDto dto = userStatusMapper.toDto(userStatus);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
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
