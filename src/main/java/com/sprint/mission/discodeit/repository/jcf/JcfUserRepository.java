package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Participant;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
public class JcfUserRepository implements UserRepository {

    private static final Map<UUID, User> data = new HashMap<>();

    @Override
    public User createUser(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<User> findByloginId(String loginId) {
        List<User> all=findAll();
        for (User user : all) {
            if(user.getLoginId().equals(loginId)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findUsersInChannel(Channel channel) {
        List<User> userLists=new ArrayList<>();
        List<Participant> participants = channel.getParticipants();
        for (Participant participant : participants) {
            userLists.add(participant.getUser());
        }
        return userLists;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existLoginId(String loginId) {
        List<User> all = findAll();
        for (User findUser : all) {
            if (findUser.getLoginId().equals(loginId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existUserEmail(String userEmail) {
        List<User> all = findAll();
        for (User findUser : all) {
            if (findUser.getUserEmail().equals(userEmail)) {
                return true;
            }
        }
        return false;
    }
}
