package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.aop.IsLogin;
import com.qf.entity.Goods;
import com.qf.entity.ShopCart;
import com.qf.entity.User;
import com.qf.service.ICartService;
import com.qf.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.plugin.util.UIUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Reference
    private ICartService cartService;

    /**
     * 添加购物车
     * @param cartToken
     * @param shopCart
     * @param user
     * @param response
     * @return
     */
    @RequestMapping("/add")
    @IsLogin(mustLogin = false)//自定义注解，是否自动跳转至登录界面
    public String addCart(@CookieValue(name = "cart_token",required = false)String cartToken, ShopCart shopCart,
                          User user, HttpServletResponse response){
        /*获取购物车的cookie*/
        if(cartToken==null){
            String uuid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("cart_token",uuid);
            cookie.setMaxAge(60*60*24*10);
            //可在同一应用服务器内共享方法
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        cartService.addCart(cartToken,shopCart,user);

        return "success";
    }

    /**
     * 获取购物车信息
     * @param cartToken
     * @param user
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    @IsLogin(mustLogin = false)
    public String getcartList(@CookieValue(name = "cart_token",required = false)String cartToken,User user){
        List<ShopCart> shopCarts = cartService.selectCartByUid(cartToken,user);
        System.out.println("shopcarts的值"+shopCarts);
        return "cartlist("+ JSON.toJSONString(shopCarts)+")";
    }

    /**
     * 购物车列表
     * @param cartToken
     * @param user
     * @return
     */
    @RequestMapping("/cartlist")
    public String tocartlsit(@CookieValue(name = "cart_token",required = false)String cartToken, User user, Model model){
        List<ShopCart> shopCarts = cartService.selectCartByUid(cartToken,user);
        model.addAttribute("shopcarts",shopCarts);
        /*小计*/
        BigDecimal priceall = BigDecimal.valueOf(0.0);
        for (ShopCart shopCart:shopCarts){
            //总价等于单价相加
            priceall = priceall.add(shopCart.getAllprice());
            model.addAttribute("priceall",priceall.doubleValue());
        }
        return "cartlist";
    }
}
