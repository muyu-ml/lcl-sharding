package com.lcl.sharding.strategy;

import groovy.lang.Closure;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @Author conglongli
 * @date 2024/7/30 23:10
 */
public class HashShardingStrategy implements ShardingStrategy {

    private String shardingColumn;
    private String algorithmExpression;
    public HashShardingStrategy(Properties properties) {
        this.shardingColumn = (String) properties.get("shardingColumn");
        this.algorithmExpression = (String) properties.get("algorithmExpression");
    }

    @Override
    public List<String> getShardingColums() {
        return List.of(this.shardingColumn);
    }

    /**
     *
     * @param availableTargetNames 目标名称（库名/表名）
     * @param logicTableName 逻辑表名
     * @param shardingParams 参数集合
     * @return
     */
    @Override
    public String doSharding(List<String> availableTargetNames, String logicTableName, Map<String, Object> shardingParams) {
        // 解析表达式
        String expression = InlineExpressionParser.handlePlaceHolder(algorithmExpression);
        InlineExpressionParser parser = new InlineExpressionParser(expression);
        Closure<?> closure = parser.evaluateClosure();
        // 使用表达式获取最终值
        closure.setProperty(this.shardingColumn, shardingParams.get(this.shardingColumn));
        return closure.call().toString();
    }
}
