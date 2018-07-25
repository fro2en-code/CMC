package com.cdc.cdccmc.domain.print;

import java.util.List;
import java.util.Map;

/**
 * 包装流转单
 */
public class PritCirculateOrder {

    private String consignorOrgId = "";
    private String title = "";                     // 标题
    private String barCode = "";                   // 条形码 order_code
    private String sendLocation = "";             // 发货地点consignor_org_name
    private String receiveLocation = "";          // 收货地点target_org_type_name
    private String transactionType = "";          // 交易类型 trade_type_name

    private String wsSendFlag = "";                // 发货车间
    private String wsSendCode = "";                // 发货道口代码
    private String wsReceiveFlag = "";             // 车间
    private String wsReceiveCode = "";             // 道口代码

    private String cmcSendFlag = "";                // CMC
    private String cmcSendCode = "";                // 道口代码
    private String cmcReceiveFlag = "";            // CMC
    private String cmcReceiveCode = "";            // 道口代码

    private String dcSendFlag = "";                 // DC
    private String dcSendCode = "";                 // 道口代码
    private String dcReceiveFlag = "";              // DC
    private String dcReceiveCode = "";              // 道口代码

    private String providerSendFlag = "";           // 供应商
    private String providerSendCode = "";           // 道口代码
    private String providerReceiveFlag = "";       // 供应商
    private String providerReceiveCode = "";       // 道口代码

    private String sendByShopFlag = "";             // 维修工厂
    private String receiveByShopFlag = "";          // 维修工厂

    private String outByWarehouse = "";              // 仓库调拨
    private String outByRepair = "";                  // 维修转移
    private String outByReturn = "";                  // 客户退回
    private String outByModify = "";                  // 料架修改出库
    private String outByScrap = "";                   // 报废出库
    private String outByNewPackage = "";             // 新包装入库

    private String otherSend = "";                    // 其他
    private String otherReceive = "";                 // 其他
    private String otherOut = "";                      // 其他

    private String description = "";                  // 特别描述
    private String transportCompany = "";            // 运输公司
    private String confirm = "";                       // 司机确认
    private String carNo = "";                         // 车号
    private String contact = "";                       // 司机联系方式
    private String senderConfirm = "";                // 发货方确认
    private String senderConfirmDate = "";            // 发货方确认日期时间
    private String receiverConfirm = "";               // 收货方确认
    private String receiverConfirmDate = "";          // 收货方确认日期时间
    private String billNumber = "";                     // 有关单号
    private String orgId = "";                          // 仓库ID
    private String peopleId = "";                     //打印人Id
    private String peopleName = "";                   //打印人姓名
    private String printTime = "";                    //打印时间
    private String orgName = "";                      //打印仓库名称
    /*
    *
    *  字典里的key
    *  id:             序号
    *  code            包装代码
    *  name;           包装名称
    *  size;           尺寸
    *  planCount:      计划数量
    *  sendCount;      实发数量
    *  receiveCount:   实收数量
    *  remark:         备注
    *
    * */
    private List<Map<String, Object>> details;


