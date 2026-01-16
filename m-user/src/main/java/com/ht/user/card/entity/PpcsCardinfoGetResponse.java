package com.ht.user.card.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PpcsCardinfoGetResponse implements Serializable {

    private String resTimestamp;

    private String resSign;

    private CardInfo cardInfo;
}
