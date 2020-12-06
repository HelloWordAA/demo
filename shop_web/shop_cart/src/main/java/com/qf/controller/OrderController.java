package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qf.aop.IsLogin;
import com.qf.entity.Address;
import com.qf.entity.ShopCart;
import com.qf.entity.User;
import com.qf.service.IAddressService;
import com.qf.service.ICartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Reference
    private ICartService cartService;
    @Reference
    private IAddressService addressService;

    @RequestMapping("/edit")
    @IsLogin(mustLogin = true)
    public String orderedit(User user , Model model){
        List<ShopCart> shopCarts = cartService.selectCartByUid(null,user);
        //通过用户id查找到地址
        List<Address> addresses = addressService.queryByUid(user.getId());
        BigDecimal priceall = BigDecimal.valueOf(0.0);
        for (ShopCart shopCart : shopCarts) {
            priceall.add(shopCart.getAllprice());
        }
        model.addAttribute("address",addresses);
        model.addAttribute("shopcarts",shopCarts);
        model.addAttribute("priceall",priceall);
        return "orderedit";
    }


}
