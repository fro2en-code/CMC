package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.container.Container;

/** 
 * 器具表  T_CONTAINER
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerForDoorScanDto extends Container {
	private String groupId;//托盘组ID
	private Integer groupState;//0有组 1无组
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	/**
	 * @return 0有组托 1无组托
	 */
	public Integer getGroupState() {
		return groupState;
	}

	/**
	 * @param groupState  0有组托 1无组托
	 */
	public void setGroupState(Integer groupState) {
		this.groupState = groupState;
	}

	@Override
	public String toString() {
		return "ContainerForDoorScanDto{" +
				"groupId='" + groupId + '\'' +
				", groupState=" + groupState +
				'}';
	}
}
