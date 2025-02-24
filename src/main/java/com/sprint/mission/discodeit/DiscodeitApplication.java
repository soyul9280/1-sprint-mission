package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Slf4j
@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}
}
