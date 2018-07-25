package com.cdc.cdccmc.controller.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;

@Controller
@SessionAttributes(SysConstants.SESSION_USER)
public class PageController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PageController.class);

	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	
	////////////////////////// 基础信息   ////////////////////////////////////
	/**
	 * 基础信息——器具代码管理
	 */
	@RequestMapping("/page/basic/containerCode")
	public String containerCode(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/containerCode";
	}
	/**
	 * 基础信息——仓库区域管理
	 */
	@RequestMapping("/page/basic/area")
	public String area(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/area";
	}
	/**
	 * 基础信息——维修级别管理
	 */
	@RequestMapping("/page/basic/maintainLevel")
	public String maintainLevel(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/maintainLevel";
	}
	/**
	 * 基础信息——报废处理方式
	 */
	@RequestMapping("/page/basic/scrapway")
	public String scrapway(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/scrapway";
	}
	/**
	 * 基础信息——承运商管理
	 */
	@RequestMapping("/page/basic/shipper")
	public String shipper(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/shipper";
	}
	/**
	 * 基础信息——车牌号管理
	 */
	@RequestMapping("/page/basic/car")
	public String carList(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/basic/car";
	} 
	//////////////////////////  收发货   ////////////////////////////////////
	/**
	 * 收发货——采购预备表
	 */
	@RequestMapping("/page/purchase/purchasePrepare")
	public String purchasePrepare(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/purchase/purchasePrepare";
	} 
	/**
	 * 收发货——采购入库
	 */
	@RequestMapping("/page/purchase/purchaseInput")
	public String purchaseInput(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/purchase/purchaseInput";
	} 
	/**
	 * 收发货——收货
	 */
	@RequestMapping("/page/purchase/receiveOrder")
	public String receiveOrder(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/purchase/receiveOrder";
	} 
	/**
	 * 收发货——发货
	 */
	@RequestMapping("/page/purchase/purchaseDelivery")
	public String purchaseDelivery(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/purchase/purchaseDelivery";
	} 
	/**
	 * 收发货——手工流转单
	 */
	@RequestMapping("/page/purchase/manualCirculateOrder")
	public String manualCirculateOrder(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/purchase/manualCirculateOrder";
	} 
	//////////////////////////  器具信息   //////////////////////////////////// 
	/**
	 * 器具信息——器具列表
	 */
	@RequestMapping("/page/container/containerList")
	public String containerList(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/containerList";
	} 
	/**
	 * 器具信息——过时器具列表
	 */
	@RequestMapping("/page/container/outdatedContainer")
	public String outdatedContainer(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/outdatedContainer";
	} 
	/**
	 * 器具信息——器具最新流转状态
	 */
	@RequestMapping("/page/container/circulateLatest")
	public String circulateLatest(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/circulateLatest";
	} 
	/**
	 * 器具信息——包装流转单
	 */
	@RequestMapping("/page/container/circulateOrder")
	public String circulateOrder(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/circulateOrder";
	} 
	/**
	 * 器具信息——流转单收货详情
	 */
	@RequestMapping("/page/container/circulateDetail")
	public String orderDifferentDeal(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/circulateDetail";
	} 
	/**
	 * 器具信息——组托和解托
	 */
	@RequestMapping("/page/container/containerGroup")
	public String containerGroup(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/containerGroup";
	} 
	/**
	 * 器具信息——器具维修
	 */
	@RequestMapping("/page/container/containerMaintain")
	public String containerMaintain(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/containerMaintain";
	} 
	/**
	 * 器具信息——器具流转历史
	 */
	@RequestMapping("/page/container/circulateHistory")
	public String circulateHistory(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/container/circulateHistory";
	} 
	//////////////////////////  报表中心  ////////////////////////////////////
	/**
	 * 报表中心——索赔明细
	 */
	@RequestMapping("/page/report/claimDetail")
	public String claimDetail(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/report/claimDetail";
	} 
	/**
	 * 报表中心——丢失器具
	 */
	@RequestMapping("/page/report/lostContainer")
	public String lostContainer(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/report/lostContainer";
	} 
	//////////////////////////  器具盘点  ////////////////////////////////////
	/**
	 * 器具盘点——库存盘点
	 */
	@RequestMapping("/page/inventory/inventoryCheck")
	public String inventoryCheck(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/inventory/inventoryCheck";
	} 
	/**
	 * 器具盘点——盘点单详情
	 */
	@RequestMapping("/page/inventory/inventoryDetail")
	public String inventoryDetail(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/inventory/inventoryDetail";
	} 
	/**
	 * 器具盘点——盘点单统计
	 */
	@RequestMapping("/page/inventory/inventoryDetailSum")
	public String inventoryDetailSum(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/inventory/inventoryDetailSum";
	} 
	/**
	 * 器具盘点——当前库存
	 */
	@RequestMapping("/page/inventory/inventoryLatest")
	public String inventoryLatest(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/inventory/inventoryLatest";
	} 
	/**
	 * 器具盘点——库存历史
	 */
	@RequestMapping("/page/inventory/inventoryHistory")
	public String inventoryHistory(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/inventory/inventoryHistory";
	} 
	//////////////////////////  日志查询  ////////////////////////////////////
	/**
	 * 日志查询——用户操作日志
	 */
	@RequestMapping("/page/log/logAccount")
	public String logAccount(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/log/logAccount";
	} 
	/**
	 * 日志查询——用户登录日志
	 */
	@RequestMapping("/page/log/logLogin")
	public String logLogin(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/log/logLogin";
	} 
	/**
	 * 日志查询——用户错误日志
	 */
	@RequestMapping("/page/log/logError")
	public String logError(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/log/logError";
	} 
	//////////////////////////  系统管理  ////////////////////////////////////
	/**
	 *系统管理——工种管理
	 */
	@RequestMapping("/page/sys/job")
	public String job(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/job";
	}
	/**
	 *系统管理——用户管理
	 */
	@RequestMapping("/page/sys/user")
	public String user(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/user";
	}
	/**
	 *系统管理——用户web端权限管理
	 */
	@RequestMapping("/page/sys/menuWeb")
	public String menuWeb(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/menuWeb";
	}
	/**
	 *系统管理——用户app端权限管理
	 */
	@RequestMapping("/page/sys/menuApp")
	public String menuApp(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/menuApp";
	}
	/**
	 *系统管理——组织机构管理
	 */
	@RequestMapping("/page/sys/org")
	public String org(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/org";
	}

	/**
	 * 跳转到404页面
	 * @return
	 */
	@RequestMapping(value = "/404", method = RequestMethod.GET)
    public ModelAndView to404(){
        ModelAndView model = new ModelAndView("/404");
//        model.addObject("name", "Spring Boot");
        return model;
    }
	/**
	 * 跳转到500页面
	 * @return
	 */
	@RequestMapping(value = "/500", method = RequestMethod.GET)
    public ModelAndView to500(){
        ModelAndView model = new ModelAndView("500");
//        model.addObject("name", "Spring Boot");
        return model;
    }
	
	/**
	 * 默认首页
	 */
	@RequestMapping("/page/defaultPage")
	public String defaultPage(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser,HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		
		String defaultPageUri = "/blank"; //设置默认打开页为空白页
		one: for (SystemMenuWeb m : systemUser.getSystemMenuWebList()) {
			if(!CollectionUtils.isEmpty(m.getChildMenu())){
				List<SystemMenuWeb> childMenuList = m.getChildMenu();
				for(SystemMenuWeb childMenu : childMenuList){
					if(StringUtils.isNotBlank(childMenu.getMenuUri())){
						defaultPageUri = "redirect:"+childMenu.getMenuUri(); //设置找到的第一个有URI存在的菜单为默认打开菜单
						break one;
					}
				}
			}
		}
		
		return defaultPageUri;
	}

	/**
	 * URL辅助请求方法，包含确定当前请求URI
	 */
	private void requestUtil(HttpServletRequest request, ModelMap modelMap){
		String url = request.getRequestURI().toString();
		String contextPath = request.getContextPath();
		String currentUri = StringUtils.replace(url, contextPath, "");
		LOG.info("请求url: " + currentUri);
		modelMap.addAttribute("currentUri", currentUri);
	}

	/**
	 * 用户切换当前选中仓库
	 * @param map
	 * @return
	 */
	@RequestMapping("/page/changeCurrentOrg")
	public String changeCurrentOrg(HttpSession session, HttpServletRequest request,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,ModelMap modelMap,String orgId){
		LOG.info("请求url: /user/changeCurrentOrg, orgId="+orgId);
		SystemOrg currentSystemOrg = systemOrgService.findById(orgId);
		sessionUser.setCurrentSystemOrg(currentSystemOrg);
		LOG.info("用户["+sessionUser.getAccount()+"]切换仓库到orgId=["+orgId+"]orgName["+currentSystemOrg.getOrgName()+"]");
		//重新加载登录用户权限
		String result = systemUserService.loadUserAuthority(session,sessionUser,modelMap);
		//重新加载菜单列表，因为切换了仓库
		requestUtil(request, modelMap);
		return result;
	} 

	/**
	 * URL辅助请求方法，包含确定当前请求URI
	 */
	@RequestMapping("/page/buildLeftmenuHtml")
	@ResponseBody
	public AjaxBean buildLeftmenuHtml(HttpServletRequest request, ModelMap modelMap,String currentUri){
		
		//web端菜单html渲染代码
		SystemUser sessionUser = (SystemUser)request.getSession().getAttribute(SysConstants.SESSION_USER);
		String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		List<SystemMenuWeb> menuList = sessionUser.getSystemMenuWebList(); //获取用户的菜单权限列表
		StringBuffer leftmenuHtml = new StringBuffer();
		for(SystemMenuWeb menu : menuList){
			if(menu.getMenuLevel() == 1){
				leftmenuHtml.append("<li class=\"layui-nav-item ");   // 一级菜单包裹层
				leftmenuHtml.append(cssLayuiNavItemed(menu,currentUri));  //返回一个样式，决定一级菜单是否展开
				leftmenuHtml.append(" \">");
				leftmenuHtml.append("<a href=\""); 
				//如果uri不为空，则是uri链接，否则是js代码
				if(StringUtils.isNotBlank(menu.getMenuUri())){
					leftmenuHtml.append(basePath + menu.getMenuUri()); 
				}else{
					leftmenuHtml.append("javascript:;");
				}
				leftmenuHtml.append("\">"); 
				leftmenuHtml.append(menu.getMenuName());  //一级菜单名称
				leftmenuHtml.append(" </a>"); 
	    			
				  List<SystemMenuWeb> childMenuList = menu.getChildMenu(); 
				  //如果二级菜单不为空，增加二级菜单的html渲染代码
				  if(!CollectionUtils.isEmpty(childMenuList)){ 
						leftmenuHtml.append("<dl class=\"layui-nav-child\">");   //二级菜单包裹层
						for(SystemMenuWeb childMenu : childMenuList){  
							// class="layui-this"设置当前菜单为选中状态
							leftmenuHtml.append("<dd ");
							if(currentUri.equals(childMenu.getMenuUri())){ 
								leftmenuHtml.append("class=\"layui-this\" ");
							}
							leftmenuHtml.append(" > ");
							leftmenuHtml.append("<a href=\"");
							//如果uri不为空，则是uri链接，否则是js代码
							if(StringUtils.isNotBlank(childMenu.getMenuUri())){
								leftmenuHtml.append(basePath + childMenu.getMenuUri());
							}else{
								leftmenuHtml.append("javascript:;");
							}
							leftmenuHtml.append("\">");
							leftmenuHtml.append(childMenu.getMenuName()); //二级菜单名字
							leftmenuHtml.append("</a>");
							leftmenuHtml.append("</dd>");
							
						}
						leftmenuHtml.append("</dl>");  //二级菜单包裹层结束
				  }
				leftmenuHtml.append("</li>");  // 一级菜单包裹层结束
			}
		}
//		modelMap.addAttribute("leftmenuHtml", leftmenuHtml.toString());
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setMsg(leftmenuHtml.toString());
		return ajaxBean;
	}
	/**
	 * 返回CSS样式字符串：layui-nav-itemed  此样式决定一级菜单为展开状态。
	 * 如果返回空字符串，则一级菜单为收缩状态
	 * @param menu
	 * @param currentUri
	 * @return
	 */
	private String cssLayuiNavItemed(SystemMenuWeb menu,String currentUri){
		for(SystemMenuWeb childMenu : menu.getChildMenu()){
			if(childMenu.getMenuUri().equals(currentUri)){
				return "layui-nav-itemed";
			}
		}
		return "";
	}


	/**
	 * 打印机管理
	 */
	@RequestMapping("/page/sys/printer")
	public String printer(HttpServletRequest request, ModelMap modelMap){
		requestUtil(request, modelMap);
		return "/sys/printer";
	}
}
