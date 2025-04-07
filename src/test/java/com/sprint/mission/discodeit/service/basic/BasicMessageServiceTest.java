package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BasicMessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private BinaryContentStorage binaryContentStorage;
    @Mock
    private BinaryContentRepository contentRepository;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID channelId;
    private UUID senderId;
    private Channel channel;
    private User sender;

    @BeforeEach
    void setUp() {
        channelId = UUID.randomUUID();
        senderId = UUID.randomUUID();
        channel = new Channel("channelA", "public", ChannelType.PUBLIC);
        sender = new User("userA", "user@ex.com", "1234", null);
        ReflectionTestUtils.setField(channel, "id", channelId);
        ReflectionTestUtils.setField(sender, "id", senderId);

    }

    @Nested
    @DisplayName("메세지 생성")
    class CreateMessage {
        @Test
        @DisplayName("메세지 생성 성공")
        void CreateMessageSuccess() {
            //given
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
            given(userRepository.findById(senderId)).willReturn(Optional.of(sender));
            BinaryContentCreateRequestDto fileDto1 = new BinaryContentCreateRequestDto("test.png", 1024L, "image/png", new byte[]{1,2,3});
            BinaryContentCreateRequestDto fileDto2 = new BinaryContentCreateRequestDto("test2.png", 128L, "image/png", new byte[]{4,5,6});
            List<BinaryContentCreateRequestDto> fileDtos = List.of(fileDto1, fileDto2);
            BinaryContent file1 = new BinaryContent("test.png", 1024L, "image/png");
            BinaryContent file2 = new BinaryContent("test2.png", 128L, "image/png");
            List<BinaryContent> files = List.of(file1, file2);
            given(contentRepository.save(any(BinaryContent.class))).willAnswer(i->{
                    BinaryContent content=i.getArgument(0);
                    ReflectionTestUtils.setField(content, "id", UUID.randomUUID());
                    return content;
                }
            );

            MessageCreateRequestDto dto = new MessageCreateRequestDto("hi", senderId, channelId, files.stream().map(BinaryContent::getId).collect(Collectors.toList()));
            Message message = new Message("hi", channel, sender, files);
            given(messageRepository.save(any(Message.class))).willReturn(message);
            when(messageMapper.toDto(any(Message.class))).thenAnswer(i->{
                Message savedMessage = i.getArgument(0);
                return new MessageDto(savedMessage.getId(),
                        savedMessage.getCreatedAt(),
                        savedMessage.getUpdatedAt(),
                        savedMessage.getContent(),
                        channelId,null,null
                );
            });
            //when
            MessageDto result = messageService.messageSave(dto, fileDtos);
            //then
            assertThat(result.getId()).isEqualTo(message.getId());
            assertThat(result.getContent()).isEqualTo("hi");
            verify(messageRepository).save(any(Message.class));
            verify(userRepository).findById(senderId);
            verify(channelRepository).findById(channelId);
        }

        @Test
        @DisplayName("존재하지 않은 채널로 메세지 생성 실패")
        public void CreateMessageFailNoChannel() {
            //given
            UUID givenId = UUID.randomUUID();
            given(channelRepository.findById(givenId)).willReturn(Optional.empty());
            //when
            MessageCreateRequestDto dto = new MessageCreateRequestDto("hello", senderId, givenId, null);
            //then
            assertThatThrownBy(() -> messageService.messageSave(dto, null)).isInstanceOf(ChannelNotFoundException.class);

        }

        @Test
        @DisplayName("존재하지 않은 유저로 메세지 생성 실패")
        public void CreateMessageFailNoUser() throws Exception {
            //given
            given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));

            UUID givenId = UUID.randomUUID();
            given(userRepository.findById(givenId)).willReturn(Optional.empty());
            //when
            MessageCreateRequestDto dto = new MessageCreateRequestDto("hello", givenId, channelId, null);

            //then
            assertThatThrownBy(() -> messageService.messageSave(dto, null)).isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("메세지 수정")
    class UpdateMessage {
        @Test
        @DisplayName("메세지 수정 성공")
        void UpdateMessageSuccess() {
            //given
            UUID messageId = UUID.randomUUID();
            Message message = new Message("orginal", channel, sender, null);
            MessageUpdateDto dto = new MessageUpdateDto("updateMessage");
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            when(messageMapper.toDto(any(Message.class))).thenAnswer(i->{
                Message savedMessage = i.getArgument(0);
                        return new MessageDto(savedMessage.getId(),
                                savedMessage.getCreatedAt(),
                                savedMessage.getUpdatedAt(),
                                savedMessage.getContent(),
                                channelId,null,null
                        );
            });
            //when
            MessageDto result = messageService.updateMessage(messageId, dto);
            //then
            assertThat(result.getContent()).isEqualTo("updateMessage");
            verify(messageRepository).findById(messageId);
        }

        @Test
        @DisplayName("메세지가 존재하지 않아 수정 실패")
        void UpdateMessageFail() {
            //given
            UUID messageId = UUID.randomUUID();
            //when
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> messageService.updateMessage(messageId, new MessageUpdateDto("updateMessage"))).isInstanceOf(MessageNotFoundException.class);

        }
    }

    @Nested
    @DisplayName("메세지 삭제")
    class DeleteMessage {
        @Test
        @DisplayName("메세지 삭제 성공")
        void DeleteMessageSuccess() {
            //given
            UUID messageId = UUID.randomUUID();
            Message message = new Message("orginal", channel, sender, new ArrayList<>());
            given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
            //when
            messageService.deleteMessage(messageId);
            //then
            verify(messageRepository).deleteById(messageId);
        }

        @Test
        @DisplayName("메시지 존재하지 않아 삭제 실패")
        void DeleteMessageFail() {
            //given
            UUID messageId = UUID.randomUUID();
            given(messageRepository.findById(messageId)).willReturn(Optional.empty());
            //when
            //then
            assertThatThrownBy(() -> messageService.deleteMessage(messageId)).isInstanceOf(MessageNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("채널 ID로 메세지 조회")
    class GetMessage {
        @Test
        @DisplayName("조회 성공")
        void findMessageByChannelIdSuccess() {
            //given
            Message message1 = new Message("Hello", channel, sender, null);
            Message message2 = new Message("Word", channel, sender, null);
            Slice<Message> messageSlice=new SliceImpl<>(List.of(message1, message2));
            given(messageRepository.findAllByChannelId(eq(channelId),any(Pageable.class))).willReturn(messageSlice);
            when(messageMapper.toDto(any(Message.class))).thenAnswer(i->{
                Message savedMessage = i.getArgument(0);
                return new MessageDto(savedMessage.getId(),
                        savedMessage.getCreatedAt(),
                        savedMessage.getUpdatedAt(),
                        savedMessage.getContent(),
                        channelId,null,null
                );
            });
            given(pageResponseMapper.fromSlice(any(Slice.class))).willAnswer(i->{
                Slice<MessageDto> slice = i.getArgument(0);
                return new PageResponse<>(
                        slice.getContent(),
                        slice.getNumber(),
                        slice.getSize(),
                        null,
                        slice.hasNext()
                );
            });
            //when
            PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, 0);
            //then
            assertThat(result.getContent().size()).isEqualTo(2);
            assertThat(result.getContent().get(0).getContent()).isEqualTo("Hello");
            verify(messageRepository).findAllByChannelId(eq(channelId), any(Pageable.class));
            verify(pageResponseMapper).fromSlice(any(Slice.class));
            verify(messageMapper,times(2)).toDto(any(Message.class));
        }

        @Test
        @DisplayName("채널ID에 해당하는 메세지 없을 경우 빈 Slice반환")
        void findMessageByChannelIdFail(){
            //given
            Slice<Message> emptySlice = new SliceImpl<>(List.of());
            given(messageRepository.findAllByChannelId(eq(channelId),any(Pageable.class))).willReturn(emptySlice);
            //when
            PageResponse<MessageDto> result = messageService.findAllByChannelId(channelId, 0);
            //then
            verify(messageRepository).findAllByChannelId(eq(channelId),any(Pageable.class));

        }
    }
}