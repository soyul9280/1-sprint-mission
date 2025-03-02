package com.sprint.mission.discodeit.api.message;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Message API",description = "메세지 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController {
    private final MessageService messageService;

    @Operation(summary = "메세지 생성")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Message> createMessage(@RequestPart("messageCreateRequest") @Valid MessageCreateRequestDto request, @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments) {
        List<BinaryContentCreateRequestDto> fileRequests = new ArrayList<>();
        for (MultipartFile file : attachments) {
            try {
                BinaryContentCreateRequestDto binaryContent = new BinaryContentCreateRequestDto(file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getBytes());
                fileRequests.add(binaryContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Message message = messageService.messageSave(request, fileRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @Operation(summary = "메세지 수정")
    @PatchMapping("/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable UUID messageId, @RequestBody MessageUpdateDto messageParam) {
        Message message = messageService.updateMessage(messageId, messageParam);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Operation(summary = "메세지 삭제")
    @DeleteMapping("/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "채널의 모든 메세지 조회")
    @GetMapping
    public ResponseEntity<List<Message>> findAllByChannelId(@RequestParam("channelId") UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

}
