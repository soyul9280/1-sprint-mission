package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String admin="admin";
        if(userRepository.findUserByUsername(admin).isEmpty()){
            User user = new User("admin", "admin@example.com", passwordEncoder.encode("admin1234"), null);
            user.updateRole(Role.ROLE_ADMIN);
            userRepository.save(user);
            log.info("Admin user created");
        }else{
            log.info("Admin user already exists");
        }
    }
}
