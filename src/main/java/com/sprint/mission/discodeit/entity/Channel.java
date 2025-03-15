package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "channels")
@ToString(of = {"type","name","description"})
public class Channel extends BaseUpdatableEntity{

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private String name;
    private String description;

    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public Channel(String channelName, String description, ChannelType type) {
        this.name = channelName;
        this.description = description;
        this.type = type;
    }

    public void update(String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
        }
        changeUpdatedAt();
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setChannel(this);
    }

    public boolean isPublic(Channel channel) {
        if(channel.getType()== ChannelType.PUBLIC) {
            return true;
        }
        return false;
    }
}
