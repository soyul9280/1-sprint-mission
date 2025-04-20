package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ChannelApiController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ChannelApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/channels/public - 퍼블릭 채널 생성 성공")
    void createPublicChannelSuccess() throws Exception {
        // given
        PublicChannelCreateRequestDto requestDto = new PublicChannelCreateRequestDto("Public Channel","description");
        ChannelDto responseDto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "Public Channel","description",null, Instant.now());

        when(channelService.createPublicChannel(any(PublicChannelCreateRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Public Channel"))
                .andExpect(jsonPath("$.type").value("PUBLIC"));
    }

    @Test
    @DisplayName("POST /api/channels/private - 프라이빗 채널 생성 성공")
    void createPrivateChannelSuccess() throws Exception {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateRequestDto requestDto = new PrivateChannelCreateRequestDto(List.of(userId1, userId2));
        ChannelDto responseDto = new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE,null,null,null,Instant.now());

        when(channelService.createPrivateChannel(any(PrivateChannelCreateRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("PRIVATE"));
    }

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - 채널 업데이트 성공")
    void updateChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        ChannelUpdateRequestDto requestDto = new ChannelUpdateRequestDto("UpdatedChannel","Update");
        ChannelDto responseDto = new ChannelDto(channelId,ChannelType.PUBLIC,"UpdatedChannel", "Update",null, Instant.now());

        when(channelService.updateChannel(eq(channelId), any(ChannelUpdateRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedChannel"));
    }

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 채널 삭제 성공")
    void deleteChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        willDoNothing().given(channelService).deleteChannel(channelId);

        // when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/channels?userId= - 유저 ID로 채널 목록 조회 성공")
    void findAllByUserIdSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        List<ChannelDto> channels = List.of(
                new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "PUBLIC","description",null, Instant.now()),
                new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "PRIVATE","description",null, Instant.now())
        );

        when(channelService.findAllByUserId(userId)).thenReturn(channels);

        // when & then
        mockMvc.perform(get("/api/channels")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("PUBLIC"))
                .andExpect(jsonPath("$[1].name").value("PRIVATE"));
    }
}