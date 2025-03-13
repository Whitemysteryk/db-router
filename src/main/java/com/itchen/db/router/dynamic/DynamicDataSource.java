package com.itchen.db.router.dynamic;

import com.itchen.db.router.DBContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return "db"+ DBContextHolder.getDBKey();
    }
}
