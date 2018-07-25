package com.cdc.cdccmc.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.LoginController;

import static org.junit.Assert.*;
/** 
 * 
 * @author ZhuWen
 * @date 2017-12-29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogServiceTest {
	private static final org.slf4j.Logger LOG= org.slf4j.LoggerFactory.getLogger(LogServiceTest.class); 

	@Autowired
	private LogService logService;
	
	@Value("#{sql['containerInsert']}")
	private String containerInsert;

	@Test
    public void testListLogAccount() throws Exception {
		LOG.info("containerInsert==" + containerInsert);
		Paging paging = new Paging();
		paging.setCurrentPage(1);
		paging.setPageSize(2);
		//paging = logService.listLogAccount(paging , "", "","","");
		Assert.assertTrue(paging.getData() != null);
		LOG.info("finished in OK!!! data==" + JSONObject.toJSONString(paging.getData()));
    }
	
}
