package com.sprint.mission.discodeit.api.docs;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.UUID;

@Tag(name = "BinaryContent", description = "첨부파일 API")
public interface BinaryContentApiDocs {

    @Operation(summary = "여러 첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 목록 조회 성공"),
    })
    ResponseEntity<List<BinaryContentDto>> getFiles(@RequestParam("binaryContentIds") List<UUID> ids);

    @Operation(summary = "첨부 파일 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "첨부 파일 조회 성공"),
            @ApiResponse(responseCode = "404", description = "첨부 파일을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))

    })
    ResponseEntity<BinaryContentDto> getFile(@PathVariable UUID binaryContentId);


    @Operation(summary = "파일 다운로드")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "파일 다운로드 성공")
            })
    ResponseEntity<?> download(@PathVariable UUID binaryContentId);

}
