package com.cdc.cdccmc.domain;

import java.math.BigInteger;
import java.sql.Timestamp;

/** 
 * 两张表结构基本一致，除了T_INVENTORY_LATEST多了一个主键字段：inventory_latest_id
 * 库存记录历史表  T_INVENTORY_HISTORY
 * 库存记录最新表    T_INVENTORY_LATEST
 * @author ZhuWen
 * @date 2018-05-30
 */
public class InventoryHistory {

	private BigInteger inventoryLatestId; //自增主建
	private String orgId; //仓库ID
	private String orgName; //仓库名称
	private String containerCode; //器具代码
	private String orderCode; //流转单号
	private String containerName; //器具名称
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private Integer sendNumber; //发货数量
	private Integer receiveNumber; //收货数量
	private Integer inOrgNumber; //在库数量
	private Timestamp createTime; //创建时间
	private String remark; //备注 
	private String createAccount; //操作人
	private String createRealName; //操作人的姓名
	private Boolean isReceive = true; //是否是收货
	
	public BigInteger getInventoryLatestId() {
		return inventoryLatestId;
	}
	public void setInventoryLatestId(BigInteger inventoryLatestId) {
		this.inventoryLatestId = inventoryLatestId;
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
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
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
	public Integer getSendNumber() {
		return sendNumber;
	}
	public void setSendNumber(Integer sendNumber) {
		this.sendNumber = sendNumber;
	}
	public Integer getReceiveNumber() {
		return receiveNumber;
	}
	public void setReceiveNumber(Integer receiveNumber) {
		this.receiveNumber = receiveNumber;
	}
	public Integer getInOrgNumber() {
		return inOrgNumber;
	}
	public void setInOrgNumber(Integer inOrgNumber) {
		this.inOrgNumber = inOrgNumber;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	/**
	 * @return  是否是收货 true 收货  false发货
	 */
	public Boolean getIsReceive() {
		return isReceive;
	}
	/**
	 * @param isReceive true 收货  false发货
	 */
	public void setIsReceive(Boolean isReceive) {
		this.isReceive = isReceive;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containerCode == null) ? 0 : containerCode.hashCode());
		result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventoryHistory other = (InventoryHistory) obj;
		if (containerCode == null) {
			if (other.containerCode != null)
				return false;
		} else if (!containerCode.equals(other.containerCode))
			return false;
		if (orgId == null) {
			if (other.orgId != null)
				return false;
		} else if (!orgId.equals(other.orgId))
			return false;
		return true;
	}
}
