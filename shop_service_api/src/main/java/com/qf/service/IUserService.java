package com.qf.service;

import com.qf.entity.User;

public interface IUserService {
    /**
     * 添加用户
     * @param user
     * @return
     */
    int addUser(User user);

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    User loginUser(String username,String password);

    /**
     * 激活用户
     * @param username
     * @return
     */
    int activateUser(String username);
}
