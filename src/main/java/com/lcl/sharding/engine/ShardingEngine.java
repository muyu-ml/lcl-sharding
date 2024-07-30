package com.lcl.sharding.engine;

/**
 * Core sharding engine
 * @Author conglongli
 * @date 2024/7/30 22:42
 */
public interface ShardingEngine {
    ShardingResult sharding(String sql, Object[] args);
}
