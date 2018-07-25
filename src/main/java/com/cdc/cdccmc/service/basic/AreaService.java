package com.cdc.cdccmc.service.basic;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/**
 * 仓库区域
 * 
 * @author Clm
 * @date 2018-01-04
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class AreaService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AreaService.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['queryAreaByAreaId']}")
	private String queryAreaByAreaId;
	@Value("#{sql['insertArea']}")
	private String insertArea;
	@Value("#{sql['updateAreaSetIsDefault0']}")
	private String updateAreaSetIsDefault0;
	@Value("#{sql['updateAreaIsDefault1']}")
	private String updateAreaIsDefault1;
	@Value("#{sql['queryDefaultArea']}")
	private String queryDefaultArea;
	@Value("#{sql['listAllArea']}")
	private String listAllArea;

	/**
	 * 区域列表，支持分页
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Paging pagingArea(Paging paging, Area area) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_area where 1=1 ");
		if (StringUtils.isNotBlank(area.getAreaName())) {
			sql.append(" and area_name like :areaName ");
			paramMap.put("areaName", "%" + area.getAreaName().trim() + "%");
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, Area.class);
		return resultPaging;
	}

	/**
	 * 仓库区域管理列表之先查询后新增
	 * 
	 * @param string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Area> queryByName(String areaName) {
		String sql = "select * from t_area where area_name= ? ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Area.class), areaName);
	}

	/**
	 * 仓库区域管理列表之先查询后新增
	 * 
	 * @param string
	 */
	@SuppressWarnings("unchecked")
	public Area getAreaByAreaId(String areaId) {
		List<Area> list = jdbcTemplate.query(queryAreaByAreaId, new BeanPropertyRowMapper(Area.class), areaId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 新增仓库区域
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AjaxBean addArea(AjaxBean ajaxBean, SystemUser sessionUser, String areaName) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		paramMap.put("areaId", UUIDUtil.creatUUID());
		paramMap.put("areaName", areaName);
		paramMap.put("createAccount", sessionUser.getAccount());
		paramMap.put("createRealName", sessionUser.getRealName());
		paramMap.put("createOrgId", sessionUser.getCurrentSystemOrg().getOrgId());
		paramMap.put("createOrgName", sessionUser.getCurrentSystemOrg().getOrgName());
		int result = namedJdbcTemplate.update(insertArea, paramMap);
		logService.addLogAccount(sessionUser, "新增区域[" + areaName + "]");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 设置默认入库区域
	 * @param sessionUser 
	 */
	public AjaxBean setDefaultArea(SystemUser sessionUser, Area area, AjaxBean ajaxBean) {
		// 全部设置为非默认区域
		int result = jdbcTemplate.update(updateAreaIsDefault1);
		if (result == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_309);
			ajaxBean.setMsg(StatusCode.STATUS_309_MSG);
			return ajaxBean;
		}
		Area findArea = getAreaByAreaId(area.getAreaId());
		// 设置指定区域为默认区域
		result = jdbcTemplate.update(updateAreaSetIsDefault0, area.getAreaId());
		logService.addLogAccount(sessionUser, "更新默认区域为[" + findArea.getAreaName() + "]");
		return AjaxBean.returnAjaxResult(result);
	}
	/**
	 * 获取默认入库区域，业务逻辑上只能存在一个
	 * @return
	 */
	public Area queryDefaultArea(){
		List<Area> areaList = jdbcTemplate.query(queryDefaultArea,new BeanPropertyRowMapper(Area.class));
		if(CollectionUtils.isEmpty(areaList)){
			return null;
		}
		return areaList.get(0);
	}

	/**
	 * 获取库区
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Area> listAllArea() {
		return jdbcTemplate.query(listAllArea, new BeanPropertyRowMapper(Area.class));
	}

}
