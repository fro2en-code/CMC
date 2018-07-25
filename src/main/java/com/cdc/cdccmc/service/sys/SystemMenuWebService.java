package com.cdc.cdccmc.service.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
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

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.domain.sys.SystemUsermenu;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/** 
 * web端菜单
 * @author ZhuWen
 * @date 2018-01-10
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemMenuWebService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemMenuWebService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;

	@Value("#{sql['deleteSystemUsermenuWeb']}")
	private String deleteSystemUsermenuWeb;
	@Value("#{sql['insertSystemUsermenuWeb']}")
	private String insertSystemUsermenuWeb;
	@Value("#{sql['listSystemMenuWeb']}")
	private String listSystemMenuWeb;
	@Value("#{sql['insertSystemJobmenu']}")
	private String insertSystemJobmenu;
	@Value("#{sql['listSystemMenuWebByAccount']}")
	private String listSystemMenuWebByAccount;
	@Value("#{sql['queryMenuByJobAndOrgId']}")
	private String queryMenuByJobAndOrgId;
	@Value("#{sql['deleteSystemJobmenu']}")
	private String deleteSystemJobmenu;
	
	/**
	 * 根据账号和机构号获取该用户相对应的权限菜单
	 * @param account 账号
	 * @param orgId 机构ID
	 * @return
	 */
	public List<SystemMenuWeb> listSystemMenuWebByAccount(String account, String orgId){
		return jdbcTemplate.query(listSystemMenuWebByAccount, new BeanPropertyRowMapper(SystemMenuWeb.class), account,orgId);
	}
	/**
	 * 列出所有菜单
	 * @return
	 */ 
	public List<SystemMenuWeb> listAllMenu() {
		return jdbcTemplate.query(listSystemMenuWeb, new BeanPropertyRowMapper(SystemMenuWeb.class));
	}
	/**
	 * 更新指定账号、指定仓库下的，web端权限菜单
	 * @param memberOrg 指定仓库
	 * @param account 指定账号
	 * @param menuList 新的权限菜单
	 * @param sessionUser 当前登录用户
	 */
	@SuppressWarnings({ "rawtypes" })
	public void updateAccountMenuWeb(SystemOrg memberOrg, String account, List menuList,
			SystemUser sessionUser) {
		//删除用户旧的权限菜单
		jdbcTemplate.update(deleteSystemUsermenuWeb, account,memberOrg.getOrgId());
		//如果用户有新的菜单权限，则需要更新
		if(CollectionUtils.isNotEmpty(menuList)){
			//拿到全部菜单
			List<SystemMenuWeb> allMenuList = listAllMenu();
			//批量插入用户新的权限菜单
			List<SystemUsermenu> paramList = new ArrayList<SystemUsermenu>();
			List<String> menuNameList = new ArrayList<String>();
			for (int i = 0; i < menuList.size(); i++) {
				String menuId = menuList.get(i).toString();
				SystemUsermenu userMenu = new SystemUsermenu();
				userMenu.setAccount(account);
				userMenu.setOrgId(memberOrg.getOrgId());
				userMenu.setOrgName(memberOrg.getOrgName());
				userMenu.setCreateAccount(sessionUser.getAccount());
				userMenu.setCreateRealName(sessionUser.getRealName());
				two:for(SystemMenuWeb menu : allMenuList){
					if(menu.getMenuId().equals(menuId)){
						userMenu.setMenuId(menuId);
						userMenu.setMenuName(menu.getMenuName());
						menuNameList.add(menu.getMenuName());
						break two;
					}
				}
				paramList.add(userMenu);
			}
			namedJdbcTemplate.batchUpdate(insertSystemUsermenuWeb, SqlParameterSourceUtils.createBatch(paramList.toArray()));
			logService.addLogAccount(sessionUser, "更新账号["+account+"]["+memberOrg.getOrgName()+"]仓库下的新的web端菜单权限"+JSONObject.toJSONString(menuNameList));
		}else{
			logService.addLogAccount(sessionUser, "更新账号["+account+"]["+memberOrg.getOrgName()+"]仓库下的新的web端菜单权限为空");
		}
	}
	/**
	 * 获取指定仓库指定工种的权限菜单
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param jobId 指定工种 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SystemMenuWeb> queryMenuByJobAndOrgId(AjaxBean ajaxBean,
			SystemUser sessionUser, String jobId) {
		SystemJob job = systemJobService.findJobById(jobId);
	    List<SystemMenuWeb> menuList = jdbcTemplate.query(queryMenuByJobAndOrgId, new BeanPropertyRowMapper(SystemMenuWeb.class), 
	    		job.getOrgId(),jobId);
		return menuList;
	}
	/**
	 * 保存某个工种，新的web权限菜单和新的权限菜单列表
	 * @param job 工种
	 * @param menuList 新的权限菜单
	 * @param sessionUser 当前登录用户
	 */
	@SuppressWarnings("unchecked")
	public void updateJobMenu(SystemJob job, List menuList, SystemUser sessionUser) {
		//删除工种旧的权限菜单
		jdbcTemplate.update(deleteSystemJobmenu, job.getOrgId(),job.getJobId());
		if(!CollectionUtils.isEmpty(menuList)){
			//拿到全部菜单
			List<SystemMenuWeb> allMenuList = listAllMenu();
			
			Map<String, ?>[] batchValues = new HashMap[menuList.size()] ; //组织批量参数
			List<String> menuNameList = new ArrayList<String>();
			for (int i = 0; i < menuList.size(); i++) {
				String menuId = menuList.get(i).toString();
				Map param = new HashMap();
				param.put("orgId", job.getOrgId());
				param.put("jobId", job.getJobId());
				param.put("orgName", job.getOrgName());
				param.put("jobName", job.getJobName());
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				two:for(SystemMenuWeb menu : allMenuList){
					if(menu.getMenuId().equals(menuId)){
						param.put("menuId", menu.getMenuId());
						param.put("menuName", menu.getMenuName());
						menuNameList.add(menu.getMenuName());
						break two;
					}
				}
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertSystemJobmenu, batchValues);
		}
	}	
}
