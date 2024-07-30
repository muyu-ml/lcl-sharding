package com.lcl.sharding.engine;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.lcl.sharding.config.ShardingPropertites;
import com.lcl.sharding.demo.model.User;
import com.lcl.sharding.strategy.HashShardingStrategy;
import com.lcl.sharding.strategy.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author conglongli
 * @date 2024/7/30 22:46
 */
@Slf4j
public class StandardShardingEngine implements ShardingEngine{
    /**
     * 逻辑表 -> 库集合
     */
    private final MultiValueMap<String, String> actualDatabaseNames = new LinkedMultiValueMap<>();
    /**
     * 逻辑表 -> 真实表集合
     */
    private final MultiValueMap<String, String> actualTableNames = new LinkedMultiValueMap<>();
    /**
     * 逻辑表 -> 分库策略
     */
    private final Map<String, ShardingStrategy> databaseStrategies = new HashMap<>();
    /**
     * 逻辑表 -> 分表策略
     */
    private final Map<String, ShardingStrategy> tableStrategies = new HashMap<>();

    /**
     * 使用构造函数加载配置文件，初始化相关配置
     * @param propertites
     */
    public StandardShardingEngine(ShardingPropertites propertites) {
        // 获取每一个 table 配置
        propertites.getTables().forEach((table, tablePropertites) -> {
            // 解析 actualDataNodes，将其存入 db->table 和 table->db 对应关系中
            tablePropertites.getActualDataNodes().forEach(actualDataNode -> {
                String[] split = actualDataNode.split("\\.");
                String databaseName = split[0], tableName = split[1];
                actualDatabaseNames.add(databaseName, tableName);
                actualTableNames.add(tableName, databaseName);
            });
            // 解析并存储库表策略
            databaseStrategies.put(table, new HashShardingStrategy(tablePropertites.getDatabaseStrategy()));
            tableStrategies.put(table, new HashShardingStrategy(tablePropertites.getTableStrategy()));
        });
    }

    /**
     * 根据sql和参数获取真实库与真实表
     * @param sql
     * @param args
     * @return
     */
    @Override
    public ShardingResult sharding(String sql, Object[] args) {
        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(sql);

        // insert 处理逻辑
        if(sqlStatement instanceof SQLInsertStatement sqlInsertStatement) {
            // 获取多有参数项及参数值，放入参数集合shardingColumnsMap
            Map<String, Object> shardingColumnsMap = new HashMap<>();
            List<SQLExpr> columns = sqlInsertStatement.getColumns();
            for (int i=0; i<columns.size(); i++) {
                SQLExpr column = columns.get(i);
                SQLIdentifierExpr columnExpr = (SQLIdentifierExpr) column;
                String columnName = columnExpr.getName();
                shardingColumnsMap.put(columnName, args[i]);
            }

            String tableName = sqlInsertStatement.getTableName().getSimpleName();
            // 使用分库策略获取真实库
            ShardingStrategy databaseStrategy = databaseStrategies.get(tableName);
            String targetDatabase = databaseStrategy.doSharding(actualDatabaseNames.get(tableName), tableName, shardingColumnsMap);
            // 使用分表策略获取真实表
            ShardingStrategy tableStrategy = tableStrategies.get(tableName);
            String targetTable = tableStrategy.doSharding(actualTableNames.get(tableName), tableName, shardingColumnsMap);
            log.info("======>>>>>> target db.table = {}.{}", targetDatabase, targetTable);
        } else {

        }


        Object parameterObject = args[0];
        log.info(" =====>>> sql statement: {},   sql parameters type: {}", sql, parameterObject.getClass());
        // 根据参数设置数据源
        int id = 0;
        if(parameterObject instanceof User user){
            id = user.getId();
        } else if (parameterObject instanceof Integer uid) {
            id = uid;
        }

        log.info(" =====>>> sql parameters: {}", parameterObject);
        return new ShardingResult(id % 2 == 0 ? "ds0" : "ds1", sql);
    }

}
