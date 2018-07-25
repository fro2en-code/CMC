package com.cdc.cdccmc.domain.dto;

import java.util.List;

import com.cdc.cdccmc.domain.circulate.CirculateDetail;

/**
 * Created by zlc on 5.24.
 */
public class CirculateDetailManualDto {
    String orderCode;
    String orderCode1;

    List<CirculateDetail> circulateDetailList;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<CirculateDetail> getCirculateDetailList() {
        return circulateDetailList;
    }

    public void setCirculateDetailList(List<CirculateDetail> circulateDetailList) {
        this.circulateDetailList = circulateDetailList;
    }
}
