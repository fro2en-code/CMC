package com.cdc.cdccmc.controller.web.container;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.domain.container.ContainerType;
import com.cdc.cdccmc.service.ContainerTypeService;

/** 
 * 器具类型
 * @author Jerry
 * @date 2017-01-04
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ContainerTypeController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerTypeController.class);
	@Autowired
	private ContainerTypeService containerTypeService;

	/**
	 * 根据公司ID查询所有器具类型
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	@RequestMapping("/containerType/listAllContainerType")
	public List<ContainerType> queryByOrgId(){
		LOG.info("请求url: /containerType/listAllContainerType");
		List<ContainerType> list = containerTypeService.listAllContainerType();
		return list;
	}
	
	
	
}
