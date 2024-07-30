package com.lcl.sharding.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * @Author conglongli
 * @date 2024/7/30 15:42
 */
@Slf4j
@Component
@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})) // 拦截prepare方法
public class SqlStatementInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
//        // 获取sql
//        String sql = handler.getBoundSql().getSql();
//        log.info(" =====>>> sql statement: {}", sql);
//        Object parameterObject = handler.getBoundSql().getParameterObject();
//        log.info(" =====>>> sql parameters type: {}", parameterObject.getClass());
//        // 根据参数设置数据源
//        if(parameterObject instanceof User user){
//            ShardingContext.setShardingResult(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
//        } else if (parameterObject instanceof Integer id) {
//            ShardingContext.setShardingResult(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
//        }
//        log.info(" =====>>> sql parameters: {}", parameterObject);
        return invocation.proceed();
    }
}
