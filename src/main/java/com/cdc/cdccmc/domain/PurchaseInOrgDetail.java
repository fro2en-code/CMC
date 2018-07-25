package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 采购入库单详细表 T_PURCHASE_IN_ORG_DETAIL
 * @author ZhuWen
 * @date 2018-02-02
 */
public class PurchaseInOrgDetail {

	private String purchaseInOrgDetailId; //UUID主键
	private String purchaseInOrgMainId; //入库单单号
	private String epcId; //EPC编号
	private String containerName; //器具名称
	private String epcType; //EPC类型
	private String printCode; //印刷编号
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerCode; //器具代码
	private Integer isAloneGroup; //是否单独成托，0不是，1是
	private String containerSpecification; //规格
	private Integer planNumber; //计划数量
	private Integer sendNumber; //实发数量
	private Integer receiveNumber; //实收数量
	private String remark; //备注
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId; //创建公司ID
	private String createOrgName; //创建公司名称
	
	public String getPurchaseInOrgMainId() {
		return purchaseInOrgMainId;
	}
	public void setPurchaseInOrgMainId(String purchaseInOrgMainId) {
		this.purchaseInOrgMainId = purchaseInOrgMainId;
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
	public String getCreateOrgName() {
		return createOrgName;
	}
	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}
	public String getPurchaseInOrgDetailId() {
		return purchaseInOrgDetailId;
	}
	public void setPurchaseInOrgDetailId(String purchaseInOrgDetailId) {
		this.purchaseInOrgDetailId = purchaseInOrgDetailId;
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
	public Integer getIsAloneGroup() {
		return isAloneGroup;
	}
	public void setIsAloneGroup(Integer isAloneGroup) {
		this.isAloneGroup = isAloneGroup;
	}

	public String getContainerSpecification() {
		return containerSpecification;
	}

	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
	}

	public Integer getPlanNumber() {
		return planNumber;
	}

	public void setPlanNumber(Integer planNumber) {
		this.planNumber = planNumber;
	}

	public Integer getSendNumber() {
		return sendNumber;
	}

	public void setSendNumber(Integer sendNumber) {
		this.sendNumber = sendNumber;
	}

	public Integer getReceiveNumber() {
		return receiveNumber;
	}

	public void setReceiveNumber(Integer receiveNumber) {
		this.receiveNumber = receiveNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
