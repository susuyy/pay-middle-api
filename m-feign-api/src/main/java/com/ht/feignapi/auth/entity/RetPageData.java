package com.ht.feignapi.auth.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetPageData implements Serializable {

        private List  records;
        private Integer total;
        private Integer size;
        private Integer current;
        private List orders;
        private Boolean optimizeCountSql;
        private Boolean hitCount;
        private Boolean searchCount;
        private Integer pages;

}
