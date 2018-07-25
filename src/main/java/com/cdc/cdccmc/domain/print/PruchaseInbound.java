package com.cdc.cdccmc.domain.print;


import java.util.List;
import java.util.Map;

/**
 * 发货单
 */
public class PruchaseInbound {

    private String consignorOrgId = "";
    private String title = "";                     // 标题
    private String barCode = "";                   // 条形码
    private String inBoundName = "";             // 收获仓库 create_org_name

    private String consignorOrgName = "";          // 送货方

    private String description = "";                  // 描述
    private String createRealName = "";               // 收货方确认
    private String createTime = "";                   // 日期&时间：
    private String  zhiliang= "";                     // 质量确认: 默认为空，不需要写值
    private String zltime="";                         // 日期&时间：默认为空，不需要写值

    private String orgId = "";                        //打印仓库Id
    private String peopleId = "";                     //打印人Id
    private String peopleName = "";                   //打印人姓名
    private String printTime = "";                    //打印时间
    private String orgName = "";                      //打印仓库名称

    /*
    *
    *  字典里的key
    *  code            包装代码:器具代码
    *  name;           包装名称:器具名称
    *  size;           尺寸
    *  planCount:      计划数量
    *  sendCount;      实发数量
    *  receiveCount:   实收数量
    *  remark:         备注
    *
    * */
    private List<Map<String, String>> details;

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

    public String getConsignorOrgId() {
        return consignorOrgId;
    }

    public void setConsignorOrgId(String consignorOrgId) {
        this.consignorOrgId = consignorOrgId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getInBoundName() {
        return inBoundName;
    }

    public void setInBoundName(String inBoundName) {
        this.inBoundName = inBoundName;
    }

    public String getConsignorOrgName() {
        return consignorOrgName;
    }

    public void setConsignorOrgName(String consignorOrgName) {
        this.consignorOrgName = consignorOrgName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateRealName() {
        return createRealName;
    }

    public void setCreateRealName(String createRealName) {
        this.createRealName = createRealName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getZhiliang() {
        return zhiliang;
    }

    public void setZhiliang(String zhiliang) {
        this.zhiliang = zhiliang;
    }

    public String getZltime() {
        return zltime;
    }

    public void setZltime(String zltime) {
        this.zltime = zltime;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public List<Map<String, String>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, String>> details) {
        this.details = details;
    }
}
