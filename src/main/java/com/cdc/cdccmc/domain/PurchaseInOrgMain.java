package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/** 
 * 采购入库单主表 T_PURCHASE_IN_ORG_MAIN
 * @author ZhuWen
 * @date 2018-02-02
 */
public class PurchaseInOrgMain {

	private String purchaseInOrgMainId; //入库单单号，生成规则同包装流转单单号规则相同
	private Integer inOrgNumber; //器具入库总数
	private String inOrgRemark; //入库备注
	private Integer printNumber; //打印次数
	private String consignorOrgId; //发货方组织ID
	private String consignorOrgName; //发货方组织名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private String createOrgId; //创建公司ID
	private String createOrgName; //创建公司名称
	
	public String getPurchaseInOrgMainId() {
		return purchaseInOrgMainId;
	}
	public void setPurchaseInOrgMainId(String purchaseInOrgMainId) {
		this.purchaseInOrgMainId = purchaseInOrgMainId;
	}
	public Integer getInOrgNumber() {
		return inOrgNumber;
	}
	public void setInOrgNumber(Integer inOrgNumber) {
		this.inOrgNumber = inOrgNumber;
	}
	public String getInOrgRemark() {
		return inOrgRemark;
	}
	public void setInOrgRemark(String inOrgRemark) {
		this.inOrgRemark = inOrgRemark;
	}
	public Integer getPrintNumber() {
		return printNumber;
	}
	public void setPrintNumber(Integer printNumber) {
		this.printNumber = printNumber;
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
	public String getConsignorOrgId() {
		return consignorOrgId;
	}
	public void setConsignorOrgId(String consignorOrgId) {
		this.consignorOrgId = consignorOrgId;
	}
	public String getConsignorOrgName() {
		return consignorOrgName;
	}
	public void setConsignorOrgName(String consignorOrgName) {
		this.consignorOrgName = consignorOrgName;
	}
	
}