    public String getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(String peopleId) {
        this.peopleId = peopleId;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSendLocation() {
        return sendLocation;
    }

    public void setSendLocation(String sendLocation) {
        this.sendLocation = sendLocation;
    }

    public String getReceiveLocation() {
        return receiveLocation;
    }

    public void setReceiveLocation(String receiveLocation) {
        this.receiveLocation = receiveLocation;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getWsSendFlag() {
        return wsSendFlag;
    }

    public void setWsSendFlag(String wsSendFlag) {
        this.wsSendFlag = wsSendFlag;
    }

    public String getWsSendCode() {
        return wsSendCode;
    }

    public void setWsSendCode(String wsSendCode) {
        this.wsSendCode = wsSendCode;
    }

    public String getWsReceiveFlag() {
        return wsReceiveFlag;
    }

    public void setWsReceiveFlag(String wsReceiveFlag) {
        this.wsReceiveFlag = wsReceiveFlag;
    }

    public String getWsReceiveCode() {
        return wsReceiveCode;
    }

    public void setWsReceiveCode(String wsReceiveCode) {
        this.wsReceiveCode = wsReceiveCode;
    }

    public String getCmcSendFlag() {
        return cmcSendFlag;
    }

    public void setCmcSendFlag(String cmcSendFlag) {
        this.cmcSendFlag = cmcSendFlag;
    }

    public String getCmcSendCode() {
        return cmcSendCode;
    }

    public void setCmcSendCode(String cmcSendCode) {
        this.cmcSendCode = cmcSendCode;
    }

    public String getCmcReceiveFlag() {
        return cmcReceiveFlag;
    }

    public void setCmcReceiveFlag(String cmcReceiveFlag) {
        this.cmcReceiveFlag = cmcReceiveFlag;
    }

    public String getCmcReceiveCode() {
        return cmcReceiveCode;
    }

    public void setCmcReceiveCode(String cmcReceiveCode) {
        this.cmcReceiveCode = cmcReceiveCode;
    }

    public String getDcSendFlag() {
        return dcSendFlag;
    }

    public void setDcSendFlag(String dcSendFlag) {
        this.dcSendFlag = dcSendFlag;
    }

    public String getDcSendCode() {
        return dcSendCode;
    }

    public void setDcSendCode(String dcSendCode) {
        this.dcSendCode = dcSendCode;
    }

    public String getDcReceiveFlag() {
        return dcReceiveFlag;
    }

    public void setDcReceiveFlag(String dcReceiveFlag) {
        this.dcReceiveFlag = dcReceiveFlag;
    }

    public String getDcReceiveCode() {
        return dcReceiveCode;
    }

    public void setDcReceiveCode(String dcReceiveCode) {
        this.dcReceiveCode = dcReceiveCode;
    }

    public String getProviderSendFlag() {
        return providerSendFlag;
    }

    public void setProviderSendFlag(String providerSendFlag) {
        this.providerSendFlag = providerSendFlag;
    }

    public String getProviderSendCode() {
        return providerSendCode;
    }

    public void setProviderSendCode(String providerSendCode) {
        this.providerSendCode = providerSendCode;
    }

    public String getProviderReceiveFlag() {
        return providerReceiveFlag;
    }

    public void setProviderReceiveFlag(String providerReceiveFlag) {
        this.providerReceiveFlag = providerReceiveFlag;
    }

    public String getProviderReceiveCode() {
        return providerReceiveCode;
    }

    public void setProviderReceiveCode(String providerReceiveCode) {
        this.providerReceiveCode = providerReceiveCode;
    }

    public String getSendByShopFlag() {
        return sendByShopFlag;
    }

    public void setSendByShopFlag(String sendByShopFlag) {
        this.sendByShopFlag = sendByShopFlag;
    }

    public String getReceiveByShopFlag() {
        return receiveByShopFlag;
    }

    public void setReceiveByShopFlag(String receiveByShopFlag) {
        this.receiveByShopFlag = receiveByShopFlag;
    }

    public String getOutByWarehouse() {
        return outByWarehouse;
    }

    public void setOutByWarehouse(String outByWarehouse) {
        this.outByWarehouse = outByWarehouse;
    }

    public String getOutByRepair() {
        return outByRepair;
    }

    public void setOutByRepair(String outByRepair) {
        this.outByRepair = outByRepair;
    }

    public String getOutByReturn() {
        return outByReturn;
    }

    public void setOutByReturn(String outByReturn) {
        this.outByReturn = outByReturn;
    }

    public String getOutByModify() {
        return outByModify;
    }

    public void setOutByModify(String outByModify) {
        this.outByModify = outByModify;
    }

    public String getOutByScrap() {
        return outByScrap;
    }

    public void setOutByScrap(String outByScrap) {
        this.outByScrap = outByScrap;
    }

    public String getOutByNewPackage() {
        return outByNewPackage;
    }

    public void setOutByNewPackage(String outByNewPackage) {
        this.outByNewPackage = outByNewPackage;
    }

    public String getOtherSend() {
        return otherSend;
    }

    public void setOtherSend(String otherSend) {
        this.otherSend = otherSend;
    }

    public String getOtherReceive() {
        return otherReceive;
    }

    public void setOtherReceive(String otherReceive) {
        this.otherReceive = otherReceive;
    }

    public String getOtherOut() {
        return otherOut;
    }

    public void setOtherOut(String otherOut) {
        this.otherOut = otherOut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String transportCompany) {
        this.transportCompany = transportCompany;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getCarNo() {
        return carNo;
    }

    public void setCarNo(String carNo) {
        this.carNo = carNo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSenderConfirm() {
        return senderConfirm;
    }

    public void setSenderConfirm(String senderConfirm) {
        this.senderConfirm = senderConfirm;
    }

    public String getSenderConfirmDate() {
        return senderConfirmDate;
    }

    public void setSenderConfirmDate(String senderConfirmDate) {
        this.senderConfirmDate = senderConfirmDate;
    }

    public String getReceiverConfirm() {
        return receiverConfirm;
    }

    public void setReceiverConfirm(String receiverConfirm) {
        this.receiverConfirm = receiverConfirm;
    }

    public String getReceiverConfirmDate() {
        return receiverConfirmDate;
    }

    public void setReceiverConfirmDate(String receiverConfirmDate) {
        this.receiverConfirmDate = receiverConfirmDate;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public List<Map<String, Object>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, Object>> details) {
        this.details = details;
    }

    public String getConsignorOrgId() {
        return consignorOrgId;
    }

    public void setConsignorOrgId(String consignorOrgId) {
        this.consignorOrgId = consignorOrgId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
}
