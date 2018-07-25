package com.cdc.cdccmc.domain.dto;
/**
 * @author zhuwen
 * 盘点单统计页面用到
 */
public class InventorySumDto {
	private String inventoryId; //盘点ID
	private String containerCode; //器具代码
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerName; //器具名称
	private Integer allNum; //系统保有量，实际全部数量
	private Integer actualNum; //实际盘点数
	private Integer differentNum;  //差异数
	
	public String getInventoryId() {
		return inventoryId;
	}
	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
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
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public Integer getAllNum() {
		return allNum;
	}
	public void setAllNum(Integer allNum) {
		this.allNum = allNum;
	}
	public Integer getActualNum() {
		return actualNum;
	}
	public void setActualNum(Integer actualNum) {
		this.actualNum = actualNum;
	}
	public Integer getDifferentNum() {
		return differentNum;
	}
	public void setDifferentNum(Integer differentNum) {
		this.differentNum = differentNum;
	}
	
	
}
