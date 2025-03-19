package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Participant;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    @Transactional
    public Channel createPublicChannel(PublicChannelCreateRequestDto channelParam) {
        Channel channel = new Channel(channelParam.getName(), channelParam.getDescription(), ChannelType.PUBLIC);
        return channelRepository.save(channel);
    }


    @Override
    @Transactional
    public Channel createPrivateChannel(PrivateChannelCreateRequestDto channelParam) {
        Channel channel = new Channel(null,null,ChannelType.PRIVATE);
        List<UUID> userIds = channelParam.getUserIds();
        for (UUID userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            Participant participant = new Participant(user);
            channel.addParticipant(participant);
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        }
        return channelRepository.save(channel);
    }


    @Override
    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        List<Channel> allChannels = findAllChannels();
        List<Channel> channelList = new ArrayList<>();
        for (Channel channel : allChannels) {
            List<Participant> participants = channel.getParticipants();
            for (Participant participant : participants) {
                if(participant.getUser().getId().equals(userId)) {
                    channelList.add(channel);
                }
            }
        }
        return channelList;
    }

    @Override
    @Transactional
    public Channel updateChannel(UUID id, ChannelUpdateRequestDto channelParam) {
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
        channel.update(channelParam.getNewName(), channelParam.getNewDescription());
        return channel;
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
        messageRepository.deleteByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

}
