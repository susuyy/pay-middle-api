package com.ht.feignapi.tonglian.user.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PpcsCloudCardOpenReturnData implements Serializable {

    private PpcsCloudCardOpenResponse ppcs_cloud_card_open_response;
}
