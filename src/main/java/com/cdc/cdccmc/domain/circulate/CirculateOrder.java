package com.cdc.cdccmc.domain.circulate;

import java.sql.Timestamp;

/**
 * 包装流转单表 T_CIRCULATE_ORDER
 * 
 * @author ZhuWen
 * @date 2017-12-28
 */
public class CirculateOrder {

	private String orderCode; // 包装流转单单号，生成规则：仓库代码+日期年月日+四位流水号，例子：CMCSP201712250001
	private Integer isInvalid; //流转单是否作废。0正常，1作废
	private String isManualOrder; //是否为手工流转单。0普通流转单，有epc器具信息, 1手工流转单，没有epc只有器具代码信息
	private String consignorOrgId; // 发货组织ID
	private String consignorOrgName; // 发货组织名称
	private String consignorOrgTypeId; // 发货组织类别ID
	private String consignorOrgTypeName; // 发货组织类别名称
	private String consignorWayOut;// 发货方，道口代码
	private String targetOrgId; // 收货组织ID(配送目的地:xx仓库)
	private String targetOrgName; // 收货组织名称
	private String targetOrgTypeId; // 收货组织类别ID
	private String targetOrgTypeName; // 收货组织类别名称
	private String targetWayOut;// '收货方，道口代码'
	private String tradeTypeCode; // 交易类型code 即流转类型，具体查看CirculateState.java'
	private String tradeTypeName; // 交易类型名称 比如维修、流转、销售、报废，具体查看CirculateState.java',
	private String specialDescription; // 特别描述
	private String remark; // 入库备注
	private String shipperId; // 承运商ID
	private String shipperName; // 承运商名称
	private String carNo; // 车牌号
	private String doorAccount; // 流转单绑定门型，门型设备账号
	private String doorRealName; // 流转单绑定门型，门型设备的名称
	private Timestamp carArriveTime; //车辆到达时间
	private Timestamp carLeaveTime; //车辆离开时间
	private Timestamp loadingEndTime; //装货完毕时间
	private Timestamp printOrderTime; //首次打印流转单时间;
	private String isReceive; // 收货状态。是否已收货。0未收货，1已部分收货，2已全部收货
	private String isManualReceive;// 人工收货状态 0未收货，1已人工收货
	private String isCirculateDetailReceive; //是否为普通单人工输入实收数量，如果是那么T_CIRCULATE_DETAIL_RECEIVE表就有这个流转单号的收货明细。0否，1是
	private String differenceRemark; //差异备注
	private String consignorAccount; // 发货人
	private String consignorRealName;// 发货人的姓名
	private Timestamp consignorTime; // 发货日期
	private String targetAccount; // 收货人
	private String targetRealName;// 收货人的姓名
	private Timestamp targetTime; // 收货日期
	private String driverName; // 司机姓名
	private String driverPhone; // 司机联系方式
	private Integer printNumber; // 打印次数
	private Timestamp createTime; // 创建时间
	private String createAccount; // 创建账号
	private String createRealName;// 创建账号的姓名
	private Timestamp modifyTime; // 修改时间
	private String modifyAccount; // 修改账号
	private String modifyRealName;// 修改账号的姓名
	private Integer version; // 版本控制

	/**
	 * 是否为普通单人工输入实收数量，如果是那么T_CIRCULATE_DETAIL_RECEIVE表就有这个流转单号的收货明细。0否，1是
	 * @return
	 */
	public String getIsCirculateDetailReceive() {
		return isCirculateDetailReceive;
	}

	/**
	 * 是否为普通单人工输入实收数量，如果是那么T_CIRCULATE_DETAIL_RECEIVE表就有这个流转单号的收货明细。0否，1是
	 * @param isCirculateDetailReceive
	 */
	public void setIsCirculateDetailReceive(String isCirculateDetailReceive) {
		this.isCirculateDetailReceive = isCirculateDetailReceive;
	}

	/**
	 * 流转单是否作废。0正常，1作废
	 * @return
	 */
	public Integer getIsInvalid() {
		return isInvalid;
	}

	/**
	 * 流转单是否作废。0正常，1作废
	 * @param isInvalid
	 */
	public void setIsInvalid(Integer isInvalid) {
		this.isInvalid = isInvalid;
	}

	public String getDoorAccount() {
		return doorAccount;
	}

	public void setDoorAccount(String doorAccount) {
		this.doorAccount = doorAccount;
	}
	/**
	 * 是否为手工流转单。0普通流转单，有epc器具信息, 1手工流转单，没有epc只有器具代码信息
	 * @return
	 */
	public String getIsManualOrder() {
		return isManualOrder;
	}

	/**
	 * 是否为手工流转单。0普通流转单，有epc器具信息, 1手工流转单，没有epc只有器具代码信息
	 * @param isManualOrder
	 */
	public void setIsManualOrder(String isManualOrder) {
		this.isManualOrder = isManualOrder;
	}

	public String getDoorRealName() {
		return doorRealName;
	}

