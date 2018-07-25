package com.cdc.cdccmc.domain.door;

import java.sql.Timestamp;
import java.util.Date;

/** 
 * 门型统计 返回APP端展示的每组明细对像
 * @author Ypw
 * @date 2018-05-10
 */
public class DoorScanGroupResult {

	private String containerCode; //器具代码
	private String containerName; //器具名称
	private Integer containerCount; //器具统计数量

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public Integer getContainerCount() {
		return containerCount;
	}

	public void setContainerCount(Integer containerCount) {
		this.containerCount = containerCount;
	}

	public String getContainerCode() {
		return containerCode;
	}

	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}

	@Override
	public String toString() {
		return "DoorScanGroupResult{" +
				"containerCode='" + containerCode + '\'' +
				", containerName='" + containerName + '\'' +
				", containerCount=" + containerCount +
				'}';
	}
}
