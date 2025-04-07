package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message",description = "Message API")
public interface MessageApi {

    @Operation(summary = "Message 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201",description = "Message가 성공적으로 생성됨"),
            @ApiResponse(responseCode = "404",description = "Channel 또는 User를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<MessageDto> createMessage(@RequestPart("messageCreateRequest") @Valid MessageCreateRequestDto request, @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments);


    @Operation(summary = "Message 내용 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Message가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404",description = "Message를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<MessageDto> updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateDto messageParam);

    @Operation(summary = "Message 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204",description = "Message가 성공적으로 삭제됨"),
            @ApiResponse(responseCode = "404",description = "Message를 찾을 수 없음",
                            content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId);


    @Operation(summary = "Channel의 Message 목록 조회", responses = {
            @ApiResponse(responseCode = "200",description = "Message 목록 조회 성공")
    })
    ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam("channelId") UUID channelId, @RequestParam(value = "page",defaultValue = "0") int page);
}
