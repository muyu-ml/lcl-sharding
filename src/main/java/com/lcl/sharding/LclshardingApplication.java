package com.lcl.sharding;

import com.lcl.sharding.demo.ShardingAutoConfiguration;
import com.lcl.sharding.demo.ShardingMapperFactoryBean;
import com.lcl.sharding.demo.User;
import com.lcl.sharding.demo.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootApplication
@Import(ShardingAutoConfiguration.class)
@MapperScan(value = "com.lcl.sharding.demo", factoryBean = ShardingMapperFactoryBean.class)
public class LclshardingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LclshardingApplication.class, args);
	}

	@Autowired
	private UserMapper userMapper;

	@Bean
	ApplicationRunner applicationRunner() {
		return args -> {
			for (int i = 1; i <= 10; i++) {
				test(i);
			}
		};
	}

	private void test(int id) {
		log.info(" ====>>> 1. test insert ...");

		userMapper.insert(new User(id, "lcl", 18));

		log.info(" ====>>> 2. test select ...");
		User user = userMapper.selectById(id);
		log.info(" ====>>> user: {}", user);

		log.info(" ====>>> 3. test update ...");
		user.setName("lcl-update");
		user.setAge(20);
		int updated = userMapper.update(user);
		log.info(" ====>>> updateRow: {}", updated);
		User newUser = userMapper.selectById(id);
		log.info(" ====>>> newUser: {}", newUser);

//			log.info(" ====>>> 4. test delete ...");
//			int deleted = userMapper.deleteById(1);
//			log.info(" ====>>> deleteRow: {}", deleted);
	}

}
