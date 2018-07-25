package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;

/** 
 *  用户按钮权限表 T_SYSTEM_USERBUTTON
 * @author ZhuWen
 * @date 2017-12-28
 */
public class SystemUserbutton {
	private String account; //联合主键1。账号，登录名
	private String orgId; // 联合主键2。仓库ID
	private String buttonId; //联合主键3。按钮ID
	private String buttonName; //按钮名称
	private String orgName; // 组织名称
	private Timestamp createTime;
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getButtonId() {
		return buttonId;
	}
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
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
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
}
