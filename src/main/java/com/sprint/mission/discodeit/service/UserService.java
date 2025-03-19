package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
   User createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest);
   User findUser(UUID id);
   User findByLoginId(String loginId);
   List<User> findAllUsers();
   User updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest);
   void deleteUser(UUID id);
}
