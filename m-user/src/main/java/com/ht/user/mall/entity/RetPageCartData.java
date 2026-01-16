package com.ht.user.mall.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetPageCartData implements Serializable {

        private List<ReturnShowShoppingCartData>  records;
        private long total;
        private long size;
        private long current;
        private List orders;
        private Boolean optimizeCountSql;
        private Boolean hitCount;
        private Boolean searchCount;
        private long pages;

//        /**
//         * 内容分页
//         */
//        private List<Page<ShowShoppingCartDate>> showShoppingCartDatePageList;
}
