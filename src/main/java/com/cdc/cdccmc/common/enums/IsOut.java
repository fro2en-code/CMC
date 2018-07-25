package com.cdc.cdccmc.common.enums;
/** 
 * 是否已出库，0已出库，1未出库
 * @author ZhuWen
 * @date 2018-01-18
 */
public enum IsOut {
	
	OUT_ORG("0", "已出库"), IN_ORG("1", "未出库");

	// 成员变量
	private String code;
	private String name;

	// 构造方法
	private IsOut(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getName(String name) {
		for (IsOut t : IsOut.values()) {
			if (t.getCode().equals(name)) {
				return t.name;
			}
		}
		return null;
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
