package com.ht.feignapi.prime.entity;

import lombok.Data;
import org.springframework.security.core.parameters.P;

import java.io.Serializable;

@Data
public class SearchSummaryData implements Serializable {

    private String startTime;

    private String endTime;


}
