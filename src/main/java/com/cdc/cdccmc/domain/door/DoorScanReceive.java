package com.cdc.cdccmc.domain.door;

import java.sql.Timestamp;

/**
 * 表T_DOOR_SCAN_RECEIVE和T_DOOR_SCAN_RECEIVE_HISTORY
 * 两个表结构一模一样，其中一个是历史表
 * @author zhuwen
 * 2018-07-06
 */
public class DoorScanReceive {

	private String doorScanReceiveId; //收货门型设备扫描ID，UUID
	private String doorAccount; //门型设备账号
	private String doorRealName; //门型设备的名称
	private String epcId; //EPC编号
	private String containerCode; //器具代码
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private Timestamp createTime; //创建时间
	private String createOrgId; //创建组织ID，门型设备隶属组织ID
	private String createOrgName; //创建组织名称，门型设备隶属组织名称
	public String getDoorScanReceiveId() {
		return doorScanReceiveId;
	}
	public void setDoorScanReceiveId(String doorScanReceiveId) {
		this.doorScanReceiveId = doorScanReceiveId;
	}
	public String getDoorAccount() {
		return doorAccount;
	}
	public void setDoorAccount(String doorAccount) {
		this.doorAccount = doorAccount;
	}
	public String getDoorRealName() {
		return doorRealName;
	}
	public void setDoorRealName(String doorRealName) {
		this.doorRealName = doorRealName;
	}
	public String getEpcId() {
		return epcId;
	}
	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getContainerTypeId() {
		return containerTypeId;
	}
	public void setContainerTypeId(String containerTypeId) {
		this.containerTypeId = containerTypeId;
	}
	public String getContainerTypeName() {
		return containerTypeName;
	}
	public void setContainerTypeName(String containerTypeName) {
		this.containerTypeName = containerTypeName;
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
