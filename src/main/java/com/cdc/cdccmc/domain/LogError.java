package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 系统错误日日志表  T_LOG_ERROR
 * @author ZhuWen
 * @date 2017-12-28
 */
public class LogError {
	
	private String logErrorId; //主键ID
	private String account; //账号。谁的操作导致此错误产生。
	private String errorContent; //java exception错误内容
	private String errorEvent; //错误事件，例如：新增用户失败
	private String errorCode; //错误代码，异常代码，例如：ERR001
	private Timestamp createTime; //创建时间
	private String orgId; //操作公司ID
	private String orgName; //操作公司名称
	public String getLogErrorId() {
		return logErrorId;
	}
	public void setLogErrorId(String logErrorId) {
		this.logErrorId = logErrorId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getErrorContent() {
		return errorContent;
	}
	public void setErrorContent(String errorContent) {
		this.errorContent = errorContent;
	}
	public String getErrorEvent() {
		return errorEvent;
	}
	public void setErrorEvent(String errorEvent) {
		this.errorEvent = errorEvent;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
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
