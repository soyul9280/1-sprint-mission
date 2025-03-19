package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.LocalBinaryContentStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class DiscodeitApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}
	@Bean
	@Profile("local")
	public BinaryContentStorage localBinaryContentStorage() {
		return new LocalBinaryContentStorage();
	}
}
