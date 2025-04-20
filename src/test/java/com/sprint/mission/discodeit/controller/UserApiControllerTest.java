package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class UserApiControllerTest {//api가 잘작동하는지 테스트니까 통합테스트 service기능을 테스트하고싶으면 단위테스트로 뺴라.
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService; //공유의존성이니까 목,여기서 목 시키려고 인터페이스가 필요한건가?p.289
    @MockitoBean
    private UserStatusService userStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/users - 유저 생성 성공")
    void createUserSuccess() throws Exception {
        // given
        UserCreateRequestDto requestDto = new UserCreateRequestDto("userA", "userA@ex.com", "password");
        UserDto responseDto = new UserDto(UUID.randomUUID(), "userA", "userA@ex.com", null,null);

        when(userService.createUser(any(UserCreateRequestDto.class), nullable(BinaryContentCreateRequestDto.class))).thenReturn(responseDto);

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest", "userCreate.json",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("userA"))
                .andExpect(jsonPath("$.email").value("userA@ex.com"));
    }

    @Test
    @DisplayName("POST /api/users - 유저 생성 실패 (Validation Error)")
    void createUserFail() throws Exception {
        UserCreateRequestDto invalidRequest = new UserCreateRequestDto(null, "invalid-email", "short");

        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest", null,
                "application/json",
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        mockMvc.perform(multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users - 유저 전체 조회 성공")
    void findAllUsersSuccess() throws Exception {
        // given
        List<UserDto> users = List.of(
                new UserDto(UUID.randomUUID(), "userA", "userA@ex.com", null, null),
                new UserDto(UUID.randomUUID(), "userB", "userB@ex.com", null, null)
        );

        given(userService.findAllUsers()).willReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("userA"))
                .andExpect(jsonPath("$[1].username").value("userB"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId} - 유저 업데이트 성공")
    void updateUserSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto("newName", "newPass","newEmail@ex.com");

        UserDto updatedUser = new UserDto(userId, "newName", "newEmail@ex.com", null, null);

        given(userService.updateUser(eq(userId), any(UserUpdateRequestDto.class), any())).willReturn(updatedUser);

        MockMultipartFile userUpdateRequest = new MockMultipartFile(
                "userUpdateRequest", null,
                "application/json",
                objectMapper.writeValueAsBytes(updateRequestDto)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(userUpdateRequest)
                        .with(request -> {
                            request.setMethod("PATCH"); // PATCH 설정
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newName"))
                .andExpect(jsonPath("$.email").value("newEmail@ex.com"));
    }

    @Test
    @DisplayName("PATCH /api/users/{userId}/userStatus - 유저 상태 업데이트 성공")
    void updateUserStatusSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UUID userStatusId = UUID.randomUUID();
        Instant now= Instant.now();
        UserStatusUpdateDto statusUpdateDto = new UserStatusUpdateDto(now);

        UserStatusDto updatedStatus = new UserStatusDto(userStatusId,userId, now);

        given(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateDto.class))).willReturn(updatedStatus);

        // when & then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastActiveAt").value(now.toString()));
    }

    @Test
    @DisplayName("DELETE /api/users/{userId} - 유저 삭제 성공")
    void deleteUserSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        willDoNothing().given(userService).deleteUser(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());
    }
}
