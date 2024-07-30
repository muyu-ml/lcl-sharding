package com.lcl.sharding;

import com.lcl.sharding.demo.User;
import com.lcl.sharding.demo.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class LclshardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LclshardingApplication.class, args);
	}

	@Autowired
	private UserMapper userMapper;

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			log.info(" ====>>> 1. test insert ...");
			userMapper.insert(new User(1, "lcl", 18));

			log.info(" ====>>> 2. test select ...");
			User user = userMapper.selectById(1);
			log.info(" ====>>> user: {}", user);

			log.info(" ====>>> 3. test update ...");
			user.setName("lcl-update");
			user.setAge(20);
			int updated = userMapper.update(user);
			log.info(" ====>>> updateRow: {}", updated);
			User newUser = userMapper.selectById(1);
			log.info(" ====>>> newUser: {}", newUser);

			log.info(" ====>>> 4. test delete ...");
			int deleted = userMapper.deleteById(1);
			log.info(" ====>>> deleteRow: {}", deleted);

		};
	}

}
