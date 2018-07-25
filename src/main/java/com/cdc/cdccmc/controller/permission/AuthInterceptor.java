package com.cdc.cdccmc.controller.permission;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;

/**
 * controller层URL拦截器
 * 
 * @author ZhuWen
 * @date 2018-01-02
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AuthInterceptor.class);

	private static List<String> noCheckUrl = new ArrayList<String>();
	private String staticResourceURLPrefix = "/resources";
	static {
		noCheckUrl.add("/");// 项目根目录
		noCheckUrl.add("/login"); // web端登录页面
		noCheckUrl.add("/doorEquipment/login"); // 门型设备端登录URL
		noCheckUrl.add("/doorEquipment/receiveEpc"); // 门型设备特殊接口，需要响应无登录情况
		noCheckUrl.add("/handsetLogin/login"); // 手持机端登录URL
		noCheckUrl.add("/appLogin/login"); // app端登录URL
		noCheckUrl.add("/timeout"); // 登录超时
		noCheckUrl.add("/validSessionLogin"); // 校验当前session是否已登录用户
		noCheckUrl.add("/logout"); // 退出登录
		noCheckUrl.add("/static/"); // 静态资源
		noCheckUrl.add("/authcode");// 验证码
		noCheckUrl.add("/returnStatusCode100");// 返回用户未登录信息到前端
		noCheckUrl.add("/404");// 验证码
		noCheckUrl.add("/swagger-ui.html");
		
	}

	/**
	 * 处理拦截，检验是否session里已有登录用户
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String url = request.getRequestURI().toString();
		LOG.info("拦截到url: " + url);
		String contextPath = request.getContextPath();
		
		HttpSession session = null;
		try {
			String authUrl = StringUtils.replace(url, contextPath, "");
			if (authUrl.startsWith("//")) {
				authUrl = authUrl.substring(1);
			}
			// 如果是免登陆访问URL，直接放行
			if (noCheckUrl.contains(authUrl) || isStaticResourceURL(authUrl)) {
				return true;
			}
			session = request.getSession();
			Object sessionUser = session.getAttribute(SysConstants.SESSION_USER);
			// 如果是需要登录才能访问的url，但是session里已没有登录用户，说明这个用户已经登录超时，session已做超时处理
			if (sessionUser == null) {
				response.setContentType("text/html;charset=utf-8");
				//如果是来自手持机端请求
				if(authUrl.startsWith("/handset")){
					AjaxBean ajaxBean = AjaxBean.SUCCESS();
					ajaxBean.setStatus(StatusCode.STATUS_100);
					ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
					PrintWriter pw = response.getWriter();
					pw.print(JSONObject.toJSON(ajaxBean));
					pw.flush();
					pw.close();
					LOG.info("拦截器检测到手持机端的请求["+authUrl+"]，但是用户未登录，返回到前端信息："+JSONObject.toJSON(ajaxBean));
					return false;
				}
				//如果是来自手机端请求
				if(authUrl.startsWith("/app") ){
					AjaxBean ajaxBean = AjaxBean.SUCCESS();
					ajaxBean.setStatus(StatusCode.STATUS_100);
					ajaxBean.setMsg(StatusCode.STATUS_100_MSG);
					PrintWriter pw = response.getWriter();
					pw.print(JSONObject.toJSON(ajaxBean));
					pw.flush();
					pw.close();
					LOG.info("拦截器检测到app手机端的请求["+authUrl+"]，但是用户未登录，返回到前端信息："+JSONObject.toJSON(ajaxBean));
					return false;
				}
				// 重定向到超时页面
				LOG.info("拦截器检测到web端的请求["+authUrl+"]，但是用户未登录，重定向到/timeout");
				response.sendRedirect(contextPath + "/timeout");
				return false;
			}
			// SystemUser systemUser = (SystemUser) sessionUser; //得到登录用户
			return super.preHandle(request, response, handler);
		} catch (Exception e) {
			//如果有session异常
			LOG.error("拦截器中获取session发生异常！请求URL为："+url,e);
			// 重定向到100异常
			response.sendRedirect(contextPath + "/returnStatusCode100");
			return false;
		}
	}

	/**
	 * 是否是静态资源URL
	 * 
	 * @author Jack
	 * @date 2015-12-25 上午11:10:24
	 * @param url
	 * @return
	 * @see
	 */
	private boolean isStaticResourceURL(String url) {
		if (StringUtils.isNotBlank(url)) {
			return url.startsWith(staticResourceURLPrefix);
		}
		return false;
	}

}
