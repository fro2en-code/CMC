package com.cdc.cdccmc.common.enums;
/** 
 * 组织类别
 * @author ZhuWen
 * @date 2018-01-18
 */
public enum OrgEnum {
	
	HEADER_COMPANY("100", "中久物流有限公司"), 
	VIRTUAL_SCRAP_ORG("10", "虚拟报废库"), 
	VIRTUAL_LOST_ORG("20", "虚拟丢失库"), 
	VIRTUAL_SELL_ORG("30", "虚拟销售库");

	// 成员变量
	private String orgId; //公司ID
	private String orgName; //公司名称

	// 构造方法
	private OrgEnum(String orgId, String orgName) {
		this.orgId = orgId;
		this.orgName = orgName;
	}
	
	public String getOrgName(String orgId) {
		for (OrgEnum o : OrgEnum.values()) {
			if (o.getOrgId().equals(orgId)) {
				return o.orgName;
			}
		}
		return null;
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

}
