package com.lcl.sharding.demo;

import org.apache.ibatis.annotations.*;

/**
 * @Author conglongli
 * @date 2024/7/30 14:49
 */
@Mapper
public interface UserMapper {
    @Insert("insert into user(id, name, age) values(#{id}, #{name}, #{age})")
    int insert(User user);

    @Select("select * from user where id = #{id}")
    User selectById(int id);

    @Update("update user set name = #{name}, age = #{age} where id = #{id}")
    int update(User user);

    @Delete("delete from user where id = #{id}")
    int deleteById(int id);

}
