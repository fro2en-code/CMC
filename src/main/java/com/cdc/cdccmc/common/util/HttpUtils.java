package com.cdc.cdccmc.common.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class HttpUtils {
	/**
	 * 对象转换成url参数字符串
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static String parseURLPair(Object o) throws Exception{  
        Class<? extends Object> c = o.getClass();  
        Field[] fields = c.getDeclaredFields();  
        Map<String, Object> map = new TreeMap<String, Object>();  
        for (Field field : fields) {  
            field.setAccessible(true);  
            String name = field.getName();  
            Object value = field.get(o);  
            if(value != null)  
                map.put(name, value);  
        }  
        Set<Entry<String, Object>> set = map.entrySet();  
        Iterator<Entry<String, Object>> it = set.iterator();  
        StringBuffer sb = new StringBuffer();  
        while (it.hasNext()) {  
            Entry<String, Object> e = it.next();  
            sb.append(e.getKey()).append("=").append(e.getValue()).append("&");  
        }  
        return sb.deleteCharAt(sb.length()-1).toString();  
    }  
}
