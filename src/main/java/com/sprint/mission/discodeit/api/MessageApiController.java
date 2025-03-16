package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.MessageApiDocs;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController implements MessageApiDocs {
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;


    @PostMapping(consumes = {"multipart/form-data"})
    @Override
    public ResponseEntity<MessageDto> createMessage(@RequestPart("messageCreateRequest") @Valid MessageCreateRequestDto request, @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments) {
        List<BinaryContentCreateRequestDto> fileRequests = new ArrayList<>();
        for (MultipartFile attachment : attachments) {
            BinaryContentCreateRequestDto file = convertToBinaryContent(attachment);
            fileRequests.add(file);
        }
        Message message = messageService.messageSave(request, fileRequests);
        MessageDto dto = messageMapper.toDto(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @PatchMapping("/{messageId}")
    @Override
    public ResponseEntity<MessageDto> updateMessage(@PathVariable("messageId") UUID messageId, @RequestBody MessageUpdateDto messageParam) {
        Message message = messageService.updateMessage(messageId, messageParam);
        MessageDto dto = messageMapper.toDto(message);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }


    @DeleteMapping("/{messageId}")
    @Override
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Override
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam("channelId") UUID channelId,@RequestParam(value = "page",defaultValue = "0") int page) {
        Slice<Message> messagePage = messageService.findAllByChannelId(channelId, page);
        Slice<MessageDto> dtoPage = messagePage.map(messageMapper::toDto);
        List<Message> contents = messagePage.getContent();
        List<MessageDto> messageDtos = new ArrayList<>();
        for (Message content : contents) {
            MessageDto dto = messageMapper.toDto(content);
            messageDtos.add(dto);
        }
        PageResponse<MessageDto> result = pageResponseMapper.fromSlice(dtoPage,messageDtos);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    private BinaryContentCreateRequestDto convertToBinaryContent(MultipartFile attachment) {
        if (attachment.isEmpty() || attachment == null) {
            return null;
        }
        try{
            return new BinaryContentCreateRequestDto(
                    attachment.getName(),
                    attachment.getSize(),
                    attachment.getContentType(),
                    attachment.getBytes()
            );
        }catch (IOException e){
            throw new RuntimeException("파일 오류",e);
        }
    }

}
