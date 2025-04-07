package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("findAllByTypeOrIdIn - 성공 (PUBLIC 타입 채널 조회)")
    void findAll() {
        //given
        Channel channel1 = new Channel(null, null, ChannelType.PRIVATE);
        Channel channel2 = new Channel("hi", "hichannel", ChannelType.PUBLIC);
        channelRepository.saveAndFlush(channel1);
        channelRepository.saveAndFlush(channel2);

        //when
        List<Channel> channels = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());

        //then
        assertThat(channels).hasSize(1);
        assertThat(channels).extracting(Channel::getName).containsExactly("hi");
    }

    @Test
    @DisplayName("채널이 없을 경우 빈 리스트 반환")
    void findAllFail() {
        //given
        //when
        List<Channel> result= channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, List.of());
        //then
        assertThat(result).isEmpty();
    }
}