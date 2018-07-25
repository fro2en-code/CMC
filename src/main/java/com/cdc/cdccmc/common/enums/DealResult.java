package com.cdc.cdccmc.common.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 包装流转单差异处理结果
 * @author Clm
 * @date 2018-01-29
 */
public enum DealResult {

	 UN_DISPOSE("1", "待处理"), EPC_DISPOSE("3", "EPC覆盖"),
	 INORG_DISPOSE("2", "收货入库"),  CLAIM_DISPOSE("4", "索赔"),  NO_DIFFERENCE("5", "无差异");
	
		// 成员变量
		private String differenceId;
		private String differenceName;

		// 构造方法
		private DealResult(String differenceId, String differenceName) {
			this.differenceId = differenceId;
			this.differenceName = differenceName;
		}
		
		public static String getDifferenceName(String differenceId) {
			for (DealResult dr : DealResult.values()) {
				if (dr.getDifferenceId().equals(differenceId)) {
					return dr.differenceName;
				}
			}
			return null;
		}
		
		public static List<Map<String,Object>> listAll() {
			DealResult[] differenceResult = DealResult.values();
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < differenceResult.length; i++) {
				Map<String,Object> map = new HashMap();
				map.put("differenceId", differenceResult[i].getDifferenceId());
				map.put("differenceName", differenceResult[i].getDifferenceName());
				list.add(map);
			}
			return list;
		}

		public String getDifferenceId() {
			return differenceId;
		}

		public void setDifferenceId(String differenceId) {
			this.differenceId = differenceId;
		}

		public String getDifferenceName() {
			return differenceName;
		}

		public void setDifferenceName(String differenceName) {
			this.differenceName = differenceName;
		}
}
