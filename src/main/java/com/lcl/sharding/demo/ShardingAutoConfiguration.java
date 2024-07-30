package com.lcl.sharding.demo;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author conglongli
 * @date 2024/7/30 15:26
 */
@Configuration
@EnableConfigurationProperties(ShardingPropertites.class)
public class ShardingAutoConfiguration {

    @Bean
    public ShardingDataSource shardingDataSource(ShardingPropertites propertites) {
        return new ShardingDataSource(propertites);
    }
}
