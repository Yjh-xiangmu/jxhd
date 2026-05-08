package com.jxhd.backend.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxhd.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}