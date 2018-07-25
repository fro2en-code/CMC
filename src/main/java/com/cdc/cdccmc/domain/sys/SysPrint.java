package com.cdc.cdccmc.domain.sys;

public class SysPrint {
    private String orgId;
    private String orgName;
    private String printName;
    private String printCode;

    public String getPrintCode() {
        return printCode;
    }

    public void setPrintCode(String printCode) {
        this.printCode = printCode;
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

    public String getPrintName() {
        return printName;
    }

    public void setPrintName(String printName) {
        this.printName = printName;
    }

    @Override
    public String toString() {
        return "SysPrint{" +
                "orgId='" + orgId + '\'' +
                ", orgName='" + orgName + '\'' +
                ", printName='" + printName + '\'' +
                ", printCode='" + printCode + '\'' +
                '}';
    }
}