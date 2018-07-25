package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
/**
 * 用户机构对应表 t_system_userorg
 * @author ZhuWen
 * 2017-12-28
 */
public class SystemUserorg {

	private String account; //联合主键1。账号，登录名
	private String orgId; //联合主键2。组织机构ID
	private String realName; //真实姓名
	private String orgName; //组织名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName; //创建用户的姓名
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
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
	
}
