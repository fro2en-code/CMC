package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/**
 * 采购预备表
 * @author ZhuWen
 * @date 2018-2-2
 */
public class PurchasePrepare {
	private String epcId;// EPC编号
	private String containerName;// 器具名称
	private String epcType;// EPC类型
	private String printCode;// 印刷编号
	private String containerTypeId;// 器具类型ID
	private String containerTypeName;// 器具类型名称
	private String containerCode;// 器具代码
	private String containerSpecification; //规格
	private String containerTexture; //材质
	private Integer isAloneGroup;// 是否单独成托，0不是，1是
	private String isReceive; //收货状态。0未收货，1已收货
	private String purchaseInOrgMainId; //采购入库单单号，T_PURCHASE_IN_ORG_MAIN表主键
	private Timestamp createTime;// 创建时间
	private String createAccount;// 创建账号
	private String createRealName;// 创建账号的姓名
	private String createOrgId;// 创建组织ID
	private String createOrgName;// 创建组织名称
	private Timestamp receiveTime;// 收货时间
	private String receiveAccount;// 收货账号
	private String receiveRealName;// 收货账号的姓名
	private String receiveOrgId;// 收货组织ID
	private String receiveOrgName;// 收货组织名称
	private Timestamp inOrgTime;// 入库时间
	private String inOrgAccount;// 入库账号
	private String inOrgRealName;// 入库账号的姓名
	private String inOrgOrgId;// 入库组织ID
	private String inOrgOrgName;// 入库组织名称
	private Timestamp modifyTime;// 修改时间
	private String modifyAccount;// 修改账号
	private String modifyRealName;// 修改账号的姓名
	private String modifyOrgId;// 修改组织ID
	private String modifyOrgName;// 修改组织名称

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

	public Integer getIsAloneGroup() {
		return isAloneGroup;
	}

	public void setIsAloneGroup(Integer isAloneGroup) {
		this.isAloneGroup = isAloneGroup;
	}

	public Timestamp getInOrgTime() {
		return inOrgTime;
	}

	public void setInOrgTime(Timestamp inOrgTime) {
		this.inOrgTime = inOrgTime;
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

	public String getCreateOrgId() {
		return createOrgId;
	}

	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
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

	public String getIsReceive() {
		return isReceive;
	}

	public void setIsReceive(String isReceive) {
		this.isReceive = isReceive;
	}

	public String getPurchaseInOrgMainId() {
		return purchaseInOrgMainId;
	}

	public void setPurchaseInOrgMainId(String purchaseInOrgMainId) {
		this.purchaseInOrgMainId = purchaseInOrgMainId;
	}

	public String getCreateOrgName() {
		return createOrgName;
	}

	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}

	public Timestamp getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Timestamp receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getReceiveAccount() {
		return receiveAccount;
	}

	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}

	public String getReceiveRealName() {
		return receiveRealName;
	}

	public void setReceiveRealName(String receiveRealName) {
		this.receiveRealName = receiveRealName;
	}

	public String getReceiveOrgId() {
		return receiveOrgId;
	}

	public void setReceiveOrgId(String receiveOrgId) {
		this.receiveOrgId = receiveOrgId;
	}

	public String getReceiveOrgName() {
		return receiveOrgName;
	}

	public void setReceiveOrgName(String receiveOrgName) {
		this.receiveOrgName = receiveOrgName;
	}

	public String getInOrgAccount() {
		return inOrgAccount;
	}

	public void setInOrgAccount(String inOrgAccount) {
		this.inOrgAccount = inOrgAccount;
	}

	public String getInOrgRealName() {
		return inOrgRealName;
	}

	public void setInOrgRealName(String inOrgRealName) {
		this.inOrgRealName = inOrgRealName;
	}

	public String getInOrgOrgId() {
		return inOrgOrgId;
	}

	public void setInOrgOrgId(String inOrgOrgId) {
		this.inOrgOrgId = inOrgOrgId;
	}

	public String getInOrgOrgName() {
		return inOrgOrgName;
	}

	public void setInOrgOrgName(String inOrgOrgName) {
		this.inOrgOrgName = inOrgOrgName;
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
	
}
