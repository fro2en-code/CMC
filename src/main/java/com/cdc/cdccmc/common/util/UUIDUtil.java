package com.cdc.cdccmc.common.util;

import java.util.UUID;

/**
 * uuid工具类
 * 
 * @author Clm
 * @date 2018-01-05
 */
public class UUIDUtil {
	private UUIDUtil() {

	}

	public static String creatUUID() {
		return UUID.randomUUID().toString().replace("-", "").toLowerCase();
	}
}
