package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Participant;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public Channel createPublicChannel(PublicChannelCreateRequestDto channelParam) {
        if (channelParam.getChannelName().trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름을 입력해주세요.");
        }
        if (channelParam.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("채널 설명을 입력해주세요.");
        }
        Channel channel = new Channel(channelParam.getChannelName(), channelParam.getDescription(), channelParam.getGroup());
        channelRepository.createChannel(channel);
        return channel;
    }


    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequestDto channelParam) {
        Channel channel = new Channel(null,null,channelParam.getGroup());
        List<UUID> userIds = channelParam.getUserIds();
        for (UUID userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            Participant participant = new Participant(user);
            channel.addParticipant(participant);
            ReadStatus readStatus = new ReadStatus(userId, channel.getId());
            readStatusRepository.createReadStatus(readStatus);
        }
        channelRepository.createChannel(channel);
        return channel;
    }


    @Override
    public List<ChannelResponseDto> findAllChannels() {
        List<Channel> all = channelRepository.findAll();
        List<ChannelResponseDto> channelList = new ArrayList<>();
        for (Channel channel : all) {
            List<Message> messages=messageRepository.findAllByChannelId(channel.getId());
            Instant lastMessageAt=null;
            if(!messages.isEmpty()) {
                Message lastMessage = messages.get(0);
                for (Message message : messages) {
                    if (message.getCreatedAt().isAfter(lastMessage.getCreatedAt())) {
                        lastMessage=message;
                    }
                }
                lastMessageAt=lastMessage.getCreatedAt();
            }

            List<UUID> userIds=new ArrayList<>();
            if(!channel.isPublic(channel)) {
                List<ReadStatus> allByChannelId = readStatusRepository.findAllByChannelId(channel.getId());
                for (ReadStatus readStatus : allByChannelId) {
                    userIds.add(readStatus.getUserId());
                }
            }
            String channelName = channel.getChannelName();
            String description = channel.getDescription();
            channelList.add(new ChannelResponseDto(channelName,description,userIds,lastMessageAt));
        }
        return channelList;
    }

    @Override
    public List<ChannelResponseDto> findAllByUserId(UUID userId) {
        List<ChannelResponseDto> allChannels = findAllChannels();
        List<ChannelResponseDto> channelList = new ArrayList<>();
        for (ChannelResponseDto channel : allChannels) {
            List<UUID> participantIds = channel.getParticipantIds();
            for (UUID participantId : participantIds) {
                if(userId.equals(participantId)) {
                    channelList.add(channel);
                }
            }
        }
        return channelList;
    }

    @Override
    public Channel updateChannel(UUID id, ChannelUpdateRequestDto channelParam) {
        validateChannelExits(id);
        return channelRepository.updateChannel(id, channelParam);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        validateChannelExits(channelId);
        channelRepository.findById(channelId);
        messageRepository.deleteMessage(channelId);
        channelRepository.deleteChannel(channelId);
    }
    private void validateChannelExits(UUID uuid) {
        if (channelRepository.findById(uuid).isEmpty()) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
    }
}
