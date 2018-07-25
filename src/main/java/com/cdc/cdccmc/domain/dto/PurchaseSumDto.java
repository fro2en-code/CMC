package com.cdc.cdccmc.domain.dto;

/**
 * 采购入库单器具明细
 * @author Clm
 * @date 2018/3/08
 */
public class PurchaseSumDto {
	
	private String containerName; //器具名称
	private String containerCode; //器具代码
	private String containerSpecification;//尺寸
	private Integer purchaseCount; //统计数量
	
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getContainerSpecification() {
		return containerSpecification;
	}
	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
	}
	public Integer getPurchaseCount() {
		return purchaseCount;
	}
	public void setPurchaseCount(Integer purchaseCount) {
		this.purchaseCount = purchaseCount;
	}
}
