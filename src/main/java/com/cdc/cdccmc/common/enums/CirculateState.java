package com.cdc.cdccmc.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流转类别，适用于包装流转单，也适用于流转历史记录
 * 
 * @author ZhuWen
 * @date 2018-01-17
 */
public enum CirculateState {

	IN_ORG("1", "流转入库"), ON_WAY("2", "在途"), OUT_ORG("3", "流转出库"), 
	MAINTAIN("4", "维修出库"),LEASE("5", "租赁出库"), SCRAP("6", "报废出库"), 
	CLAIM("7", "索赔"),SELL("9", "销售出库")
	,ON_ORG("10", "在库"),PURCHASE_IN_ORG("11", "采购入库");

	// 成员变量
	private String circulate;
	private String code;

	// 构造方法
	private CirculateState(String code, String circulate) {
		this.code = code;
		this.circulate = circulate;
	}

	public static String getCirculate(String code) {
		for (CirculateState s : CirculateState.values()) {
			if (s.getCode().equals(code)) {
				return s.circulate;
			}
		}
		return null;
	}
	
	public static List<Map<String,Object>> listAll() {
		CirculateState[] circulateState = CirculateState.values();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < circulateState.length; i++) {
			Map<String,Object> map = new HashMap();
			map.put("circulate", circulateState[i].getCirculate());
			map.put("code", circulateState[i].getCode());
			list.add(map);
		}
		return list;
	}

	public String getCirculate() {
		return circulate;
	}

	public void setCirculate(String circulate) {
		this.circulate = circulate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
