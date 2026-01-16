package com.ht.feignapi.prime.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QuerySummaryRefundCardData {

    private String merId;

    private List<SummaryCardNoRefundData> summaryCardNoRefundDataList = new ArrayList<>();

}
