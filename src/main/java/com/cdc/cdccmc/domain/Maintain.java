package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/**
 * 器具维修表 T_MAINTAIN
 * 
 * @author ZhuWen
 * @date 2017-12-28
 */
public class Maintain {

	private String maintainId; // 维修ID
	private String epcId; // EPC编号
	private String printCode; // 印刷编号
	private String containerCode; // 器具代码
	private String containerTypeId; // 器具类型ID
	private String containerTypeName; // 器具类型名称
	private String maintainState; // 维修状态。1在库维修 2出库维修  3维修完毕
	private String orderCode;//包装流转单单号，如果是出库维修，则此字段不为空
	private String maintainLevel; // 维修级别，A,B,C
	private String maintainOrgId; // 维修厂ID
	private String maintainOrgName; // 维修厂名称
	private Timestamp maintainApplyTime; // 报修时间
	private String maintainApplyAccount; // 报修人
	private String maintainApplyRealName;// 报修人的姓名
	private String maintainApplyOrgId;// 报修公司ID
	private String maintainApplyOrgName;// 报修公司名称
	private String maintainApplyAreaId;// 报修区域ID
	private String maintainApplyAreaName;// 报修区域名称
	private Timestamp maintainCheckTime; // 维修鉴定时间
	private String maintainCheckAccount; // 维修鉴定人
	private String maintainCheckRealName;// 维修鉴定人的姓名
	private String maintainCheckOrgId;// 维修鉴定人的公司ID
	private String maintainCheckOrgName;// 维修鉴定人的公司名称
	private Timestamp maintainFinishTime; // 维修完毕时间
	private String maintainFinishAccount; // 维修完毕操作人
	private String maintainFinishRealName;// 维修完毕操作人的姓名
	private String maintainFinishOrgId;// 维修完毕操作人的公司ID
	private String maintainFinishOrgName;// 维修完毕操作人的公司名称
	private Timestamp scrapTime; // 报废审批时间
	private String scrapAccount; // 报废审批人
	private String scrapRealName;// 报废审批人的姓名
	private String scrapWayId; // 报废方式ID
	private String scrapWayName; // 报废方式名称
	private Integer version; // 版本控制
	private String maintainApplyBadReason;//报修时，填写不良原因
	private String maintainFinishBadReason;//维修完毕时，填写不良原因
	private String maintainFinishSolution;//维修完毕时，填写维修措施


	public String getMaintainApplyBadReason() {
		return maintainApplyBadReason;
	}

	public void setMaintainApplyBadReason(String maintainApplyBadReason) {
		this.maintainApplyBadReason = maintainApplyBadReason;
	}

	public String getMaintainFinishBadReason() {
		return maintainFinishBadReason;
	}

	public void setMaintainFinishBadReason(String maintainFinishBadReason) {
		this.maintainFinishBadReason = maintainFinishBadReason;
	}

	public String getMaintainFinishSolution() {
		return maintainFinishSolution;
	}

	public void setMaintainFinishSolution(String maintainFinishSolution) {
		this.maintainFinishSolution = maintainFinishSolution;
	}

	public String getMaintainId() {
		return maintainId;
	}

	public void setMaintainId(String maintainId) {
		this.maintainId = maintainId;
	}

