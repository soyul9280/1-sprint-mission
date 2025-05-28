package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"username","email","password"})
public class User extends BaseUpdatableEntity{

    @Column(unique = true, nullable = false,length = 50)
    private String username;
    @Column(unique = true, nullable = false,length = 100)
    private String email;
    @Column(nullable = false,length = 60)
    private String password;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Role role;

    public User(String username, String email, String password,  BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = Role.ROLE_USER;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void updateUser(String newUserName, String newPassword, String newUserEmail, BinaryContent newProfile) {
        if (newUserName != null && !newUserName.equals(this.username)) {
            this.username = newUserName;
        }
        if (newUserEmail != null && !newUserEmail.equals(this.email)) {
            this.email = newUserEmail;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
        if (newProfile != null) {
            this.profile = newProfile;
        }
        changeUpdatedAt();

    }

    public void updateRole(Role newRole) {
        this.role = newRole;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(profile, user.profile) && Objects.equals(status, user.status) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, profile, status, role);
    }
}
