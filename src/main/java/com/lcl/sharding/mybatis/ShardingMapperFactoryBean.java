package com.lcl.sharding.mybatis;

import com.lcl.sharding.engine.ShardingContext;
import com.lcl.sharding.engine.ShardingEngine;
import com.lcl.sharding.engine.ShardingResult;
import com.lcl.sharding.demo.model.User;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    @Setter
    ShardingEngine shardingEngine;
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
            ShardingResult result = shardingEngine.sharding(sql, args);
            ShardingContext.setShardingResult(result);
            return method.invoke(proxy, args);
        });
    }

}
