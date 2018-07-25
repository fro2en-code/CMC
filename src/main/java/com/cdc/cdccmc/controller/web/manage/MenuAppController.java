package com.cdc.cdccmc.controller.web.manage;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemMenuApp;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SystemMenuAppService;
/**
 * APP菜单
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class MenuAppController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MenuAppController.class); 

	@Autowired
	private SystemMenuAppService systemMenuAppService;
	/**
	 * 查询所有app菜单
	 * @param map
	 * @return
	 */
	@RequestMapping("/menuapp/listAllMenu")
	public AjaxBean listAllMenu(){
		LOG.info("请求url: /menuapp/listAllMenu");
		List<SystemMenuApp> menuList = systemMenuAppService.listAllMenu();
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(menuList);
		return ajaxBean;
	}
	/**
	 * 查询所有app菜单
	 * @param map
	 * @return
	 */
	@RequestMapping("/menuapp/pagingAllMenu")
	public Paging pagingAllMenu(Paging paging){
		LOG.info("请求url: /menuapp/pagingAllMenu");
		paging = systemMenuAppService.pagingAllMenu(paging);
		return paging;
	}
	/**
	 * 保存某个用户，新的app权限菜单列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @return
	 */
	@RequestMapping("/menuapp/saveAccountMenuApp")
	public AjaxBean saveAccountMenuApp(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,String selectAccount,String selectMemberOrgId , @RequestParam(value = "menuList[]",required=false) List menuList ){
		LOG.info("request URL /menuapp/saveAccountMenuApp");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(selectAccount)){
			ajaxBean.setMsg("请先选择一个账号");
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		if(StringUtils.isBlank(selectMemberOrgId)){
			ajaxBean.setMsg("请先选择一个隶属仓库");
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		//超级管理员账号[admin]权限不可变更！
		if(SysConstants.SUPER_ADMIN.equals(selectAccount)){
			ajaxBean.setMsg(StatusCode.STATUS_106_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_106);
			return ajaxBean;
		}
		//更新选中账号的权限菜单	
		systemMenuAppService.updateAccountMenuApp(selectAccount,selectMemberOrgId, menuList, sessionUser);
		return ajaxBean;
	}

}
