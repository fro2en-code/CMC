package com.cdc.cdccmc.service.sys;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpSession;

import com.cdc.cdccmc.common.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.door.DoorScan;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemJobbutton;
import com.cdc.cdccmc.domain.sys.SystemJobmenu;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.domain.sys.SystemUserbutton;
import com.cdc.cdccmc.domain.sys.SystemUserjob;
import com.cdc.cdccmc.domain.sys.SystemUsermenu;
import com.cdc.cdccmc.domain.sys.SystemUserorg;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.domain.sys.SystemUserprint;

/**
 * 用户
 * @author ZhuWen
 * @date 2018-01-03
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemUserService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemUserService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private SystemMenuWebService systemMenuWebService;
	@Autowired
	private SystemButtonService systemButtonService;
	@Autowired
	private LogService logService;

	private static final String QUERY_USER_BY_ACCOUNT = "select * from T_SYSTEM_USER where account = :account";
	
	@Value("#{sql['insertSystemUser']}")
	private String insertSystemUser;
	@Value("#{sql['insertMemberOrg']}")
	private String insertMemberOrg;
	@Value("#{sql['insertSystemUserJob']}")
	private String insertSystemUserJob;
	@Value("#{sql['changePassword']}")
	private String changePassword;
	@Value("#{sql['updateLastLoginTime']}")
	private String updateLastLoginTime;
	@Value("#{sql['findUserByAccount']}")
	private String findUserByAccount;
	@Value("#{sql['querySystemJobmenuByOrgidAndJobId']}")
	private String querySystemJobmenuByOrgidAndJobId;
	@Value("#{sql['insertSystemUsermenuWeb']}")
	private String insertSystemUsermenuWeb;
	@Value("#{sql['querySystemJobbuttonByOrgidAndJobId']}")
	private String querySystemJobbuttonByOrgidAndJobId;
	@Value("#{sql['insertSystemUserbutton']}")
	private String insertSystemUserbutton;

	public SystemUser querySystemUserByAccount(String account) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		paramMap.put("account", account);
		List<SystemUser> systemUserList = namedJdbcTemplate.query(QUERY_USER_BY_ACCOUNT, paramMap,
				new BeanPropertyRowMapper(SystemUser.class));
		if (CollectionUtils.isEmpty(systemUserList)) {
			return null;
		}
		return systemUserList.get(0);
	}

	/**
	 * 加载登录用户权限
	 * @param sessionUser
	 */
	public String loadUserAuthority(HttpSession session, SystemUser sessionUser, ModelMap modelMap) {
		SystemOrg currentSystemOrg = sessionUser.getCurrentSystemOrg();
//		List<SystemOrg> memberOfOrgList = sessionUser.getMemberOfOrgList();

		// 设置systemUser对象的两个字段值：filialeSystemOrgList、filialeSystemOrgIds
		sessionUser = systemOrgService.loadFilialeOrg(sessionUser);
		
		/* 以下代码暂时保留，勿删！
		 * 
		// 加载登录用户的当前选中仓库的web端菜单权限
		List<SystemMenuWeb> menuWebList = systemMenuWebService.listSystemMenuWebByAccount(sessionUser.getAccount(),
				currentSystemOrg.getOrgId());
		// 一二级菜单重组，方便jsp页面一二层级渲染
		List<SystemMenuWeb> menuList = new ArrayList<SystemMenuWeb>();
		for (SystemMenuWeb menu : menuWebList) {
			if (menu.getMenuLevel() == 1) { // 如果是一级菜单
				for (SystemMenuWeb childMenu : menuWebList) { // 查找该一级菜单下的所有二级子菜单
					if (childMenu.getMenuLevel() == 2 && childMenu.getParentMenuId().equals(menu.getMenuId())) {
						menu.getChildMenu().add(childMenu); // 往一级菜单里添加一个二级子菜单
					}
				}
				menuList.add(menu); // 经过组合的一二级菜单重新放入一个新的集合里
			}
		}
		sessionUser.setSystemMenuWebList(menuList);*/
		//加载指定用户的指定仓库的web端菜单权限
		sessionUser.setSystemMenuWebList(buildSystemMenuWebList( sessionUser.getAccount(), currentSystemOrg.getOrgId()));

		List<SystemMenuWeb> webList =sessionUser.getSystemMenuWebList();
		List<String> menuNameList = new ArrayList<String>();
		for (SystemMenuWeb w : webList) {
			menuNameList.add(w.getMenuName());
		}
		// 用户登录成功，把用户放入session
		LOG.info("[" + sessionUser.getAccount() + "]用户登录成功，菜单权限："
				+ JSONObject.toJSONString(menuNameList));
		logService.addLogLogin(sessionUser,
				"用户登录成功，当前选择仓库为[" + currentSystemOrg.getOrgId() + "][" + currentSystemOrg.getOrgName() + "]");
		session.setAttribute(SysConstants.SESSION_USER, sessionUser);

		if (CollectionUtils.isEmpty(sessionUser.getSystemMenuWebList())) { // 如果当前选中仓库没有任何菜单权限
			LOG.info("用户[" + sessionUser.getAccount() + "]机构[" + currentSystemOrg.getOrgId() + "]["
					+ currentSystemOrg.getOrgName() + "]" + StatusCode.STATUS_103_MSG);
			modelMap.put("errmsg", "[" + sessionUser.getAccount() + "]" + StatusCode.STATUS_103_MSG);
			return "/noMenuPermission";
		}

		// 加载用户的页面的按钮权限，也就是可以看到哪些按钮
		List<SystemButton> systemButtonList = systemButtonService.listSystemButton(sessionUser.getAccount(),
				currentSystemOrg.getOrgId());
		sessionUser.setSystemButtonList(systemButtonList);
		return "redirect:/page/defaultPage";
	}
	/**
	 * 加载指定用户的指定仓库的web端菜单权限
	 * @param account 指定用户
	 * @param orgId 指定仓库
	 * @return
	 */
	public List<SystemMenuWeb> buildSystemMenuWebList(String account,String orgId){
		// 加载登录用户的当前选中仓库的web端菜单权限
		List<SystemMenuWeb> menuWebList = systemMenuWebService.listSystemMenuWebByAccount(account,orgId);
		// 一二级菜单重组，方便jsp页面一二层级渲染
		List<SystemMenuWeb> menuList = new ArrayList<SystemMenuWeb>();
		for (SystemMenuWeb menu : menuWebList) {
			if (menu.getMenuLevel() == 1) { // 如果是一级菜单
				for (SystemMenuWeb childMenu : menuWebList) { // 查找该一级菜单下的所有二级子菜单
					if (childMenu.getMenuLevel() == 2 && childMenu.getParentMenuId().equals(menu.getMenuId())) {
						menu.getChildMenu().add(childMenu); // 往一级菜单里添加一个二级子菜单
					}
				}
				menuList.add(menu); // 经过组合的一二级菜单重新放入一个新的集合里
			}
		}
		return menuWebList;
	}

	/**
	 * 查询所有分公司的用户列表
	 * @param filialeSystemOrgIds 拼接好的orgId字符串，例如： '123','456','789'
	 * @return
	 */
	public List<SystemUser> listAllUserByFiliale(String filialeSystemOrgIds) {
		String sql = "select DISTINCT u.* from t_system_userorg uo,t_system_user u where uo.org_id in ( "
				+ filialeSystemOrgIds + " ) and uo.account = u.account";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(SystemUser.class));
	}

	/**
	 * 添加新用户
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemUser 要添加的新用户
	 * @param jobList 新用户的权限工种列表
	 * @param orgList 新用户的隶属机构列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean addSystemUser(AjaxBean ajaxBean, SystemUser sessionUser, SystemUser systemUser, List orgList,
			List jobList) {
		//检查账号是否已存在
		SystemUser findUser = findUserByAccount(systemUser.getAccount());
		if(null != findUser){
			ajaxBean.setStatus(StatusCode.STATUS_108);
			ajaxBean.setMsg("["+systemUser.getAccount()+"]"+StatusCode.STATUS_108_MSG);
			return ajaxBean;
		}
		// 组建SQL参数
		Map paramMap = new HashMap();
		paramMap.put("account", systemUser.getAccount());
		paramMap.put("password", Md5Util.md5(systemUser.getPassword()));
		paramMap.put("realName", systemUser.getRealName());
		paramMap.put("idCardNum", systemUser.getIdCardNum());
		if (systemUser.getIsActive() == null) {
			systemUser.setIsActive(0);
		}
		paramMap.put("isActive", systemUser.getIsActive());
		paramMap.put("isDoor", systemUser.getIsDoor());
		paramMap.put("createAccount", sessionUser.getAccount());
		paramMap.put("createRealName", sessionUser.getRealName());

		// 新增用户
		int result = namedJdbcTemplate.update(insertSystemUser, paramMap);
		if (result == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("新增用户失败");
			return ajaxBean;
		}
		logService.addLogAccount(sessionUser, "添加新账号["+systemUser.getAccount()+"]["+systemUser.getRealName()+"]");
		//初始化新增用户隶属仓库
		initNewUserSystemOrg(sessionUser,systemUser,orgList);
		//新增用户权限工种
		initNewUserSystemJob(sessionUser,systemUser,jobList);
		return ajaxBean;
	}
	/**
	 * 初始化新增用户隶属仓库
	 */
	@SuppressWarnings("unchecked")
	private void initNewUserSystemOrg(SystemUser sessionUser, SystemUser systemUser,List orgList){
		Map<String, ?>[] batchValues = new HashMap[orgList.size()];
		List<String> orgNameList = new ArrayList<String>();
		for (int i = 0; i < orgList.size(); i++) {
			String orgId = orgList.get(i).toString();
			Map param = new HashMap();
			param.put("account", systemUser.getAccount());
			param.put("realName", systemUser.getRealName());
			param.put("createAccount", sessionUser.getAccount());
			param.put("createRealName", sessionUser.getRealName());
			param.put("orgId", orgId);
			String orgName = systemOrgService.findById(orgId).getOrgName();
			orgNameList.add(orgName);
			param.put("orgName", orgName);
			batchValues[i] = param;
		}
		namedJdbcTemplate.batchUpdate(insertMemberOrg, batchValues);
		logService.addLogAccount(sessionUser,"添加账号[" + systemUser.getAccount() + "]的隶属机构" + orgNameList);
	}
	/**
	 * 新增用户权限工种
	 */
	private void initNewUserSystemJob(SystemUser sessionUser,SystemUser systemUser,List jobList){
		if (!CollectionUtils.isEmpty(jobList)) {
			List<SystemUserjob> batchList = new ArrayList<SystemUserjob>();
			List<SystemJob> findJobList = new ArrayList<SystemJob>();
			for (int i = 0; i < jobList.size(); i++) {
				String jobId = jobList.get(i).toString();
				SystemUserjob job = new SystemUserjob();
				job.setAccount(systemUser.getAccount());
				job.setCreateAccount(sessionUser.getAccount());
				job.setCreateRealName(sessionUser.getRealName());
				job.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
				//获取工种对象
				SystemJob findJob = systemJobService.findJobById(jobId);
				findJobList.add(findJob);
				job.setJobId(jobId);
				job.setJobName(findJob.getJobName());
				batchList.add(job);
			}
			namedJdbcTemplate.batchUpdate(insertSystemUserJob, SqlParameterSourceUtils.createBatch(batchList.toArray()));
			logService.addLogAccount(sessionUser,"添加账号[" + systemUser.getAccount() + "]的权限工种: " + JSONObject.toJSONString(batchList));
			//初始化新增用户web菜单权限，t_system_usermenu_web中间表
			initNewUserSystemMenuWeb(sessionUser,systemUser,findJobList);
			//初始化新增用户按钮权限，t_system_userbutton表
			initNewUserSystemButton(sessionUser,systemUser,findJobList);
		}
	}
	
	/**
	 * 初始化新增用户web菜单权限，t_system_usermenu_web中间表
	 */
	private void initNewUserSystemMenuWeb(SystemUser sessionUser,SystemUser systemUser,List<SystemJob> findJobList){

		//根据权限工种自带的web端菜单权限，初始化用户web端菜单权限
		List<SystemUsermenu> menuList = new ArrayList<SystemUsermenu>();
		for (SystemJob job : findJobList) {
			//根据仓库ID和工种ID，获取相应菜单
			List<SystemJobmenu> findMenuList = jdbcTemplate.query(querySystemJobmenuByOrgidAndJobId
					, new BeanPropertyRowMapper(SystemJobmenu.class),sessionUser.getCurrentSystemOrg().getOrgId()
					,job.getJobId());
			//web菜单集合
			for(SystemJobmenu m : findMenuList){
				SystemUsermenu menuWeb = new SystemUsermenu();
				menuWeb.setMenuId(m.getMenuId());
				menuWeb.setMenuName(m.getMenuName());
				if(!menuList.contains(menuWeb)){ //集合里面去重
					menuWeb.setAccount(systemUser.getAccount());
					menuWeb.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
					menuWeb.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
					menuWeb.setCreateAccount(sessionUser.getAccount());
					menuWeb.setCreateRealName(sessionUser.getRealName());
					menuList.add(menuWeb);
				}
			}
		}
		if(CollectionUtils.isNotEmpty(menuList)){
			//初始化用户web端菜单权限
			namedJdbcTemplate.batchUpdate(insertSystemUsermenuWeb, SqlParameterSourceUtils.createBatch(menuList.toArray()));
			logService.addLogAccount(sessionUser,"添加账号[" + systemUser.getAccount() + "]的新的菜单权限: " + JSONObject.toJSONString(menuList));
			menuList = null;
		}
	}
	/**
	 * 初始化新增用户按钮权限，t_system_userbutton表
	 */
	@SuppressWarnings("unchecked")
	private void initNewUserSystemButton(SystemUser sessionUser,SystemUser systemUser,List<SystemJob> findJobList){
		//根据权限工种自带的web端菜单权限，初始化用户web端按钮权限
		List<SystemUserbutton> buttonList = new ArrayList<SystemUserbutton>();
		for (SystemJob job : findJobList) {
			//根据仓库ID和工种ID，获取相应按钮
			List<SystemJobbutton> findButtonList = jdbcTemplate.query(querySystemJobbuttonByOrgidAndJobId
					, new BeanPropertyRowMapper(SystemJobbutton.class),sessionUser.getCurrentSystemOrg().getOrgId()
					,job.getJobId());
			//按钮集合
			for (SystemJobbutton b : findButtonList) {
				SystemUserbutton button = new SystemUserbutton();
				button.setButtonId(b.getButtonId());
				button.setButtonName(b.getButtonName());
				if(!buttonList.contains(button)){
					button.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
					button.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
					button.setAccount(systemUser.getAccount());
					button.setCreateAccount(sessionUser.getAccount());
					button.setCreateRealName(sessionUser.getRealName());
					buttonList.add(button);
				}
			}
			if(CollectionUtils.isNotEmpty(buttonList)){
				//初始化用户按钮权限
				namedJdbcTemplate.batchUpdate(insertSystemUserbutton, SqlParameterSourceUtils.createBatch(buttonList.toArray()));
				buttonList = null;
			}
		}
	}

	/**
	 * 
	 * @param paging
	 * @param sessionUser 当前登录用户
	 * @param orgId 查询条件——仓库ID
	 * @param jobId 查询条件——工种ID
	 * @param account 账号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Paging pagingAllUserByFiliale(Paging paging, SystemUser sessionUser, String account, String jobId,
			String orgId) {
		String sql = "select distinct u.* from t_system_user u "
				+ " left join t_system_userorg uo on u.account = uo.account "
				+ " left join t_system_userjob uj on u.account = uj.account  " + " where uo.org_id in ( "
				+ sessionUser.getFilialeSystemOrgIds() + " ) ";
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(account)) {
			sql += " and uo.account = :account ";
			paramMap.put("account", account);
		}
		if (StringUtils.isNotBlank(orgId)) {
			sql += " and uo.org_id = :orgId ";
			paramMap.put("orgId", orgId);
		}
		if (StringUtils.isNotBlank(jobId)) {
			sql += " and uj.job_id = :jobId ";
			paramMap.put("jobId", jobId);
		}
		sql += " order by create_time desc ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, SystemUser.class);
		if (paging.getData() != null && !CollectionUtils.isEmpty((List<SystemUser>) paging.getData())) {
			for (SystemUser user : (List<SystemUser>) paging.getData()) {
				List<SystemOrg> systemOrgList = systemOrgService.listMemberOrgByAccount(user.getAccount());
				user.setMemberOfOrgList(systemOrgList);
				List<SystemJob> systemJobList = systemJobService.listJobByAccountAndOrg(user.getAccount(),
						sessionUser.getCurrentSystemOrg().getOrgId());
				user.setSystemJobList(systemJobList);
			}
		}
		return paging;
	}

	/**
	 * 启用或禁用指定账号
	 * 
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @return
	 */
	public AjaxBean changeActiveUser(AjaxBean ajaxBean, SystemUser sessionUser,String accountInsert, Integer isActiveInsert) {
		String sql = "update t_system_user set is_active =?,modify_time=sysdate(),modify_account=?,modify_real_name=? where account = ?";
		int result = jdbcTemplate.update(sql, isActiveInsert, sessionUser.getAccount(),
				sessionUser.getRealName(), accountInsert);
		logService.addLogAccount(sessionUser,
				"[" + (isActiveInsert == 0 ? "启用" : "禁用") + "]账号[" + accountInsert + "]");
		return AjaxBean.returnAjaxResult(result);
	}
	/**
	 * 是否是门型设备账号:1是、0否。设置值
	 * @param ajaxBean
	 * @param sessionUser
	 * @param accountInsert
	 * @param isDoorInsert
	 * @return
	 */
	public AjaxBean changeDoorUser(AjaxBean ajaxBean, SystemUser sessionUser, String accountInsert,
			Integer isDoorInsert) {
		String sql = "update t_system_user set is_door =?,modify_time=sysdate(),modify_account=?,modify_real_name=? where account = ?";
		int result = jdbcTemplate.update(sql, isDoorInsert, sessionUser.getAccount(),
				sessionUser.getRealName(), accountInsert);
		logService.addLogAccount(sessionUser,
				"把账号[" + accountInsert + "]设置“是否门型设备账号”为[" + (isDoorInsert == 0 ? "否" : "是") + "]");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 编辑用户，更新指定用户的姓名、身份证号、权限工种列表
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定用户
	 * @param jobList 指定用户新的工种权限
	 * @param orgList 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean editSystemUser(AjaxBean ajaxBean, SystemUser sessionUser, SystemUser systemUser, List jobList, List orgList) {
		// 更新指定账号的姓名、身份证号
		String updateUser = "update t_system_user set real_name=:realName, id_card_num=:idCardNum,modify_time=sysdate() "
				+ " ,modify_account =:modifyAccount,modify_real_name=:modifyRealName where account =:account";
		Map param = new HashMap();
		param.put("realName", systemUser.getRealName());
		param.put("idCardNum", systemUser.getIdCardNum());
		param.put("modifyAccount", sessionUser.getAccount());
		param.put("modifyRealName", sessionUser.getRealName());
		param.put("account", systemUser.getAccount());
		namedJdbcTemplate.update(updateUser, param);
		logService.addLogAccount(sessionUser, "更新用户["+systemUser.getAccount()+"]的姓名、身份证号为["+systemUser.getRealName()+"]["+systemUser.getIdCardNum()+"]");

		// 更新指定账号的工种权限列表
		systemJobService.updateUserJob(ajaxBean, sessionUser, systemUser, jobList);
		// 更新指定账号的隶属机构权限列表
		systemOrgService.updateUserMemberOrg(ajaxBean, sessionUser, systemUser, orgList);
		return ajaxBean;
	}

	/**
	 * 修改指定账号密码
	 * @param sessionUser 当前登录用户
	 * @param account 指定账号
	 * @param newPassword 新密码
	 * @return
	 */
	public AjaxBean changePassword(AjaxBean ajaxBean, SystemUser sessionUser, String account, String newPassword) {
		int result = jdbcTemplate.update(changePassword, Md5Util.md5(newPassword), account);
		logService.addLogAccount(sessionUser, "修改账号[" + account + "]的登录密码");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 修改当前用户最后登录时间
	 * @param sessionUser
	 * @param account
	 */
	public void updateLastLoginTime(SystemUser sessionUser,Timestamp now, String account) {
		jdbcTemplate.update(updateLastLoginTime,now, account);
	}

	/**
	 * 通过账户查用户
	 * @param account
	 * @return
	 */
	public SystemUser findUserByAccount(String account) {
		List<SystemUser> list = jdbcTemplate.query(findUserByAccount,new BeanPropertyRowMapper(SystemUser.class), account);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 批量保存上传excel的用户
	 * @param sessionUser
	 * @param file
	 * @return
	 */
    public AjaxBean batchUpload(SystemUser sessionUser, File file) {
		//数据校验
		AjaxBean ajaxBean = validExcelData(sessionUser, file.getPath());
		//如果校验不通过，直接返回
		if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		ConcurrentLinkedQueue<SystemUser> userList = new ConcurrentLinkedQueue<>();
		//校验通过,构建map数组
		List<Map<Integer, String>> excelList = ajaxBean.getList();
		for (int i = 0; i < excelList.size(); i++) {
			if(null == excelList.get(i)){ //如果是空行，直接跳过
				continue;
			}
			SystemUser user = new SystemUser();
			user.setAccount(StringUtils.trim(excelList.get(i).get(0)));
			user.setRealName(StringUtils.trim(excelList.get(i).get(1)));
			user.setIdCardNum(StringUtils.trim(excelList.get(i).get(2)));
			user.setPassword("123456");
			user.setCreateAccount(sessionUser.getAccount());
			user.setCreateRealName(sessionUser.getRealName()); 
			//添加车辆到新增队列
			userList.add(user);

			//如果达到批量插入条数，就进行批量插入
			if(userList.size() >= SysConstants.MAX_INSERT_NUMBER){
				this.batchInsertUser(sessionUser,userList);
				userList.clear();
			}
		}
		//如果还有需要批量插入的数据
		if(userList.size() > SysConstants.INTEGER_0){
			batchInsertUser(sessionUser,userList);
			userList.clear();
		}
		return ajaxBean;
    }

	/**
	 * 用户批量导入
	 * @param sessionUser 
	 * @param userList
	 */
	private void batchInsertUser(SystemUser sessionUser, ConcurrentLinkedQueue<SystemUser> userList) {
		String insertUser = "INSERT INTO t_system_user (account, password, real_name, id_card_num, create_time, create_account, create_real_name ) VALUES (:account, :password, :realName, :idCardNum, sysdate(), :createAccount, :createRealName)";
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(userList.toArray());
		namedJdbcTemplate.batchUpdate(insertUser, params);
		//批量导入时，新账号隶属仓库为当前选择仓库
		List<SystemUserorg> batchParam = new ArrayList<SystemUserorg>();
		Iterator<SystemUser> it = userList.iterator();
		while (it.hasNext()) {
			SystemUser u = it.next();
			SystemUserorg uo = new SystemUserorg();
			uo.setAccount(u.getAccount());
			uo.setRealName(u.getRealName());
			uo.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			uo.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			uo.setCreateAccount(sessionUser.getAccount());
			uo.setCreateRealName(sessionUser.getRealName());
			batchParam.add(uo);
			//如果达到批量插入条数，就进行批量插入
			if(batchParam.size() >= SysConstants.MAX_INSERT_NUMBER){
				SqlParameterSource[] batchPramSource = SqlParameterSourceUtils.createBatch(batchParam.toArray());
				namedJdbcTemplate.batchUpdate(insertMemberOrg, batchPramSource);
				logService.addLogAccount(sessionUser, "批量导入[" + userList.size() + "]条");
				LOG.info("批量导入账号[" + userList.size() + "]条");
				batchParam.clear();
			}
		}
		//如果达到批量插入条数，就进行批量插入
		if(batchParam.size() > SysConstants.INTEGER_0){
			SqlParameterSource[] batchPramSource = SqlParameterSourceUtils.createBatch(batchParam.toArray());
			namedJdbcTemplate.batchUpdate(insertMemberOrg, batchPramSource);
			logService.addLogAccount(sessionUser, "批量导入[" + userList.size() + "]条");
			LOG.info("批量导入账号[" + userList.size() + "]条");
		}
	}

	/**
	 * excel数据校验
	 * @param path
	 * @return
	 */
	private AjaxBean validExcelData(SystemUser sessionUser, String path) {
		AjaxBean ajaxBean = new AjaxBean();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,3);
		} catch (Exception e) { //文件批量导入失败！
			LOG.error(StatusCode.STATUS_402_MSG, e);
			logService.addLogError(sessionUser, e, StatusCode.STATUS_402_MSG, null);
			ajaxBean.setStatus(StatusCode.STATUS_402);
			ajaxBean.setMsg(StatusCode.STATUS_402_MSG);
			return ajaxBean;
		}
		if(mapList.size() == 0){ //如果数据一行都没有，未检测到需导入数据，请检查上传文件内数据输入是否正确。
			LOG.error(StatusCode.STATUS_403_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_403);
			ajaxBean.setMsg(StatusCode.STATUS_403_MSG);
			return ajaxBean;
		}
		//校验最大上传行数
		if (mapList.size() > SysConstants.MAX_UPLOAD_ROWS){
			LOG.error(StatusCode.STATUS_401_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_401);
			ajaxBean.setMsg(StatusCode.STATUS_401_MSG);
			return ajaxBean;
		}

		List<String> errList = new ArrayList<String>();
		LOG.info("本次需批量导入"+mapList.size()+"条用户数据");

		int row = 1;
		for(Map<Integer, String> map : mapList){
			row = row + 1;
			if(null == map){  //如果是空行，直接跳过
				continue;
			}
			String account = map.get(0);
			if(StringUtils.isBlank(account)){
				errList.add("第"+row+"行，登录名不能为空。");
			}else if(account.length() > 50){
				errList.add("第"+row+"行，登录名长度不能超过50个字符。");
			}else{
				SystemUser u = this.querySystemUserByAccount(account);
				if (u != null){
					errList.add("第"+row+"行，登录名["+account+"]已存在，不能新增。");
				}else{
					int row2 = 1;
	            	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
	            		if(null != map2 && map.get(0).equals(map2.get(0)) && row != row2){ //如果excel内发现重复EPC编号
	            			errList.add("第"+row+"行，登录名["+account+"]在excel内重复，请核查。");
	            			break two;
	            		}
	            	}
				}
			}

			String realName = map.get(1);
			if(StringUtils.isBlank(realName)){
				errList.add("第"+row+"行，真实姓名不能为空。");
			}else if(realName.length() > 35){
				errList.add("第"+row+"行，真实姓名长度不能超过35个字符。");
			}

			String idCardNum = map.get(2);
			if(StringUtils.isNotBlank(idCardNum) && !(idCardNum.matches(SysConstants.REGEX_ID_CARD_NUM))){
				errList.add("第"+row+"行，身份证号有误。");
			}
		}
		if (errList.size() > 0) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setList(errList);
		} else {
			ajaxBean.setList(mapList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		}
		return ajaxBean;
	}

	public String getUserPrintCode(SystemUser systemUser)
	{
		//查询t_system_userprint  account  org_id
		String sql="select * from t_system_userprint where account=? and org_id =?";
		List<SystemUserprint> systemUserprints =  jdbcTemplate.query(sql, new BeanPropertyRowMapper(SystemUserprint.class), systemUser.getAccount(),systemUser.getCurrentSystemOrg().getOrgId());

		if(CollectionUtils.isNotEmpty(systemUserprints))
		{
			return systemUserprints.get(0).getPrintCode();
		}
		return null;
	}

	/**
	 * 打印流转单后 需要清空流转单对应的门型SESSION数据 不能再回退数据
	 * @param session
	 * @param orderCode
	 */
	public void clearDoorScanSessionCacheByOrderCode(HttpSession session,String orderCode)
	{
		LOG.info("---clearDoorScanSessionCacheByOrderCode   orderCode="+orderCode+"   session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE)="+session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE));
		if(null != session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE))
		{
			if(orderCode.equals(session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE).toString()))
			{
				clearDoorScanSessionCache(session);
			}
		}
	}
	/**
	 * 清空门型SESSION数据 不能再回退数据
	 * @param session
	 */
	public void clearDoorScanSessionCache(HttpSession session)
	{
		session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE, null);
		session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD, new ArrayList<DoorScan>());
		session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD_EPCIDS,null);
	}
}
