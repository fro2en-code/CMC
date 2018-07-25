package com.cdc.cdccmc.domain.circulate;

import java.sql.Timestamp;

/** 
 * 两张表结构一模一样：
 * 器具流转历史表  T_CIRCULATE_HISTORY
 * 器具最新流转记录表    T_CIRCULATE_LATEST
 * @author ZhuWen
 * @date 2017-12-28
 */
public class Circulate {
	
	private String circulateHistoryId; //流转历史ID
	private String epcId; //EPC编号
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerCode; //器具代码
	private String circulateState; //流转状态ID
	private String circulateStateName; //流转状态名称
	private String orgId; //操作公司ID
	private String orgName; //操作公司名称 
	private String fromOrgId;//器具来源公司ID,当仓库是丢失库,租赁库或销售库时,不可以为空
	private String fromOrgName;//器具来源公司名称,当仓库是丢失库,租赁库或销售库时,不可以为空
	private String targetOrgId; //目标公司ID
	private String targetOrgName; //目标公司名称
	private String areaId; //区域ID
	private String areaName; //区域名称
	private String remark; //流转备注
	private Timestamp createTime; //操作时间
	private String createAccount; //操作人
	private String createRealName;//操作人的姓名
	private String orderCode;//包装流转单编号
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getCirculateHistoryId() {
		return circulateHistoryId;
	}
	public void setCirculateHistoryId(String circulateHistoryId) {
		this.circulateHistoryId = circulateHistoryId;
	}
	public String getEpcId() {
		return epcId;
	}
	public void setEpcId(String epcId) {
		this.epcId = epcId;
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

	public String getCirculateState() {
		return circulateState;
	}
	public void setCirculateState(String circulateState) {
		this.circulateState = circulateState;
	}
	public String getCirculateStateName() {
		return circulateStateName;
	}
	public void setCirculateStateName(String circulateStateName) {
		this.circulateStateName = circulateStateName;
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
	public String getFromOrgId() {
		return fromOrgId;
	}
	public void setFromOrgId(String fromOrgId) {
		this.fromOrgId = fromOrgId;
	}
	public String getFromOrgName() {
		return fromOrgName;
	}
	public void setFromOrgName(String fromOrgName) {
		this.fromOrgName = fromOrgName;
	}
	public String getTargetOrgId() {
		return targetOrgId;
	}
	public void setTargetOrgId(String targetOrgId) {
		this.targetOrgId = targetOrgId;
	}
	public String getTargetOrgName() {
		return targetOrgName;
	}
	public void setTargetOrgName(String targetOrgName) {
		this.targetOrgName = targetOrgName;
	}
	public String getAreaId() {
		return areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
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
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
