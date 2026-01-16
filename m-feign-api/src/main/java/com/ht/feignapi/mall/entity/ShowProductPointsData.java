package com.ht.feignapi.mall.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class ShowProductPointsData implements Serializable {

    private Long primesId;

    private Integer usePoints;

    private Map<String,Integer> productionPointsMap;

    private int userPoints;
}
