package com.cdc.cdccmc.domain.dto;
/**
 * 门型设备接口收货返回对象
 * @author wangzezhi
 * 2018-03-05
 */
public class DoorDto {

	private String orderCode; //包装流转单号
	private String circulateOrderStatus; //t_circulate_order.is_receive 收货状态。是否已收货。0未收货，1已部分收货，2已全部收货
	private String groupStatus; //整托已收货为1已收货，否则返回"0"
	private String groupId; //t_container_group.group_id 组托识别号
	
	public String getCirculateOrderStatus() {
		return circulateOrderStatus;
	}
	public void setCirculateOrderStatus(String circulateOrderStatus) {
		this.circulateOrderStatus = circulateOrderStatus;
	}
	public String getGroupStatus() {
		return groupStatus;
	}
	public void setGroupStatus(String groupStatus) {
		this.groupStatus = groupStatus;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
}
