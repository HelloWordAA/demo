package com.qf.service;

import com.qf.entity.ShopCart;
import com.qf.entity.User;

import java.util.List;

public interface ICartService {
    /**
     * 添加购物车
     * @param cartToken
     * @param shopCart
     * @param user
     * @return
     */
    int addCart(String cartToken,ShopCart shopCart, User user);

    /**
     * 查询购物车
     * @param cartToken
     * @param user
     * @return
     */
    List<ShopCart> selectCartByUid(String cartToken, User user);

    /**
     * 合并购物车
     * @param cartToken
     * @param user
     * @return
     */
    int merteCart(String cartToken,User user);

    /**
     * 删除购物车
     * @param uid   用户id
     * @return
     */
    int delCartByUid(int uid);
}
