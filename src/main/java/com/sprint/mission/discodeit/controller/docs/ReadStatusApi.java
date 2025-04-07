package com.sprint.mission.discodeit.controller.docs;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "ReadStatus", description = "ReadStatus API")
public interface ReadStatusApi {

    @Operation(summary = "User의 Message읽음 상태 목록 조회",responses = {
            @ApiResponse(responseCode = "200",description = "Message 읽음 상태 목록 조회 성공")
    })
    ResponseEntity<List<ReadStatusDto>> getUserReadStatus(@RequestParam UUID userId);

    @Operation(summary = "Message읽음 상태 생성",responses = {
            @ApiResponse(responseCode = "200",description = "Message 읽음 상태 목록 조회 성공")
    })
    ResponseEntity<ReadStatusDto> createUserReadStatus(@RequestBody ReadStatusRequestDto request);

    @Operation(summary = "Message읽음 상태 수정",responses = {
            @ApiResponse(responseCode = "200",description = "Message 읽음 상태가 성공적으로 수정됨"),
            @ApiResponse(responseCode = "404",description = "Message 읽음 상태를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<ReadStatusDto> updateUserReadStatus(@PathVariable UUID readStatusId, @RequestBody ReadStatusUpdateDto request);
}
