package com.cdc.cdccmc.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 组织类别
 * @author ZhuWen
 * @date 2018-01-18
 */
public enum OrgType {
	
	COMPANY("1", "公司"), BUSINESS_CENTER("2", "业务中心"), WAREHOUSE("3", "仓库"), 
	CMC("4", "CMC"),COA("5", "COA"), DC("6", "DC"), 
	SUPPLIER("7", "供应商"), WORKSHOP("8", "车间"),MAINTAIN_FACTORY("9", "维修工厂")
	,VIRTUAL_FACTORY("10", "虚拟仓库")
	,OTHER("0", "其它");

	// 成员变量
	private String typeId;
	private String typeName;

	// 构造方法
	private OrgType(String typeId, String typeName) {
		this.typeId = typeId;
		this.typeName = typeName;
	}
	
	public static List<Map<String,Object>> listAll() {
		OrgType[] type = OrgType.values();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < type.length; i++) {
			Map<String,Object> map = new HashMap();
			map.put("typeId", type[i].getTypeId());
			map.put("typeName", type[i].getTypeName());
			list.add(map);
		}
		return list;
	}

	public static String getTypeName(String typeId) {
		for (OrgType t : OrgType.values()) {
			if (t.getTypeId().equals(typeId)) {
				return t.typeName;
			}
		}
		return null;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	

}
