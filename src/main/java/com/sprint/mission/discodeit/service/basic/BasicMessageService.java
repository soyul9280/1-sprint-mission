package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
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
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private final MessageMapper messageMapper;
    private final PageResponseMapper pageResponseMapper;

    @Override
    @Transactional
    public MessageDto messageSave(MessageCreateRequestDto request, List<BinaryContentCreateRequestDto> binaryContents) {
        Channel channel = channelRepository.findById(request.getChannelId()).orElseThrow(() -> new ChannelNotFoundException(request.getChannelId()));
        User user = userRepository.findById(request.getSenderId()).orElseThrow(() -> new UserNotFoundException(request.getSenderId()));
        String content=request.getContent();
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
        Message message = new Message(content, channel, user, attachments);
        messageRepository.save(message);
        return messageMapper.toDto(message);
    }


    @Override
    public MessageDto findMessageById(UUID id) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
        return messageMapper.toDto(message);
    }

    @Override
    public PageResponse<MessageDto> findAllByChannelId(UUID channelId, int page) {
        Pageable pageable = PageRequest.of(page, 50, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<Message> slice = messageRepository.findAllByChannelId(channelId, pageable);
        List<MessageDto> list = slice.getContent().stream()
                .map(messageMapper::toDto)
                .toList();
        SliceImpl<MessageDto> sliceDto = new SliceImpl<>(list, pageable, slice.hasNext());
        return pageResponseMapper.fromSlice(sliceDto);
    }

    @Override
    @Transactional
    public MessageDto updateMessage(UUID id, MessageUpdateDto messageParam) {
        Message message = messageRepository.findById(id).orElseThrow(() -> new MessageNotFoundException(id));
        message.updateMessage(messageParam.getNewContent());
        return messageMapper.toDto(message);
    }


    @Override
    @Transactional
    public void deleteMessage(UUID id) {
        messageRepository.findById(id).orElseThrow(()-> new MessageNotFoundException(id));
        messageRepository.deleteById(id);
    }
}
