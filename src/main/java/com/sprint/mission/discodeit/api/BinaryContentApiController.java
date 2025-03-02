package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentApiController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/{id}")
    public ResponseEntity<BinaryContent> getFile(@PathVariable UUID id) {
        return ResponseEntity.ok(binaryContentService.find(id));
    }
}
