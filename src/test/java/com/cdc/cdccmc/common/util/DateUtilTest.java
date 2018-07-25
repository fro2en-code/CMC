package com.cdc.cdccmc.common.util;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.cdc.cdccmc.service.ContainerService;

public class DateUtilTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DateUtilTest.class);

	@Test
	public void 测试2() throws ParseException {
		
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		String fromDate = simpleFormat.format(DateUtil.parseToDate("2016-05-01 12:00", "yyyy-MM-dd hh:mm"));  
		String toDate = simpleFormat.format(DateUtil.parseToDate("2016-05-01 12:50", "yyyy-MM-dd hh:mm"));  
		long from = simpleFormat.parse(fromDate).getTime();  
		long to = simpleFormat.parse(toDate).getTime();  
		int minutes = (int) ((to - from)/(1000 * 60));  
		LOG.info("minutes==="+minutes); 
		LOG.info("format==="+simpleFormat.format(new Date(minutes))); 
	}

}
