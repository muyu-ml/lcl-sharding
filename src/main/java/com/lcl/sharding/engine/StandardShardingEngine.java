package com.lcl.sharding.engine;

import com.lcl.sharding.config.ShardingPropertites;
import com.lcl.sharding.demo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * @Author conglongli
 * @date 2024/7/30 22:46
 */
@Slf4j
public class StandardShardingEngine implements ShardingEngine{
    public StandardShardingEngine(ShardingPropertites propertites) {
    }

    @Override
    public ShardingResult sharding(String sql, Object[] args) {
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
        return new ShardingResult(id % 2 == 0 ? "ds0" : "ds1");
    }

}
