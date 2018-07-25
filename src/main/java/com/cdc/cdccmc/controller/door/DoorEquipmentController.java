package com.cdc.cdccmc.controller.door;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.Md5Util;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.DoorEquipmentService;
import com.cdc.cdccmc.service.HandsetService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import com.esotericsoftware.minlog.Log;

/**
 * 门型设备接口
 * 
 * @author wangzz
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class DoorEquipmentController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoorEquipmentController.class); 
	@Autowired
	private DoorEquipmentService doorEquipmentService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;

	/**
	 * 门型收货。门型设备扫描EPC编号
	 */
	@RequestMapping(value="/doorEquipment/receiveEpc", method=RequestMethod.POST)
	public AjaxBean receiveEpc(HttpSession session,@RequestParam(value = "epcIdList[]") List<String> epcIdList) {
		LOG.info("请求url: /doorEquipment/receiveEpc,epcIdList="+JSONObject.toJSONString(epcIdList));
		Object sessionObj = session.getAttribute(SysConstants.SESSION_USER);
		AjaxBean ajaxBean = new AjaxBean();
		if(null == sessionObj){
			ajaxBean.setStatus(StatusCode.STATUS_100);
			ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
			return ajaxBean; //用户未登录
		}
		SystemUser sessionUser = (SystemUser) sessionObj;
		if(CollectionUtils.isEmpty(epcIdList)){
			ajaxBean.setStatus(StatusCode.STATUS_342);
			ajaxBean.setMsg(StatusCode.STATUS_342_MSG);
			return ajaxBean; //登录失败
		}
		long s1 = System.currentTimeMillis();
		ajaxBean = doorEquipmentService.receiveEpc(ajaxBean,sessionUser,epcIdList);
		long s2 = System.currentTimeMillis();
		LOG.info("门型收货接口执行完毕，耗时：" + (s2 - s1));
		return ajaxBean;
	}

	/**
	 * 门型发货。门型设备扫描EPC编号   RequestBody传参无长度限制
	 */
	@RequestMapping(value="/doorEquipment/sendEpcByBodyParam", method=RequestMethod.POST)
	public AjaxBean sendEpcByBodyParam(HttpSession session,@RequestBody List<String> epcIdList) {
		LOG.info("请求url: /doorEquipment/sendEpcByBodyParam ,epcIdList="+epcIdList.size());
		Object sessionObj = session.getAttribute(SysConstants.SESSION_USER);
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(null == sessionObj){
			ajaxBean.setStatus(StatusCode.STATUS_100);
			ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
			return ajaxBean; //用户未登录
		}
		SystemUser sessionUser = (SystemUser) sessionObj;
		if(CollectionUtils.isEmpty(epcIdList)){
			ajaxBean.setStatus(StatusCode.STATUS_342);
			ajaxBean.setMsg(StatusCode.STATUS_342_MSG);
			return ajaxBean; //登录失败
		}
		return doorEquipmentService.sendEpc(ajaxBean,sessionUser,epcIdList);
	}

	/**
	 * 门型发货。门型设备扫描EPC编号   RequestParam传参有长度限制
	 */
	@RequestMapping(value="/doorEquipment/sendEpc", method=RequestMethod.POST)
	public AjaxBean sendEpc(HttpSession session,@RequestParam(value = "epcIdList[]") List<String> epcIdList) {
		LOG.info("请求url: /doorEquipment/sendEpc,epcIdList="+JSONObject.toJSONString(epcIdList));
		Object sessionObj = session.getAttribute(SysConstants.SESSION_USER);
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(null == sessionObj){
			ajaxBean.setStatus(StatusCode.STATUS_100);
			ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
			return ajaxBean; //用户未登录
		}
		SystemUser sessionUser = (SystemUser) sessionObj;
		if(CollectionUtils.isEmpty(epcIdList)){
			ajaxBean.setStatus(StatusCode.STATUS_342);
			ajaxBean.setMsg(StatusCode.STATUS_342_MSG);
			return ajaxBean; //登录失败
		}
		return doorEquipmentService.sendEpc(ajaxBean,sessionUser,epcIdList);
	}
	/**
	 * 门型设备登录
	 */
	@RequestMapping("/doorEquipment/login")
	public AjaxBean login(AjaxBean ajaxBean,HttpServletRequest request, HttpSession session
			,String username,String password){
		LOG.info("请求url: /doorEquipment/login,username="+username+",password="+password);
		//校验用户名密码是否为空
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			LOG.info("门型设备登录失败，用户名或密码不能为空, username="+username+", password="+password);
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("用户名或密码不能为空");
			return ajaxBean; //登录失败
		}
		SystemUser sessionUser = systemUserService.querySystemUserByAccount(username.trim());
		//如果能查询到这个用户，并且登录密码也正确
		if(null == sessionUser){ 
			//如果用户不存在
			ajaxBean.setStatus(StatusCode.STATUS_104);
			ajaxBean.setMsg("门型设备登录失败。"+StatusCode.STATUS_104_MSG+", username="+username+", password="+password);
			LOG.info("门型设备登录失败。"+StatusCode.STATUS_104_MSG+", username="+username+", password="+password);
			return ajaxBean; 
		} 
		//是否是门型设备账号。0否，1是
		if(sessionUser.getIsDoor() != SysConstants.INTEGER_1) { 
			ajaxBean.setStatus(StatusCode.STATUS_111);
			ajaxBean.setMsg("["+username+"]"+StatusCode.STATUS_111_MSG);
			return ajaxBean; //登录失败
		}
		if(Md5Util.isMatchPassword(password.trim(), sessionUser.getPassword())) {
			//加载用户隶属仓库,用户可能隶属于多个仓库
			List<SystemOrg> memberOfOrgList = systemOrgService.listMemberOrgByAccount(sessionUser.getAccount());
			if(CollectionUtils.isEmpty(memberOfOrgList)){ //如果用户不隶属于任何仓库，则没有登录权限
				Log.info("门型设备["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
				ajaxBean.setMsg("["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
				ajaxBean.setStatus(StatusCode.STATUS_201);
				return ajaxBean;
			}
			sessionUser.setMemberOfOrgList(memberOfOrgList);
			//设置session登录用户当前所选仓库为自己的隶属仓库的第一个仓库
			sessionUser.setCurrentSystemOrg(memberOfOrgList.get(0));  //如果没有默认仓库，则默认选择隶属仓库第一个为当前选中仓库
			
			logService.addLogLogin(sessionUser,
					"门型设备账号登录成功，当前选择仓库为[" + sessionUser.getCurrentSystemOrg().getOrgId() + "][" + sessionUser.getCurrentSystemOrg().getOrgName() + "]");
			LOG.info("["+sessionUser.getAccount()+"]["+sessionUser.getRealName()+"]门型设备账号登录成功，当前选择仓库为[" + sessionUser.getCurrentSystemOrg().getOrgId() + "][" + sessionUser.getCurrentSystemOrg().getOrgName() + "]");
			//将登录成功的账号放入session
			session.setAttribute(SysConstants.SESSION_USER, sessionUser);
			session.setMaxInactiveInterval(-1); //由于是门型设备，此登录长久有效，永不超时  
			ajaxBean.setBean(sessionUser);
			return ajaxBean;
		}else {
			//如果密码错误
			ajaxBean.setStatus(StatusCode.STATUS_104);
			ajaxBean.setMsg("门型设备登录失败。"+StatusCode.STATUS_104_MSG+", username="+username+", password="+password);
			LOG.info("门型设备登录失败。"+StatusCode.STATUS_104_MSG+", username="+username+", password="+password);
			return ajaxBean; 
		}
	}

}
