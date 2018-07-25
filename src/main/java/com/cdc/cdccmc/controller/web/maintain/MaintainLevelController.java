package com.cdc.cdccmc.controller.web.maintain;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.CarShipper;
import com.cdc.cdccmc.domain.MaintainLevel;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.MainTainLevelService;

/** 
 * 维修级别
 * @author Clm
 * @date 2018-01-05
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class MaintainLevelController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(MaintainLevelController.class);
	@Autowired
	private MainTainLevelService mainTainLevelService;
	
	/**
	 * 列表查询
	 * @param paging
	 * @param systemUser
	 * @param maintainLevel
	 * @return
	 */
	@RequestMapping(value = "/mainTainLevel/pagingMainTainLevel")
    public Paging pagingMainTainLevel(Paging paging, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, MaintainLevel maintainLevel){
		paging = mainTainLevelService.pagingMainTainLevel(paging,maintainLevel);
		return paging;
    }
	
	/**
	 * 新增
	 * @param systemUser
	 * @param maintainLevel
	 * @param maintainHourStr
	 * @return
	 */
	@RequestMapping(value = "/mainTainLevel/addMainTainLevel")
    public AjaxBean addContainerCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser,MaintainLevel maintainLevel, String maintainHourStr){
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(maintainLevel.getMaintainLevel())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("维修级别"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(maintainLevel.getMaintainLevelName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("维修级别名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(!(maintainHourStr.matches(SysConstants.REGEX_MAINTAIN_LEVEL_HOUR))){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("维修时间限制(小时)必须是整数且大于0同时长度不能超过6位");
			return ajaxBean;
		}
		MaintainLevel findMaintainLevel = mainTainLevelService.queryMaintainLevelByMaintainLevel(maintainLevel.getMaintainLevel().trim());
		if(null == findMaintainLevel) {
			MaintainLevel findMaintain = mainTainLevelService.queryMaintainByName(maintainLevel.getMaintainLevelName().trim());
			if(null != findMaintain) {
				ajaxBean.setStatus(StatusCode.STATUS_302);
	            ajaxBean.setMsg("当前维修级别名称["+maintainLevel.getMaintainLevelName()+"]" + StatusCode.STATUS_302_MSG);
			}else {
				maintainLevel.setMaintainHour(Integer.parseInt(maintainHourStr));
				ajaxBean = mainTainLevelService.addMainTainLevel(sessionUser,ajaxBean,maintainLevel);
			}
			return ajaxBean;
		}else {
			ajaxBean.setStatus(StatusCode.STATUS_302);
            ajaxBean.setMsg("当前维修级别["+maintainLevel.getMaintainLevel()+"]" + StatusCode.STATUS_302_MSG);
		}
		return ajaxBean;
    }
	
	/**
	 * 更新
	 * @param systemUser
	 * @param maintainLevel
	 * @param ajaxBean
	 * @param maintainHourStr
	 * @return
	 */
	@RequestMapping("/mainTainLevel/updateMainTainLevel")
	public AjaxBean updateCarShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, MaintainLevel maintainLevel, AjaxBean ajaxBean, String maintainHourStr){
		if(StringUtils.isBlank(maintainLevel.getMaintainLevelName())) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("维修级别名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		MaintainLevel findMaintain = mainTainLevelService.queryMaintainByName(maintainLevel.getMaintainLevelName().trim());
		if(null != findMaintain && !(findMaintain.getMaintainLevel().equals(maintainLevel.getMaintainLevel().trim()))) {
				ajaxBean.setStatus(StatusCode.STATUS_302);
	            ajaxBean.setMsg("当前维修级别名称["+maintainLevel.getMaintainLevelName()+"]" + StatusCode.STATUS_302_MSG);
	            return ajaxBean;
		}
		
		if(!(maintainHourStr.matches(SysConstants.REGEX_MAINTAIN_LEVEL_HOUR))){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("维修时间限制(小时)必须是整数且大于0同时长度不能超过6位");
			return ajaxBean;
		}
		maintainLevel.setModifyAccount(sessionUser.getAccount());
		maintainLevel.setModifyRealName(sessionUser.getRealName());
		maintainLevel.setMaintainHour(Integer.parseInt(maintainHourStr));
		ajaxBean = mainTainLevelService.updateMainTainLevel(sessionUser,maintainLevel);
		return ajaxBean;
	}

	/**
	 * 查询出所有的维修级别
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/mainTainLevel/queryMaintainLevelList")
	public AjaxBean queryMaintainLevelList(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<MaintainLevel> list = mainTainLevelService.listAllMaintainLevel();
		ajaxBean.setList(list);
		return ajaxBean;
	}
	
}
