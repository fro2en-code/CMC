package com.cdc.cdccmc.service.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemMenuWeb;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/** 
 * 页面按钮
 * @author ZhuWen
 * @date 2018-01-08
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemButtonService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemButtonService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private LogService logService;
	
	@Value("#{sql['deleteSystemUserbutton']}")
	private String deleteSystemUserbutton;
	@Value("#{sql['insertSystemUserbutton']}")
	private String insertSystemUserbutton;
	
	private final String LIST_SYSTEM_BUTTON = "select b.* from T_SYSTEM_USERBUTTON ub,t_system_button b "
			+ " where ub.account =? and ub.org_id = ? and ub.button_id = b.button_id"; 
	private final String ALL_BUTTON = "select * from t_system_button";
	/**
	 * 根据登录用户名和当前选中仓库ID查询该用户的权限按钮列表
	 * @param account 登录用户名
	 * @return
	 */
	public List<SystemButton> listSystemButton(String account,String orgId){
		return jdbcTemplate.query(LIST_SYSTEM_BUTTON, new BeanPropertyRowMapper(SystemButton.class), account,orgId);
	}
	/**
	 * 查询所有按钮
	 * @return
	 */
	public List<SystemButton> listAllButton() {
		return jdbcTemplate.query(ALL_BUTTON, new BeanPropertyRowMapper(SystemButton.class));
	}
	/**
	 * 更新指定账号、指定仓库的，权限按钮
	 * @param memberOrg 指定的隶属仓库
	 * @param account 指定账号
	 * @param buttonList 权限按钮
	 * @param sessionUser 当前登录账号
	 */
	@SuppressWarnings("unchecked")
	public void updateAccountButton(SystemOrg memberOrg, String account, List buttonList,
			SystemUser sessionUser) {
		//删除用户旧的权限菜单
		jdbcTemplate.update(deleteSystemUserbutton, account,memberOrg.getOrgId());
		//logService.addLogAccount(sessionUser, "删除账号["+account+"]旧的所有按钮权限", null);
		//如果用户有新的菜单权限，则需要更新
		if(!CollectionUtils.isEmpty(buttonList)){
			//拿到所有按钮列表
			List<SystemButton> systemButtonList = listAllButton();
			//批量插入用户新的权限菜单
			Map<String, ?>[] batchValues = new HashMap[buttonList.size()] ; //组织批量参数
			List<String> buttonNameList = new ArrayList<String>();
			for (int i = 0; i < buttonList.size(); i++) {
				String buttonId = buttonList.get(i).toString();
				Map param = new HashMap();
				param.put("account", account);
				param.put("buttonId", buttonId);
				param.put("orgId", memberOrg.getOrgId());
				param.put("orgName", memberOrg.getOrgName());
				two: for (SystemButton button : systemButtonList) {
					if(button.getButtonId().equals(buttonId)){
						param.put("buttonName", button.getButtonName());
						buttonNameList.add(button.getButtonName());
						break two;
					}
				}
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertSystemUserbutton, batchValues);
			logService.addLogAccount(sessionUser, "更新账号["+account+"]["+memberOrg.getOrgName()+"]仓库下新的按钮权限"+JSONObject.toJSONString(buttonNameList));
		}else{
			logService.addLogAccount(sessionUser, "更新账号["+account+"]["+memberOrg.getOrgName()+"]仓库下新的按钮权限为空");
		}
	}
	/**
	 * 获取指定仓库指定工种的权限按钮
	 * @param ajaxBean 
	 * @param sessionUser 当前登录用户
	 * @param jobId 指定工种 
	 * @param memberOrgId 指定仓库
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SystemButton> queryButtonByJobAndOrgId(AjaxBean ajaxBean,
			SystemUser sessionUser, String jobId) {
		SystemJob job = systemJobService.findJobById(jobId);
		String sql = "select b.* from t_system_jobbutton jb ,t_system_button b where jb.org_id = ? and jb.job_id = ? and jb.button_id = b.button_id";
		List<SystemButton> buttonList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(SystemButton.class), 
				job.getOrgId(),jobId);
		return buttonList;
	}
	/**
	 * 保存指定工种，新的web权限菜单和新的权限按钮列表
	 * @param job 指定工种
	 * @param buttonList 新的按钮权限
	 * @param sessionUser
	 */
	@SuppressWarnings("unchecked")
	public void updateJobButton(SystemJob job, List buttonList,
			SystemUser sessionUser) {
		//删除用户旧的权限菜单
		String deleteSql = "delete from t_system_jobbutton where org_id = ? and job_id = ? ";
		jdbcTemplate.update(deleteSql,job.getOrgId(),job.getJobId());
		//logService.addLogAccount(sessionUser, "删除账号["+sessionUser.getAccount()+"]旧的web菜单和按钮权限", null);
		//如果用户有新的菜单权限，则需要更新
		if(!CollectionUtils.isEmpty(buttonList)){
			//拿到所有按钮列表
			List<SystemButton> systemButtonList = listAllButton();
			//批量插入用户新的权限菜单
			String insertSql = "INSERT INTO t_system_jobbutton (org_id, job_id, button_id, org_name, job_name, button_name, create_time, create_account, create_real_name) VALUES (:orgId, :jobId, :buttonId, :orgName, :jobName, :buttonName, sysdate(), :createAccount, :createRealName)";
			Map<String, ?>[] batchValues = new HashMap[buttonList.size()] ; //组织批量参数
			List<String> buttonNameList = new ArrayList<String>();
			for (int i = 0; i < buttonList.size(); i++) {
				String buttonId = buttonList.get(i).toString();
				Map param = new HashMap();
				param.put("orgId", job.getOrgId());
				param.put("jobId", job.getJobId());
				param.put("jobName", job.getJobName());
				param.put("buttonId", buttonId);
				param.put("orgName", job.getOrgName());
				two: for (SystemButton button : systemButtonList) {
					if(button.getButtonId().equals(buttonId)){
						param.put("buttonName", button.getButtonName());
						buttonNameList.add(button.getButtonName());
						break two;
					}
				}
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertSql, batchValues);
			logService.addLogAccount(sessionUser, "更新工种["+job.getJobName()+"]["+job.getOrgName()+"]仓库下新的按钮权限"+JSONObject.toJSONString(buttonNameList));
		}else{
			logService.addLogAccount(sessionUser, "更新工种["+job.getJobName()+"]["+job.getOrgName()+"]仓库下新的按钮权限为空");
		}
	}
}
