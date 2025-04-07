package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BinaryContentRepository contentRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserStatusRepository userStatusRepository;


    @InjectMocks
    private BasicUserService userService;

    @Nested
    @DisplayName("유저 생성")
    class createUserTest {
        @Test
        @DisplayName("사용자 생성 성공 테스트")
        void SuccessCreateUser() {
          //given
            UserCreateRequestDto dto = new UserCreateRequestDto("userA", "userA@example.com", "1234");
              BinaryContentCreateRequestDto binaryDto = new BinaryContentCreateRequestDto(
                      "profile.png",
                      1024L,
                      "image/png",
                      new byte[]{1, 2, 3}
              );
            BinaryContent savedBinaryContent = new BinaryContent("profile.png", 1024L, "image/png");
            User user = new User("userA", "userA@example.com", "1234", savedBinaryContent);
            given(contentRepository.save(any(BinaryContent.class))).willReturn(savedBinaryContent);

            given(userRepository.existsByUsername("userA")).willReturn(false);
            given(userRepository.existsByEmail("userA@example.com")).willReturn(false);

            when(userRepository.save(any(User.class))).thenReturn(user);
            UserStatus userStatus = new UserStatus(user, Instant.now());
            when(userStatusRepository.save(any(UserStatus.class))).thenReturn(userStatus);
            given(userMapper.toDto(any(User.class))).willAnswer(invocation -> {
                User updatedUser = invocation.getArgument(0);
                return new UserDto(
                        updatedUser.getId(),
                        updatedUser.getUsername(),
                        updatedUser.getEmail(),
                        null,
                        true
                );
            });

            //when
            UserDto createdUser = userService.createUser(dto, binaryDto);

            //then
            assertThat(createdUser.getUsername()).isEqualTo("userA");
            assertThat(createdUser.getEmail()).isEqualTo("userA@example.com");
            verify(userRepository).save(any(User.class));
            verify(userStatusRepository).save(any(UserStatus.class));
        }

        @Test
        @DisplayName("중복 유저로 생성 실패")
        void FailCreateUser() {
            //given
            UserCreateRequestDto dto = new UserCreateRequestDto("userA", "userA@example.com", "1234");
            given(userRepository.existsByUsername("userA")).willReturn(true);
            //when
            //then
            assertThatThrownBy(() -> userService.createUser(dto, null)).isInstanceOf(UserAlreadyExistException.class);
        }
    }

    @Nested
    @DisplayName("유저 수정")
    class updateUserTest {

        @Test
        @DisplayName("유저 수정 성공")
        void SuccessUpdateUser()  {
            //given
            UUID id = UUID.randomUUID();
            User user = new User("user", "user@exa.com", "123", null);
            UserUpdateRequestDto dto = new UserUpdateRequestDto("newUser", "1234","newUser@example.com");
            given(userRepository.findById(id)).willReturn(Optional.of(user));
            given(userRepository.existsByUsername("newUser")).willReturn(false);
            given(userRepository.existsByEmail("newUser@example.com")).willReturn(false);

            given(userMapper.toDto(any(User.class))).willAnswer(invocation->{
                User updatedUser= invocation.getArgument(0);
                return new UserDto(
                        updatedUser.getId(),
                        updatedUser.getUsername(),
                        updatedUser.getEmail(),
                        null,
                        true
                );
            });
            //when
            UserDto result = userService.updateUser(id, dto, null);
            //then
            assertThat(result.getUsername()).isEqualTo(dto.getUserName());
            assertThat(result.getEmail()).isEqualTo(dto.getUserEmail());
        }

        @Test
        @DisplayName("없는 유저 수정 실패")
        void FailUpdateUser() {
            //given
            UUID id = UUID.randomUUID();
            UserUpdateRequestDto dto = new UserUpdateRequestDto("newUser", "newUser@example.com", "1234");
            //when
            given(userRepository.findById(id)).willReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> userService.updateUser(id, dto, null)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("유저 삭제")
    class deleteUserTest {
        @Test
        @DisplayName("유저 삭제 성공")
        void deleteUserSuccess() {
            //given
            UUID id = UUID.randomUUID();
            User user = new User("user", "user@example.com", "123", null);
            ReflectionTestUtils.setField(user, "id", id);
            given(userRepository.findById(id)).willReturn(Optional.of(user));
            //when
            userService.deleteUser(id);
            //then
            verify(userRepository).deleteById(id);
        }

        @Test
        @DisplayName("없는 유저 삭제 실패")
       void deleteUserTestFail(){
            //given
            UUID id = UUID.randomUUID();
            given(userRepository.findById(id)).willReturn(Optional.empty());
            //when
            //then
            assertThatThrownBy(() -> userService.deleteUser(id)).isInstanceOf(UserNotFoundException.class);
        }
    }
}