	public void setDoorRealName(String doorRealName) {
		this.doorRealName = doorRealName;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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

	public String getConsignorOrgTypeId() {
		return consignorOrgTypeId;
	}

	public void setConsignorOrgTypeId(String consignorOrgTypeId) {
		this.consignorOrgTypeId = consignorOrgTypeId;
	}

	public String getConsignorOrgTypeName() {
		return consignorOrgTypeName;
	}

	public void setConsignorOrgTypeName(String consignorOrgTypeName) {
		this.consignorOrgTypeName = consignorOrgTypeName;
	}

	public String getTargetOrgId() {
		return targetOrgId;
	}

	public void setTargetOrgId(String targetOrgId) {
		this.targetOrgId = targetOrgId;
	}

	public String getTargetOrgName() {
		return targetOrgName;
	}

	public void setTargetOrgName(String targetOrgName) {
		this.targetOrgName = targetOrgName;
	}

	public String getTargetOrgTypeId() {
		return targetOrgTypeId;
	}

	public void setTargetOrgTypeId(String targetOrgTypeId) {
		this.targetOrgTypeId = targetOrgTypeId;
	}

	public String getTargetOrgTypeName() {
		return targetOrgTypeName;
	}

	public void setTargetOrgTypeName(String targetOrgTypeName) {
		this.targetOrgTypeName = targetOrgTypeName;
	}

	public String getTradeTypeCode() {
		return tradeTypeCode;
	}

	public void setTradeTypeCode(String tradeTypeCode) {
		this.tradeTypeCode = tradeTypeCode;
	}

	public String getTradeTypeName() {
		return tradeTypeName;
	}

	public void setTradeTypeName(String tradeTypeName) {
		this.tradeTypeName = tradeTypeName;
	}

	public String getSpecialDescription() {
		return specialDescription;
	}

	public void setSpecialDescription(String specialDescription) {
		this.specialDescription = specialDescription;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShipperId() {
		return shipperId;
	}

	public void setShipperId(String shipperId) {
		this.shipperId = shipperId;
	}

	public String getShipperName() {
		return shipperName;
	}

	public void setShipperName(String shipperName) {
		this.shipperName = shipperName;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public String getConsignorAccount() {
		return consignorAccount;
	}

	public void setConsignorAccount(String consignorAccount) {
		this.consignorAccount = consignorAccount;
	}

	public String getConsignorRealName() {
		return consignorRealName;
	}

	public void setConsignorRealName(String consignorRealName) {
		this.consignorRealName = consignorRealName;
	}

	public Timestamp getConsignorTime() {
		return consignorTime;
	}

	public void setConsignorTime(Timestamp consignorTime) {
		this.consignorTime = consignorTime;
	}

	public String getTargetAccount() {
		return targetAccount;
	}

	public void setTargetAccount(String targetAccount) {
		this.targetAccount = targetAccount;
	}

	public String getTargetRealName() {
		return targetRealName;
	}

	public void setTargetRealName(String targetRealName) {
		this.targetRealName = targetRealName;
	}

	public Timestamp getTargetTime() {
		return targetTime;
	}

	public void setTargetTime(Timestamp targetTime) {
		this.targetTime = targetTime;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverPhone() {
		return driverPhone;
	}

	public void setDriverPhone(String driverPhone) {
		this.driverPhone = driverPhone;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getConsignorWayOut() {
		return consignorWayOut;
	}

	public void setConsignorWayOut(String consignorWayOut) {
		this.consignorWayOut = consignorWayOut;
	}

	public String getTargetWayOut() {
		return targetWayOut;
	}

	public void setTargetWayOut(String targetWayOut) {
		this.targetWayOut = targetWayOut;
	}

	public String getIsReceive() {
		return isReceive;
	}

	public void setIsReceive(String isReceive) {
		this.isReceive = isReceive;
	}

	public String getIsManualReceive() {
		return isManualReceive;
	}

	public void setIsManualReceive(String isManualReceive) {
		this.isManualReceive = isManualReceive;
	}

	public Timestamp getCarArriveTime() {
		return carArriveTime;
	}

	public void setCarArriveTime(Timestamp carArriveTime) {
		this.carArriveTime = carArriveTime;
	}

	public Timestamp getCarLeaveTime() {
		return carLeaveTime;
	}

	public void setCarLeaveTime(Timestamp carLeaveTime) {
		this.carLeaveTime = carLeaveTime;
	}

	public Timestamp getLoadingEndTime() {
		return loadingEndTime;
	}

	public void setLoadingEndTime(Timestamp loadingEndTime) {
		this.loadingEndTime = loadingEndTime;
	}

	public Timestamp getPrintOrderTime() {
		return printOrderTime;
	}

	public void setPrintOrderTime(Timestamp printOrderTime) {
		this.printOrderTime = printOrderTime;
	}

	public String getDifferenceRemark() {
		return differenceRemark;
	}

	public void setDifferenceRemark(String differenceRemark) {
		this.differenceRemark = differenceRemark;
	}
}
