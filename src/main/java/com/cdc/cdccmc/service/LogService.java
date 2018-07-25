package com.cdc.cdccmc.service;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.LogAccount;
import com.cdc.cdccmc.domain.LogError;
import com.cdc.cdccmc.domain.LogLogin;
import com.cdc.cdccmc.domain.sys.SystemUser;

/** 
 * 日志
 * @author ZhuWen
 * @date 2017-12-28
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class LogService {
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['insertLogLogin']}")
	private String insertLogLogin;
	@Value("#{sql['insertLogError']}")
	private String insertLogError;

	public Paging listLogAccount(Paging paging, SystemUser sessionUser, String epcId, String account, String startDate, String endDate) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_log_account where org_id in ( "+sessionUser.getFilialeSystemOrgIds()+" )  ");
		if(StringUtils.isNotBlank(epcId)){
			sql.append(" and epc_id = :epcId ");
			paramMap.put("epcId", epcId.trim());
		}
		if(StringUtils.isNotBlank(account)){
			sql.append(" and account = :account ");
			paramMap.put("account", account.trim());
		}
		if(StringUtils.isNotBlank(startDate)){
			sql.append(" and create_time >= :startDate ");
			paramMap.put("startDate", startDate.trim());
		}
		if(StringUtils.isNotBlank(endDate)){
			sql.append(" and create_time <= :endDate ");
			paramMap.put("endDate", endDate.trim());
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, LogAccount.class);
		return resultPaging;

	}

	public Paging listLogLogin(Paging paging, SystemUser sessionUser, String account) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_log_login where org_id in ( "+sessionUser.getFilialeSystemOrgIds()+" )  ");
		if(StringUtils.isNotBlank(account)){
			sql.append(" and account = :account ");
			paramMap.put("account", account.trim());
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, LogLogin.class);
		return resultPaging;
	}

	public Paging listLogError(Paging paging, SystemUser sessionUser, String account) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_log_error where org_id in ( "+sessionUser.getFilialeSystemOrgIds()+" )  ");
		if(StringUtils.isNotBlank(account)){
			sql.append(" and account = :account ");
			paramMap.put("account", account.trim());
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, LogError.class);
		return resultPaging;
	}
	/**
	 * 添加用户操作日志
	 * @param sessionUser 当前登录用户
	 * @param logContent 操作日志内容
	 //* @param epcId 被操作的epc编号
	 * @return
	 */
	public void addLogAccount(SystemUser sessionUser, String logContent){
		addLogAccountAboutEpc(sessionUser,logContent,null);
	}
	
	/**
	 * 添加用户操作日志
	 * @param sessionUser 当前登录用户
	 * @param logContent 操作日志内容
	 * @param epcId 被操作的epc编号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void addLogAccountAboutEpc(SystemUser sessionUser, String logContent, String epcId){
		try
		{
			String sql = "INSERT INTO t_log_account (log_account_id, account, log_content, epc_id, create_time,org_id,org_name) VALUES (:uuid, :account, :logContent, :epcId, sysdate(), :orgId, :orgName)";
			ConcurrentHashMap paramMap = new ConcurrentHashMap();
			paramMap.put("uuid", UUIDUtil.creatUUID());
			paramMap.put("account", sessionUser.getAccount());
			paramMap.put("logContent", logContent);
			paramMap.put("epcId", epcId == null ? "" : epcId);
			paramMap.put("orgId", sessionUser.getCurrentSystemOrg().getOrgId());
			paramMap.put("orgName", sessionUser.getCurrentSystemOrg().getOrgName());
			namedJdbcTemplate.update(sql, paramMap);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加用户登录日志
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	public void addLogLogin(SystemUser sessionUser,String logContent){
		jdbcTemplate.update(insertLogLogin , UUIDUtil.creatUUID() , sessionUser.getAccount()
		,logContent ,sessionUser.getCurrentSystemOrg().getOrgId() ,sessionUser.getCurrentSystemOrg().getOrgName());
	}
	
	/**
	 * 添加操作错误日志 
	 * @param sessionUser 当前登录用户
	 * @param ex 抛出的异常，比如NullPointException
	 * @param errorEvent  错误事件，比如：新增用户失败
	 * @param errorCode 错误码，例如：201（未知错误），参考类StatusCode.java
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addLogError(SystemUser sessionUser, Exception ex, String errorEvent, String errorCode){
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		paramMap.put("uuid", UUIDUtil.creatUUID());
		paramMap.put("account", sessionUser.getAccount());
		paramMap.put("errorContent", ex == null?"":ex.getMessage());
		paramMap.put("errorEvent", errorEvent == null?"":errorEvent);
		paramMap.put("errorCode", errorCode == null?"":errorCode);
		paramMap.put("orgId", sessionUser.getCurrentSystemOrg().getOrgId());
		paramMap.put("orgName", sessionUser.getCurrentSystemOrg().getOrgName());
		namedJdbcTemplate.update(insertLogError, paramMap);
	}
}
