package com.cdc.cdccmc.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;

/** 
 * 日志
 * @author ZhuWen
 * @date 2017-12-28
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class LogController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(LogController.class);
	@Autowired
	private LogService logService;

	/**
	 * 用户操作日志列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 */
	@RequestMapping(value = "/log/pagingLogAccount")
	@ResponseBody
    public Paging pagingLogAccount(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,ModelMap modelmap,Paging paging,String epcId,String selectAccount,String startDate,String endDate){
		LOG.info("request URL /log/listLogAccount");
		paging = logService.listLogAccount(paging,sessionUser,epcId,selectAccount,startDate,endDate);
		return paging;
    }
	/**
	 * 用户登录日志列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 */
	@RequestMapping(value = "/log/pagingLogLogin")
    public Paging pagingLogLogin(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,ModelMap modelmap,Paging paging,String selectAccount){
		LOG.info("request URL /log/listLogLogin");
		paging = logService.listLogLogin(paging,sessionUser,selectAccount);
		return paging;
    }
	/**
	 * 用户登录日志列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 */
	@RequestMapping(value = "/log/pagingLogError")
    public Paging pagingLogError(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,ModelMap modelmap,Paging paging,String selectAccount){
		LOG.info("request URL /log/listLogError");
		paging = logService.listLogError(paging,sessionUser,selectAccount);
		return paging;
    }

}
