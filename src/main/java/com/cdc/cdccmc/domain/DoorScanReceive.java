package com.cdc.cdccmc.domain;

import com.cdc.cdccmc.domain.door.DoorScanGroupResult;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Timestamp;
import java.util.List;

/**
 * 门型统计 返回APP端展示的每组明细对像
 * @author Ypw
 * @date 2018-05-10
 */
public class DoorScanReceive {
	private Timestamp createTime; //创建时间
	private String doorAccount;
	private Integer count = 0;

	private List<DoorScanGroupResult> doorScanGroupResultList;//

	public List<String> getDoorScanReceiveOwnOrderList() {
		return doorScanReceiveOwnOrderList;
	}

	public void setDoorScanReceiveOwnOrderList(List<String> doorScanReceiveOwnOrderList) {
		this.doorScanReceiveOwnOrderList = doorScanReceiveOwnOrderList;
	}

	public List<String> getDoorScanReceiveOtherOrderList() {
		return doorScanReceiveOtherOrderList;
	}

	public void setDoorScanReceiveOtherOrderList(List<String> doorScanReceiveOtherOrderList) {
		this.doorScanReceiveOtherOrderList = doorScanReceiveOtherOrderList;
	}

	private List<String> doorScanReceiveOwnOrderList;//
	private List<String> doorScanReceiveOtherOrderList;//

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
	public String getDoorAccount() {
		return doorAccount;
	}

	public void setDoorAccount(String doorAccount) {
		this.doorAccount = doorAccount;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "DoorScanReceive{" +
				"createTime=" + createTime +
				", doorAccount='" + doorAccount + '\'' +
				", count=" + count +
				", doorScanGroupResultList=" + doorScanGroupResultList +
				", doorScanReceiveOwnOrderList=" + doorScanReceiveOwnOrderList +
				", doorScanReceiveOtherOrderList=" + doorScanReceiveOtherOrderList +
				'}';
	}
}
