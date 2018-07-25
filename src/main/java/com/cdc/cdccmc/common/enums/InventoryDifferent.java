package com.cdc.cdccmc.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 盘点差异 t_inventory_detail表 is_have_different字段
 * 0未知 1 没有差异 2有区域差异 3 器具未扫描到 4 区域内扫描到新器具 5 已处理
 * 
 * @author ShiChuang
 * @date 2018-02-08
 */
public enum InventoryDifferent {
	UNKNOWN("0", "未知"), NOT_DIFFERENT("1", "没有差异"), AREA_DIFFERENT("2", "区域差异"), 
	NOT_SCAN("3", "未扫描到 "),NEW_SCAN("4", "扫描到新器具"),DONE("5", "已处理");

	// 成员变量
	private String code;
	private String name;

	/**
	 * 构造方法
	 * @param code
	 * @param name
	 */
	private InventoryDifferent(String code, String name) {
		this.code = code;
		this.name = name;
	}
	/**
	 * 根据id获取name
	 * @param code
	 * @return
	 */
	public static String getName(String code) {
		for (InventoryDifferent t : InventoryDifferent.values()) {
			if (t.getCode().equals(code)) {
				return t.name;
			}
		}
		return null;
	}
	/**
	 * 获取列表
	 * @return
	 */
	public static List<Map<String,Object>> listAll() {
		InventoryDifferent[] arr = InventoryDifferent.values();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < arr.length; i++) {
			Map<String,Object> map = new HashMap();
			map.put("code", arr[i].getCode());
			map.put("name", arr[i].getName());
			list.add(map);
		}
		return list;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
