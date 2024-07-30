package com.lcl.sharding.strategy;

import java.util.List;
import java.util.Map;

/**
 * @Author conglongli
 * @date 2024/7/30 23:10
 */
public interface ShardingStrategy {
    List<String> getShardingColums();
    String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams);
}