	public String getEpcId() {
		return epcId;
	}

	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}

	public String getPrintCode() {
		return printCode;
	}

	public void setPrintCode(String printCode) {
		this.printCode = printCode;
	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
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

	public String getMaintainState() {
		return maintainState;
	}

	public void setMaintainState(String maintainState) {
		this.maintainState = maintainState;
	}

	public String getMaintainLevel() {
		return maintainLevel;
	}

	public void setMaintainLevel(String maintainLevel) {
		this.maintainLevel = maintainLevel;
	}

	public String getMaintainOrgId() {
		return maintainOrgId;
	}

	public void setMaintainOrgId(String maintainOrgId) {
		this.maintainOrgId = maintainOrgId;
	}

	public String getMaintainOrgName() {
		return maintainOrgName;
	}

	public void setMaintainOrgName(String maintainOrgName) {
		this.maintainOrgName = maintainOrgName;
	}

	public Timestamp getMaintainApplyTime() {
		return maintainApplyTime;
	}

	public void setMaintainApplyTime(Timestamp maintainApplyTime) {
		this.maintainApplyTime = maintainApplyTime;
	}

	public String getMaintainApplyAccount() {
		return maintainApplyAccount;
	}

	public void setMaintainApplyAccount(String maintainApplyAccount) {
		this.maintainApplyAccount = maintainApplyAccount;
	}

	public String getMaintainApplyRealName() {
		return maintainApplyRealName;
	}

	public void setMaintainApplyRealName(String maintainApplyRealName) {
		this.maintainApplyRealName = maintainApplyRealName;
	}

	public String getMaintainCheckAccount() {
		return maintainCheckAccount;
	}

	public void setMaintainCheckAccount(String maintainCheckAccount) {
		this.maintainCheckAccount = maintainCheckAccount;
	}

	public String getMaintainCheckRealName() {
		return maintainCheckRealName;
	}

	public void setMaintainCheckRealName(String maintainCheckRealName) {
		this.maintainCheckRealName = maintainCheckRealName;
	}

	public String getMaintainFinishAccount() {
		return maintainFinishAccount;
	}

	public void setMaintainFinishAccount(String maintainFinishAccount) {
		this.maintainFinishAccount = maintainFinishAccount;
	}

	public String getMaintainFinishRealName() {
		return maintainFinishRealName;
	}

	public void setMaintainFinishRealName(String maintainFinishRealName) {
		this.maintainFinishRealName = maintainFinishRealName;
	}

	public Timestamp getScrapTime() {
		return scrapTime;
	}

	public void setScrapTime(Timestamp scrapTime) {
		this.scrapTime = scrapTime;
	}

	public String getScrapAccount() {
		return scrapAccount;
	}

	public void setScrapAccount(String scrapAccount) {
		this.scrapAccount = scrapAccount;
	}

	public String getScrapRealName() {
		return scrapRealName;
	}

	public void setScrapRealName(String scrapRealName) {
		this.scrapRealName = scrapRealName;
	}

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getMaintainApplyOrgId() {
		return maintainApplyOrgId;
	}

	public void setMaintainApplyOrgId(String maintainApplyOrgId) {
		this.maintainApplyOrgId = maintainApplyOrgId;
	}

	public String getMaintainApplyOrgName() {
		return maintainApplyOrgName;
	}

	public void setMaintainApplyOrgName(String maintainApplyOrgName) {
		this.maintainApplyOrgName = maintainApplyOrgName;
	}

	public String getMaintainCheckOrgId() {
		return maintainCheckOrgId;
	}

	public void setMaintainCheckOrgId(String maintainCheckOrgId) {
		this.maintainCheckOrgId = maintainCheckOrgId;
	}

	public String getMaintainCheckOrgName() {
		return maintainCheckOrgName;
	}

	public void setMaintainCheckOrgName(String maintainCheckOrgName) {
		this.maintainCheckOrgName = maintainCheckOrgName;
	}

	public String getMaintainFinishOrgId() {
		return maintainFinishOrgId;
	}

	public void setMaintainFinishOrgId(String maintainFinishOrgId) {
		this.maintainFinishOrgId = maintainFinishOrgId;
	}

	public String getMaintainFinishOrgName() {
		return maintainFinishOrgName;
	}

	public void setMaintainFinishOrgName(String maintainFinishOrgName) {
		this.maintainFinishOrgName = maintainFinishOrgName;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Timestamp getMaintainCheckTime() {
		return maintainCheckTime;
	}

	public void setMaintainCheckTime(Timestamp maintainCheckTime) {
		this.maintainCheckTime = maintainCheckTime;
	}

	public Timestamp getMaintainFinishTime() {
		return maintainFinishTime;
	}

	public void setMaintainFinishTime(Timestamp maintainFinishTime) {
		this.maintainFinishTime = maintainFinishTime;
	}

	public String getMaintainApplyAreaId() {
		return maintainApplyAreaId;
	}

	public void setMaintainApplyAreaId(String maintainApplyAreaId) {
		this.maintainApplyAreaId = maintainApplyAreaId;
	}

	public String getMaintainApplyAreaName() {
		return maintainApplyAreaName;
	}

	public void setMaintainApplyAreaName(String maintainApplyAreaName) {
		this.maintainApplyAreaName = maintainApplyAreaName;
	}

}
