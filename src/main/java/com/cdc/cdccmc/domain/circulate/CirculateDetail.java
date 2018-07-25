package com.cdc.cdccmc.domain.circulate;

import java.sql.Timestamp;

/** 
 * 包装流转单明细表  T_CIRCULATE_DETAIL
 * @author ZhuWen
 * @date 2017-12-28
 */
public class CirculateDetail {

	private String circulateDetailId; //主键ID
	private String orderCode; //包装流转单单号
	private String epcId; //EPC编号
	private String sequenceNo; //序号
	private String containerCode; //器具代码
	private String containerName; //器具名称
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private String containerSpecification; //规格
	private Integer planNumber; //计划数量
	private Integer sendNumber; //实发数量
	private Integer receiveNumber; //实收数量。机器扫描实收数量为1，或者人工确认实收数量为1，实收数量都为1
	private Timestamp receiveTime; //收货时间
	private String receiveOrgId; //收货组织ID
	private String receiveOrgName; //收货组织名称
	private String receiveAccount; //收货帐号
	private String receiveRealName; //收货账号的姓名
	private String dealResult; //差异处理结果。1EPC编号覆盖处理 2入库处理 3索赔 4索赔 5无差异   参考DifferenceResult.java
	private String remark; //备注
	private String dealCoverageEpcId; //进行“EPC覆盖”差异处理后，新的覆盖EPC编号
	private String dealAccount;//差异处理人账号
	private String dealRealName;//差异处理人的姓名
	private Timestamp dealTime;//差异处理时间
	private String dealOrgId;//差异处理操作组织ID
	private String dealOrgName;//差异处理操作组织名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	
	
	public String getCirculateDetailId() {
		return circulateDetailId;
	}
	public void setCirculateDetailId(String circulateDetailId) {
		this.circulateDetailId = circulateDetailId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getEpcId() {
		return epcId;
	}
	public void setEpcId(String epcId) {
		this.epcId = epcId;
	}
	public String getSequenceNo() {
		return sequenceNo;
	}
	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}
	public String getContainerCode() {
		return containerCode;
	}
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getContainerTypeId() {
		return containerTypeId;
	}
	public void setContainerTypeId(String containerTypeId) {
		this.containerTypeId = containerTypeId;
	}
	public String getContainerTypeName() {
		return containerTypeName;
	}
	public void setContainerTypeName(String containerTypeName) {
		this.containerTypeName = containerTypeName;
	}
	public String getContainerSpecification() {
		return containerSpecification;
	}
	public void setContainerSpecification(String containerSpecification) {
		this.containerSpecification = containerSpecification;
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
	public Timestamp getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Timestamp receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getReceiveOrgId() {
		return receiveOrgId;
	}
	public void setReceiveOrgId(String receiveOrgId) {
		this.receiveOrgId = receiveOrgId;
	}
	public String getReceiveOrgName() {
		return receiveOrgName;
	}
	public void setReceiveOrgName(String receiveOrgName) {
		this.receiveOrgName = receiveOrgName;
	}
	public String getReceiveAccount() {
		return receiveAccount;
	}
	public void setReceiveAccount(String receiveAccount) {
		this.receiveAccount = receiveAccount;
	}
	public String getReceiveRealName() {
		return receiveRealName;
	}
	public void setReceiveRealName(String receiveRealName) {
		this.receiveRealName = receiveRealName;
	}
	public String getDealResult() {
		return dealResult;
	}
	public void setDealResult(String dealResult) {
		this.dealResult = dealResult;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getDealCoverageEpcId() {
		return dealCoverageEpcId;
	}
	public void setDealCoverageEpcId(String dealCoverageEpcId) {
		this.dealCoverageEpcId = dealCoverageEpcId;
	}
	public String getDealAccount() {
		return dealAccount;
	}
	public void setDealAccount(String dealAccount) {
		this.dealAccount = dealAccount;
	}
	public String getDealRealName() {
		return dealRealName;
	}
	public void setDealRealName(String dealRealName) {
		this.dealRealName = dealRealName;
	}
	public Timestamp getDealTime() {
		return dealTime;
	}
	public void setDealTime(Timestamp dealTime) {
		this.dealTime = dealTime;
	}
	public String getDealOrgId() {
		return dealOrgId;
	}
	public void setDealOrgId(String dealOrgId) {
		this.dealOrgId = dealOrgId;
	}
	public String getDealOrgName() {
		return dealOrgName;
	}
	public void setDealOrgName(String dealOrgName) {
		this.dealOrgName = dealOrgName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCreateAccount() {
		return createAccount;
	}
	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}
	public String getCreateRealName() {
		return createRealName;
	}
	public void setCreateRealName(String createRealName) {
		this.createRealName = createRealName;
	}

	@Override
	public String toString() {
		return "CirculateDetail{" +
				"circulateDetailId='" + circulateDetailId + '\'' +
				", orderCode='" + orderCode + '\'' +
				", epcId='" + epcId + '\'' +
				", sequenceNo='" + sequenceNo + '\'' +
				", containerCode='" + containerCode + '\'' +
				", containerName='" + containerName + '\'' +
				", containerTypeId='" + containerTypeId + '\'' +
				", containerTypeName='" + containerTypeName + '\'' +
				", containerSpecification='" + containerSpecification + '\'' +
				", planNumber=" + planNumber +
				", sendNumber=" + sendNumber +
				", receiveNumber=" + receiveNumber +
				", receiveTime=" + receiveTime +
				", receiveOrgId='" + receiveOrgId + '\'' +
				", receiveOrgName='" + receiveOrgName + '\'' +
				", receiveAccount='" + receiveAccount + '\'' +
				", receiveRealName='" + receiveRealName + '\'' +
				", dealResult='" + dealResult + '\'' +
				", remark='" + remark + '\'' +
				", dealCoverageEpcId='" + dealCoverageEpcId + '\'' +
				", dealAccount='" + dealAccount + '\'' +
				", dealRealName='" + dealRealName + '\'' +
				", dealTime=" + dealTime +
				", dealOrgId='" + dealOrgId + '\'' +
				", dealOrgName='" + dealOrgName + '\'' +
				", createTime=" + createTime +
				", createAccount='" + createAccount + '\'' +
				", createRealName='" + createRealName + '\'' +
				'}';
	}
}
