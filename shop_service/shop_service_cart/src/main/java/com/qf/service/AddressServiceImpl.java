package com.qf.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.AddressMapper;
import com.qf.entity.Address;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 查找用户下的地址
     * @param uid
     * @return
     */
    @Override
    public List<Address> queryByUid(int uid) {

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);

        return addressMapper.selectList(queryWrapper);
    }

    @Override
    public int insertAddress(Address address) {
        return addressMapper.insert(address);
    }
}
