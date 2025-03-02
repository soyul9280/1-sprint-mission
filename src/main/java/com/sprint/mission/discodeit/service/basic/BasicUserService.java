package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository contentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User createUser(UserCreateRequestDto userCreateRequest, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        String loginId = userCreateRequest.getLoginId();
        String userEmail = userCreateRequest.getUserEmail();
        if (userRepository.existLoginId(loginId)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if (userRepository.existUserEmail(userEmail)) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
        }
        String password = userCreateRequest.getPassword();
        String userName = userCreateRequest.getUserName();
        if (binaryContentCreateRequest != null) {
            String fileName = binaryContentCreateRequest.getFileName();
            Long size = binaryContentCreateRequest.getSize();
            String contentType = binaryContentCreateRequest.getContentType();
            byte[] bytes = binaryContentCreateRequest.getBytes();
            BinaryContent binaryContent = new BinaryContent(fileName, size, contentType, bytes);
            contentRepository.save(binaryContent);
            User user = new User(loginId, password, userName, userEmail, binaryContent.getId());
            User savedUser = userRepository.createUser(user);
            UserStatus userStatus = new UserStatus(savedUser.getId());
            userStatusRepository.save(userStatus);
            return savedUser;
        }
        else {
            User user = new User(loginId, password, userName, userEmail,null);
            User savedUser = userRepository.createUser(user);
            UserStatus userStatus = new UserStatus(savedUser.getId());
            userStatusRepository.save(userStatus);
            return savedUser;
        }
    }

    @Override
    public UserResponseDto findUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        return new UserResponseDto(user, isUserOnline(user.getId()));
    }

    @Override
    public UserResponseDto findByLoginId(String loginId) {
        User user = userRepository.findByloginId(loginId).orElseThrow(()->new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        return new UserResponseDto(user, isUserOnline(user.getId()));
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> all = userRepository.findAll();
        List<UserResponseDto> userList=new ArrayList<>();
        for (User user : all) {
            userList.add(new UserResponseDto(user, isUserOnline(user.getId())));
        }
       return userList;
    }

    @Override
    public User updateUser(UUID id, UserUpdateRequestDto userParam, BinaryContentCreateRequestDto binaryContentCreateRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        String newLoginId = userParam.getLoginId();
        String newUserEmail = userParam.getUserEmail();
        if(userRepository.existLoginId(newLoginId)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }
        if(userRepository.existUserEmail(newUserEmail)) {
            throw new IllegalArgumentException("중복된 이메일 입니다.");
        }
        String newPassword = userParam.getPassword();
        String newUserName = userParam.getUserName();
        if (binaryContentCreateRequest != null) {
            String fileName = binaryContentCreateRequest.getFileName();
            Long size = binaryContentCreateRequest.getSize();
            String contentType = binaryContentCreateRequest.getContentType();
            byte[] bytes = binaryContentCreateRequest.getBytes();
            BinaryContent binaryContent = new BinaryContent(fileName, size, contentType, bytes);
            contentRepository.save(binaryContent);
            user.updateUser(newLoginId,newPassword,newUserName,newUserEmail,binaryContent.getId());
            return userRepository.createUser(user);
        }
        else {
            user.updateUser(newLoginId,newPassword,newUserName,newUserEmail,null);
            return userRepository.createUser(user);
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다."));
        BinaryContent binaryContent = contentRepository.findById(user.getProfileId()).orElse(null);
        userStatusRepository.deleteByUserId(userId);
        if(binaryContent != null) {
            contentRepository.deleteById(binaryContent.getId());
        }
        userRepository.deleteUser(userId);
    }

    private boolean isUserOnline(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저상태가 존재하지 않습니다."));
        return userStatus.isOnline();
    }
}
