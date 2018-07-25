package com.cdc.cdccmc.controller.web.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.web.PageController;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.sys.SystemButtonService;
import com.cdc.cdccmc.service.sys.SystemMenuWebService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
/**
 * 按钮
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ButtonController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ButtonController.class); 

	@Autowired
	private BaseService baseService;
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
	 * 查询所有按钮
	 * @param map
	 * @return
	 */
	@RequestMapping("/button/listAllButton")
	public AjaxBean listAllMenu(){
		LOG.info("请求url: /menuweb/listAllButton");
		List<SystemButton> buttonList = systemButtonService.listAllButton();
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(buttonList);
		return ajaxBean;
	}  
	/**
	 * 获取指定仓库指定工种的权限按钮
	 * @param sessionUser
	 * @param jobId 指定工种
	 * @return
	 */
	@RequestMapping(value = "/button/queryButtonByJob")
    public AjaxBean queryButtonByJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String jobId){
		LOG.info("request URL /button/queryButtonByJob");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemButton> buttonList = systemButtonService.queryButtonByJobAndOrgId(ajaxBean,sessionUser,jobId);
		ajaxBean.setList(buttonList);
		return ajaxBean;
    }
}
