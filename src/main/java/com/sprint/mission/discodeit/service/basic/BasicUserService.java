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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final BinaryContentRepository contentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository; //있는 이유? 고민하기

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        log.debug("사용자 생성 시작: {}", userCreateRequest);
        String userName = userCreateRequest.getUsername();
        String userEmail = userCreateRequest.getEmail();
        validateDuplicateUser(userName, userEmail);
        String password = passwordEncoder.encode(userCreateRequest.getPassword());

        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        User user = new User(userName, userEmail,password,binaryContent);
        User savedUser = userRepository.save(user);
        UserStatus userStatus = new UserStatus();
        userStatus.setUser(savedUser);
        userStatusRepository.save(userStatus);
        log.info("사용자 생성 완료: id={},username={}",user.getId(),userName);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto findUser(UUID id) {
        log.debug("사용자 조회 시작: id={}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        log.info("사용자 조회 완료: id={}", id);
        return userMapper.toDto(user);

    }

    @Override
    public UserDto findByUsername(String username) {
        log.debug("사용자 이름으로 조회 시작: username{}", username);
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        log.info("사용자 이름으로 조회 완료: id={},username={}",user.getId(),username);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        log.debug("모든 사용자 조회 시작");
        List<UserDto> list = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
        log.info("모든 사용자 조회 완료: 총 {}명",list.size());
        return list;
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        log.debug("사용자 수정 시작: id={},request={}",id,userParam);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        String newUserName = userParam.getNewUsername();
        String newUserEmail = userParam.getNewEmail();
        validateDuplicateUser(newUserName, newUserEmail);
        String newPassword = userParam.getNewPassword();
        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        user.updateUser(newUserName,newPassword,newUserEmail,binaryContent);
        log.info("사용자 수정 완료:id={}", id);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        log.debug("사용자 삭제 시작: id={}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());
        log.info("사용자 삭제 완료: id={}", id);
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
