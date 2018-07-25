package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.Car;

/**
 * @author Jerry
 * @date 2018/2/5 11:24
 */


public class CarDto extends Car {
    private String licenseValidDateString;//行驶证有效日期
    private String shipperAddress;//承运商地址

    public String getLicenseValidDateString() {
        return licenseValidDateString;
    }

    public void setLicenseValidDateString(String licenseValidDateString) {
        this.licenseValidDateString = licenseValidDateString;
    }

    public String getShipperAddress() {
        return shipperAddress;
    }

    public void setShipperAddress(String shipperAddress) {
        this.shipperAddress = shipperAddress;
    }
}
