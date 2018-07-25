package com.cdc.cdccmc.controller.handset;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.Md5Util;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.app.AppLoginController;
import com.cdc.cdccmc.controller.web.LoginController;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import com.esotericsoftware.minlog.Log;

/**
 * 手持端--登陆类
 * 
 * @author 75645
 *
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class HandsetLoginController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HandsetLoginController.class);
	@Autowired
	private LogService logService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LoginController loginController;

	/**
	 * 手持机登录请求
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/handsetLogin/login")
	public AjaxBean login(HttpSession session, String username, String password) {
		LOG.info("请求url: /handsetLogin/login,username=" + username);
		AjaxBean ajaxBean = new AjaxBean();
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("用户名或密码" + StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		// 查询用户信息
		SystemUser systemUser = systemUserService.querySystemUserByAccount(username.trim());
		if (null == systemUser) {
			// 如果用户不存在
			LOG.info("手持机端登录失败。[" + username + "]" + StatusCode.STATUS_110_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_110);
			ajaxBean.setMsg("手持机端登录失败。[" + username + "]" + StatusCode.STATUS_110_MSG);
			return ajaxBean;
		}
		//是否是门型设备账号。0否，1是
		if(systemUser.getIsDoor() == SysConstants.INTEGER_1) { 
			LOG.info("手持机端用户登录失败。["+username+"]"+StatusCode.STATUS_112_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_112);
			ajaxBean.setMsg("["+username+"]"+StatusCode.STATUS_112_MSG);
			return ajaxBean; //登录失败，返回到登录页重新登录
		}
		if (!Md5Util.isMatchPassword(password.trim(), systemUser.getPassword())) {
			ajaxBean.setStatus(StatusCode.STATUS_104);
			ajaxBean.setMsg(StatusCode.STATUS_104_MSG);
			return ajaxBean;
		}

		// 用来判断用户的状态只有为0的时候才可以登录
		if (systemUser.getIsActive() == SysConstants.INTEGER_1) {
			Log.info("用户[" + systemUser.getAccount() + "]" + StatusCode.STATUS_109_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("[" + systemUser.getAccount() + "]" + StatusCode.STATUS_109_MSG);
			return ajaxBean;
		}
		// 加载用户隶属仓库
		List<SystemOrg> memberOfOrgList = systemOrgService.listMemberOrgByAccount(systemUser.getAccount());
		if (CollectionUtils.isEmpty(memberOfOrgList)) {
			LOG.info("[" + username + "]" + StatusCode.STATUS_101_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_101);
			ajaxBean.setMsg("[" + username + "]" + StatusCode.STATUS_101_MSG);
			return ajaxBean;
		}
		systemUser.setMemberOfOrgList(memberOfOrgList);
		// 设置session登录用户当前所选仓库为自己的隶属仓库的第一个仓库
		systemUser.setCurrentSystemOrg(memberOfOrgList.get(0));
		// 更新用户最后登陆时间
		systemUserService.updateLastLoginTime(systemUser, DateUtil.currentTimestamp(), systemUser.getAccount());
		session.setAttribute(SysConstants.SESSION_USER, systemUser);
		LOG.info("用户[" + username + "]已成功登录手持机端,当前默认仓库为[" + systemUser.getCurrentSystemOrg().getOrgName() + "]");
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setBean(systemUser);
		return ajaxBean;
	}

	/**
	 * 响应用户选择仓库
	 */
	@RequestMapping("/handsetLogin/setCurrentOrg")
	public AjaxBean setCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orgId) {
		AjaxBean ajaxBean = new AjaxBean();
		// 设置用户选择仓库
		List<SystemOrg> memberOfOrgList = sessionUser.getMemberOfOrgList();
		for (SystemOrg org : memberOfOrgList) {
			if (org.getOrgId().equals(orgId)) {
				sessionUser.setCurrentSystemOrg(org);
				break;
			}
		}
		logService.addLogLogin(sessionUser, "设置当前选择仓库为[" + sessionUser.getCurrentSystemOrg().getOrgId() + "]["
				+ sessionUser.getCurrentSystemOrg().getOrgName() + "]");
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setBean(sessionUser);
		return ajaxBean;

	}

	/**
	 * 退出登陆
	 */
	@RequestMapping("/handsetLogin/logout")
	public AjaxBean logout(HttpSession session) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		loginController.logout(session);
		return ajaxBean;// 删除会话里的用户后，跳转到登录页面
	}

}
