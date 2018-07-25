package com.cdc.cdccmc.controller.app;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.ContainerGroupService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.basic.AreaService;

/**
 * 库内移位
 * 
 * @author licao
 *
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppAreaController {
	@Autowired
	private AreaService areaService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private ContainerService containerService;

	/**
	 * 扫描epcId获取器具信息,如果该器具是托盘,则返回整托的器具信息
	 * 
	 * @param sessionUser
	 * @param epcId
	 * @return
	 */
	@RequestMapping("/appArea/containerInfoByEpcId")
	public AjaxBean containerInfoByEpcId(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String epcId) {
		AjaxBean ajaxBean = new AjaxBean();
		Container container = containerService.getContainerByEpcId(epcId);
		if (null == container) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("[" + epcId + "]" + StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		// 判断器具是否属于当前仓库下的器具，并且流转状态为在库状态！
		Circulate circulate = circulateService.queryCirculateLatestByEpcId(epcId, sessionUser.getCurrentSystemOrg().getOrgId());
		if (circulate == null) {
			ajaxBean.setStatus(StatusCode.STATUS_312);
			ajaxBean.setMsg("[" + epcId + "]" + StatusCode.STATUS_312_MSG);
			return ajaxBean;
		}
		if (!circulate.getCirculateState().equals(CirculateState.ON_ORG.getCode())) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("器具[" + epcId + "]不是在库状态，请核查");
			return ajaxBean;
		}
		// 创建返回前台的list
		List<ContainerGroup> containerGroupList = new ArrayList<ContainerGroup>();
		if (container.getContainerTypeId().equals(SysConstants.STRING_1)) {// 当器具是托盘时
			// 获取整托器具信息
			containerGroupList = containerGroupService.getContainerGroupInfo(epcId);
			if (!CollectionUtils.isEmpty(containerGroupList)) {// 当托盘整托信息不为空时,返回所有托盘信息
				ajaxBean.setList(containerGroupList);
				ajaxBean.setStatus(StatusCode.STATUS_200);
				ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
				return ajaxBean;
			}
		}
		// 如果是单个器具,或者托盘信息为空时,则返回单个器具信息
		ContainerGroup containerGroup = new ContainerGroup();
		containerGroup.setEpcId(container.getEpcId());
		containerGroup.setContainerCode(container.getContainerCode());
		containerGroupList.add(containerGroup);
		ajaxBean.setList(containerGroupList);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		return ajaxBean;
	}

	/**
	 * app获取当前仓库的库区
	 */
	@RequestMapping("/appArea/listAllArea")
	public AjaxBean listAllArea(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = new AjaxBean();
		List<Area> listArea = areaService.listAllArea();
		if (CollectionUtils.isEmpty(listArea)) {
			ajaxBean.setStatus(StatusCode.STATUS_300);
			ajaxBean.setMsg("没有找到库区!");
			return ajaxBean;
		}
		ajaxBean.setList(listArea);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		return ajaxBean;
	}

	/**
	 * 移位
	 * 
	 * @param sessionUser
	 * @param epcIdList
	 * @param areaId
	 * @return
	 */
	@RequestMapping("/appArea/updateContainerArea")
	public AjaxBean updateContainerArea(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			@RequestParam(value = "epcIdList[]") List<String> epcIdList, String areaId) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		// 对epcId进行去重
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < epcIdList.size(); i++) {
			set.add(epcIdList.get(i).toString());
		}
		// 获取库区信息
		Area area = areaService.getAreaByAreaId(areaId);
		if (null == area) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("目标库区不存在!");
			return ajaxBean;
		}
		ajaxBean = circulateService.buildAndInsertCirculateHistoryForMove(ajaxBean, sessionUser, epcIdList, areaId);
		return ajaxBean;
	}

}
