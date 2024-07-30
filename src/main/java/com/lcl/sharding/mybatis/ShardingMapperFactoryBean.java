package com.lcl.sharding.mybatis;

import com.lcl.sharding.engine.ShardingContext;
import com.lcl.sharding.engine.ShardingEngine;
import com.lcl.sharding.engine.ShardingResult;
import com.lcl.sharding.demo.model.User;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

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
            // 将 args 转为参数列表 params，防止传入的是对象，无法解析到每一个列值
            Object[] params = getParams(boundSql, args);
            String sql = boundSql.getSql();
            ShardingResult result = shardingEngine.sharding(sql, params);
            ShardingContext.setShardingResult(result);
            return method.invoke(proxy, args);
        });
    }

    /**
     *
     * @param boundSql
     * @param args
     * @return
     */
    @SneakyThrows
    private Object[] getParams(BoundSql boundSql, Object[] args) {
        Object[] params = args;
        // 如果args长度为1 且 类型不是基本类型或基本类型的包装类，需要特殊处理
        if(args.length == 1 && !ClassUtils.isPrimitiveOrWrapper(args[0].getClass())){
            Object arg = args[0];
            List<String> columns = boundSql.getParameterMappings().stream().map(ParameterMapping::getProperty).toList();
            Object[] newParams = new Object[columns.size()];
            for (int i=0; i<columns.size(); i++) {
                newParams[i] = getFieldValue(arg, columns.get(i));
            }
            params = newParams;
        }
        return params;
    }

    /**
     * 获取字段名称
     * @param o
     * @param f
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private static Object getFieldValue(Object o, String f) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(f);
        field.setAccessible(true);
        return field.get(o);
    }

}
