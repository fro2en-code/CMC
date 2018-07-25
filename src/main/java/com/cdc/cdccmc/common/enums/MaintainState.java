package com.cdc.cdccmc.common.enums;
/** 
 * 维修状态。
 * @author ZhuWen
 * @date 2018-01-25
 */
public enum MaintainState {
	
	IN_ORG("1", "在库维修"), OUT_ORG("2", "出库维修"), FINISH("3", "维修完毕");

	// 成员变量
	private String code;
	private String state;

	// 构造方法
	private MaintainState(String code, String state) {
		this.code = code;
		this.state = state;
	}

	public static String getTypeName(String code) {
		for (MaintainState m : MaintainState.values()) {
			if (m.getCode().equals(code)) {
				return m.state;
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

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
