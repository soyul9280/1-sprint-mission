package com.sprint.mission.discodeit.entity;

public class MissionMain {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        User user1 = new User("Alice", "alice@example.com");
        User user2 = new User("Bob", "bob@example.com");

        // 등록
        userService.create(user1);
        userService.create(user2);

        // 조회 (단건)
        System.out.println("User 조회 (단건): " + userService.read(user1.getId()));

        // 조회 (다건)
        System.out.println("User 조회 (다건): " + userService.readAll());

        // 수정
        user1.updateUsername("AliceUpdated");
        user1.updateEmail("alice.updated@example.com");
        userService.update(user1.getId(), user1);

        // 수정된 데이터 조회
        System.out.println("수정된 User: " + userService.read(user1.getId()));

        // 삭제
        userService.delete(user2.getId());

        // 삭제 확인
        System.out.println("User 삭제 후 조회: " + userService.read(user2.getId()));

        // Channel Service 테스트
        JCFChannelService channelService = new JCFChannelService();
        Channel channel1 = new Channel("General", "General discussions");
        Channel channel2 = new Channel("Announcements", "Official announcements");

        // 등록
        channelService.create(channel1);
        channelService.create(channel2);

        // 조회 (단건)
        System.out.println("Channel 조회 (단건): " + channelService.read(channel1.getId()));

        // 조회 (다건)
        System.out.println("Channel 조회 (다건): " + channelService.readAll());

        // 수정
        channel1.updateName("GeneralUpdated");
        channel1.updateDescription("Updated general discussions");
        channelService.update(channel1.getId(), channel1);

        // 수정된 데이터 조회
        System.out.println("수정된 Channel: " + channelService.read(channel1.getId()));

        // 삭제
        channelService.delete(channel2.getId());

        // 삭제 확인
        System.out.println("Channel 삭제 후 조회: " + channelService.read(channel2.getId()));

        // Message Service 테스트
        JCFMessageService messageService = new JCFMessageService();
        Message message1 = new Message("Hello, World!", user1.getId().toString(), channel1.getId().toString());
        Message message2 = new Message("Welcome to the channel!", user1.getId().toString(), channel2.getId().toString());

        // 등록
        messageService.create(message1);
        messageService.create(message2);

        // 조회 (단건)
        System.out.println("Message 조회 (단건): " + messageService.read(message1.getId()));

        // 조회 (다건)
        System.out.println("Message 조회 (다건): " + messageService.readAll());

        // 수정
        message1.updateContent("Hello, Updated World!");
        messageService.update(message1.getId(), message1);

        // 수정된 데이터 조회
        System.out.println("수정된 Message: " + messageService.read(message1.getId()));

        // 삭제
        messageService.delete(message2.getId());

        // 삭제 확인
        System.out.println("Message 삭제 후 조회: " + messageService.read(message2.getId()));
    }
}

