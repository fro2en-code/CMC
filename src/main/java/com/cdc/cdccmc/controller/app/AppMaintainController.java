package com.cdc.cdccmc.controller.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.web.maintain.MaintainController;
import com.cdc.cdccmc.domain.Maintain;
import com.cdc.cdccmc.domain.MaintainLevel;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.MaintainService;
import com.cdc.cdccmc.service.basic.MainTainLevelService;

/**
 * app端器具报修,维修鉴定,报废审批
 * 
 * @author licao
 *
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppMaintainController {
	@Autowired
	private MainTainLevelService maintainLevelService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private MaintainService maintainService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private MaintainController maintainController;
	
	/**
	 * app端器具报修页面之获取报修，待鉴定器具信息，判断条件为：维修状态为在库维修，维修级别为空
	 */
	@RequestMapping("/appMaintain/listMaintain")
	public AjaxBean listMaintain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = new AjaxBean();
		// 获取当前仓库在库维修状态下的器具列表
		List<Maintain> listMaintain = maintainService
				.listMaintainContainer(sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setList(listMaintain);
		return ajaxBean;
	}

	/**
	 * app端器具报修
	 */
	@RequestMapping("/appMaintain/addMaintain")
	public synchronized AjaxBean addMaintain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String epcId,String maintainApplyBadReason) {
		AjaxBean ajaxBean = new AjaxBean();
		// 获取器具信息
		Container container = containerService.getContainerByEpcId(epcId);
		if (null == container) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("器具在器具列表中不存在");
			return ajaxBean;
		}
		// 查询器具是否在当前仓库下进行过报修操作
		Maintain findMaintain = maintainService.existMaintain(epcId, sessionUser.getCurrentSystemOrg().getOrgId());
		if (null != findMaintain) {
			switch (findMaintain.getMaintainState()){  // 维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
				case "1":
					ajaxBean.setStatus(StatusCode.STATUS_326);
					ajaxBean.setMsg("EPC编号["+findMaintain.getEpcId()+"]"+StatusCode.STATUS_326_MSG);
					return ajaxBean;
				case "2":
					ajaxBean.setStatus(StatusCode.STATUS_327);
					ajaxBean.setMsg("EPC编号["+findMaintain.getEpcId()+"]"+StatusCode.STATUS_327_MSG);
					return ajaxBean;
			}
		}
		//为某个器具移动到当前仓库。
		circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser, container, SysConstants.CIRCULATE_REMARK_APP_APPLY_MAINTAIN);
		
		Maintain maintain = new Maintain();
		maintain.setEpcId(epcId);
		maintain.setMaintainApplyBadReason(maintainApplyBadReason);
		maintainController.addMaintain(sessionUser, maintain);
		return ajaxBean;
	}

	/**
	 * app端维修鉴定页面之获取当前仓库维修级别
	 */
	@RequestMapping("/appMaintain/listMaintainLevel")
	public AjaxBean listMaintainLevel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = new AjaxBean();
		List<MaintainLevel> listMaintainLevel = maintainLevelService.listAllMaintainLevel();
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setList(listMaintainLevel);
		return ajaxBean;
	}

	/**
	 * app端维修鉴定页面之获取当前仓库维修级别
	 */
	@RequestMapping("/appMaintain/listMaintainNoMaintainLevel")
	public AjaxBean listMaintainNoMaintainLevel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<Maintain> list = maintainLevelService.listMaintainNoMaintainLevel(sessionUser);
		ajaxBean.setList(list);
		return ajaxBean;
	}

	/**
	 * app端维修完成页面，获取待维修完成器具列表
	 */
	@RequestMapping("/appMaintain/listWaitFinishMaintain")
	public AjaxBean listWaitFinishMaintain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<Maintain> list = maintainLevelService.listWaitFinishMaintain(sessionUser);
		ajaxBean.setList(list);
		return ajaxBean;
	}

	/**
	 * 对报修器具进行维修鉴定
	 * 
	 * @param sessionUser
	 * @param epcId
	 * @param level
	 *            维修级别,如果报废状态下维修级别为空
	 * @return
	 */
	@RequestMapping("/appMaintain/confirmMaintainLevel")
	public AjaxBean confirmMaintainLevel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String maintainId,String epcId, String level) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		Maintain maintain = new Maintain();
		maintain.setMaintainId(maintainId);
		maintain.setMaintainLevel(level);
		maintain.setEpcId(epcId);
		ajaxBean = maintainService.maintainAppraisal(ajaxBean,sessionUser,maintain);
		return ajaxBean;
	}
	
	/**
	 * 列出当前仓库下所有报修器具，维修级别为空的，尚未鉴定维修级别的，此接口用于app端【器具报修】页面列表请求
	 */
	@RequestMapping("/appMaintain/listApplyMaintainContainer")
	public AjaxBean listApplyMaintainContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean = maintainService.listApplyMaintainContainer(ajaxBean,sessionUser);
		return ajaxBean;
	}
	
	/**
	 * 维修完成
	 */
	@RequestMapping("/appMaintain/maintainFinish")
	public AjaxBean maintainFinish(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Maintain maintain){
		AjaxBean ajaxBean = maintainController.maintainFinish(sessionUser,maintain);
		return ajaxBean;
	}
}
