package com.cdc.cdccmc.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 索赔类别
 * @author ZhuWen
 * @date 2018-01-18
 */
public enum ClaimType {
	
	LESS("1", "实物少送"), MORE("2", "实物多送"), DESTROY("3", "损坏"), 
	INVENTORY_LOSS("4", "盘亏"),INVENTORY_PROFIT("5", "盘盈");

	// 成员变量
	private String claimTypeId;
	private String claimTypeName;

	// 构造方法
	private ClaimType(String claimTypeId, String claimTypeName) {
		this.claimTypeId = claimTypeId;
		this.claimTypeName = claimTypeName;
	}

	public static String getTypeName(String claimTypeId) {
		for (ClaimType t : ClaimType.values()) {
			if (t.getClaimTypeId().equals(claimTypeId)) {
				return t.claimTypeName;
			}
		}
		return null;
	}
	
	public static List<Map<String,Object>> listAll() {
		ClaimType[] claimType = ClaimType.values();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < claimType.length; i++) {
			Map<String,Object> map = new HashMap();
			map.put("claimTypeId", claimType[i].getClaimTypeId());
			map.put("claimTypeName", claimType[i].getClaimTypeName());
			list.add(map);
		}
		return list;
	}

	public String getClaimTypeId() {
		return claimTypeId;
	}

	public void setClaimTypeId(String claimTypeId) {
		this.claimTypeId = claimTypeId;
	}

	public String getClaimTypeName() {
		return claimTypeName;
	}

	public void setClaimTypeName(String claimTypeName) {
		this.claimTypeName = claimTypeName;
	}

}
