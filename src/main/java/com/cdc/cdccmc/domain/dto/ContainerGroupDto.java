package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.container.ContainerGroup;
/**
 * 组托Dto
 * @author zhuwen
 * 2018-05-18
 */
public class ContainerGroupDto extends ContainerGroup {

	private String orgId; // 组织ID
	private String orgName; // 组织名称
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
