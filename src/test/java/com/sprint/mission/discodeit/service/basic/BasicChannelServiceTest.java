package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdateForbiddenException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ReadStatusRepository readStatusRepository;
    @Mock
    private ChannelMapper channelMapper;
    @InjectMocks
    private BasicChannelService channelService;

    @Nested
    @DisplayName("채널 생성")
    class createChannelTest {
        @Test
        @DisplayName("공개 채널 생성 테스트")
        void createPublicChannel() {
            //given
            PublicChannelCreateRequestDto dto = new PublicChannelCreateRequestDto("channelA", "public채널입니다");
            Channel saved = new Channel("channelA", "public채널입니다", ChannelType.PUBLIC);
            when(channelRepository.save(any(Channel.class))).thenReturn(saved);
            when(channelMapper.toDto(any(Channel.class))).thenAnswer(invocation ->
                    {
                        Channel savedChannel = invocation.getArgument(0, Channel.class);
                        return new ChannelDto(savedChannel.getId(),
                                savedChannel.getType(),
                                savedChannel.getName(),
                                savedChannel.getDescription(),
                                List.of(),
                                null
                        );
                    }
                    );
            //when
            ChannelDto publicChannel = channelService.createPublicChannel(dto);
            //then
            assertThat(publicChannel.getType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(publicChannel.getName()).isEqualTo("channelA");
            verify(channelRepository).save(any(Channel.class));
            verify(channelMapper).toDto(any(Channel.class));
        }

        @Test
        @DisplayName("개인 채널 생성 테스트")
        void createPrivateChannel() {
            //given
            User userA = new User("userA", "userA@email.com", "userA1234", null);
            User userB = new User("userB", "userB@email.com", "userB1234", null);
            User userC = new User("userC", "userC@email.com", "userC1234", null);
            List<UUID> userIds = Arrays.asList(userA.getId(), userB.getId(), userC.getId());
            PrivateChannelCreateRequestDto dto = new PrivateChannelCreateRequestDto(userIds);
            Channel channel = new Channel(null, null, ChannelType.PRIVATE);

            when(channelRepository.save(any(Channel.class))).thenReturn(channel);
            when(channelMapper.toDto(any(Channel.class))).thenAnswer(invocation ->
                    {
                        Channel savedChannel = invocation.getArgument(0, Channel.class);
                        return new ChannelDto(savedChannel.getId(),
                                savedChannel.getType(),
                                savedChannel.getName(),
                                savedChannel.getDescription(),
                                List.of(),
                                null
                        );
                    }
                    );
            given(userRepository.findAllById(anyCollection())).willReturn(List.of(userA,userB,userC));
            given(readStatusRepository.saveAll(anyList())).willAnswer(inv->inv.getArgument(0));
            //when
            ChannelDto privateChannel = channelService.createPrivateChannel(dto);

            //then
            assertThat(privateChannel.getType()).isEqualTo(ChannelType.PRIVATE);
            verify(channelRepository).save(any());
            verify(channelMapper).toDto(any(Channel.class));
            verify(readStatusRepository).saveAll(anyList());
        }

    }

    @Nested
    @DisplayName("채널 삭제")
    class deleteChannelTest {
        @Test
        @DisplayName("존재 채널 삭제 테스트")
        void deleteChannel() {
            //given
            UUID id=UUID.randomUUID();
            Channel channel = new Channel("org", "삭제대상", ChannelType.PUBLIC);
            given(channelRepository.findById(id)).willReturn(Optional.of(channel));
            //when
            channelService.deleteChannel(id);

            //then
            verify(messageRepository).deleteByChannelId(id);
            verify(channelRepository).deleteById(id);
        }

        @Test
        @DisplayName("저장하지 않은 채널 삭제 테스트")
        void deleteNoChannel() {
            //given
            UUID channelId = UUID.randomUUID();
            given(channelRepository.findById(channelId)).willReturn(Optional.empty());
            //when
            //then
            assertThatThrownBy(()->channelService.deleteChannel(channelId)).isInstanceOf(ChannelNotFoundException.class);
        }

    }

    @Nested
    @DisplayName("채널 수정")
    class UpdateChannelTest {

        @Test
        @DisplayName("공개 채널 업데이트 테스트")
        void updateChannel() {
            //given
            UUID id = UUID.randomUUID();
            Channel channel = new Channel("old", "original", ChannelType.PUBLIC);
            ChannelUpdateRequestDto dto = new ChannelUpdateRequestDto("updateChannel", "채널 수정입니다.");
            given(channelRepository.findById(id)).willReturn(Optional.of(channel));
            when(channelMapper.toDto(any(Channel.class))).thenAnswer(invocation ->
                    {
                        Channel savedChannel = invocation.getArgument(0, Channel.class);
                        return new ChannelDto(savedChannel.getId(),
                                savedChannel.getType(),
                                savedChannel.getName(),
                                savedChannel.getDescription(),
                                List.of(),
                                null
                        );
                    }
            );
            //when
            ChannelDto result = channelService.updateChannel(id, dto);

            //then
            assertThat(result.getName()).isEqualTo("updateChannel");
            verify(channelMapper).toDto(channel);
            verify(channelRepository).findById(id);
        }

        @Test
        @DisplayName("개인 채널 업데이트 테스트")
        public void updatePrivateChannel() {
            //given
            Channel privateChannel = new Channel(null, null, ChannelType.PRIVATE);
            UUID id = UUID.randomUUID();
            ReflectionTestUtils.setField(privateChannel, "id", id);
            given(channelRepository.findById(id)).willReturn(Optional.of(privateChannel));
            ChannelUpdateRequestDto dto = new ChannelUpdateRequestDto("updateChannel", "채널 수정입니다.");
            //when
            //then
            assertThatThrownBy(()-> channelService.updateChannel(id, dto)).isInstanceOf(ChannelUpdateForbiddenException.class);
        }
    }

    @Nested
    @DisplayName("사용자 ID로 채널 조회")
    class FindAllByUserIdTest {
        @Test
        @DisplayName("사용자가 참여중인 채널을 모두 조회한다.")
        void FindAllByUserIdTestSuccess(){
            //given
            UUID id = UUID.randomUUID();
            User user = new User("userA", "userA@email.com", "userA1234", null);
            ReflectionTestUtils.setField(user, "id", id);

            Channel channel1 = new Channel("channel1", "public", ChannelType.PUBLIC);
            Channel channel2 = new Channel(null, null, ChannelType.PRIVATE);
            ReadStatus readStatus =new ReadStatus(user,channel2);


            List<Channel> allChannels=List.of(channel1,channel2);
            given(readStatusRepository.findAllByUserId(user.getId())).willReturn(List.of(readStatus));
            given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),anyList())).willReturn(allChannels);
            when(channelMapper.toDto(any(Channel.class))).thenAnswer(invocation ->
                    {
                        Channel savedChannel = invocation.getArgument(0, Channel.class);
                        return new ChannelDto(savedChannel.getId(),
                                savedChannel.getType(),
                                savedChannel.getName(),
                                savedChannel.getDescription(),
                                List.of(),
                                null
                        );
                    }
            );
            //when
            List<ChannelDto> result = channelService.findAllByUserId(id);

            //then
            assertThat(result.size()).isEqualTo(2);
            verify(readStatusRepository).findAllByUserId(user.getId());
            verify(channelRepository).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),anyList());
        }

        @Test
        @DisplayName("사용자가 참여중인 채널이 없을 경우 빈 리스트 반환")
       void FindAllByUserIdTestFail() {
            //given
            UUID id = UUID.randomUUID();
            User user = new User("userA", "userA@email.com", "userA1234", null);
            ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
            given(readStatusRepository.findAllByUserId(id)).willReturn(Collections.emptyList());
            given(channelRepository.findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),anyList())).willReturn(Collections.emptyList());
            //when
            List<ChannelDto> result = channelService.findAllByUserId(id);
            //then
            assertThat(result).isEmpty();
            verify(readStatusRepository).findAllByUserId(id);
            verify(channelRepository).findAllByTypeOrIdIn(eq(ChannelType.PUBLIC),anyList());
        }
    }

}