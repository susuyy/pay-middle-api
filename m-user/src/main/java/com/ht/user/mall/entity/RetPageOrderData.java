package com.ht.user.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetPageOrderData implements Serializable {

        private List<ReturnShowOrderDetailData>  records;
        private long total;
        private long size;
        private long current;
        private List orders;
        private Boolean optimizeCountSql;
        private Boolean hitCount;
        private Boolean searchCount;
        private long pages;

}
