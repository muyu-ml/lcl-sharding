package com.lcl.sharding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @Author conglongli
 * @date 2024/7/30 15:09
 */
@Data
@ConfigurationProperties(prefix = "spring.sharding")
public class ShardingPropertites {

    private Map<String, Properties> datasources = new LinkedHashMap<>();
    private Map<String, TableProperties> tables = new LinkedHashMap<>();


    @Data
    public class TableProperties {
        private List<String> actualDataNodes;
        private Properties databaseStrategy;
        private Properties tableStrategy;
    }

}
