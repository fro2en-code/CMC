package com.cdc.cdccmc.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/** 
 * 
 * @author ZhuWen
 * @date 2018-01-08
 */
@RunWith(SpringRunner.class)
 @SpringBootTest
public class SystemOrgServiceTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemOrgServiceTest.class); 

	@Autowired
	private SystemOrgService systemOrgService;
	
	@Before
	public void before(){
		assertNotNull(systemOrgService);
	}

	@Test
	public void testListAllFiliale() {
		List<SystemOrg> result = systemOrgService.listAllFiliale("100");
		LOG.info(":::"+JSONObject.toJSONString(result));
	}

}
