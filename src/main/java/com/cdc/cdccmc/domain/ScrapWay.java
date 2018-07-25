package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 报废方式表  T_SCRAP_WAY
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ScrapWay {

	private String scrapWayId; //报废方式ID
	private String scrapWayName; //报废方式名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	
	public String getScrapWayId() {
		return scrapWayId;
	}
	public void setScrapWayId(String scrapWayId) {
		this.scrapWayId = scrapWayId;
	}
	public String getScrapWayName() {
		return scrapWayName;
	}
	public void setScrapWayName(String scrapWayName) {
		this.scrapWayName = scrapWayName;
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
	
}
