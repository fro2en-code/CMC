package com.cdc.cdccmc.controller.web.manage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cdc.cdccmc.common.util.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.PageController;
import com.cdc.cdccmc.controller.web.car.CarController;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemMenuApp;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemButtonService;
import com.cdc.cdccmc.service.sys.SystemJobService;
import com.cdc.cdccmc.service.sys.SystemMenuAppService;
import com.cdc.cdccmc.service.sys.SystemMenuWebService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import com.cdc.cdccmc.service.sys.SystemUserService;

import org.springframework.web.multipart.MultipartFile;

/**
 * 用户管理
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class UserController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserController.class); 
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private SystemMenuWebService systemMenuWebService;
	@Autowired
	private SystemMenuAppService systemMenuAppService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemButtonService systemButtonService;
	@Autowired
	private PageController pageController;
	@Autowired
	private LogService logService;
	@Value("${upload.file.xlsx.path}")
	private String uploadPath;

	/**
	 * 添加新用户
	 * @param sessionUser 当前登录用户
	 * @param systemUser 要添加的新用户
	 * @param orgList 新用户的隶属公司列表
	 * @param jobList 新用户的权限工种列表
	 * @return
	 */
	@RequestMapping("/user/addSystemUser")
	public AjaxBean addSystemUser(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,@RequestParam(value = "orgList[]",required=false) List orgList 
			,@RequestParam(value = "jobList[]",required=false) List jobList
			,String accountInsert,String passwordInsert,String realNameInsert,String idCardNumInsert,String isDoorInsert){
		LOG.info("request URL /user/addSystemUser");
		
		//组装参数对象
		SystemUser systemUser = new SystemUser();
		systemUser.setAccount(accountInsert);
		systemUser.setPassword(passwordInsert);
		systemUser.setRealName(realNameInsert);
		systemUser.setIdCardNum(idCardNumInsert);
		systemUser.setIsDoor(Integer.valueOf(isDoorInsert));
		
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(systemUser.getAccount())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("账号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemUser.getPassword())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("密码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemUser.getRealName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("真实姓名"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isNotEmpty(systemUser.getIdCardNum().trim())) {
			if(!(systemUser.getIdCardNum().matches(SysConstants.REGEX_ID_CARD_NUM))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("身份证号码输入有误");
				return ajaxBean;
			}
		}
		if(CollectionUtils.isEmpty(orgList)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("用户隶属仓库"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		// 如果是门型账号，则只允许选择一个隶属仓库
		if(SysConstants.INTEGER_1 == systemUser.getIsDoor() && CollectionUtils.size(orgList) > 1) {
			ajaxBean.setStatus(StatusCode.STATUS_368);
			ajaxBean.setMsg(StatusCode.STATUS_368_MSG);
			return ajaxBean;
		}
		ajaxBean = systemUserService.addSystemUser(ajaxBean,sessionUser,systemUser,orgList,jobList);
		return ajaxBean;
	}
	/**
	 * 列出当前所选仓库的包含所有子公司的用户
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	@RequestMapping("/user/listAllUserByFiliale")
	public AjaxBean listAllUserByFiliale(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		LOG.info("request URL /user/listAllUserByFiliale");
		List<SystemUser> list = systemUserService.listAllUserByFiliale(sessionUser.getFilialeSystemOrgIds());
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(list);
		return ajaxBean;
	}
	/**
	 * 列出当前所选仓库的包含所有子公司的用户，支持分页，所以用户对象里会包含该用户的隶属仓库列表，和权限工种列表
	 * @param sessionUser 当前登录用户
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @return
	 */
	@RequestMapping("/user/pagingAllUserByFiliale")
	public Paging pagingAllUserByFiliale(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Paging paging
			,String selectAccount,String jobId,String orgId){
		LOG.info("request URL /user/pagingAllUserByFiliale");
		paging = systemUserService.pagingAllUserByFiliale(paging,sessionUser,selectAccount,jobId,orgId);
		return paging;
	}
	/**
	 * 查询某个用户的web权限菜单和权限按钮列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @param selectMemberOrgId 选中的隶属仓库 
	 * @return
	 */
	@RequestMapping("/user/listAccountMenuWeb")
	public AjaxBean listAccountMenuWeb(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String selectAccount
			,String selectMemberOrgId){
		LOG.info("request URL /user/listAccountMenuWeb");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		SystemUser user = new SystemUser();
		user.setAccount(selectAccount);
		//加载用户权限菜单
		List<SystemMenuWeb> menuList = systemMenuWebService.listSystemMenuWebByAccount(selectAccount, selectMemberOrgId);
		user.setSystemMenuWebList(menuList);
		//加载用户权限按钮
		List<SystemButton> buttonList = systemButtonService.listSystemButton(selectAccount, selectMemberOrgId);
		user.setSystemButtonList(buttonList);
		//加载用户工种列表
		List<SystemJob> jobList = systemJobService.listJobByAccountAndOrg(selectAccount, selectMemberOrgId);
		user.setSystemJobList(jobList);
		//返回用户到前端
		ajaxBean.setBean(user);
		return ajaxBean;
	}
	/**
	 * 获取当前登录用户的所有隶属仓库列表
	 */
	@RequestMapping("/user/listMemberOfOrg")
	public AjaxBean listMemberOfOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemOrg> filialeSystemOrgList = sessionUser.getMemberOfOrgList();
		ajaxBean.setList(filialeSystemOrgList);
		return ajaxBean;
	}
	/**
	 * 查询某个账号的隶属仓库，过滤后的隶属仓库
	 * @param sessionUser 当前登录用户 
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @return 过滤用户隶属仓库，只能看到当前登录用户有权限看到的仓库。否则当前登录用户就可以越权处理该账号的更高级别公司的菜单权限
	 */
	@RequestMapping("/user/listFilterMemberOrg")
	public AjaxBean listFilterMemberOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String selectAccount){
		LOG.info("request URL /user/listAccountMenuWeb");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(selectAccount)){
			ajaxBean.setMsg("请先选择一个账号");
			ajaxBean.setStatus(StatusCode.STATUS_201);
			return ajaxBean;
		}
		SystemUser user = new SystemUser();
		//加载账号的隶属仓库
		List<SystemOrg> memberOrgList = systemOrgService.listMemberOrgByAccount(selectAccount);
		//过滤用户隶属仓库，只能看到当前登录用户有权限看到的仓库。否则当前登录用户就可以越权处理该账号的更高级别公司的菜单权限
		List<SystemOrg> filterOrgList = new ArrayList<SystemOrg>();
		if(!CollectionUtils.isEmpty(memberOrgList)){ //如果指定账号的隶属仓库不为空
			List<SystemOrg> filialeOrgList = sessionUser.getFilialeSystemOrgList();
			for(SystemOrg filialeOrg : filialeOrgList){
				two:for(SystemOrg memberOrg : memberOrgList){
					if(filialeOrg.getOrgId().equals(memberOrg.getOrgId())){
						filterOrgList.add(memberOrg);
						break two;
					}
				}
			}
		}
		user.setMemberOfOrgList(filterOrgList);
		//返回到前端
		ajaxBean.setBean(user);
		return ajaxBean;
	}
	/**
	 * 查询某个用户的app权限菜单和权限按钮列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @return
	 */
	@RequestMapping("/user/listAccountMenuApp")
	public AjaxBean listAccountMenuApp(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String selectAccount,String selectMemberOrgId){
		LOG.info("request URL /user/listAccountMenuApp");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(selectAccount)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("账号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(selectMemberOrgId)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("隶属仓库"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		SystemUser user = new SystemUser();
		user.setAccount(selectAccount);
		//加载用户权限菜单
		List<SystemMenuApp> menuList = systemMenuAppService.listSystemMenuAppByAccount(selectAccount, selectMemberOrgId);
		user.setSystemMenuAppList(menuList);
		//加载用户工种列表
		List<SystemJob> jobList = systemJobService.listJobByAccountAndOrg(selectAccount, selectMemberOrgId);
		user.setSystemJobList(jobList);
		//返回用户到前端
		ajaxBean.setBean(user);
		return ajaxBean;
	}
	/**
	 * 启用或禁用指定账号
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @return
	 */
	@RequestMapping("/user/changeActiveUser")
	public AjaxBean changeActiveUser(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String accountInsert, Integer isActiveInsert){
		LOG.info("request URL /user/changeActiveUser");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		//加载用户权限菜单
		ajaxBean = systemUserService.changeActiveUser(ajaxBean,sessionUser,accountInsert,isActiveInsert);
		return ajaxBean;
	}
	/**
	 * 启用或禁用指定账号
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @return
	 */
	@RequestMapping("/user/changeDoorUser")
	public AjaxBean changeDoorUser(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String accountInsert, Integer isDoorInsert){
		LOG.info("request URL /user/changeDoorUser");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		//加载用户权限菜单
		ajaxBean = systemUserService.changeDoorUser(ajaxBean,sessionUser,accountInsert,isDoorInsert);
		return ajaxBean;
	}
	/**
	 * 编辑指定账号
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @param jobList 指定账号新的权限工种
	 * @return
	 */
	@RequestMapping("/user/editSystemUser")
	public AjaxBean editSystemUser(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,@RequestParam(value = "jobList[]",required=false) List jobList
			,@RequestParam(value = "orgList[]",required=false) List orgList
			,String accountEdit,String realNameEdit,String idCardNumEdit){
		LOG.info("request URL /user/editSystemUser");
		
		//组装参数对象
		SystemUser systemUser = new SystemUser();
		systemUser.setAccount(accountEdit);
		systemUser.setRealName(realNameEdit);
		systemUser.setIdCardNum(idCardNumEdit);
		
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(systemUser.getAccount())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("用户登录名"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemUser.getRealName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("真实姓名"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isNotEmpty(systemUser.getIdCardNum().trim())) {
			if(!(systemUser.getIdCardNum().matches(SysConstants.REGEX_ID_CARD_NUM))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("身份证号码输入有误");
				return ajaxBean;
			}
		}
		if(CollectionUtils.isEmpty(orgList)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("隶属机构"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//更新用户真实姓名、身份证号、隶属机构、隶属工种
		ajaxBean = systemUserService.editSystemUser(ajaxBean,sessionUser,systemUser,jobList,orgList);
		return ajaxBean;
	}
	/**
	 * 修改指定账号密码
	 * @param sessionUser 当前登录用户
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 * @param newPassword 新密码
	 * @param confirmPassword 确认新密码
	 * @return
	 */
	@RequestMapping("/user/changePassword")
	public AjaxBean changePassword(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String selectAccount
			,String newPassword,String confirmPassword ){
		LOG.info("request URL /user/changePassword");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(selectAccount)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("用户登录名"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(newPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("新密码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(confirmPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("确认新密码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(!newPassword.equals(confirmPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("两次密码输入不一致");
			return ajaxBean;
		}
        String passwordRegex = "[0-9a-zA-Z_]{6,15}";
		if(!newPassword.matches(passwordRegex)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("新密码必须为6到15位字母、数字、下划线的任意组合");
			return ajaxBean;
		}
		//更新用户隶属公司列表
		ajaxBean = systemUserService.changePassword(ajaxBean,sessionUser,selectAccount,newPassword);
		return ajaxBean;
	}
	
	/**
	 * 更改密码
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @return
	 */
	@RequestMapping("/user/queryUserAccount")
	public AjaxBean queryUserAccount(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String oldPassword, String newPassword, String confirmPassword, AjaxBean ajaxBean, String selectAccount){
		if(StringUtils.isBlank(oldPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("旧密码不能为空");
			return ajaxBean;
		}
		if(StringUtils.isBlank(newPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("新密码不能为空");
			return ajaxBean;
		}
		if(StringUtils.isBlank(confirmPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("确认新密码不能为空");
			return ajaxBean;
		}
		if(!newPassword.equals(confirmPassword)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("两次密码输入不一致");
			return ajaxBean;
		}
		String passwordRegex = "[0-9a-zA-Z_]{6,15}";
		if(!newPassword.matches(passwordRegex)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("新密码必须为6到15位字母、数字、下划线的任意组合");
			return ajaxBean;
		}
		//通过账户查用户的密码
		SystemUser findUser = systemUserService.findUserByAccount(selectAccount);
		if(null == findUser) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("账号["+selectAccount+"]"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		if(!findUser.getPassword().equals(Md5Util.md5(oldPassword))) {
			ajaxBean.setStatus(StatusCode.STATUS_107);
			ajaxBean.setMsg(StatusCode.STATUS_107_MSG);
			return ajaxBean;
		}
		ajaxBean = systemUserService.changePassword(ajaxBean,sessionUser,selectAccount,newPassword);
		return ajaxBean;
	}

	/**
	 * 用户模版下载
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/user/userExcelDownload", method = {RequestMethod.POST, RequestMethod.GET})
	public void downLoadFile(HttpServletRequest req, HttpServletResponse resp) {
		String fileDownName ="BatchInsert-Account.xlsx";
		ExcelUtil.downLoadExcel(req,resp,fileDownName);
	}

	/**
	 * excel 上传,入库
	 * @param multipartFile
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/user/batchUpload")
	public AjaxBean batchUpload(@RequestParam("file")MultipartFile multipartFile, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean){
		if(!multipartFile.getOriginalFilename().endsWith(SysConstants.XLSX)){
			ajaxBean.setStatus(StatusCode.STATUS_405);
			ajaxBean.setMsg(StatusCode.STATUS_405_MSG);
			return ajaxBean;
		}
		long fileSize = multipartFile.getSize();
		if(fileSize > SysConstants.MAX_FILE_UPLOAD_SIZE){ //如果文件大小超出上限
			new BigDecimal(SysConstants.MAX_FILE_UPLOAD_SIZE);
			ajaxBean.setStatus(StatusCode.STATUS_409);
			ajaxBean.setMsg(StatusCode.STATUS_409_MSG);
			return ajaxBean;
		}
		String[] endWith = multipartFile.getOriginalFilename().split("\\.");
		File file = new File(uploadPath +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-Account]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()){
			fileParent.mkdirs();
		}

		try {
			multipartFile.transferTo(file); //上传到服务器
		} catch (IllegalStateException | IOException e) {
			LOG.error(e.getMessage(),e);
			logService.addLogError(sessionUser, e, e.getMessage(), null);
			
			ajaxBean.setStatus(StatusCode.STATUS_400);
			ajaxBean.setMsg(StatusCode.STATUS_400_MSG);
			return ajaxBean;
		}
		ajaxBean = systemUserService.batchUpload(sessionUser,file);
		return ajaxBean;
	}

}
