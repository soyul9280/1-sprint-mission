package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("select u from User u where u.loginId=:loginId")
    User findUserByLoginId(@Param("loginId") String loginId);

    Optional<User> findOptionalUserByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);
}
