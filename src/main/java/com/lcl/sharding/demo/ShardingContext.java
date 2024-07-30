package com.lcl.sharding.demo;

/**
 * @Author conglongli
 * @date 2024/7/30 15:18
 */
public class ShardingContext {
    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static void setShardingResult(ShardingResult shardingResult) {
        LOCAL.set(shardingResult);
    }

    public static ShardingResult getShardingResult() {
        return LOCAL.get();
    }
}
