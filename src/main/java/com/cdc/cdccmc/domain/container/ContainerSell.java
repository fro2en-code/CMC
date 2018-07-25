package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具销售表  T_CONTAINER_SELL
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerSell {

	private String containerSellId;//器具销售ID
	private String epcId;//EPC编号
	private String printCode;//印刷编号
	private String containerTypeId;//器具类型ID
	private String containerTypeName;//器具类型名称
	private String containerCode;//器具代码
	private String isOut;//是否已报废出库,0是，1否
	private String orderCode;//包装流转单单据编号，生成规则：仓库代码+日期年月日+四位流水号，例子：CMCSP201712250001
	private String targetOrgId;//销售目标公司ID
	private String targetOrgName;//销售目标公司名称
	private Timestamp createTime; //操作时间
	private String createAccount; //操作人
	private String createRealName;//操作人的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	
	public String getContainerSellId() {
		return containerSellId;
	}
	public void setContainerSellId(String containerSellId) {
		this.containerSellId = containerSellId;
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
	public String getIsOut() {
		return isOut;
	}
	public void setIsOut(String isOut) {
		this.isOut = isOut;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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
