package com.cdc.cdccmc.domain;

/**
 * 收货门型监控 返回APP端展示的收货单
 * @author Ypw
 * @date 2018-05-10
 */
public class DoorScanReceiveOrder {
    private String orderCode;
    private Integer isOwnOrg;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getIsOwnOrg() {
        return isOwnOrg;
    }

    public void setIsOwnOrg(Integer isOwnOrg) {
        this.isOwnOrg = isOwnOrg;
    }

    @Override
    public String toString() {
        return "DoorScanReceiveOrder{" +
                "orderCode='" + orderCode + '\'' +
                ", isOwnOrg=" + isOwnOrg +
                '}';
    }
}
