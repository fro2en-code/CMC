package com.cdc.cdccmc.domain.door;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.sql.Timestamp;
import java.util.List;

/**
 * 门型统计 返回APP端展示的每组明细对像
 * @author Ypw
 * @date 2018-05-10
 */
public class DoorScanGroup {
	private Integer isGroup; //是否是托盘，0不是，1是
	private Timestamp createTime; //创建时间
	//@JsonInclude(JsonInclude.Include.NON_NULL)
	private String groupId; //托盘EPCID

	private List<DoorScanGroupResult> doorScanGroupResultList;//

	public Integer getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(Integer isGroup) {
		this.isGroup = isGroup;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public List<DoorScanGroupResult> getDoorScanGroupResultList() {
		return doorScanGroupResultList;
	}

	public void setDoorScanGroupResultList(List<DoorScanGroupResult> doorScanGroupResultList) {
		this.doorScanGroupResultList = doorScanGroupResultList;
	}

	@Override
	public String toString() {
		return "DoorScanGroup{" +
				"isGroup=" + isGroup +
				", createTime=" + createTime +
				", groupId='" + groupId + '\'' +
				", doorScanGroupResultList=" + doorScanGroupResultList +
				'}';
	}
}
