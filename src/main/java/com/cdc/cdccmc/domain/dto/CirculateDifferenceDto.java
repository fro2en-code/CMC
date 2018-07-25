package com.cdc.cdccmc.domain.dto;

import java.sql.Timestamp;

import com.cdc.cdccmc.domain.circulate.CirculateDifference;

/**
 * 包装流转差异
 * @author Clm
 * @date 2018/1/26
 */
public class CirculateDifferenceDto extends CirculateDifference{
	
	private String consignorOrgName; //发货组织名称
	private String targetOrgName; //收货组织名称
	private Timestamp targetTime; //收货日期
	private String targetRealName;//收货人的姓名
	private String orderCode;//包装流转单号
	
	
	public String getConsignorOrgName() {
		return consignorOrgName;
	}
	public void setConsignorOrgName(String consignorOrgName) {
		this.consignorOrgName = consignorOrgName;
	}
	public String getTargetOrgName() {
		return targetOrgName;
	}
	public void setTargetOrgName(String targetOrgName) {
		this.targetOrgName = targetOrgName;
	}
	public Timestamp getTargetTime() {
		return targetTime;
	}
	public void setTargetTime(Timestamp targetTime) {
		this.targetTime = targetTime;
	}
	public String getTargetRealName() {
		return targetRealName;
	}
	public void setTargetRealName(String targetRealName) {
		this.targetRealName = targetRealName;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
