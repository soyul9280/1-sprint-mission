package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository contentRepository;

    @Override
    public Message messageSave(MessageCreateRequestDto request, List<BinaryContentCreateRequestDto> binaryContents) {
        UUID channelId=request.getChannelId();
        UUID senderId=request.getSenderId();
        String content=request.getContent();
        if (request.getContent().trim().isEmpty()) {
            log.info("메세지 내용을 입력해주세요.");
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }
        List<UUID> messageIds = new ArrayList<>();
        for (BinaryContentCreateRequestDto binaryContentrequest : binaryContents) {
            if (binaryContentrequest != null) {
                String fileName = binaryContentrequest.getFileName();
                Long size = binaryContentrequest.getSize();
                String contentType = binaryContentrequest.getContentType();
                byte[] bytes = binaryContentrequest.getBytes();
                BinaryContent binaryContent = new BinaryContent(fileName, size, contentType, bytes);
                BinaryContent savedFile = contentRepository.save(binaryContent);
                messageIds.add(savedFile.getId());
            }
        }
        return  messageRepository.createMessage(new Message(content, senderId, channelId,messageIds));
    }

    @Override
    public Message messageSaveWithContents(UUID senderId,Message message, List<BinaryContent> files) {
        if (message.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("메세지 내용을 입력해주세요.");
        }
        User user = userRepository.findById(senderId).orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Channel channel = channelRepository.findById(message.getChannelId()).orElseThrow(()->new IllegalArgumentException("채널을 찾을 수 없습니다."));

        Participant findParticipant=null;
        for (Participant participant : channel.getParticipants()) {
            if (participant.getUser().getId().equals(senderId)) {
                findParticipant = participant;
                break;
            }
        }
        if(findParticipant == null) {
            throw new IllegalStateException("채널에 가입하지 않았습니다.");
        }
        List<UUID> fileIds=new ArrayList<>();
        for (BinaryContent file : files) {
            BinaryContent savedFile = binaryContentRepository.save(file);
            fileIds.add(savedFile.getId());
        }
        Message fileMessage = new Message(message.getId(),message.getContent(), senderId, message.getChannelId(), fileIds);
        findParticipant.setMessage(fileMessage);
        return messageRepository.createMessage(fileMessage);
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
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId);
    }

    @Override
    public Message updateMessage(UUID id, MessageUpdateDto messageParam) {
        validateMessageExits(id);
        String message = messageParam.getMessage();
        return messageRepository.updateMessage(id, message);
    }


    @Override
    public void deleteMessage(UUID id) {
        Message message = messageRepository.findById(id).orElseThrow(()-> new NoSuchElementException("message가 존재하지 않습니다."));
        List<UUID> messageFiles = message.getMessageFiles();
        for (UUID messageFile : messageFiles) {
            contentRepository.deleteById(messageFile);
        }
        messageRepository.deleteMessage(id);
    }
    private void validateMessageExits(UUID uuid) {
        if (messageRepository.findById(uuid).isEmpty()) {
            throw new RuntimeException("해당 message가 존재하지 않습니다.");
        }
    }
}
