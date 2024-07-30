package com.lcl.sharding.config;

import com.lcl.sharding.datasource.ShardingDataSource;
import com.lcl.sharding.engine.ShardingEngine;
import com.lcl.sharding.engine.StandardShardingEngine;
import com.lcl.sharding.mybatis.SqlStatementInterceptor;
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

    @Bean
    public ShardingEngine shardingEngine(ShardingPropertites propertites) {
        return new StandardShardingEngine(propertites);
    }

    @Bean
    public SqlStatementInterceptor interceptor(ShardingPropertites propertites) {
        return new SqlStatementInterceptor();
    }
}
