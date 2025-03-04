package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
   User createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest);
   UserResponseDto findUser(UUID id);
   UserResponseDto findByLoginId(String loginId);
   List<UserResponseDto> findAllUsers();
   User updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest);
   void deleteUser(UUID id);
}
