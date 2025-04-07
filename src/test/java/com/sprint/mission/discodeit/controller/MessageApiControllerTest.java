package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MessageApiController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class MessageApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/messages - 메시지 생성 성공")
    void createMessageSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        MessageCreateRequestDto requestDto = new MessageCreateRequestDto("Hello message",userId,channelId,null);
        MessageDto responseDto = new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(), "Hello message", channelId,null,null);

        when(messageService.messageSave(any(MessageCreateRequestDto.class), anyList()))
                .thenReturn(responseDto);

        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest", "messageCreate.json",
                "application/json",
                objectMapper.writeValueAsBytes(requestDto)
        );

        // when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(messageCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello message"));
    }

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 메시지 수정 성공")
    void updateMessageSuccess() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageUpdateDto requestDto = new MessageUpdateDto("Updated message content");

        MessageDto responseDto = new MessageDto(messageId, Instant.now(),Instant.now(),"Updated message content",channelId,null,null);

        when(messageService.updateMessage(eq(messageId), any(MessageUpdateDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated message content"));
    }

    @Test
    @DisplayName("DELETE /api/messages/{messageId} - 메시지 삭제 성공")
    void deleteMessageSuccess() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        willDoNothing().given(messageService).deleteMessage(messageId);

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/messages?channelId={channelId} - 채널 ID로 메시지 목록 조회 성공")
    void findAllByChannelIdSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant now= Instant.now();
        List<MessageDto> messages = List.of(
                new MessageDto(UUID.randomUUID(), now,now,"Message 1", channelId, null, null),
                new MessageDto(UUID.randomUUID(),now.minusSeconds(10),now.minusSeconds(10),"Message 2", channelId, null, null)
        );
        PageResponse<MessageDto> pageResponse = new PageResponse<>(messages,0,messages.size(),(long) messages.size(),true);

        when(messageService.findAllByChannelId(channelId, 0)).thenReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString())
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("Message 1"))
                .andExpect(jsonPath("$.content[1].content").value("Message 2"));
    }

}