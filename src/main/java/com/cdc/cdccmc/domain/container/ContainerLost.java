package com.cdc.cdccmc.domain.container;

import java.sql.Timestamp;

/** 
 * 器具丢失表  T_CONTAINER_LOST
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerLost {

	private String containerLostId;//器具丢失ID
	private String epcId;//EPC编号
	private String printCode;//印刷编号
	private String containerTypeId;//器具类型ID
	private String containerTypeName;//器具类型名称
	private String containerCode;//器具代码
	private String containerName;//器具名称
	private String inventoryId;//盘点编号,来源于库存盘点表T_INVENTORY_MAIN
	private String isOut;//是否已丢失出库,0是，1否
	private String orderCode;//包装流转单单据编号，生成规则：仓库代码+日期年月日+四位流水号，例子：CMCSP201712250001',
	private Integer isClaim;//0未索赔，1已进索赔库',
	private String lostRemark;//丢失备注,比如:盘亏,实物少送,损坏
	private Timestamp createTime; //操作时间
	private String createAccount; //操作人
	private String createRealName;//操作人的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	
	public String getContainerLostId() {
		return containerLostId;
	}
	public void setContainerLostId(String containerLostId) {
		this.containerLostId = containerLostId;
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
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
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
	public Integer getIsClaim() {
		return isClaim;
	}
	public void setIsClaim(Integer isClaim) {
		this.isClaim = isClaim;
	}
	public String getLostRemark() {
		return lostRemark;
	}
	public void setLostRemark(String lostRemark) {
		this.lostRemark = lostRemark;
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
