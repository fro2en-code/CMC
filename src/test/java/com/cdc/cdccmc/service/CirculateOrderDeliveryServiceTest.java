package com.cdc.cdccmc.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.dto.CirculateDto;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.service.basic.CarService;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CirculateOrderDeliveryServiceTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateOrderDeliveryServiceTest.class);

    @Autowired
    private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private InventoryMainService inventoryMainService;
	@Autowired
	private SystemOrgService systemOrgService;
    @Autowired
	private SystemUserService systemUserService;


    @Test
	public void testCount()
	{
		List<CirculateDto> circulateDtos = circulateOrderDeliveryService.queryCirculateLatestByOrgIdLimit("100","10",0,5000);
		LOG.info("-----  count="+circulateDtos.size());
	}

	@Test
	public void testStartInventoryMain()
	{
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		SystemUser sessionUser = systemUserService.findUserByAccount("zhuwen");
		String orgId = "100";

		//盘点仓库
		SystemOrg inventoryOrg = systemOrgService.findById(orgId);

		//查询当前仓库下是否存在状态为“盘点中”的盘点单
		InventoryMain main = inventoryMainService.listNotFinishInventoryMain(inventoryOrg);

		//盘点功能改造新逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
		ajaxBean = inventoryMainService.startInventoryByThread(ajaxBean,sessionUser,inventoryOrg,"","");
		//盘点功能原老逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
		//ajaxBean = inventoryMainService.addInventoryMain(ajaxBean,sessionUser,inventoryOrg);

	}

	@Test
	public void testCheckContainExistNew() {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		//维修出库
		CirculateOrder circulateOrder = new CirculateOrder();
		circulateOrder.setTradeTypeCode(CirculateState.MAINTAIN.getCode());
		//常熟仓库ID
		String currentOrgId = "dc6531dc653c4fea897dcbe84a1be3e3"; 
		//在常熟仓库报修的EPC
		List<Container> containerList = new ArrayList<Container>();
		Container c1 = new Container();
		c1.setEpcId("CFP001BU-0006263");
		containerList.add(c1);
		
		ajaxBean = circulateOrderDeliveryService.checkContainExistNew(ajaxBean, circulateOrder, containerList, currentOrgId);
		LOG.info(ajaxBean.toString());
	}
	@Test
	public void testCheckContainExist() {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		//维修出库
		CirculateOrder circulateOrder = new CirculateOrder();
		circulateOrder.setTradeTypeCode(CirculateState.MAINTAIN.getCode());
		//常熟仓库ID
		String currentOrgId = "dc6531dc653c4fea897dcbe84a1be3e3"; 
		//在常熟仓库报修的EPC
		List<Container> containerList = new ArrayList<Container>();
		Container c1 = new Container();
		c1.setEpcId("CFP001BU-0006263");
		containerList.add(c1);
		
		ajaxBean = circulateOrderDeliveryService.checkContainExist(ajaxBean, circulateOrder, containerList, currentOrgId);
		LOG.info(ajaxBean.toString());
	}

}
