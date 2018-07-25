package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具组托表  T_CONTAINER_GROUP
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerGroup {

	private String containerGroupId; //组托表ID,主键
	private String groupId; //组托识别号，UUID生成，例如每个组托60个器具，那么在这个组托里，60个器具拥有相同的组托识别号
	private String epcId; //EPC编号
	private String containerCode; //器具代码
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerName; //器具名称 
	private String groupEpcId; //组托EPC编号
	private String groupType; //组托EPC器具关键字，值只有：“托盘”或者“单独成托”
	private Integer groupNumber; //器具组托个数
	private Integer groupState; //组托状态0已组托 1已解托
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName;//修改账号的姓名
	private Integer version; //版本控制
	
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
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
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getGroupEpcId() {
		return groupEpcId;
	}
	public void setGroupEpcId(String groupEpcId) {
		this.groupEpcId = groupEpcId;
	}
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public Integer getGroupNumber() {
		return groupNumber;
	}
	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}
	public Integer getGroupState() {
		return groupState;
	}
	public void setGroupState(Integer groupState) {
		this.groupState = groupState;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCreateAccount() {
		return createAccount;
	}
	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}
	public String getCreateRealName() {
		return createRealName;
	}
	public void setCreateRealName(String createRealName) {
		this.createRealName = createRealName;
	}
	public Timestamp getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getModifyAccount() {
		return modifyAccount;
	}
	public void setModifyAccount(String modifyAccount) {
		this.modifyAccount = modifyAccount;
	}
	public String getModifyRealName() {
		return modifyRealName;
	}
	public void setModifyRealName(String modifyRealName) {
		this.modifyRealName = modifyRealName;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getContainerGroupId() {
		return containerGroupId;
	}
	public void setContainerGroupId(String containerGroupId) {
		this.containerGroupId = containerGroupId;
	}

	@Override
	public String toString() {
		return "ContainerGroup{" +
				"containerGroupId='" + containerGroupId + '\'' +
				", groupId='" + groupId + '\'' +
				", epcId='" + epcId + '\'' +
				", containerCode='" + containerCode + '\'' +
				", containerTypeId='" + containerTypeId + '\'' +
				", containerTypeName='" + containerTypeName + '\'' +
				", containerName='" + containerName + '\'' +
				", groupEpcId='" + groupEpcId + '\'' +
				", groupType='" + groupType + '\'' +
				", groupNumber=" + groupNumber +
				", groupState=" + groupState +
				", createTime=" + createTime +
				", createAccount='" + createAccount + '\'' +
				", createRealName='" + createRealName + '\'' +
				", modifyTime=" + modifyTime +
				", modifyAccount='" + modifyAccount + '\'' +
				", modifyRealName='" + modifyRealName + '\'' +
				", version=" + version +
				'}';
	}
}
