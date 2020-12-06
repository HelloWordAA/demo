package com.qf.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qf.dao.CartMapper;
import com.qf.dao.GoodsMapper;
import com.qf.entity.Goods;
import com.qf.entity.ShopCart;
import com.qf.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.List;
@Service
public class CartServiceImpl implements ICartService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartMapper cartMapper;
    @Override
    public int addCart(String cartToken, ShopCart shopCart, User user) {
        //通过购物车内的商品id查找商品
        Goods goods = goodsMapper.selectById(shopCart.getGid());
        //算出购物车商品总价，  获取商品价格乘以（multiply）商品的数量
        shopCart.setAllprice(goods.getGprice().multiply(BigDecimal.valueOf(shopCart.getGnumber())));
        /*判断是否登录，登录的话加入用户的购物车中，没登录则按照购物车的key来存*/
        if(user!=null){
            //设置购物车的uid
            shopCart.setUid(user.getId());
            //将购物车加入
            cartMapper.insert(shopCart);
        }
        //按购物车在redis中的key添加进去
        redisTemplate.opsForList().leftPush(cartToken,shopCart);
        return 1;
    }

    @Override
    public List<ShopCart> selectCartByUid(String cartToken, User user) {
        List<ShopCart> shopCarts = null;
        /*已登录*/
        if(user!=null){
            QueryWrapper<ShopCart> queryWarpper = new QueryWrapper<>();
            queryWarpper.eq("uid",user.getId());
            shopCarts = cartMapper.selectList(queryWarpper);
        }
        /*未登录*/
        else if(cartToken!=null){
            //数量
            Long size = redisTemplate.opsForList().size(cartToken);
            //获取数据,获取redis中指定区间的值，即获取全部购物车的值
            shopCarts = redisTemplate.opsForList().range(cartToken,0,size);
        }
        /*购物车不为空，查询商品详细信息*/
        if(shopCarts!=null){
            for(ShopCart shopCart:shopCarts){
                Goods goods = goodsMapper.selectById(shopCart.getGid());
                shopCart.setGoods(goods);
            }

        }
        return shopCarts;
    }

    /**
     * 合并购物车
     * @param cartToken
     * @param user
     * @return
     */
    @Override
    public int merteCart(String cartToken, User user) {
        if(cartToken!=null){
            Long size = redisTemplate.opsForList().size(cartToken);
            List<ShopCart> carts = redisTemplate.opsForList().range(cartToken,0,size);

            if(carts==null){
                return 1;
            }
            for (ShopCart cart : carts) {
                cart.setUid(user.getId());
                cartMapper.insert(cart);
            }
            redisTemplate.delete(cartToken);
        }

        return 1;
    }

    /**
     * 删除购物车
     * @param uid   用户id
     * @return
     */
    @Override
    public int delCartByUid(int uid) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("uid",uid);
        cartMapper.delete(queryWrapper);
        return 1;
    }
}
