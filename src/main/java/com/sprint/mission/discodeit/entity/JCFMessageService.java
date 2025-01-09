package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService{
    private final List<Message> data;

    public JCFMessageService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(Message message) {
        data.add(message);
    }

    @Override
    public Optional<Message> read(UUID id) {
        return data.stream().filter(message -> message.getId().equals(id)).findFirst();
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void update(UUID id, Message updatedMessage) {
        read(id).ifPresent(message -> {
            message.updateContent(updatedMessage.getContent());
        });
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }
}
