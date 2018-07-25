package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.List;

/** 
 * 用户关联打印机表 t_system_userprint
 * @author YPW
 * @date 2018-05-31
 */
public class SystemUserprint {
	private String account; //联合主键1. 账号，登录名
	private String orgId; //联合主键2。仓库ID
	private String printCode; //打印机编码
	private String printName; //打印机名称
	private String orgName; //组织名称
	private Timestamp createTime; //创建时间
	private String createAccount;  // 创建账号
	private String createRealName; //创建账号的姓名

	public String getPrintCode() {
		return printCode;
	}

	public void setPrintCode(String printCode) {
		this.printCode = printCode;
	}

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

	public String getPrintName() {
		return printName;
	}

	public void setPrintName(String printName) {
		this.printName = printName;
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

	@Override
	public String toString() {
		return "SystemUserprint{" +
				"account='" + account + '\'' +
				", orgId='" + orgId + '\'' +
				", printCode='" + printCode + '\'' +
				", printName='" + printName + '\'' +
				", orgName='" + orgName + '\'' +
				", createTime=" + createTime +
				", createAccount='" + createAccount + '\'' +
				", createRealName='" + createRealName + '\'' +
				'}';
	}
}

