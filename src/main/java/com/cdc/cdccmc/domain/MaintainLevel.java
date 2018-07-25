package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 维修级别表  T_MAINTAIN_LEVEL
 * @author ZhuWen
 * @date 2017-12-28
 */
public class MaintainLevel {
	private String maintainLevel; //维修级别，A,B,C
	private String maintainLevelName; //维修级别名称
	private Integer maintainHour; //维修时间限制(小时)
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName;//修改账号的姓名
	private String modifyOrgId;//创建组织ID
	private String modifyOrgName;//创建组织名称
	
	public String getMaintainLevel() {
		return maintainLevel;
	}
	public void setMaintainLevel(String maintainLevel) {
		this.maintainLevel = maintainLevel;
	}
	public String getMaintainLevelName() {
		return maintainLevelName;
	}
	public void setMaintainLevelName(String maintainLevelName) {
		this.maintainLevelName = maintainLevelName;
	}
	public Integer getMaintainHour() {
		return maintainHour;
	}
	public void setMaintainHour(Integer maintainHour) {
		this.maintainHour = maintainHour;
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
	
}
