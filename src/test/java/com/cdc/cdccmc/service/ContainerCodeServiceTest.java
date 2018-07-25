package com.cdc.cdccmc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/** 
 * @author Clm
 * @date 2018-1-3
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContainerCodeServiceTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerCodeServiceTest.class); 
	
	@Autowired
	private ContainerCodeService containerCodeService;
	
	//测试在器具表添加相关的数据
	@Test
    public void testAddContainerCode() throws Exception {
		AjaxBean ajaxBean = AjaxBean.FAILURE();
		ContainerCode containerCode = new ContainerCode();
		containerCode.setContainerCode("6");
		containerCode.setContainerCodeType("6");
		containerCode.setCreateAccount("6");
		containerCode.setCreateRealName("6");
		//ajaxBean = containerCodeService.addContainerCode(ajaxBean, containerCode);
    }
}
