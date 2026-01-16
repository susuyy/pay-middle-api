package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ReturnPointsData implements Serializable {

    private Long primesId;

    private Integer usePoints;

    private Map<Long,Integer> productionPointsMap;
}
