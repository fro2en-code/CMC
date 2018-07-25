package com.cdc.cdccmc.domain.door;

import java.sql.Timestamp;

/** 
 * 门型设备扫描到的器具表  T_DOOR_SCAN
 * @author ZhuWen
 * @date 2018-05-07
 */
public class DoorScan {

	private String doorScanId; //门型设备扫描ID，UUID
	private String doorAccount; //门型设备账号
	private String doorRealName; //门型设备的名称
	private String epcId; //EPC编号
	private String groupId; //组托识别号，来源于T_CONTAINER_GROUP组托表
	private String containerCode; //器具代码
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private Integer isGroup; //是否是托盘，0不是，1是
	private Timestamp createTime; //创建时间
	private String createOrgId; //创建组织ID，门型设备隶属组织ID
	private String createOrgName; //创建组织名称，门型设备隶属组织名称
	public String getDoorScanId() {
		return doorScanId;
	}
	public void setDoorScanId(String doorScanId) {
		this.doorScanId = doorScanId;
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
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	/**
	 * @return 是否是托盘，0不是，1是
	 */
	public Integer getIsGroup() {
		return isGroup;
	}
	/**
	 * 是否是托盘，0不是，1是
	 * @param isGroup
	 */
	public void setIsGroup(Integer isGroup) {
		this.isGroup = isGroup;
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

	@Override
	public String toString() {
		return "DoorScan{" +
				"doorScanId='" + doorScanId + '\'' +
				", doorAccount='" + doorAccount + '\'' +
				", doorRealName='" + doorRealName + '\'' +
				", epcId='" + epcId + '\'' +
				", groupId='" + groupId + '\'' +
				", containerCode='" + containerCode + '\'' +
				", containerTypeId='" + containerTypeId + '\'' +
				", containerTypeName='" + containerTypeName + '\'' +
				", isGroup=" + isGroup +
				", createTime=" + createTime +
				", createOrgId='" + createOrgId + '\'' +
				", createOrgName='" + createOrgName + '\'' +
				'}';
	}
}
