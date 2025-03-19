package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository contentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserStatusRepository userStatusRepository;

    @Override
    @Transactional
    public User createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        String loginId = userCreateRequest.getLoginId();
        String userEmail = userCreateRequest.getUserEmail();
        validateDuplicateUser(loginId, userEmail);
        String password = userCreateRequest.getPassword();
        String userName = userCreateRequest.getUserName();

        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        User user = new User(loginId,  userName, userEmail,password,binaryContent);
        UserStatus userStatus = new UserStatus();
        User savedUser = userRepository.save(user);
        userStatus.setUser(savedUser);
        return savedUser;
    }

    @Override
    public User findUser(UUID id) {
        return userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public User findByLoginId(String loginId) {
        return userRepository.findOptionalUserByLoginId(loginId).orElseThrow(()->new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        String newLoginId = userParam.getLoginId();
        String newUserEmail = userParam.getUserEmail();
        validateDuplicateUser(newLoginId, newUserEmail);
        String newPassword = userParam.getPassword();
        String newUserName = userParam.getUserName();
        BinaryContent binaryContent=(binaryContentCreateRequest !=null)?createBinaryContent(binaryContentCreateRequest):null;
        user.updateUser(newLoginId,newPassword,newUserName,newUserEmail,binaryContent);
        return user;
    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
        userRepository.deleteById(user.getId());
    }

    private void validateDuplicateUser(String loginId, String userEmail) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if (userRepository.existsByEmail(userEmail)) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
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

    private boolean isUserOnline(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저상태가 존재하지 않습니다."));
        return userStatus.isOnline();
    }
}
