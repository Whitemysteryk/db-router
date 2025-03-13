package com.itchen.db.router;

import com.itchen.db.router.annotation.DBRouter;
import com.itchen.db.router.strategy.IDBRouterStrategy;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Aspect
public class DbRouterJoinPoint {
    private Logger Logger = LoggerFactory.getLogger(DbRouterJoinPoint.class);
    @Resource
    private DBRouterConfig dbRouterConfig;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;

    @Pointcut("@annotation(com.itchen.db.router.annotation.DBRouter)")
    public void aopPoint(){}

    @Around("aopPoint() && @annotation(dbROuter)")
    public Object doRouter(ProceedingJoinPoint pjp, DBRouter dbROuter) throws Throwable{
        //计算路由时设置的key，就是我们希望用那个实例字段的值来计算路由
        String dbKey = dbROuter.key();
        if(StringUtils.isBlank(dbKey) && StringUtils.isBlank(dbRouterConfig.getRouterKey())){
            throw new RuntimeException("未设置路由字段");
        }
        dbKey = StringUtils.isNotBlank(dbKey) ? dbKey : dbRouterConfig.getRouterKey();
        // 路由属性，其中jp.getArgs()就是获取被代理执行方法的参数
        String dbKeyAttr = getAttrValue(dbKey, pjp.getArgs());
        // 路由策略
        dbRouterStrategy.doRouter(dbKeyAttr);
        // 返回结果
        try {
            return pjp.proceed();
        } finally {
            dbRouterStrategy.clear();
        }
    }

    /**
     * 根据字段名获取参数对象的指定的字段数据
     *
     * @param attr
     * @param args
     * @return
     */
    private String getAttrValue(String attr, Object[] args) {
        if (1 == args.length){
            if (args[0] instanceof String){
                return args[0].toString();
            }
        }
        String filedValue = null;
        for(Object object : args){
            try {
                if (StringUtils.isNotBlank(filedValue)){
                    break;
                }
                filedValue = String.valueOf(this.getValueByName(args[0], attr));
            }catch (Exception e){
                Logger.error("获取属性值失败，属性：{}", attr);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     * @param item
     * @param name
     * @return
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByname(item, name);
            if(field == null){
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾获取继承类获取父类的属性
     * @param item
     * @param name
     * @return
     */
    private Field getFieldByname(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
