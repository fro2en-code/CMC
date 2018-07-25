package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 用户操作日志表  T_LOG_ACCOUNT
 * @author ZhuWen
 * @date 2017-12-28
 */
public class LogAccount {

	private String logAccountId; //主键ID
	private String account; //账号，登录名
	private String logContent; //操作日志内容
	private String epcId;//EPC编号
	private Timestamp createTime; //创建时间
	private String orgId; //操作公司ID
	private String orgName; //操作公司名称
	
	public String getLogAccountId() {
		return logAccountId;
	}
	public void setLogAccountId(String logAccountId) {
		this.logAccountId = logAccountId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getLogContent() {
		return logContent;
	}
	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
	public String getEpcId() {
		return epcId;
	}
	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
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
	
}
