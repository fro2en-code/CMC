package com.cdc.cdccmc.service.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemMenuApp;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/** 
 * web端菜单
 * @author ZhuWen
 * @date 2018-01-12
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemMenuAppService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemMenuAppService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private LogService logService;
	@Autowired
	private SystemOrgService systemOrgService;
	
	@Value("#{sql['insertSystemUsermenuApp']}")
	private String insertSystemUsermenuApp;
	@Value("#{sql['listSystemMenuAppByAccount']}")
	private String listSystemMenuAppByAccount;
	@Value("#{sql['listAllSystemMenuApp']}")
	private String listAllSystemMenuApp;
	@Value("#{sql['deleteSystemUsermenuAppByAccountAndOrgId']}")
	private String deleteSystemUsermenuAppByAccountAndOrgId;
	/**
	 * 根据账号和机构号获取该用户相对应的权限菜单
	 * @param account 账号
	 * @param orgId 机构ID
	 * @return
	 */
	public List<SystemMenuApp> listSystemMenuAppByAccount(String account, String orgId){
		return jdbcTemplate.query(listSystemMenuAppByAccount, new BeanPropertyRowMapper(SystemMenuApp.class), account,orgId);
	}
	/**
	 * 列出所有菜单
	 * @return
	 */ 
	public List<SystemMenuApp> listAllMenu() {
		return jdbcTemplate.query(listAllSystemMenuApp, new BeanPropertyRowMapper(SystemMenuApp.class));
	}
	/**
	 * 列出所有菜单，并且支持分页
	 * @param paging
	 * @return
	 */
	public Paging pagingAllMenu(Paging paging) {
		paging = baseService.pagingParamMap(paging, listAllSystemMenuApp, new HashMap(), SystemMenuApp.class);
		return paging;
	}
	/**
	 * 更新指定账号的app端权限菜单
	 * @param account 指定账号
	 * @param menuList 新的权限菜单
	 * @param sessionUser 当前登录用户
	 */
	@SuppressWarnings("unchecked")
	public void updateAccountMenuApp(String account,String selectMemberOrgId, List menuList,
			SystemUser sessionUser) {
		//删除用户旧的权限菜单
		jdbcTemplate.update(deleteSystemUsermenuAppByAccountAndOrgId, account,selectMemberOrgId);
		SystemOrg findOrg = systemOrgService.findById(selectMemberOrgId);
		
		//如果用户有新的菜单权限，则需要更新
		if(!CollectionUtils.isEmpty(menuList)){
			//拿到全部菜单
			List<SystemMenuApp> allMenuList = listAllMenu();
			//批量插入用户新的权限菜单
			Map<String, ?>[] batchValues = new HashMap[menuList.size()] ; //组织批量参数
			List<String> appMenuName = new ArrayList<String>();
			for (int i = 0; i < menuList.size(); i++) {
				String menuId = menuList.get(i).toString();
				Map param = new HashMap();
				param.put("account", account);
				param.put("orgId", findOrg.getOrgId());
				param.put("orgName", findOrg.getOrgName());
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				two:for(SystemMenuApp menu : allMenuList){
					if(menu.getMenuId().equals(menuId)){
						param.put("menuId", menu.getMenuId());
						param.put("menuName", menu.getMenuName());
						appMenuName.add(menu.getMenuName());
						break two;
					}
				}
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertSystemUsermenuApp, batchValues);
			logService.addLogAccount(sessionUser, "添加账号["+account+"]["+findOrg.getOrgName()+"]新的app端菜单权限"+JSONObject.toJSONString(appMenuName));
		}
	}
}
