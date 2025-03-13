package com.itchen.db.router.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface DBRouterStrategy {
    /**
     * 路由策略
     * 是否分表
     */
    boolean splitTable() default false;

}
