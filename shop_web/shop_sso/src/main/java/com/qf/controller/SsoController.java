package com.qf.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.qf.entity.Email;
import com.qf.entity.User;
import com.qf.service.IUserService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/sso")
public class SsoController {
    /**
     * 用户服务
     */
    @Reference
    private IUserService userService;
    /**
     * redis模板
     */
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 到登录页面
     * @param returnUrl     返回的Url，当在别的页面登录时可以返回登录前页面
     * @param model
     * @return
     */
    @RequestMapping("/tologin")
    public String toLogin(String returnUrl,Model model){//returnUrl之前的地址，登录时携带登录前页面的地址，登录后可以返回原页面
        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }

    /**
     * 到注册页面
     * @return
     */
    @RequestMapping("/toregister")
    public String toRegister(){
        return "register";
    }

    /**
     * 注册
     * @param user  用户信息
     * @param model 用于传参
     * @return
     */
    @RequestMapping("/register")
    public String register(User user,Model model){
        int result = userService.addUser(user);
        if(result<=0){
            model.addAttribute("error","0");
            return "register";
        }
        /*注册成功后发送邮件*/
        Email email = new Email();
        email.setSubject("腾讯官方邮件");
        email.setTo(user.getEmail());
        //设置一个随机数，保证唯一性，防止篡改地址发送请求
        String uuid = UUID.randomUUID().toString();
        //将uuid写入redis
        redisTemplate.opsForValue().set("email_token_"+user.getUsername(),uuid);
        //设置超时时间为5分钟
        redisTemplate.expire("email_token",5,TimeUnit.MINUTES);
        //连接跳转到地址带上用户名和唯一标识
        String url = "http://localhost:8084/sso/activation?username="+user.getUsername()+"&token="+uuid;
        email.setContent("您的账号异常已被冻结，点击<a href="+url+">激活</a>");
        email.setCreatetime(new Date());
        //将邮件放入名为"email_queue的消息队列中"
        rabbitTemplate.convertAndSend("email_queue",email);
        return "login";
    }

    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @param model     用于传参
     * @param response  用于写入cookie
     * @param returnUrl 用于返回原页面
     * @return
     */
    @RequestMapping("/login")
    public String login(String username, String password, Model model, HttpServletResponse response,String returnUrl){
        User user = userService.loginUser(username,password);
        if(user==null){//登录失败
            model.addAttribute("error","0");
            return "login";
        }
        if(returnUrl==null){
            return "redirect:http://localhost:8081/";
        }
        //将用户信息存放进redis中
        String token = UUID.randomUUID().toString();//相当于这个cookie的主键
        redisTemplate.opsForValue().set(token,user);//将用户信息存放到redis中
        redisTemplate.expire(token,10, TimeUnit.DAYS);//设置超时时间为十天
        //将uuid回写到cookie中，需要用到HttpServletResponse
        Cookie cookie = new Cookie("login_token",token);
        cookie.setMaxAge(60*60*24*10);//设置超时时间为十天
        //标识当前cookie不能通过前端脚本获得，只能通过http请求获取，防止xss跨站脚本攻击
        cookie.setHttpOnly(true);
        //设置为true，标识当前这个cookie只有在https协议时，才会传给服务器，如果是http协议不会传给服务器。
        cookie.setSecure(true);
        response.addCookie(cookie);//添加cookie
        return "redirect:"+returnUrl;
    }

    /**
     * 判断是否登录
     * @param token 登录的标识
     * @return
     */
    @RequestMapping("/islogin")
    @ResponseBody
    //@CookieValue从请求的参数中找到一个值设置到cookie中去,required=false表示可以没有,即为null
    public String isLogin(@CookieValue(name = "login_token",required = false)String token){
        //获取浏览器cookie中的login_token
        System.out.println("浏览器中的login_token"+token);
        //从redis中找到对应cookie的值（前面登录的时候已经保存）
        User user = null;
        if(token!=null) {
            user = (User) redisTemplate.opsForValue().get(token);
        }
        //判断有没有这个对象，有将找到的user对象以json格式返回，没有返回null
        return user == null ?"islogin(null)" :"islogin('"+ JSON.toJSONString(user)+"')";
    }

    /**
     * 注销
     * @param token     登录的标识
     * @param response  用于清除cookie
     * @return
     */
    @RequestMapping("/logout")
    public String loginout(@CookieValue(name = "login_token",required = false)String token,HttpServletResponse response){
        //将redis中的记录删除
        redisTemplate.delete(token);
        //将cookie清空
        Cookie cookie = new Cookie("login_token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "login";
    }

    /**
     * 激活
     * @param username  激活链接带过来的账号和uuid标识
     * @param token
     * @return
     */
    @RequestMapping("/activation")
    public String activation(String username,String token){
        //"email_token_"+username为前面加入的key值，value是uuid，保证唯一性
        String redisToken = (String)redisTemplate.opsForValue().get("email_token_"+username);
        if(redisToken==null||!redisToken.equals(token)){//token不相等或为空则激活失败
            return "error";
        }
        //调用激活的方法，改变状态值，激活用户
        userService.activateUser(username);
        return "redirect:/sso/tologin";
    }
}
