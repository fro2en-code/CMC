package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.InventoryDetail;

public class InventoryDetailDto extends InventoryDetail {

	private Integer inventoryState; //盘点状态。0盘点中 1盘点完毕

	public Integer getInventoryState() {
		return inventoryState;
	}

	public void setInventoryState(Integer inventoryState) {
		this.inventoryState = inventoryState;
	}
	
}
