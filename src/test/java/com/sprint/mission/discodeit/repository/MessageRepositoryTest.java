package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private Channel channel;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test", "te@ex.ceom", "123", null);
        userRepository.save(user);
        channel = new Channel(null, null, ChannelType.PRIVATE);
        channelRepository.save(channel);
    }

    @Test
    @DisplayName("채널 id로 메세지 페이징 조회 성공")
    void findAllByChannelIdSuccess(){
        //given
        for (int i = 0; i < 10; i++) {
            Message message = new Message("hi " + i, channel, user, new ArrayList<>());
            messageRepository.save(message);
        }
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        //when
        Slice<Message> result = messageRepository.findAllByChannelId(channel.getId(), pageable);
        //then
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않은 채널 ID로 조회시 빈 결과 반환")
    void findAllByChannelIdFail() {
        //given
        UUID uuid = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        //when
        Slice<Message> result = messageRepository.findAllByChannelId(uuid, pageable);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 ID로 메세지 삭제 성공")
    void deleteMessageByChannelId(){
        //given
        Message message1 = new Message("hello", channel, user, new ArrayList<>());
        Message message2 = new Message("world", channel, user, new ArrayList<>());
        messageRepository.save(message1);
        messageRepository.save(message2);
        //when
        messageRepository.deleteByChannelId(channel.getId());

        //then
        List<Message> findMessage = messageRepository.findAll();
        assertThat(findMessage).isEmpty();

    }

    @Test
    @DisplayName("채널 ID로 메세지 삭제 실패")
    void deleteMessageByChannelIdFail(){
        //given
        UUID uuid = UUID.randomUUID();
        Message message1 = new Message("hello", channel, user, new ArrayList<>());
        Message message2 = new Message("world", channel, user, new ArrayList<>());
        messageRepository.save(message1);
        messageRepository.save(message2);
        //when
        messageRepository.deleteByChannelId(uuid);

        //then
        List<Message> findMessage = messageRepository.findAll();
        assertThat(findMessage).hasSize(2);

    }

    @Test
    @DisplayName("N+1문제 검증")
   void entityGraphTest() {
        //given
        List<BinaryContent> files=List.of(
                new BinaryContent("file1.txt",123L,"text/plain"),
                new BinaryContent("file2.txt",456L,"text/plain")
        );
        binaryContentRepository.saveAll(files);

        Message message = new Message("hello", channel, user, files);
        messageRepository.save(message);
        Pageable pageable = PageRequest.of(0, 10);
        //when
        Slice<Message> result = messageRepository.findAllByChannelId(channel.getId(), pageable);
        Message fetchMessage = result.getContent().get(0);
        //then
        assertThat(fetchMessage.getChannel()).isNotNull();
        assertThat(fetchMessage.getAuthor()).isNotNull();
        assertThat(fetchMessage.getAttachments()).hasSize(2);
    }


}