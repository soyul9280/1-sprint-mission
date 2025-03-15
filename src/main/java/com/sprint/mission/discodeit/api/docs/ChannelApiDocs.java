package com.sprint.mission.discodeit.api.docs;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Channel API",description = "채널 관리 API")
public interface ChannelApiDocs {
    @Operation(summary = "Public Channel 생성", responses = {
            @ApiResponse(responseCode = "201",description = "Public Channel이 성공적으로 생성됨")
    })
    ResponseEntity<Channel> createPublicChannel(@RequestBody @Valid PublicChannelCreateRequestDto publicChannelParam);


    @Operation(summary = "Private Channel 생성", responses = {
            @ApiResponse(responseCode = "201",description = "Private Channel이 성공적으로 생성됨")
    })
    ResponseEntity<ChannelDto> createPrivateChannel(@RequestBody @Valid PrivateChannelCreateRequestDto privateChannelParam);


    @Operation(summary = "Channel 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Channel 정보가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "400",description = "Private Channel은 수정할 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404",description = "Channel을 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Channel> updateChannel(@PathVariable UUID channelId, @RequestBody ChannelUpdateRequestDto channelParam);


    @Operation(summary = "Channel 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Channel이 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404",description = "Channel을 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId);

    @Operation(summary = "User가 참여 중인 Channel 목록 조회",
            responses = {
                    @ApiResponse(responseCode = "200",description = "Channel 목록 조회 성공")
            })
    ResponseEntity<List<ChannelDto>> findAllByUserId(@RequestParam UUID userId);
}
