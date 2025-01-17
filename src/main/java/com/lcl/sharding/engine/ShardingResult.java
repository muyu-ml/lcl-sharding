package com.lcl.sharding.engine;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author conglongli
 * @date 2024/7/30 15:19
 */
@Data
@AllArgsConstructor
public class ShardingResult {
    private String targetDatasourceName;
    private String targetSqlStatement;
}
