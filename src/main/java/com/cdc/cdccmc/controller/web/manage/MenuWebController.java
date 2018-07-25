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
import com.cdc.cdccmc.controller.web.PageController;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SystemButtonService;
import com.cdc.cdccmc.service.sys.SystemMenuWebService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
/**
 * WEB菜单
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class MenuWebController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MenuWebController.class); 

	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private SystemMenuWebService systemMenuWebService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemButtonService systemButtonService;
	@Autowired
	private PageController pageController;
	/**
	 * 查询所有web菜单
	 * @param map
	 * @return
	 */
	@RequestMapping("/menuweb/listAllMenu")
	public AjaxBean listAllMenu(){
		LOG.info("请求url: /menuweb/listAllMenu");
		List<SystemMenuWeb> menuList = systemMenuWebService.listAllMenu();
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(menuList);
		return ajaxBean;
	}
	/**
	 * 获取指定仓库指定工种的权限菜单
	 * @param sessionUser
	 * @param jobId 指定工种
	 * @return
	 */
	@RequestMapping(value = "/menuweb/queryMenuByJob")
    public AjaxBean queryMenuByJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String jobId){
		LOG.info("request URL /menuweb/queryMenuByJob");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemMenuWeb> menuList = systemMenuWebService.queryMenuByJobAndOrgId(ajaxBean,sessionUser,jobId);
		ajaxBean.setList(menuList);
		return ajaxBean;
    }
	/**
	 * 保存某个用户，指定仓库下，新的web权限菜单和新的权限按钮列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @param memberOrgId 指定仓库ID，账号的一个隶属仓库ID
	 * @param menuList 指定仓库下新的菜单权限列表
	 * @param buttonList 指定仓库下新的按钮权限列表
	 * @return 
	 */
	@RequestMapping("/menuweb/saveAccountMenuWeb")
	public AjaxBean saveAccountMenuWeb(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,String selectAccount,String memberOrgId
			,@RequestParam(value = "menuList[]",required=false) List menuList
			,@RequestParam(value = "buttonList[]",required=false) List buttonList ){
		LOG.info("request URL /menuweb/saveAccountMenuWeb");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(selectAccount)){
			ajaxBean.setMsg("请先选择一个账号");
			ajaxBean.setStatus(StatusCode.STATUS_201);
			return ajaxBean;
		}
		//超级管理员账号[admin]权限不可变更！
		if(SysConstants.SUPER_ADMIN.equals(selectAccount)){
			ajaxBean.setMsg(StatusCode.STATUS_106_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_106);
			return ajaxBean;
		}
		if(StringUtils.isBlank(memberOrgId)){
			ajaxBean.setMsg("请先选择一个隶属仓库");
			ajaxBean.setStatus(StatusCode.STATUS_201);
			return ajaxBean;
		}
		SystemOrg memberOrg = systemOrgService.findById(memberOrgId);
		if(null == memberOrg){
			ajaxBean.setMsg("选择的隶属仓库"+StatusCode.STATUS_311_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_311);
			return ajaxBean;
		}
		//更新选中账号的权限菜单
		systemMenuWebService.updateAccountMenuWeb(memberOrg,selectAccount,menuList,sessionUser);
		//更新选中账号的权限按钮
		systemButtonService.updateAccountButton(memberOrg,selectAccount,buttonList,sessionUser);
		//如果更新的是自己账号，当即刷新权限菜单和权限按钮，不必等到重新登录后再重新加载
		if(sessionUser.getAccount().equals(selectAccount)){
			List<SystemMenuWeb> newMenuList = systemUserService.buildSystemMenuWebList(sessionUser.getAccount(), sessionUser.getCurrentSystemOrg().getOrgId());
			sessionUser.setSystemMenuWebList(newMenuList);
			List<SystemButton> newButtonList = systemButtonService.listSystemButton(sessionUser.getAccount(),sessionUser.getCurrentSystemOrg().getOrgId());
			sessionUser.setSystemButtonList(newButtonList);
		}
		return AjaxBean.SUCCESS();
	}
}
