package com.cdc.cdccmc.service;

import java.sql.Timestamp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.LoginController;
import com.cdc.cdccmc.domain.container.Container;

import static org.junit.Assert.*;
/** 
 * 
 * @author ZhuWen
 * @date 2017-01-02
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContainerServiceTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerServiceTest.class); 

	@Autowired
	private ContainerService containerService;

	@Before
	public void before(){
		assertNotNull(containerService);
	}

	@Test
	public  void test111() throws Exception
	{
		int count = 50001;
		int forCount = count/5000;
		if(count % 5000>0)
		{
			forCount++;
		}
		System.out.println("---forCount="+forCount);
	}

	@Test
    public void testAddContainer() throws Exception {
		AjaxBean ajaxBean = AjaxBean.FAILURE();
		Container container = new Container();
		container.setContainerName("器具名称A");
		container.setEpcId("CFR227GY-0000149");
		container.setEpcType("CFR227GY");
		container.setPrintCode("203215BD-01685");
		container.setContainerTypeId("1");
		container.setContainerTypeName("器具名称A");
		container.setContainerCode("37712");
		container.setLastOrgId("100");
		//ajaxBean = containerService.addContainer(container);
    }
	
}
