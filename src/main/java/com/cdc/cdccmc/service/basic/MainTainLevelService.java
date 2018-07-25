package com.cdc.cdccmc.service.basic;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
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
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Maintain;
import com.cdc.cdccmc.domain.MaintainLevel;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/**
 * 维修级别
 * 
 * @author Clm
 * @date 2018-01-05
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class MainTainLevelService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainTainLevelService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['updateMaintainLevel']}")
	private String updateMaintainLevel;
	@Value("#{sql['insertMaintain']}")
	private String insertMaintain;
	@Value("#{sql['queryMaintainLevelByMaintainLevel']}")
	private String queryMaintainLevelByMaintainLevel;
	@Value("#{sql['queryMaintainByMaintainLevelName']}")
	private String queryMaintainByMaintainLevelName;
	@Value("#{sql['listAllMaintainLevel']}")
	private String listAllMaintainLevel;
	/**
	 * 维修级别管理列表查询/修级别名称查询
	 */
	public Paging pagingMainTainLevel(Paging paging, MaintainLevel maintainLevel) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_maintain_level where 1=1 ");
		if (StringUtils.isNotBlank(maintainLevel.getMaintainLevelName())) {
			sql.append(" and maintain_level_name like :maintainLevelName ");
			paramMap.put("maintainLevelName", "%" + maintainLevel.getMaintainLevelName() + "%");
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, MaintainLevel.class);
		return resultPaging;
	}

	/**
	 * 新增维修级别
	 */
	public AjaxBean addMainTainLevel(SystemUser sessionUser, AjaxBean ajaxBean, MaintainLevel maintainLevel) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		maintainLevel.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		maintainLevel.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		maintainLevel.setCreateAccount(sessionUser.getAccount());
		maintainLevel.setCreateRealName(sessionUser.getRealName());
		int result = namedJdbcTemplate.update(insertMaintain, new BeanPropertySqlParameterSource(maintainLevel));
		logService.addLogAccount(sessionUser, "新增维修级别[" + maintainLevel.getMaintainLevel() 
				+ "]["+maintainLevel.getMaintainLevelName()+"]["+maintainLevel.getMaintainHour()+"]"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 更新维修级别
	 */
	public AjaxBean updateMainTainLevel(SystemUser sessionUser, MaintainLevel maintainLevel) {
		int result = this.namedJdbcTemplate.update(updateMaintainLevel, new BeanPropertySqlParameterSource(maintainLevel));
		logService.addLogAccount(sessionUser, "更新维修级别[" + maintainLevel.getMaintainLevel() 
				+ "]["+maintainLevel.getMaintainLevelName()+"]["+maintainLevel.getMaintainHour()+"]"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 根据维修级别查询维修对象
	 * @param maintainLevel
	 * @return
	 */
	public MaintainLevel queryMaintainLevelByMaintainLevel(String maintainLevel) {
		List<MaintainLevel> list = jdbcTemplate.query(queryMaintainLevelByMaintainLevel, new BeanPropertyRowMapper(MaintainLevel.class), maintainLevel);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	/**
	 * 根据维修名称查询维修对象
	 * @param maintainLevelName
	 * @return
	 */
	public MaintainLevel queryMaintainByName(String maintainLevelName) {
		List<MaintainLevel> list = jdbcTemplate.query(queryMaintainByMaintainLevelName, new BeanPropertyRowMapper(MaintainLevel.class), maintainLevelName);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

    public List<MaintainLevel> listAllMaintainLevel() {
		return namedJdbcTemplate.query(listAllMaintainLevel,new BeanPropertyRowMapper(MaintainLevel.class));
    }

    /**
     * 列出所有维修级别是空的器具
     * @return
     */
	public List<Maintain> listMaintainNoMaintainLevel(SystemUser sessionUser) {
		String sql = "select * from t_maintain where maintain_level is null and maintain_state != '3' and maintain_apply_org_id = ?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), sessionUser.getCurrentSystemOrg().getOrgId());
	}
	/**
	 * app端维修完成页面，获取待维修完成器具列表
	 * @param sessionUser
	 * @return
	 */
	public List<Maintain> listWaitFinishMaintain(SystemUser sessionUser) {
		String sql = "select * from t_maintain where maintain_state != '3' and maintain_apply_org_id =?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), sessionUser.getCurrentSystemOrg().getOrgId());
	}
}
