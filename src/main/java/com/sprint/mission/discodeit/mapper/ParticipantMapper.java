package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ParticipantDto;
import com.sprint.mission.discodeit.entity.Participant;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {
    private final UserMapper userMapper;

    public ParticipantMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public ParticipantDto toDto(Participant participant) {
        return new ParticipantDto(
                participant.getId(),
                userMapper.toDto(participant.getUser()) // ✅ User -> UserDto 변환
        );
    }
}
