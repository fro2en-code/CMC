package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;

/**
 * 组织机构表T_SYSTEM_ORG
 * 
 * @author ZhuWen
 * @date 2018-01-02
 */
public class SystemOrg {

	private String orgId; // 组织ID
	private String orgName; // 组织名称
	private String orgCode; // 组织代码，最少2位，最多7位
	private String orgTypeId;// 组织类型ID, 参考常量类OrgType.java
	private String orgTypeName;// 组织类型名称，例如：公司、仓库、CMC、供应商、维修厂
	private String orgAddress; // 地址
	private Integer isActive; // 0正常，1禁用
	private String contactName; // 联系人姓名
	private String contactPhone; // 联系电话
	private String parentOrgId; // 上级组织ID，没有上级组织就为空
	private String parentOrgName; //上级组织名称
	private String mainOrgId; // 主公司ID，最顶级的那个机构ID，一级组织主公司ID就是自己
	private Timestamp createTime; // 创建时间
	private String createAccount; // 创建账号
	private String createRealName;// 创建账号的姓名
	private Timestamp modifyTime; // 修改时间
	private String modifyAccount; // 修改账号
	private String modifyRealName;// 修改账号的姓名

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgAddress() {
		return orgAddress;
	}

	public void setOrgAddress(String orgAddress) {
		this.orgAddress = orgAddress;
	}

	public String getParentOrgId() {
		return parentOrgId;
	}

	public void setParentOrgId(String parentOrgId) {
		this.parentOrgId = parentOrgId;
	}

	public String getMainOrgId() {
		return mainOrgId;
	}

	public void setMainOrgId(String mainOrgId) {
		this.mainOrgId = mainOrgId;
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

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getOrgTypeId() {
		return orgTypeId;
	}

	public void setOrgTypeId(String orgTypeId) {
		this.orgTypeId = orgTypeId;
	}

	public String getOrgTypeName() {
		return orgTypeName;
	}

	public void setOrgTypeName(String orgTypeName) {
		this.orgTypeName = orgTypeName;
	}

	public String getParentOrgName() {
		return parentOrgName;
	}

	public void setParentOrgName(String parentOrgName) {
		this.parentOrgName = parentOrgName;
	}

}
