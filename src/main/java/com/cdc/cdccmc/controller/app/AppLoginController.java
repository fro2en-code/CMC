package com.cdc.cdccmc.controller.app;

import java.sql.Timestamp;
import java.util.ArrayList;
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

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.Md5Util;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.web.LoginController;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemMenuApp;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemJobService;
import com.cdc.cdccmc.service.sys.SystemMenuAppService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import com.esotericsoftware.minlog.Log;

/**
 * app端登录类
 * 
 * @author ZhuWen
 * @date 2018-01-19
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppLoginController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppLoginController.class);
	@Autowired
	private LogService logService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private SystemMenuAppService systemMenuAppService;
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private LoginController loginController;

	/**
	 * app端登录请求
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("/appLogin/login")
	public AjaxBean login(HttpServletRequest request, HttpSession session, String userName, String pwd) {
		LOG.info("请求url: /app/login,userName=" + userName + ",pwd =" + pwd);
		AjaxBean ajaxBean = new AjaxBean();
		if (StringUtils.isBlank(userName) || StringUtils.isBlank(pwd)) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("用户名或密码为空!");
			return ajaxBean;
		}
		// 查询用户信息
		SystemUser systemUser = systemUserService.querySystemUserByAccount(userName.trim());
		if (systemUser == null) {
			// 如果用户不存在
			LOG.info("用户[" + userName + "]登录失败,用户不存在!" + StatusCode.STATUS_104_MSG + ", userName=" + userName);
			ajaxBean.setStatus(StatusCode.STATUS_110);
			ajaxBean.setMsg(StatusCode.STATUS_110_MSG);
			return ajaxBean;
		}
		//是否是门型设备账号。0否，1是
		if(systemUser.getIsDoor() == SysConstants.INTEGER_1) { 
			LOG.info("app端用户登录失败。["+userName+"]"+StatusCode.STATUS_112_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_112);
			ajaxBean.setMsg("["+userName+"]"+StatusCode.STATUS_112_MSG);
			return ajaxBean; //登录失败，返回到登录页重新登录
		}
		if (!Md5Util.isMatchPassword(pwd.trim(), systemUser.getPassword())) {
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
			LOG.info("用户[" + userName + "]没有隶属仓库,无法登陆!");
			ajaxBean.setStatus(StatusCode.STATUS_101);
			ajaxBean.setMsg(StatusCode.STATUS_101_MSG);
			return ajaxBean;
		}
		systemUser.setMemberOfOrgList(memberOfOrgList);
		// 设置session登录用户当前所选仓库为自己的隶属仓库的第一个仓库
		systemUser.setCurrentSystemOrg(memberOfOrgList.get(0));
		// 加载当前选择仓库的菜单权限
		List<SystemMenuApp> systemMenuApp = systemMenuAppService.listSystemMenuAppByAccount(systemUser.getAccount(),
				systemUser.getCurrentSystemOrg().getOrgId());
		// 更新用户最后登陆时间
		Timestamp now = new Timestamp(new Date().getTime());
		systemUserService.updateLastLoginTime(systemUser, now, systemUser.getAccount());
		// 设置登陆用户的菜单权限列表
		systemUser.setSystemMenuAppList(systemMenuApp);
		// 获取用户在当前仓库的工种权限列表
		List<SystemJob> systemJobList = systemJobService.listJobByAccountAndOrg(systemUser.getAccount(),
				systemUser.getCurrentSystemOrg().getOrgId());
		systemUser.setSystemJobList(systemJobList);
		// add 20150518 --begin
		systemUser = systemOrgService.loadFilialeOrg(systemUser);

		session.setAttribute(SysConstants.SESSION_USER, systemUser);
		session.setMaxInactiveInterval(-1); //此登录长久有效，永不超时 观察APP是否还有超时情况
		LOG.info("用户[" + userName + "]已登录,当前默认仓库为[" + systemUser.getCurrentSystemOrg().getOrgName() + "]");
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setBean(systemUser);
		return ajaxBean;

	}

	/**
	 * 响应用户选择仓库
	 */
	@RequestMapping("/appLogin/setCurrentOrg")
	public AjaxBean setCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orgId) {
		LOG.info("---appLogin setCurrentOrg   orgId="+orgId);
		AjaxBean ajaxBean = new AjaxBean();
		// 设置用户选择仓库
		List<SystemOrg> memberOfOrgList = sessionUser.getMemberOfOrgList();
		for (SystemOrg org : memberOfOrgList) {
			if (org.getOrgId().equals(orgId)) {
				sessionUser.setCurrentSystemOrg(org);
				break;
			}
		}
		// 加载当前选择仓库的菜单权限
		List<SystemMenuApp> systemMenuApp = systemMenuAppService.listSystemMenuAppByAccount(sessionUser.getAccount(),
				sessionUser.getCurrentSystemOrg().getOrgId());
		if (systemMenuApp.size() == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_352_MSG);
			ajaxBean.setBean(sessionUser);
			return ajaxBean;
		}
		// 设置登陆用户的菜单权限列表
		sessionUser.setSystemMenuAppList(systemMenuApp);
		// 获取用户在当前仓库的工种权限列表
		List<SystemJob> systemJobList = systemJobService.listJobByAccountAndOrg(sessionUser.getAccount(),sessionUser.getCurrentSystemOrg().getOrgId());
		sessionUser.setSystemJobList(systemJobList);
		List<String> menuNameList = new ArrayList<String>();
		for (SystemMenuApp m : systemMenuApp) {
			menuNameList.add(m.getMenuName());
		}
		//TODO 临时解决APP切换仓库权限不好使 需要多测试看看是否还有问题
		sessionUser = systemOrgService.loadFilialeOrg(sessionUser);
		LOG.info("用户[" + sessionUser.getAccount() + "]获取app端菜单权限成功:" + JSONObject.toJSONString(menuNameList));

		logService.addLogLogin(sessionUser, "用户登录成功，当前选择仓库为[" + sessionUser.getCurrentSystemOrg().getOrgId() + "]["
				+ sessionUser.getCurrentSystemOrg().getOrgName() + "]");
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg("仓库切换成功");
		ajaxBean.setBean(sessionUser);
		return ajaxBean;

	}

	/**
	 * 退出登陆
	 */
	@RequestMapping("/appLogin/logout")
	public AjaxBean logout(HttpSession session) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		loginController.logout(session);
		return ajaxBean;// 删除会话里的用户后，跳转到登录页面
	}
}
