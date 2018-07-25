package com.cdc.cdccmc.controller.web;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.AreaService;

/** 
 * 仓库管理
 * @author Clm
 * @date 2018-01-05
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AreaController {
	@Autowired
	private AreaService areaService;
	
	/**
	 * 列表查询/区域名称查询
	 * @param systemUser
	 * @param area
	 * @return
	 */
	@RequestMapping(value = "/area/pagingArea")
    public Paging pagingArea(Paging paging,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Area area){
		paging = areaService.pagingArea(paging ,area);
		return paging;
    }
	
	/**
	 * 新增
	 * @param systemUser
	 * @param areaName
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping(value = "/area/addArea")
    public AjaxBean addArea(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, String areaName, AjaxBean ajaxBean){
		if(StringUtils.isBlank(areaName)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("区域名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		List<Area> list = areaService.queryByName(areaName.trim());
		if(CollectionUtils.isEmpty(list)) {
			ajaxBean = areaService.addArea(ajaxBean,sessionUser,areaName);
		}else {
			ajaxBean.setStatus(StatusCode.STATUS_302);
            ajaxBean.setMsg("当前区域名称["+list.get(0).getAreaName()+"]"+StatusCode.STATUS_302_MSG);
		}
		return ajaxBean;
    }
	
	/**
	 * 设置默认入库区域
	 * @param systemUser
	 * @param area
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping(value = "/area/setDefaultArea")
	public AjaxBean setDefaultArea(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, Area area, AjaxBean ajaxBean) {
		area.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean = areaService.setDefaultArea(sessionUser,area,ajaxBean);
		return ajaxBean;
	}
}
