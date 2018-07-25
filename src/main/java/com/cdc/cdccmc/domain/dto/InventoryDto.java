package com.cdc.cdccmc.domain.dto;

import java.sql.Timestamp;

/**
 * 盘点DTO
 * @author shch-pc
 *
 */
public class InventoryDto {

	private String inventoryCode;//盘点单号
	private Integer inventoryState; //盘点状态。0盘点中 1盘点完毕
	private Timestamp inventoryFinishTime; //盘点完毕时间
	private String inventoryFinishAccount;//盘点完毕操作人
	private String inventoryFinishRealName;//盘点完毕操作人的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	private String inventoryDetailId; //主键ID
	private String inventoryId; //盘点编号
	private String inventoryAccount; //盘点操作人
	private String areaId;//区域id
	private String areaName;//区域名称
	private Timestamp inventoryTime; //盘点时间
	private String epcId; //EPC编号
	private Integer isHaveDifferent; //是否有差异。0未知 1 没有差异 2有区域差异 3 器具未扫描到 4 区域内扫描到新器具
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private Integer version; //版本控制
	public String getInventoryCode() {
		return inventoryCode;
	}
	public void setInventoryCode(String inventoryCode) {
		this.inventoryCode = inventoryCode;
	}
	public Integer getInventoryState() {
		return inventoryState;
	}
	public void setInventoryState(Integer inventoryState) {
		this.inventoryState = inventoryState;
	}
	public Timestamp getInventoryFinishTime() {
		return inventoryFinishTime;
	}
	public void setInventoryFinishTime(Timestamp inventoryFinishTime) {
		this.inventoryFinishTime = inventoryFinishTime;
	}
	public String getInventoryFinishAccount() {
		return inventoryFinishAccount;
	}
	public void setInventoryFinishAccount(String inventoryFinishAccount) {
		this.inventoryFinishAccount = inventoryFinishAccount;
	}
	public String getInventoryFinishRealName() {
		return inventoryFinishRealName;
	}
	public void setInventoryFinishRealName(String inventoryFinishRealName) {
		this.inventoryFinishRealName = inventoryFinishRealName;
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
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
