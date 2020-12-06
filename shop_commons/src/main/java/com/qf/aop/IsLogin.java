package com.qf.aop;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)//标记方法，方法上使用该注解
@Retention(RetentionPolicy.RUNTIME)
public @interface IsLogin {
    boolean mustLogin() default false;
}
