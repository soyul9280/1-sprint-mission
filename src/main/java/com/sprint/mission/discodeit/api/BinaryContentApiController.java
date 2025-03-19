package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.BinaryContentApiDocs;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentApiController implements BinaryContentApiDocs {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @GetMapping
    @Override
    public ResponseEntity<List<BinaryContentDto>> getFiles(@RequestParam("binaryContentIds") List<UUID> ids) {
        List<BinaryContent> all = binaryContentService.findAllByIdIn(ids);
        List<BinaryContentDto> result = new ArrayList<>();
        for (BinaryContent binaryContent : all) {
            BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("{binaryContentId}")
    @Override
    public ResponseEntity<BinaryContentDto> getFile(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{binaryContentId}/download")
    @Override
    public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
        return binaryContentStorage.download(dto);
    }
}
