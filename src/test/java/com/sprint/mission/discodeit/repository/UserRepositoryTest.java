package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
@ActiveProfiles("test")
class UserRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    private UserRepository userRepository;
    
    @Test
    @DisplayName("유저 저장 성공")
    public void UserSaveSuccess() {
        //given
        User user = new User("userA", "userA@ex.com", "1234", null);

        //when
        User savedUser = userRepository.save(user);

        //then 상태기반 테스트->DB상태 검증
        assertThat(savedUser.getUsername()).isEqualTo("userA");
        assertThat(savedUser.getEmail()).isEqualTo("userA@ex.com");
        assertThat(savedUser.getPassword()).isEqualTo("1234");
    }

    @Test
    @DisplayName("유저 저장 실패")
    void UserSaveFail() {
        //given
        User user = new User(null, null, "123", null);
        //when
        //then
        assertThatThrownBy(()->{
            userRepository.save(user);
            userRepository.flush();
            }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("username 존재여부 성공")
    void existByUsernameSuccess(){
        //given
        userRepository.save(new User("userA", "userA@ex.com", "1234", null));
        //when
        //then
        assertThat(userRepository.existsByUsername("userA")).isTrue();
    }

    @Test
    @DisplayName("username 존재여부 실패")//비즈니스 시나리오 커버리지 확보
    void existByUsernameFail(){
        //given
        userRepository.save(new User("userA", "userA@ex.com", "1234", null));
        //when
        //then
        assertThat(userRepository.existsByUsername("userB")).isFalse();
    }

    @Test
    @DisplayName("email 존재여부 성공")
    void existByEmailSuccess(){
        //given
        userRepository.save(new User("userA", "userA@ex.com", "1234", null));
        //when
        //then
        assertThat(userRepository.existsByEmail("userA@ex.com")).isTrue();
    }

    @Test
    @DisplayName("email 존재여부 실패")
    void existByEmailFail(){
        //given
        userRepository.save(new User("userA", "userA@ex.com", "1234", null));
        //when
        //then
        assertThat(userRepository.existsByEmail("us@ex.com")).isFalse();
    }

    @Test
    @DisplayName("findUserByUsername - 성공")
    void findUserByUsernameSuccess() {
        // given
        User user = new User("userA", "userA@ex.com", "1234", null);
        userRepository.saveAndFlush(user); // 저장 후 즉시 flush

        // when
        Optional<User> foundUser = userRepository.findUserByUsername("userA");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("userA@ex.com");
    }

    @Test
    @DisplayName("findUserByUsername - 실패 (존재하지 않는 유저)")
    void findUserByUsernameFail() {
        // given
        String notExistUsername = "nonExistingUser";

        // when
        Optional<User> foundUser = userRepository.findUserByUsername(notExistUsername);

        // then
        assertThat(foundUser).isEmpty();
    }


}