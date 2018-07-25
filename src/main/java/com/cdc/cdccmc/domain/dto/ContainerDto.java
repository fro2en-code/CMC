package com.cdc.cdccmc.domain.dto;

import java.sql.Timestamp;

import com.cdc.cdccmc.domain.container.Container;

/** 
 * 器具表  T_CONTAINER
 * @author ZhuWen
 * @date 2017-12-28
 */
public class ContainerDto extends Container {
	String imgPath;
	private String circulateState; //流转状态ID
	private String circulateStateName; //流转状态名称
	private String orgId; //操作公司ID
	private String orgName; //操作公司名称
	private String orderCode; //包装流转单号
	private String maintainState; // 维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
	private String isOutLost;//是否已丢失出库,0是，1否

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getCirculateState() {
		return circulateState;
	}

	public void setCirculateState(String circulateState) {
		this.circulateState = circulateState;
	}

	public String getCirculateStateName() {
		return circulateStateName;
	}

	public void setCirculateStateName(String circulateStateName) {
		this.circulateStateName = circulateStateName;
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

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getMaintainState() {
		return maintainState;
	}

	public void setMaintainState(String maintainState) {
		this.maintainState = maintainState;
	}

	public String getIsOutLost() {
		return isOutLost;
	}

	public void setIsOutLost(String isOutLost) {
		this.isOutLost = isOutLost;
	}
	
}
