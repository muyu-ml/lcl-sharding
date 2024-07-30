package com.lcl.sharding.demo;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * sharding datasource
 * @Author conglongli
 * @date 2024/7/30 15:11
 */
@Slf4j
public class ShardingDataSource extends AbstractRoutingDataSource {

    public ShardingDataSource(ShardingPropertites propertites) {
        // init datasource
        Map<Object, Object> dataSourceMap = new LinkedHashMap<>();
        propertites.getDatasources().forEach((key, value) -> {
            try {
                dataSourceMap.put(key, DruidDataSourceFactory.createDataSource(value));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // set datasource
        setTargetDataSources(dataSourceMap);
        // set default datasource
        setDefaultTargetDataSource(dataSourceMap.values().iterator().next());
    }


    /**
     * get datasource
     * @return
     */
    @Override
    protected Object determineCurrentLookupKey() {
        ShardingResult shardingResult = ShardingContext.getShardingResult();
        Object object = shardingResult == null ? null : shardingResult.getTargetDatasourceName();
        log.info(" =====>>> current datasource: {}", object);
        return object;
    }
}
