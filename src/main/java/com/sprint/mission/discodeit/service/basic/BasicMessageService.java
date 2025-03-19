package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository contentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public Message messageSave(MessageCreateRequestDto request, List<BinaryContentCreateRequestDto> binaryContents) {
        Channel channel = channelRepository.findById(request.getChannelId()).orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
        User user = userRepository.findById(request.getSenderId()).orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        String content=request.getContent();
        if (request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }
        List<BinaryContent> attachments = new ArrayList<>();
        for (BinaryContentCreateRequestDto binaryContentrequest : binaryContents) {
            if (binaryContentrequest != null) {
                String fileName = binaryContentrequest.getFileName();
                Long size = binaryContentrequest.getSize();
                String contentType = binaryContentrequest.getContentType();
                byte[] bytes = binaryContentrequest.getBytes();

                BinaryContent binaryContent = new BinaryContent(fileName, size, contentType);
                BinaryContent savedFile = contentRepository.save(binaryContent);
                binaryContentStorage.put(savedFile.getId(),bytes);
                attachments.add(savedFile);
            }
        }
        return  messageRepository.save(new Message(content, channel, user,attachments));
    }

    @Override
    public Optional<Message> findMessage(UUID id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Slice<Message> findAllByChannelId(UUID channelId, int page) {
        Pageable pageable = PageRequest.of(page, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.findAllByChannelId(channelId, pageable);
    }

    @Override
    @Transactional
    public Message updateMessage(UUID id, MessageUpdateDto messageParam) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
        message.updateMessage(messageParam.getNewContent());
        return message;
    }


    @Override
    @Transactional
    public void deleteMessage(UUID id) {
        Message message = messageRepository.findById(id).orElseThrow(()-> new NoSuchElementException("해당 메시지가 존재하지 않습니다."));
        List<BinaryContent> messageFiles = message.getAttachments();
        for (BinaryContent messageFile : messageFiles) {
            contentRepository.deleteById(messageFile.getId());
        }
        messageRepository.deleteById(id);
    }
}
