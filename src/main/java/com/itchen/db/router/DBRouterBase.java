package com.itchen.db.router;

public class DBRouterBase {
    private String tbIdx;

    public String getTbIdx() {
        return DBContextHolder.getTBKey();
    }
}
