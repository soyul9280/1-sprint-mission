package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("사용자 생성 - 성공")
    @Transactional
    void testCreateUserSuccess() throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String json = objectMapper.writeValueAsString(new UserCreateRequestDto("andy", "andy@mail.com", "password"));

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(json, jsonHeaders);

        body.add("userCreateRequest", jsonPart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", requestEntity, UserDto.class);


        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("andy", response.getBody().getUsername());
        assertEquals("andy@mail.com", response.getBody().getEmail());
    }

    @Test
    @DisplayName("사용자 생성 - 실패 (이메일 형식 오류)")
    @Transactional
    void testCreateUserFailInvalidEmail() throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String json = objectMapper.writeValueAsString(new UserCreateRequestDto("andy", "invalid-email", "password"));

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(json, jsonHeaders);

        body.add("userCreateRequest", jsonPart);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/users", requestEntity, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getExceptionType().contains("MethodArgumentNotValidException"));    }

    @Test
    @DisplayName("사용자 수정 - 성공")
    @Transactional
    void testUpdateUserSuccess() throws Exception {
        UUID userId = createTestUser("user","user@mail.com");

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("updatedName", "newPassword", "updated@mail.com");
        MultiValueMap<String, Object> body = createMultipartBody(updateRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<UserDto> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.PATCH,
                requestEntity,
                UserDto.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updatedName", response.getBody().getUsername());
        assertEquals("updated@mail.com", response.getBody().getEmail());
    }

    @Test
    @Transactional
    @DisplayName("사용자 수정 - 실패 (없는 유저)")
    void testUpdateUserFail() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto("FailUser", "Password", "updatefail@email.com");
        MultiValueMap<String, Object> body = createMultipartBody(updateRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.PATCH,
                requestEntity,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getExceptionType().contains("UserNotFoundException"));
    }

    @Test
    @Transactional
    @DisplayName("사용자 삭제 - 성공")
    void testDeleteUserSuccess() {
        UUID userId = createTestUser("user","user@mail.com");

        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @Transactional
    @DisplayName("사용자 삭제 - 실패 (없는 유저)")
    void testDeleteUserFail() {
        UUID userId = UUID.randomUUID();

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Transactional
    @DisplayName("사용자 목록 조회 - 성공")
    void testFindUsersSuccess() {
        createTestUser("user1", "user1@mail.com");
        createTestUser("user2", "user2@mail.com");

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity("/api/users", UserDto[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2); // DB 초기상태 포함
    }

    //테스트 픽스처 -p.91 오브젝트 마더 패턴  팩토리 메서드 이용
    private UUID createTestUser(String username, String email) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            String json = objectMapper.writeValueAsString(new UserCreateRequestDto(username, email, "password"));

            HttpHeaders jsonHeaders = new HttpHeaders();
            jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> jsonPart = new HttpEntity<>(json, jsonHeaders);

            body.add("userCreateRequest", jsonPart);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", requestEntity, UserDto.class);
            return response.getBody().getId();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private MultiValueMap<String, Object> createMultipartBody(Object dto) throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        String json = new ObjectMapper().writeValueAsString(dto);

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(json, jsonHeaders);

        if (dto instanceof UserCreateRequestDto) {
            body.add("userCreateRequest", jsonPart);
        } else if (dto instanceof UserUpdateRequestDto) {
            body.add("userUpdateRequest", jsonPart);
        }
        return body;
    }
}
