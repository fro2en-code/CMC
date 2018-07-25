package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 盘点主表  T_INVENTORY_MAIN
 * @author ZhuWen
 * @date 2017-12-28
 */
public class InventoryMain {

	private String inventoryId; //盘点编号，生成规则：仓库代码-PD-201712250005日期年月日后面是四位数的流水号
	private String inventoryOrgId; //盘点组织ID
	private String inventoryOrgName; //盘点组织名称
	private String contactName; //盘点仓库联系人
	private String contactPhone; //盘点仓库联系人的联系方式
	private Timestamp inventoryTime;  //盘点时间，具体到时分秒
	private Integer inventoryState; //盘点状态。0盘点中 1盘点完毕
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName; //创建账号的姓名
	private String createOrgId; //创建组织ID
	private String createOrgName; //创建组织名称
	private Timestamp finishTime; //盘点完毕时间
	private String finishAccount; //盘点完毕账号
	private String finishRealName; //盘点完毕账号的姓名
	private String finishOrgId; //盘点完毕组织ID
	private String finishOrgName; //盘点完毕组织名称
	private Integer version; //版本控制
	
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}
	public Timestamp getInventoryTime() {
		return inventoryTime;
	}
	public void setInventoryTime(Timestamp inventoryTime) {
		this.inventoryTime = inventoryTime;
	}
	public Integer getInventoryState() {
		return inventoryState;
	}
	public void setInventoryState(Integer inventoryState) {
		this.inventoryState = inventoryState;
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
	public Timestamp getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Timestamp finishTime) {
		this.finishTime = finishTime;
	}
	public String getFinishAccount() {
		return finishAccount;
	}
	public void setFinishAccount(String finishAccount) {
		this.finishAccount = finishAccount;
	}
	public String getFinishRealName() {
		return finishRealName;
	}
	public void setFinishRealName(String finishRealName) {
		this.finishRealName = finishRealName;
	}
	public String getFinishOrgId() {
		return finishOrgId;
	}
	public void setFinishOrgId(String finishOrgId) {
		this.finishOrgId = finishOrgId;
	}
	public String getFinishOrgName() {
		return finishOrgName;
	}
	public void setFinishOrgName(String finishOrgName) {
		this.finishOrgName = finishOrgName;
	}
	public String getInventoryOrgId() {
		return inventoryOrgId;
	}
	public void setInventoryOrgId(String inventoryOrgId) {
		this.inventoryOrgId = inventoryOrgId;
	}
	public String getInventoryOrgName() {
		return inventoryOrgName;
	}
	public void setInventoryOrgName(String inventoryOrgName) {
		this.inventoryOrgName = inventoryOrgName;
	}
	
}
