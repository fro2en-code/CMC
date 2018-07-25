package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具报废表  T_CONTAINER_SCRAP
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerScrap {

	private String containerScrapId;//器具丢失ID
	private String epcId;//EPC编号
	private String printCode;//印刷编号
	private String containerCode;//器具代码
	private String containerTypeId;//器具类型ID
	private String containerTypeName;//器具类型名称
	private String isOut;//是否已报废出库,0是，1否
	private Timestamp scrapTime;//报废审批时间
	private String scrapAccount;//报废审批人
	private String scrapRealName;//报废审批人的姓名
	private String scrapWayId;//报废方式ID
	private String scrapWayName;//报废方式名称
	private String orderCode;//报废出库包装流转单单号
	private Timestamp createTime;//操作时间
	private String createAccount;//操作人
	private String createRealName;//操作人的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	
	public String getContainerScrapId() {
		return containerScrapId;
	}
	public void setContainerScrapId(String containerScrapId) {
		this.containerScrapId = containerScrapId;
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
	public String getIsOut() {
		return isOut;
	}
	public void setIsOut(String isOut) {
		this.isOut = isOut;
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
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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
