package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.ReadStatusApiDocs;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusApiController implements ReadStatusApiDocs {
    private final ReadStatusService readStatusService;
    private final ReadStatusMapper readStatusMapper;


    @GetMapping
    @Override
    public ResponseEntity<List<ReadStatusDto>> getUserReadStatus(@RequestParam("userId") UUID userId) {
        List<ReadStatus> findReadStatuses = readStatusService.findAllByUserId(userId);
        List<ReadStatusDto> result = new ArrayList<>();
        for (ReadStatus findReadStatus : findReadStatuses) {
            ReadStatusDto readStatusDto = readStatusMapper.toDto(findReadStatus);
            result.add(readStatusDto);
        }
        return ResponseEntity.ok(result);
    }


    @PostMapping
    @Override
    public ResponseEntity<ReadStatusDto> createUserReadStatus(@RequestBody ReadStatusRequestDto request) {
        ReadStatus readStatus = readStatusService.create(request);
        ReadStatusDto readStatusDto = readStatusMapper.toDto(readStatus);
        return ResponseEntity.ok(readStatusDto);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateUserReadStatus(@PathVariable("readStatusId") UUID readStatusId, @RequestBody ReadStatusUpdateDto request) {
        ReadStatus result = readStatusService.update(readStatusId, request);
        ReadStatusDto readStatusDto = readStatusMapper.toDto(result);
        return ResponseEntity.ok(readStatusDto);
    }
}
