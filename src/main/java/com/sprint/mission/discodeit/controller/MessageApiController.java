package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.MessageApi;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessageApiController implements MessageApi {
    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Override
    public ResponseEntity<MessageDto> createMessage(@Valid @RequestPart("messageCreateRequest")MessageCreateRequestDto request, @RequestPart(value = "attachments", required = false)List<MultipartFile> attachments) {
        log.info("메시지 생성 요청: request={},attachments={}",request,attachments!=null?attachments:0);
        List<BinaryContentCreateRequestDto> fileRequests = new ArrayList<>();
        if(attachments != null) {
            for (MultipartFile attachment : attachments) {
                BinaryContentCreateRequestDto file = convertToBinaryContent(attachment);
                fileRequests.add(file);
            }
        }
        MessageDto result = messageService.messageSave(request, fileRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @PatchMapping("/{messageId}")
    @Override
    public ResponseEntity<MessageDto> updateMessage(@PathVariable("messageId") UUID messageId, @Valid @RequestBody MessageUpdateDto messageParam) {
        log.info("메시지 수정 요청: id={},request={}", messageId, messageParam);
        MessageDto result = messageService.updateMessage(messageId, messageParam);
        log.debug("메시지 수정 응답: {}", result);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @DeleteMapping("/{messageId}")
    @Override
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") UUID messageId) {
        log.info("메시지 삭제 요청: id={}", messageId);
        messageService.deleteMessage(messageId);
        log.debug("메시지 삭제 완료");
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    @Override
    public ResponseEntity<PageResponse<MessageDto>> findAllByChannelId(@RequestParam("channelId") UUID channelId,@RequestParam(value = "page",defaultValue = "0") int page) {
        log.info("채널별 메시지 목록 조회 요청: channelId={},page={}", channelId, page);
        PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, page);
        log.debug("채널별 메시지 목록 조회 응답: total={}", result.getTotalElements());
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
            throw new RuntimeException("파일 처리 오류",e);
        }
    }

}
