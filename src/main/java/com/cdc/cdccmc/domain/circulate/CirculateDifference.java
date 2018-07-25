package com.cdc.cdccmc.domain.circulate;

import java.sql.Timestamp;

/** 
 * 包装流转单差异表  T_CIRCULATE_DIFFERENCE
 * @author ZhuWen
 * @date 2017-12-28
 */
public class CirculateDifference {

	private String circulateDifferenceId; //主键ID
	private String orderCode; //包装流转单单号
	private String epcId; //EPC编号
	private String containerCode; //器具代码
	private String containerName; //器具名称
	private String containerTypeId; //器具类型ID
	private String containerTypeName; //器具类型名称
	private Integer differenceNumber; //差异数量
	private String differenceRemark; //差异备注
	private String differenceDealResult; //差异处理结果。1待处理 2EPC编号覆盖处理 3入库处理 4索赔
	private String coverageEpcId; //覆盖EPC编号
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName; //创建账号的姓名
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName;//修改账号的姓名
	private String createOrgId;//创建组织ID
	private String createOrgName;//创建组织名称
	private Integer version; //版本控制
	
	public String getCirculateDifferenceId() {
		return circulateDifferenceId;
	}
	public void setCirculateDifferenceId(String circulateDifferenceId) {
		this.circulateDifferenceId = circulateDifferenceId;
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
	public Integer getDifferenceNumber() {
		return differenceNumber;
	}
	public void setDifferenceNumber(Integer differenceNumber) {
		this.differenceNumber = differenceNumber;
	}
	public String getDifferenceRemark() {
		return differenceRemark;
	}
	public void setDifferenceRemark(String differenceRemark) {
		this.differenceRemark = differenceRemark;
	}
	public String getDifferenceDealResult() {
		return differenceDealResult;
	}
	public void setDifferenceDealResult(String differenceDealResult) {
		this.differenceDealResult = differenceDealResult;
	}
	public String getCoverageEpcId() {
		return coverageEpcId;
	}
	public void setCoverageEpcId(String coverageEpcId) {
		this.coverageEpcId = coverageEpcId;
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
	public Timestamp getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getModifyAccount() {
		return modifyAccount;
	}
	public void setModifyAccount(String modifyAccount) {
		this.modifyAccount = modifyAccount;
	}
	public String getModifyRealName() {
		return modifyRealName;
	}
	public void setModifyRealName(String modifyRealName) {
		this.modifyRealName = modifyRealName;
	}
	public String getCreateOrgId() {
		return createOrgId;
	}
	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}
	public String getCreateOrgName() {
		return createOrgName;
	}
	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
