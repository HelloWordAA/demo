package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.aop.IsLogin;
import com.qf.entity.Address;
import com.qf.entity.User;
import com.qf.service.IAddressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/address")
public class AddressController {
    @Reference
    private IAddressService addressService;

    /**
     * 添加收货地址
     * @return
     */
    @RequestMapping("/insert")
    @IsLogin(mustLogin = true)
    public int insertAddress(Address address ,User user){
        address.setUid(user.getId());

        return addressService.insertAddress(address);
    }
}
