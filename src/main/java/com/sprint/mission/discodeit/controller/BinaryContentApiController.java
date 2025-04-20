package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.BinaryContentApi;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentApiController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @GetMapping
    @Override
    public ResponseEntity<List<BinaryContentDto>> findAllByIds(@RequestParam("binaryContentIds") List<UUID> ids) {
        log.info("바이너리 컨텐츠 목록 조회 요청: id={}", ids);
        List<BinaryContentDto> result = binaryContentService.findAllByIdIn(ids);
        log.debug("바이너리 컨텐츠 목록 조회 응답: count={}", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("{binaryContentId}")
    @Override
    public ResponseEntity<BinaryContentDto> find(@PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("바이너리 컨텐츠 조회 요청: id={}", binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        log.debug("바이너리 컨텐츠 조회 응담: {}", binaryContentDto);
        return ResponseEntity.ok(binaryContentDto);
    }

    @GetMapping("/{binaryContentId}/download")
    @Override
    public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
        log.info("바이너리 컨텐츠 다운로드 요청: id={}", binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentService.find(binaryContentId);
        ResponseEntity<?> response = binaryContentStorage.download(binaryContentDto);
        log.debug("바이너리 컨텐츠 다운로드 응답: contentType={}, contentLength={}", response.getHeaders().getContentType(), response.getHeaders().getContentLength());
        return response;
    }
}
