package com.itchen.db.router.strategy.impl;

import com.itchen.db.router.DBContextHolder;
import com.itchen.db.router.DBRouterBase;
import com.itchen.db.router.DBRouterConfig;
import com.itchen.db.router.strategy.IDBRouterStrategy;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBRouterStrategyHashCode implements IDBRouterStrategy {
    private Logger logger = LoggerFactory.getLogger(DBRouterStrategyHashCode.class);

    @Resource
    private DBRouterConfig dbRouterConfig;

    @Override
    public void doRouter(String dbKeyAttr) {
        //获取表的数量
        int size = dbRouterConfig.getDbCount() * dbRouterConfig.getTbCount();

        //使用扰动函数计算哈希值作为Id
        int idx = (size - 1) & (dbKeyAttr.hashCode() ^ (dbKeyAttr.hashCode() >>> 16));

        /**
         * 库表索引；相当于是把一个长条的桶，切割成段，对应分库分表中的库编号和表编号
         * 在这里对这个公式的意思做一个简要说明：
         * 1. 上一步的idx只是根据HashMap算法模型，使用散列+扰动函数计算出的索引位置，这个时候的索引位置就类似于HashMap的索引位置
         * 2. 库表总量按照2的幂次方设置，比如目前2个库*4个表= 8个表
         * 3. 将计算出的索引位置idx分摊到8个表中，这个时候只需要按照HashMap的数据结构分散到库表中即可
         * 4 比如：idx=3，那么它就是在1库3表，idx=7那么他就是在2库的3表，因为1库4表+2库3表正好是7，那么自然就有了下面的这个分配算法了。
         * */
        int dbIdx = idx / dbRouterConfig.getTbCount() + 1;
        int tbIdx = idx - dbRouterConfig.getTbCount() * (dbIdx - 1);

        // 设置到ThreadLocal
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
        logger.info("设置库表信息到ThreadLocal，库：{}，表：{}", DBContextHolder.getDBKey(), DBContextHolder.getTBKey());

    }

    @Override
    public void setDBKey(int dbIdx) {
        DBContextHolder.setDBKey(String.format("%02d", dbIdx));
    }

    @Override
    public void setTBKey(int tbIdx) {
        DBContextHolder.setTBKey(String.format("%03d", tbIdx));
    }

    @Override
    public int dbCount() {
        return dbRouterConfig.getDbCount();
    }

    @Override
    public int tbCount() {
        return dbRouterConfig.getTbCount();
    }

    @Override
    public void clear() {
        DBContextHolder.clearDBKey();
        DBContextHolder.clearTBKey();
    }
}
