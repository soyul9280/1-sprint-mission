package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChannelTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("공개 채널 생성 -성공")
    void testCreatePublicChannelSuccess() throws Exception {
        PublicChannelCreateRequestDto dto = new PublicChannelCreateRequestDto("public", "public channel");
        String json = objectMapper.writeValueAsString(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        ResponseEntity<ChannelDto> response = restTemplate.postForEntity("/api/channels/public", requestEntity, ChannelDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals("public", response.getBody().getName());
        assertEquals(ChannelType.PUBLIC, response.getBody().getType());
    }

    @Test
    @DisplayName("비공개 채널 생성 -성공")
    void testCreatePrivateChannelSuccess() throws Exception {
        UUID userId = createTestUser("user1", "user1@mail.com");
        PrivateChannelCreateRequestDto dto = new PrivateChannelCreateRequestDto(List.of(userId));
        String json = objectMapper.writeValueAsString(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<ChannelDto> response = restTemplate.postForEntity("/api/channels/private", requestEntity, ChannelDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getId());
        assertEquals(ChannelType.PRIVATE, response.getBody().getType());
    }

    @Test
    @DisplayName("채널 수정-성공")
    void testUpdateChannelSuccess() throws Exception {
        UUID channelId = createPublicChannel("channel", "before");
        ChannelUpdateRequestDto dto = new ChannelUpdateRequestDto("updateChannel", "after");
        String json = objectMapper.writeValueAsString(dto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<ChannelDto> response = restTemplate.exchange("/api/channels/" + channelId, HttpMethod.PATCH, requestEntity, ChannelDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updateChannel", response.getBody().getName());
    }

    @Test
    @DisplayName("비공개 채널 수정-실패")
    void testUpdateChannelFail() throws Exception {
        UUID usrId = createTestUser("user1", "user1@mail.com");
        UUID channelId = createPrivateChannel(List.of(usrId));
        ChannelUpdateRequestDto dto = new ChannelUpdateRequestDto("new", "newDescription");
        String json = objectMapper.writeValueAsString(dto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<ErrorResponse> response=restTemplate.exchange(
                "/api/channels/" + channelId,
                HttpMethod.PATCH,
                requestEntity,
                ErrorResponse.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().getExceptionType().contains("ChannelUpdateForbiddenException"));
    }

    @Test
    @DisplayName("채널 삭제 -성공")
    void testDeleteChannelSuccess() throws Exception {
        UUID channelId = createPublicChannel("channel", "before");
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/channels/" + channelId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    }

    @Test
    @DisplayName("채널 삭제 - 실패 (없는 채널)")
    @Transactional
    void testDeleteChannelFail() {
        UUID channelId = UUID.randomUUID();

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/api/channels/" + channelId,
                HttpMethod.DELETE,
                null,
                ErrorResponse.class
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().getExceptionType().contains("ChannelNotFoundException"));
    }

    private UUID createPrivateChannel(List<UUID> usrIds) {
        PrivateChannelCreateRequestDto request = new PrivateChannelCreateRequestDto(usrIds);
        ResponseEntity<ChannelDto> response = restTemplate.postForEntity(
                "/api/channels/private",
                request,
                ChannelDto.class
        );
        return response.getBody().getId();
    }

    private UUID createPublicChannel(String name, String description) {
        PublicChannelCreateRequestDto requestDto = new PublicChannelCreateRequestDto(name, description);
        ResponseEntity<ChannelDto> response = restTemplate.postForEntity("/api/channels/public", requestDto, ChannelDto.class);
        return response.getBody().getId();
    }

    //repository말고 restTemplate어떻게 쓸지 고민하기
    private UUID createTestUser(String username, String email) {
        User user = new User(username, email, "password", null);
        return userRepository.save(user).getId();
    }
}
