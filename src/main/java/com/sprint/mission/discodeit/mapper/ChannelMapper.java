package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Participant;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelMapper {
    private final MessageRepository messageRepository;
    private final UserMapper userMapper;

    public ChannelDto toDto(Channel channel) {
        if (channel == null) {
            return null;
        }
        PageRequest request=PageRequest.of(0,50,Sort.by(Sort.Direction.DESC,"createdAt"));
        Slice<Message> messages = messageRepository.findAllByChannelId(channel.getId(),request);
        List<Message> contents = messages.getContent();
        Instant lastMessageAt = null;
        if (!contents.isEmpty()) {
            Message lastMessage = contents.get(0);
            for (Message content : contents) {
                if (content.getCreatedAt().isAfter(lastMessage.getCreatedAt())) {
                    lastMessage = content;
                }
            }
            lastMessageAt = lastMessage.getCreatedAt();
        }
        List<UserDto> users=new ArrayList<>();
        if(!channel.isPublic(channel)) {
            List<Participant> participants = channel.getParticipants();
            for (Participant participant : participants) {
                User user = participant.getUser();
                users.add(userMapper.toDto(user));
            }
        }
        String channelName = channel.getName();
        String description = channel.getDescription();
        return new ChannelDto(channel.getId(),channel.getType(),channelName,description,users,lastMessageAt);
    }
}
