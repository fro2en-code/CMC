package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具代码表  T_CONTAINER_CODE
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerCode {
	
	private String containerCode; //器具代码
	private String containerName; //器具名称
	private String containerCodeType; //器具代码类型
	private String containerTypeId;//器具类型ID
	private String containerTypeName;//器具类型名称
	private Integer isActive;//0启用，1禁用
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	private Integer isTray;//是否是托盘，0不是，1是
	private String containerSpecification;//尺寸

	public String getContainerSpecification() {
		return containerSpecification;
	}

	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
	}

	public Integer getIsTray() {
		return isTray;
	}

	public void setIsTray(Integer isTray) {
		this.isTray = isTray;
	}

	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getContainerCodeType() {
		return containerCodeType;
	}
	public void setContainerCodeType(String containerCodeType) {
		this.containerCodeType = containerCodeType;
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
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
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
	public String getContainerName() { return containerName; }
	public void setContainerName(String containerName) { this.containerName = containerName; }
}
