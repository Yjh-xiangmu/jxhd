package com.jxhd.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jxhd.backend.dto.UserDTO;
import com.jxhd.backend.entity.User;

public interface UserService {

    /** 登录，返回用户信息（含token） */
    User login(String username, String password);

    /** 分页查询用户列表 */
    IPage<User> page(int pageNum, int pageSize, String role, String keyword);

    /** 新增用户 */
    void add(UserDTO dto);

    /** 修改用户 */
    void update(UserDTO dto);

    /** 启用/禁用 */
    void updateStatus(Long id, Integer status);

    /** 重置密码为 123456 */
    void resetPassword(Long id);

    /** 删除用户 */
    void delete(Long id);
}
