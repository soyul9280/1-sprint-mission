package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JcfMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data=new HashMap<>();


    @Override
    public Message createMessage(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        data.get(id).updateMessage(content);
        return data.get(id);
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream().filter(message -> message.getChannelId().equals(channelId)).
                sorted(Comparator.comparing(Message::getCreatedAt).reversed()).toList();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

}
