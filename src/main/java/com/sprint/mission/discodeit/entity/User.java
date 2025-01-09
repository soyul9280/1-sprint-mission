package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity{
    private String username;
    private String email;

    public User(String username, String email) {
        super();
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public void updateUsername(String username) {
        this.username = username;
        updateUpdatedAt();
    }

    public void updateEmail(String email) {
        this.email = email;
        updateUpdatedAt();
    }
}
