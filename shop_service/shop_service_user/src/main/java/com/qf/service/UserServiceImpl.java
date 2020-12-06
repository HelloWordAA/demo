package com.qf.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.UserMapper;
import com.qf.entity.User;
import com.qf.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public int addUser(User user) {
            //md5加密
            user.setPassword(MD5Util.md5(user.getPassword()));
        return userMapper.insert(user);

    }

    @Override
    public User loginUser(String username, String password) {
        System.out.println("收到的密码"+password);
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        queryWrapper.eq("password",MD5Util.md5(password));
        User user = userMapper.selectOne(queryWrapper);
        return user;
    }

    /**
     * 激活用户
     * @param username
     * @return
     */
    @Override
    public int activateUser(String username) {
        /*通过名字查找该用户*/
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username",username);
        User user = userMapper.selectOne(queryWrapper);
        //将激活状态改为1，表示已激活
        user.setStatus(1);
        //修改用户
        userMapper.updateById(user);
        return 1;
    }
}
