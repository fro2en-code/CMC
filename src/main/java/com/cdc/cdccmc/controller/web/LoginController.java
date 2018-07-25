package com.cdc.cdccmc.controller.web;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.Md5Util;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import com.esotericsoftware.minlog.Log;
import org.springframework.web.util.WebUtils;

@Controller
public class LoginController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoginController.class); 
	@Autowired
	ApplicationContext applicationContext;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;
	
	/**
	 * 请求项目根目录： http://localhost:8080/cdccmc/ 
	 * @param
	 * @return
	 */
	@RequestMapping("/")
	public String root(ModelMap modelMap, HttpSession session){
		LOG.info("请求url: / 请求项目根目录。");
		Object sessionUser = session.getAttribute(SysConstants.SESSION_USER);
		if(sessionUser != null){
			SystemUser systemUser = (SystemUser) sessionUser;
			LOG.info("当前用户["+systemUser.getAccount()+"]已登录，重定向到默认首页");
			return "redirect:/page/defaultPage";  //跳转到默认首页，那里对打开那个页面为首页做了逻辑处理  
		}
		return "/login";
	} 
	/**
	 * 用户登录请求
	 * @param username 登录用户名
	 * @param password
	 * @return
	 */
	@RequestMapping("/login")
	public String login(ModelMap modelMap,HttpServletRequest request, HttpSession session
			,String username,String password,String code){
		LOG.info("请求url: /login, username="+username+", password="+password);

		/******************* 开发时免密码免验证码登录  *******************/
		return loginUserDev(modelMap,request,session,username);
		
		/******************* 正式上线时放开此代码  *******************/
//		return loginUserProd(modelMap,request,session,username,password,code);
	}
	private String loginUserDev(ModelMap modelMap,HttpServletRequest request, HttpSession session
			,String username){
		//登录信息原样带回登录页面，用于自动填充用户输入信息
		modelMap.put("username", username);
		//校验用户名是否为空
		if(StringUtils.isBlank(username) ){
			LOG.info("用户名不能为空, username="+username);
			modelMap.put("errmsg", "用户名不能为空");
			return "/login"; //登录失败，返回到登录页重新登录
		}
		SystemUser sessionUser = systemUserService.querySystemUserByAccount(username.trim());
		if(sessionUser != null){ //如果能查询到这个用户
			//是否是门型设备账号。0否，1是
			if(sessionUser.getIsDoor() == SysConstants.INTEGER_1) { 
				LOG.info("web端用户登录失败。["+username+"]"+StatusCode.STATUS_112_MSG);
				modelMap.put("errmsg", "["+username+"]"+StatusCode.STATUS_112_MSG);
				return "/login"; //登录失败，返回到登录页重新登录
			}
			//用来判断用户的状态只有为0的时候才可以登录
			if(sessionUser.getIsActive().equals(SysConstants.INTEGER_1)){
				Log.info("用户["+sessionUser.getAccount()+"]"+StatusCode.STATUS_109_MSG);
				modelMap.put("errmsg", "["+sessionUser.getAccount()+"]"+StatusCode.STATUS_109_MSG);
				return "/login";
			}
			//加载用户隶属仓库,用户可能隶属于多个仓库
			List<SystemOrg> memberOfOrgList = systemOrgService.listMemberOrgByAccount(sessionUser.getAccount());
			if(CollectionUtils.isEmpty(memberOfOrgList)){ //如果用户不隶属于任何仓库，则没有登录权限
				Log.info("用户["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
				modelMap.put("errmsg", "["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
				return "/login";
			}
			sessionUser.setMemberOfOrgList(memberOfOrgList);
			//设置session登录用户当前所选仓库为自己的隶属仓库的第一个仓库
			sessionUser.setCurrentSystemOrg(memberOfOrgList.get(0));  //如果没有默认仓库，则默认选择隶属仓库第一个为当前选中仓库
			if(StringUtils.isNotBlank(sessionUser.getDefaultOrgId())){ //如果用户有自己设置的默认仓库ID
				for(SystemOrg org : memberOfOrgList){
					if(sessionUser.getDefaultOrgId().equals(org.getOrgId())){ //如果默认设置仓库存在于隶属仓库中
						sessionUser.setCurrentSystemOrg(org);  //设置用户当前选中仓库为默认设置仓库
						break;
					}
				}
			}
			//更新登录用户最新登录时间：last_login_time
			Timestamp now = new Timestamp(new Date().getTime());
			systemUserService.updateLastLoginTime(sessionUser,now, sessionUser.getAccount());
			sessionUser.setLastLoginTime(now);
			session.setMaxInactiveInterval(-1);
			return systemUserService.loadUserAuthority(session,sessionUser,modelMap);  //加载登录用户权限
		}
		LOG.info(StatusCode.STATUS_104_MSG+", username="+username);
		modelMap.put("errmsg", StatusCode.STATUS_104_MSG);
		return "/login"; //登录失败，返回到登录页重新登录
	}
	
	private String loginUserProd(ModelMap modelMap,HttpServletRequest request, HttpSession session
			,String username,String password,String code){
		//登录信息原样带回登录页面，用于自动填充用户输入信息
		modelMap.put("username", username);
		modelMap.put("password", password);
		
		//一次性验证码,用完之后从session中删除防止用户后退用同样的验证码再次登录
		String vCode = (String) session.getAttribute("validateCode");
		session.removeAttribute(vCode);
		//校验验证码不为空
		if(StringUtils.isBlank(code) || StringUtils.isBlank(vCode)){
			modelMap.put("errmsg", "验证码不能为空，请重试");
			return "/login"; //登录失败，返回到登录页重新登录
			}
		//校验验证码是否正确
		if(!code.equalsIgnoreCase(vCode.toString())){
			modelMap.put("errmsg", "验证码错误，请重试");
			return "/login"; //登录失败，返回到登录页重新登录
			}
		
		//校验用户名密码是否为空
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password)){
			LOG.info("用户名或密码不能为空, username="+username+", password="+password);
			modelMap.put("errmsg", "用户名或密码不能为空");
			return "/login"; //登录失败，返回到登录页重新登录
		}
		
		SystemUser sessionUser = systemUserService.querySystemUserByAccount(username.trim());
		if(null != sessionUser){ //如果能查询到这个用户，并且登录密码也正确
			//是否是门型设备账号。0否，1是
			if(sessionUser.getIsDoor() == SysConstants.INTEGER_1) { 
				LOG.info("web端用户登录失败。["+username+"]"+StatusCode.STATUS_112_MSG);
				modelMap.put("errmsg", "["+username+"]"+StatusCode.STATUS_112_MSG);
				return "/login"; //登录失败，返回到登录页重新登录
			}
			//如果密码正确
			if(Md5Util.isMatchPassword(password.trim(), sessionUser.getPassword())) {
				//加载用户隶属仓库,用户可能隶属于多个仓库
				List<SystemOrg> memberOfOrgList = systemOrgService.listMemberOrgByAccount(sessionUser.getAccount());
				if(CollectionUtils.isEmpty(memberOfOrgList)){ //如果用户不隶属于任何仓库，则没有登录权限
					Log.info("用户["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
					modelMap.put("errmsg", "["+sessionUser.getAccount()+"]"+StatusCode.STATUS_105_MSG);
					return "/login";
				}
				sessionUser.setMemberOfOrgList(memberOfOrgList);
				//设置session登录用户当前所选仓库为自己的隶属仓库的第一个仓库
				sessionUser.setCurrentSystemOrg(memberOfOrgList.get(0));  //如果没有默认仓库，则默认选择隶属仓库第一个为当前选中仓库
				if(StringUtils.isNotBlank(sessionUser.getDefaultOrgId())){ //如果用户有自己设置的默认仓库ID
					for(SystemOrg org : memberOfOrgList){
						if(sessionUser.getDefaultOrgId().equals(org.getOrgId())){ //如果默认设置仓库存在于隶属仓库中
							sessionUser.setCurrentSystemOrg(org);  //设置用户当前选中仓库为默认设置仓库
							break;
						}
					}
				}
				return systemUserService.loadUserAuthority(session,sessionUser,modelMap);  //加载登录用户权限
			}
		}
		LOG.info(StatusCode.STATUS_104_MSG+", username="+username);
		modelMap.put("errmsg", StatusCode.STATUS_104_MSG);
		return "/login"; //登录失败，返回到登录页重新登录
	}
	
	/**
	 * 退出登录
	 */
	@RequestMapping("/logout")
	public String logout(HttpSession session){
		LOG.info("请求url: /logout");
		Object sessionObj = session.getAttribute(SysConstants.SESSION_USER);
		if(sessionObj != null){
			SystemUser sessionUser = (SystemUser) sessionObj;
			LOG.info("["+sessionUser.getAccount()+"]["+sessionUser.getRealName()+"]退出系统！");
			logService.addLogLogin(sessionUser,"退出系统");
			session.removeAttribute(SysConstants.SESSION_USER);
		}
		session.invalidate();
		return "/login"; //删除会话里的用户后，跳转到登录页面
	}
	/**
	 * 登录超时
	 */
	@RequestMapping("/timeout")
	public String timeout(ModelMap modelMap,HttpServletRequest request, HttpSession session){
		LOG.info("请求url: /timeout");
		return "/timeout"; //删除会话里的用户后，跳转到登录页面
	}

	/**
	 * 《CMC周转箱管理信息系统操作手册.docx》下载
	 */
	@RequestMapping("/downloadHelpDoc")
	public void excelDownload(HttpServletRequest req, HttpServletResponse resp){
		String fileName = "CMC周转箱管理信息系统操作手册.docx";
		ExcelUtil.downLoadExcel(req,resp,fileName);
	}

	@RequestMapping("/validSessionLogin")
	@ResponseBody
	public AjaxBean validSessionLogin(HttpServletRequest request){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		Object sessionObj = WebUtils.getSessionAttribute(request, SysConstants.SESSION_USER);
		if(null != sessionObj){ //如果登录不为空
			SystemUser sessionUser = (SystemUser) sessionObj;
			String msg = "session中检测到已登录用户：account["+sessionUser.getAccount()+"]realName["+sessionUser.getRealName()+"]" ;
			LOG.info(msg);
			ajaxBean.setMsg(msg);
			return ajaxBean;
		}else{
			LOG.info("session中未检测到登录用户！！！" );
			//用户未登录错误码返回到前端
			ajaxBean.setStatus(StatusCode.STATUS_100);
			ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
		}
		return ajaxBean;
	}

	@RequestMapping("/web/testSession")
	@ResponseBody
	public AjaxBean testSessionWeb(){
		LOG.info("请求url: /web/testSession");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setMsg("web端session测试");
		return ajaxBean;
	}

	@RequestMapping("/handsetLogin/testSession")
	@ResponseBody
	public AjaxBean testSessionHandsetLogin(){
		LOG.info("请求url: /handsetLogin/testSession");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setMsg("手持机端session测试");
		return ajaxBean;
	}

	@RequestMapping("/app/testSession")
	@ResponseBody
	public AjaxBean testSessionApp(){
		LOG.info("请求url: /app/testSession");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setMsg("app手机端session测试");
		return ajaxBean;
	}
}
