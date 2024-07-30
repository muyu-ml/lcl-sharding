package com.lcl.sharding.demo;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.reflect.Proxy;

/**
 * @Author conglongli
 * @date 2024/7/30 17:23
 */
@NoArgsConstructor
@Slf4j
public class ShardingMapperFactoryBean<T> extends MapperFactoryBean<T> {
    public ShardingMapperFactoryBean(Class<T> mapperInterface) {
        super(mapperInterface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() throws Exception {
        Object proxy = super.getObject();
        SqlSession sqlSession = getSqlSession();
        Configuration configuration = sqlSession.getConfiguration();
        Class<T> calzz = getMapperInterface();

        // sharding engine
        return (T)Proxy.newProxyInstance(calzz.getClassLoader(), new Class[]{calzz}, (proxy1, method, args) -> {
            // 获取sql
            String mapperId = calzz.getName() + "." + method.getName();
            MappedStatement statement = configuration.getMappedStatement(mapperId);
            BoundSql boundSql = statement.getBoundSql(args);
            String sql = boundSql.getSql();
            log.info(" =====>>> sql statement: {}", sql);
            Object parameterObject = args[0];
            log.info(" =====>>> sql parameters type: {}", parameterObject.getClass());
            // 根据参数设置数据源
            if(parameterObject instanceof User user){
                ShardingContext.setShardingResult(new ShardingResult(user.getId() % 2 == 0 ? "ds0" : "ds1"));
            } else if (parameterObject instanceof Integer id) {
                ShardingContext.setShardingResult(new ShardingResult(id % 2 == 0 ? "ds0" : "ds1"));
            }
            log.info(" =====>>> sql parameters: {}", parameterObject);
            return method.invoke(proxy, args);
        });
    }

}
