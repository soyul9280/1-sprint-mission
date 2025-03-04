package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements Serializable {

    private UUID id;
    private Instant createAt;
    private Instant updateAt;

    @NotEmpty
    private String channelName;
    private String description;
    private ChannelGroup group;
    private Instant lastMessageTime;

    @JsonManagedReference
    private List<Participant> participants = new ArrayList<>();

    public Channel(String channelName, String description, ChannelGroup group) {
        this.id = UUID.randomUUID();
        this.createAt = Instant.now();
        this.updateAt = createAt;

        this.channelName = channelName;
        this.description = description;
        this.group = group;
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.channelName)) {
            this.channelName = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updateAt = Instant.now();
        }
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.setChannel(this);
    }

    public boolean isPublic(Channel channel) {
        if(channel.getGroup()==ChannelGroup.PUBLIC) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
