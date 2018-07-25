package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/**
 * 盘点明细表 T_INVENTORY_DETAIL
 * 
 * @author ZhuWen
 * @date 2017-12-28
 */
public class InventoryDetail {

	private String inventoryDetailId; // 主键ID
	private String inventoryId; // 盘点编号
	private String inventoryAccount; // 盘点操作人
	private String inventoryRealName;// 盘点操作人的姓名
	private String areaId;// 区域id
	private String areaName;// 区域名称
	private String oldAreaId;// 旧区域ID，即原始流转区域
	private String oldAreaName;// 旧区域名称，即原始流转区域名称
	private Timestamp inventoryTime; // 盘点时间
	private String epcId; // EPC编号
	private String containerCode; //器具代码
	private String containerTypeId; //器具类型id
	private String containerTypeName; //器具类型名称
	private String containerName; //器具名称
	private Integer systemNumber;// 需盘点器具数量
	private Integer inventoryNumber;// 盘点到器具数量
	private Integer isHaveDifferent; // 是否有差异。0未知 1 没有差异 2有区域差异 3 器具未扫描到 4 区域内扫描到新器具
	private Integer isDeal;// 是否处理 0未处理 1已处理
	private Timestamp createTime; // 创建时间
	private String createAccount; // 创建账号
	private String createRealName;// 创建账号的姓名
	private String createOrgId; // 创建公司ID
	private String createOrgName; // 创建公司名称
	private Integer inventoryState;//盘点单状态

	public Integer getSystemNumber() {
		return systemNumber;
	}

	public void setSystemNumber(Integer systemNumber) {
		this.systemNumber = systemNumber;
	}

	public Integer getInventoryNumber() {
		return inventoryNumber;
	}

	public void setInventoryNumber(Integer inventoryNumber) {
		this.inventoryNumber = inventoryNumber;
	}

	public String getInventoryDetailId() {
		return inventoryDetailId;
	}

	public void setInventoryDetailId(String inventoryDetailId) {
		this.inventoryDetailId = inventoryDetailId;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getInventoryAccount() {
		return inventoryAccount;
	}

	public void setInventoryAccount(String inventoryAccount) {
		this.inventoryAccount = inventoryAccount;
	}

	public String getInventoryRealName() {
		return inventoryRealName;
	}

	public void setInventoryRealName(String inventoryRealName) {
		this.inventoryRealName = inventoryRealName;
	}

	public Timestamp getInventoryTime() {
		return inventoryTime;
	}

	public void setInventoryTime(Timestamp inventoryTime) {
		this.inventoryTime = inventoryTime;
	}

	public String getEpcId() {
		return epcId;
	}

	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}

	public Integer getIsHaveDifferent() {
		return isHaveDifferent;
	}

	public void setIsHaveDifferent(Integer isHaveDifferent) {
		this.isHaveDifferent = isHaveDifferent;
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

	public String getOldAreaId() {
		return oldAreaId;
	}

	public void setOldAreaId(String oldAreaId) {
		this.oldAreaId = oldAreaId;
	}

	public String getOldAreaName() {
		return oldAreaName;
	}

	public void setOldAreaName(String oldAreaName) {
		this.oldAreaName = oldAreaName;
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

	public Integer getIsDeal() {
		return isDeal;
	}

	public void setIsDeal(Integer isDeal) {
		this.isDeal = isDeal;
	}

	public String getContainerCode() {
		return containerCode;
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

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	public Integer getInventoryState() {
		return inventoryState;
	}

	public void setInventoryState(Integer inventoryState) {
		this.inventoryState = inventoryState;
	}
}
