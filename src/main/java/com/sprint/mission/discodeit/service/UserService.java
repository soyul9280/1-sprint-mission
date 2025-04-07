package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
   UserDto createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest);
   UserDto findUser(UUID id);
   UserDto findByUsername(String username);
   List<UserDto> findAllUsers();
   UserDto updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest);
   void deleteUser(UUID id);
}
