package com.sprint.mission.discodeit.entity;

public class Message extends BaseEntity{
    private String content;
    private String senderId;
    private String channelId;

    public Message(String content, String senderId, String channelId) {
        super();
        this.content = content;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    public String getContent() {
        return content;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void updateContent(String content) {
        this.content = content;
        updateUpdatedAt();
    }
}
