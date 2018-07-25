package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.container.ContainerCode;

public class InventoryHistoryDto {

	private ContainerCode containerCode;
	private InventoryHistory inventoryHistory;

	public InventoryHistory getInventoryHistory() {
		return inventoryHistory;
	}

	public void setInventoryHistory(InventoryHistory inventoryHistory) {
		this.inventoryHistory = inventoryHistory;
	}

	public ContainerCode getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(ContainerCode containerCode) {
		this.containerCode = containerCode;
	}
	
	
}
