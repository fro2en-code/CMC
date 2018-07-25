
package com.cdc.cdccmc.controller.app;

import java.util.ArrayList;
import java.util.List;

import com.cdc.cdccmc.service.LogService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.ContainerGroupService;
import com.cdc.cdccmc.service.ContainerService;

/**
 * app端器具信息类
 * 
 * @author Administrator
 *
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppContainerController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppContainerController.class);

	@Autowired
	private ContainerService containerService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private LogService logService;
	/**
	 * 获取器具和组托状态
	 * 
	 * @param epcId
	 *            1 从组托页面调用接口，2 从解托界面调用接口
	 * @return
	 */
	@RequestMapping("/appContainer/getContainerInfoAndGroupStatus")
	public AjaxBean getContainerInfoAndGroupStatus(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String epcId) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		// 获取器具信息
		Container con = containerService.getContainerByEpcId(epcId);
		if (null == con) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("该器具不存在,请联系管理员核实!");
			return ajaxBean;
		}
		ajaxBean = containerGroupService.getContainerInfoAndGroupStatus(ajaxBean,con,sessionUser);
		return ajaxBean;
	}

	/**
	 * 创建器具组托
	 *
	 * @param sessionUser
	 * @param epcIdList
	 * @return
	 */
	@RequestMapping("/appContainer/createContainerGroup")
	public AjaxBean createContainerGroup(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										 @RequestParam(value = "epcId[]") List<String> epcIdList) {

		return containerGroupService.createContainerGroup(sessionUser, epcIdList);
	}



	/**
	 * 器具解托-支持单个/整托
	 * 
	 * @param sessionUser
	 * @param epcId
	 * @return
	 */
	@RequestMapping("/appContainer/relieveContainerGroup")
	public AjaxBean relieveContainerGroup(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String epcId) {
		return containerGroupService.relieveContainerGroup(sessionUser,epcId);
	}

	/**
	 * 分组查看门型扫描数据 FOR APP
	 * @param sessionUser
	 * @param doorAccount
	 * @return
	 */
	@RequestMapping("/appContainer/queryDoorScanGroup")
	public AjaxBean queryDoorScanGroup(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										  String doorAccount) {
		return containerGroupService.queryDoorScanGroupInfo(doorAccount);
	}

	/**
	 * 收货门型监控--分组查看门型扫描数据 FOR APP
	 * @param sessionUser
	 * @param doorAccount
	 * @return
	 */
	@RequestMapping("/appContainer/queryDoorScanReceive")
	public AjaxBean queryDoorScanReceive(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										  String doorAccount) {
		return containerGroupService.queryDoorScanReceive(doorAccount);
	}

	/**
	 * APP-门型器具绑定-器具重绑-绑定
	 * @param sessionUser
	 * @param circulateOrderCode
	 * @param epcId
	 * @return
	 */
	@RequestMapping("/appContainer/containerReBind")
	public AjaxBean containerReBind(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
									   String circulateOrderCode,String epcId) {
		return containerGroupService.containerReBind(sessionUser,circulateOrderCode,epcId);
	}


}
