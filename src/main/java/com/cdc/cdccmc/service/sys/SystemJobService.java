package com.cdc.cdccmc.service.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/** 
 * 工种
 * @author ZhuWen
 * @date 2018-01-09
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemJobService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemJobService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;
	
	@Value("#{sql['insertSystemJob']}")
	private String insertSystemJob;	
	@Value("#{sql['updateSystemJob']}")
	private String updateSystemJob;	
	@Value("#{sql['listCurrentOrgJob']}")
	private String listCurrentOrgJob;	
	@Value("#{sql['listJobByAccountAndOrg']}")
	private String listJobByAccountAndOrg;	
	@Value("#{sql['deleteSystemUserjob']}")
	private String deleteSystemUserjob;	
	@Value("#{sql['insertSystemUserjob']}")
	private String insertSystemUserjob;	
	@Value("#{sql['querySystemJobById']}")
	private String querySystemJobById;	
	@Value("#{sql['querySystemJobByName']}")
	private String querySystemJobByName;	
	/**
	 * 根据仓库ID查询所有工种
	 * @param paging 分页对象
	 * @param jobId 工种ID
	 * @param account 登录用户名
	 * @return
	 */
	public Paging pagingJobByOrgId(Paging paging,SystemUser sessionUser, String selectOrgId, String jobName){
		String selectSql = "select * from t_system_job where org_id in ("+sessionUser.getFilialeSystemOrgIds()+") ";
		Map paramMap = new HashMap();
		if(StringUtils.isNotBlank(selectOrgId)){
			selectSql += " and org_id = :selectOrgId ";
			paramMap.put("selectOrgId", selectOrgId);
		}
		if(StringUtils.isNotBlank(jobName)){
			selectSql += " and job_name like :jobName ";
			paramMap.put("jobName", "%"+jobName+"%");
		}
		selectSql += " order by create_time desc ";
		return baseService.pagingParamMap(paging, selectSql, paramMap , SystemJob.class);
	}

	/**
	 * 新增工种
	 * @param ajaxBean
	 * @param sessionUser
	 * @param job
	 * @return
	 */
	public AjaxBean addSystemJob(AjaxBean ajaxBean, SystemUser sessionUser,SystemJob job) {
		job.setJobId(UUIDUtil.creatUUID());
		job.setCreateAccount(sessionUser.getAccount());
		job.setCreateRealName(sessionUser.getRealName());
		job.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		job.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		int result = namedJdbcTemplate.update(insertSystemJob, new BeanPropertySqlParameterSource(job));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 编辑工种名称
	 * @param ajaxBean
	 * @param systemUser
	 * @param job
	 * @return
	 */
	public AjaxBean editJobName(AjaxBean ajaxBean, SystemUser sessionUser,
			SystemJob job) {
		SystemJob findJob = findJobById(job.getJobId());
		int result = jdbcTemplate.update(updateSystemJob, job.getJobName(),sessionUser.getAccount(),sessionUser.getRealName(),job.getJobId());
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 获取当前选择仓库的所有工种
	 * @param systemUser 当前登录用户
	 * @return
	 */
	public List<SystemJob> listCurrentOrgJob(SystemUser systemUser) {
		return jdbcTemplate.query(listCurrentOrgJob, new BeanPropertyRowMapper(SystemJob.class),systemUser.getCurrentSystemOrg().getOrgId());
	}
	/**
	 * 根据用户和仓库ID查询工种权限列表
	 * @param account
	 * @param orgId
	 * @return
	 */
	public List<SystemJob> listJobByAccountAndOrg(String account, String orgId) {
		return jdbcTemplate.query(listJobByAccountAndOrg, new BeanPropertyRowMapper(SystemJob.class),account,orgId);
	}
	/**
	 * 更新指定账号的权限工种列表
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @param jobList 指定账号新的权限工种列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean updateUserJob(AjaxBean ajaxBean, SystemUser sessionUser,
			SystemUser systemUser, List jobList){
		//先删除之前旧的工种对应关系
		jdbcTemplate.update(deleteSystemUserjob, systemUser.getAccount(),sessionUser.getCurrentSystemOrg().getOrgId());
		//logService.addLogAccount(sessionUser, "删除账号["+systemUser.getAccount()+"]旧的所有工种", null);
		if(!CollectionUtils.isEmpty(jobList)){
			Map<String, ?>[] batchValues = new HashMap[jobList.size()];
			for (int i = 0; i < jobList.size(); i++) {
				String jobId = jobList.get(i).toString();
				Map param = new HashMap();
				param.put("account", systemUser.getAccount());
				param.put("orgId", sessionUser.getCurrentSystemOrg().getOrgId());
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				param.put("jobId", jobId);
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertSystemUserjob, batchValues );
			logService.addLogAccount(sessionUser, "更新账号["+systemUser.getAccount()+"]新的权限工种列表"+JSONObject.toJSONString(batchValues));
		}else{
			logService.addLogAccount(sessionUser, "更新账号["+systemUser.getAccount()+"]新的权限工种列表为空");
		}
		return ajaxBean;
	}
	
	public SystemJob findJobById(String jobId){
		List<SystemJob> list = jdbcTemplate.query(querySystemJobById, new BeanPropertyRowMapper(SystemJob.class),jobId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 根据工种名称查找工种对象
	 * @param jobName
	 * @return
	 */
	public SystemJob findJobByName(String jobName){
		List<SystemJob> list = jdbcTemplate.query(querySystemJobByName, new BeanPropertyRowMapper(SystemJob.class),jobName);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
}
