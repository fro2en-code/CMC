package com.cdc.cdccmc.domain.door;

import java.sql.Timestamp;

/**
 * 表T_DOOR_SCAN_RECEIVE_ORDER 和T_DOOR_SCAN_RECEIVE_ORDER_HISTORY
 * 其中一张表是历史表，两张表结构一模一样
 * @author zhuwen
 * 2018-07-06
 */
public class DoorScanReceiveOrder {

	private String doorScanReceiveOrderId; //UUID主键
	private String orderCode; //流转单号
	private Integer isOwnOrg; //是否为本仓库收货流转单, 0本仓库，1其它仓库
	private String doorAccount; //门型设备账号
	private Timestamp createTime; //创建时间
	private String createOrgId; //创建组织ID，门型设备隶属组织ID
	private String createOrgName; //创建组织名称，门型设备隶属组织名称
	
	public String getDoorScanReceiveOrderId() {
		return doorScanReceiveOrderId;
	}
	public void setDoorScanReceiveOrderId(String doorScanReceiveOrderId) {
		this.doorScanReceiveOrderId = doorScanReceiveOrderId;
	}
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
	public String getDoorAccount() {
		return doorAccount;
	}
	public void setDoorAccount(String doorAccount) {
		this.doorAccount = doorAccount;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCreateOrgId() {
		return createOrgId;
	}
	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}
	public String getCreateOrgName() {
		return createOrgName;
	}
	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}
	
	
}
