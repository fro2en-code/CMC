package com.cdc.cdccmc.domain.dto;

import com.alibaba.fastjson.JSONObject;

/**
 * 包装流转单页面，对器具代码分类进行统计epc个数
 * @author ZhuWen
 * @date 2018年1月27日
 */
public class EpcSumDto {

	private String circulateDetailId; //id
	private String containerName; //器具名称
	private String containerCode; //器具代码
	private String containerSpecification;//尺寸
	private String specialDescription; //特别描述
	private Integer planNumber; //计划数量（单种器具代码）
	private Integer sendNumber; //实发数量（单种器具代码）
	private Integer receiveNumber; //实收数量（单种器具代码）
	private Integer differentNumber; //差异数（单种器具代码） = 实收数量 - 实发数量

	private Integer allSendNumber; //流转单全部实发数量
	private Integer allReceiveNumber;//流转单全部实收数量
	private Integer allDifferentNumber;//流转单全部差异数 = 流转单全部实收数量 - 流转单全部实发数量

	public String getSpecialDescription() {
		return specialDescription;
	}
	public void setSpecialDescription(String specialDescription) {
		this.specialDescription = specialDescription;
	}
	public Integer getAllDifferentNumber() {
		return allDifferentNumber;
	}
	public void setAllDifferentNumber(Integer allDifferentNumber) {
		this.allDifferentNumber = allDifferentNumber;
	}
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

	public String getCirculateDetailId() {
		return circulateDetailId;
	}

	public void setCirculateDetailId(String circulateDetailId) {
		this.circulateDetailId = circulateDetailId;
	}

	public Integer getPlanNumber() {
		return planNumber;
	}
	public void setPlanNumber(Integer planNumber) {
		this.planNumber = planNumber;
	}
	public Integer getSendNumber() {
		return sendNumber;
	}
	public void setSendNumber(Integer sendNumber) {
		this.sendNumber = sendNumber;
	}
	public Integer getReceiveNumber() {
		return receiveNumber;
	}
	public void setReceiveNumber(Integer receiveNumber) {
		this.receiveNumber = receiveNumber;
	}
	public Integer getDifferentNumber() {
		return differentNumber;
	}
	public void setDifferentNumber(Integer differentNumber) {
		this.differentNumber = differentNumber;
	}
	public Integer getAllSendNumber() {
		return allSendNumber;
	}
	public void setAllSendNumber(Integer allSendNumber) {
		this.allSendNumber = allSendNumber;
	}
	public Integer getAllReceiveNumber() {
		return allReceiveNumber;
	}
	public void setAllReceiveNumber(Integer allReceiveNumber) {
		this.allReceiveNumber = allReceiveNumber;
	}
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
