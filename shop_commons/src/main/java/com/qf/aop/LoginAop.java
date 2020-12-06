package com.qf.aop;

import com.qf.entity.User;
import com.sun.xml.internal.ws.client.RequestContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.net.URLEncoder;

@Aspect
public class LoginAop {
    @Autowired
    private RedisTemplate redisTemplate;

    @Around("@annotation(IsLogin)")//环绕增强，只要添加了IsLogin注解的都加这个增强
    public Object isLogin(ProceedingJoinPoint joinPoint){
        /*判断是否登录，先获取cookie的值，再判断redis中有没有记录，
        如果没有的话，获取注解的参数，判断是否需要跳转到登录页面*/
        try {
            // ，判断是否登录
            ServletRequestAttributes servletRequestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String loginToken = null;
            Cookie[] cookies = request.getCookies();
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("login_token")){
                    loginToken = cookie.getValue();
                    break;
                }
            }
            User user = null;
            /*判断redis中是否有值*/
            if(loginToken!=null){
                user = (User) redisTemplate.opsForValue().get(loginToken);
            }
            /*当redis中没有值时*/
            if(user==null){
                MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
                Method method = methodSignature.getMethod();
                //获取到注解(IsLogin)对象,
                IsLogin annotation = method.getAnnotation(IsLogin.class);
                Boolean flag = annotation.mustLogin();

                if(flag){

                    //获取地址
                    String returnURL = request.getRequestURL().toString();
                    //获取地址后面的参数
                    String queryString = request.getQueryString();
                    //拼接地址
                    returnURL = returnURL+"?"+queryString;
                    //对地址进行编码
                    returnURL = URLEncoder.encode(returnURL,"utf-8");
                    return "redirect:http://localhost:8084/sso/tologin?returnURL="+returnURL;
                }
            }

            /*当redis中有值时,需要用for循环，foreath不能改变参数的值*/
            Object[] params = joinPoint.getArgs();
            System.out.println("params的参数"+params);
            System.out.println("user参数"+user);
            for(int i=0;i<params.length;i++){
                /*找到user类型的参数，将值赋给他*/
                if(params[i]!=null&&params[i].getClass()==User.class)
                { params[i]=user;}
                break;
            }

            //执行目标方法,返回值相当于目标方法的返回值,将params参数替换以前的参数
            Object proceed = joinPoint.proceed(params);
            System.out.println("在目标方法后执行!!!");
            //再次将目标方法的返回值返回，不然可能会造成返回值丢失
            return proceed;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
