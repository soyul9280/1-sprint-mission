package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository contentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository; //있는 이유? 고민하기

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        String userName = userCreateRequest.getUserName();
        String userEmail = userCreateRequest.getUserEmail();
        validateDuplicateUser(userName, userEmail);
        String password = userCreateRequest.getPassword();

        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        User user = new User(userName, userEmail,password,binaryContent);
        User savedUser = userRepository.save(user);
        UserStatus userStatus = new UserStatus();
        userStatus.setUser(savedUser);
        userStatusRepository.save(userStatus);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto findUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);

    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String newUserName = userParam.getUserName();
        String newUserEmail = userParam.getUserEmail();
        validateDuplicateUser(newUserName, newUserEmail);
        String newPassword = userParam.getPassword();
        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        user.updateUser(newUserName,newPassword,newUserEmail,binaryContent);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());
    }

    private void validateDuplicateUser(String username, String userEmail) {
        if (userRepository.existsByUsername(username)) {
            log.error("중복된 이름입니다. {}", username);
            throw new UserAlreadyExistException(username);
        }
        if (userRepository.existsByEmail(userEmail)) {
            log.error("중복된 이메일입니다. {}", userEmail);
            throw new UserAlreadyExistException(userEmail);
        }
    }

    private BinaryContent createBinaryContent(BinaryContentCreateRequestDto binaryContentCreateRequest) {
        String fileName = binaryContentCreateRequest.getFileName();
        Long size = binaryContentCreateRequest.getSize();
        String contentType = binaryContentCreateRequest.getContentType();
        byte[] bytes = binaryContentCreateRequest.getBytes();

        BinaryContent binaryContent = new BinaryContent(fileName, size, contentType);
        BinaryContent savedContent = contentRepository.save(binaryContent);
        binaryContentStorage.put(savedContent.getId(),bytes);
        return savedContent;
    }
}
