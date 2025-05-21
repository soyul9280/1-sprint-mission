package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService implements UserService{
    private final List<User> data;

    public JCFUserService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(User user) {
        data.add(user);
    }

    @Override
    public Optional<User> read(UUID id) {
        return data.stream().filter(user -> user.getId().equals(id)).findFirst();
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void update(UUID id, User updatedUser) {
        read(id).ifPresent(user -> {
            user.updateUsername(updatedUser.getUsername());
            user.updateEmail(updatedUser.getEmail());
        });
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}
