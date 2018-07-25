package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具表  T_CONTAINER
 * @author ZhuWen
 * @date 2017-12-28
 */
public class Container {

	private String epcId; //EPC编号
	private String containerName; //器具名称
	private String epcType; //EPC类型
	private String printCode; //印刷编号
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerCode; //器具代码
	private String containerSpecification; //尺寸
	private String containerTexture; //材质
	private Integer isAloneGroup; //是否单独成托，0不是，1是
	private Integer isTray; //是否是托盘，0不是，1是。此列跟随器具代码改变而改变。
	private String belongOrgId; //隶属仓库ID
	private String belongOrgName; //隶属仓库名称
	private String lastOrgId; //最后一次所在的仓库ID
	private String lastOrgName; //最后一次所在的仓库名称
	private Integer isOutmode;//'是否过时，0未过时，1过时',default 0
	private String contractNumber; //合同号
	private String receiveNumber; //领用单号
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId; //创建公司ID
	private String createOrgName; //创建公司名称
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName;//修改账号的姓名
	private String modifyOrgId; //修改公司ID
	private String modifyOrgName; //修改公司名称
	private Integer version; //版本控制
	
	public String getBelongOrgId() {
		return belongOrgId;
	}
	public void setBelongOrgId(String belongOrgId) {
		this.belongOrgId = belongOrgId;
	}
	public String getBelongOrgName() {
		return belongOrgName;
	}
	public void setBelongOrgName(String belongOrgName) {
		this.belongOrgName = belongOrgName;
	}
	public String getEpcId() {
		return epcId;
	}
	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getEpcType() {
		return epcType;
	}
	public void setEpcType(String epcType) {
		this.epcType = epcType;
	}
	public String getPrintCode() {
		return printCode;
	}
	public void setPrintCode(String printCode) {
		this.printCode = printCode;
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
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getContainerSpecification() {
		return containerSpecification;
	}
	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
	}
	public String getContainerTexture() {
		return containerTexture;
	}
	public void setContainerTexture(String containerTexture) {
		this.containerTexture = containerTexture;
	}
	public Integer getIsAloneGroup() {
		return isAloneGroup;
	}
	public void setIsAloneGroup(Integer isAloneGroup) {
		this.isAloneGroup = isAloneGroup;
	}
	public String getLastOrgId() {
		return lastOrgId;
	}
	public void setLastOrgId(String lastOrgId) {
		this.lastOrgId = lastOrgId;
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
	public Integer getIsOutmode() {
		return isOutmode;
	}
	public void setIsOutmode(Integer isOutmode) {
		this.isOutmode = isOutmode;
	}
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getReceiveNumber() {
		return receiveNumber;
	}

	public void setReceiveNumber(String receiveNumber) {
		this.receiveNumber = receiveNumber;
	}
	public String getLastOrgName() {
		return lastOrgName;
	}
	public void setLastOrgName(String lastOrgName) {
		this.lastOrgName = lastOrgName;
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
	public String getModifyOrgId() {
		return modifyOrgId;
	}
	public void setModifyOrgId(String modifyOrgId) {
		this.modifyOrgId = modifyOrgId;
	}
	public String getModifyOrgName() {
		return modifyOrgName;
	}
	public void setModifyOrgName(String modifyOrgName) {
		this.modifyOrgName = modifyOrgName;
	}
	/**
	 * @return 是否是托盘，0不是，1是。
	 */
	public Integer getIsTray() {
		return isTray;
	}
	/**
	 * 是否是托盘，0不是，1是。
	 */
	public void setIsTray(Integer isTray) {
		this.isTray = isTray;
	}

	@Override
	public String toString() {
		return "Container{" +
				"epcId='" + epcId + '\'' +
				", containerName='" + containerName + '\'' +
				", epcType='" + epcType + '\'' +
				", printCode='" + printCode + '\'' +
				", containerTypeId='" + containerTypeId + '\'' +
				", containerTypeName='" + containerTypeName + '\'' +
				", containerCode='" + containerCode + '\'' +
				", containerSpecification='" + containerSpecification + '\'' +
				", containerTexture='" + containerTexture + '\'' +
				", isAloneGroup=" + isAloneGroup +
				", isTray=" + isTray +
				", belongOrgId='" + belongOrgId + '\'' +
				", belongOrgName='" + belongOrgName + '\'' +
				", lastOrgId='" + lastOrgId + '\'' +
				", lastOrgName='" + lastOrgName + '\'' +
				", isOutmode=" + isOutmode +
				", contractNumber='" + contractNumber + '\'' +
				", receiveNumber='" + receiveNumber + '\'' +
				", createTime=" + createTime +
				", createAccount='" + createAccount + '\'' +
				", createRealName='" + createRealName + '\'' +
				", createOrgId='" + createOrgId + '\'' +
				", createOrgName='" + createOrgName + '\'' +
				", modifyTime=" + modifyTime +
				", modifyAccount='" + modifyAccount + '\'' +
				", modifyRealName='" + modifyRealName + '\'' +
				", modifyOrgId='" + modifyOrgId + '\'' +
				", modifyOrgName='" + modifyOrgName + '\'' +
				", version=" + version +
				'}';
	}
}
