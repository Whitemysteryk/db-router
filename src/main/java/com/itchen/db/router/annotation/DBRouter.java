package com.itchen.db.router.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouter {
    /**
     * 路由字段
     */
    String key() default "";
}
