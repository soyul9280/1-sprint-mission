package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Getter
@Table(name = "user_statuses")
public class UserStatus extends BaseUpdatableEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,unique = true)
    private User user;

    @Column(nullable = false)
    private Instant lastActiveAt;

    public UserStatus() {
        this.lastActiveAt = Instant.now();
    }

    public UserStatus(User user, Instant lastActiveAt) {
        if(lastActiveAt!=null&&lastActiveAt.equals(this.lastActiveAt)) {
            this.user = user;
            this.lastActiveAt = lastActiveAt;
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.setStatus(this);
    }

    public void updateUserStatus(Instant newLastAttendAt) {
        this.lastActiveAt = newLastAttendAt;
    }

    public Boolean isOnline() {
        Instant now = Instant.now();
        Duration between = Duration.between(lastActiveAt, now);
        return between.toMinutes()<5;
    }
}
