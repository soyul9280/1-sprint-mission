package com.sprint.mission.discodeit.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
public class Participant{
    private UUID id;
    private User user;

    @JsonBackReference
    private Channel channel;
    private Message message;

    public Participant(User user) {
        this.id = UUID.randomUUID();
        this.user = user;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
