package com.cdc.cdccmc.service.basic;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.CarShipper;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * 承运商管理service
 * @author ZhuWen
 * @date 2017-12-28
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ShipperService {
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['addCarShipper']}")
	private String addCarShipper;
	@Value("#{sql['updateCarShipper']}")
	private String updateCarShipper;
	@Value("#{sql['delCarShipper']}")
	private String delCarShipper;
	@Value("#{sql['listAllShipper']}")
	private String listAllShipper;
	@Value("#{sql['findShipperById']}")
	private String findShipperById;
	@Value("#{sql['listShipperByShipperName']}")
	private String listShipperByShipperName;
	
	/**
	 * 承运商列表
	 * @param paging
	 * @param carShipper
	 * @return
	 */
	public Paging pagingCarShipper(Paging paging, CarShipper carShipper) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from T_CAR_SHIPPER where 1=1 ");
		if(StringUtils.isNotBlank(carShipper.getShipperName())){
			sql.append(" and shipper_name like :shipperName ");
			paramMap.put("shipperName", "%"+carShipper.getShipperName()+"%");
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, CarShipper.class,"yyyy-MM-dd HH:mm:ss");
		return resultPaging;
	}

	/**
	 * 添加承运商
	 * @param carShipper
	 * @return
	 */
	public AjaxBean addCarShipper(SystemUser sessionUser,CarShipper carShipper) {
		carShipper.setShipperId(UUIDUtil.creatUUID());
		carShipper.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		carShipper.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		carShipper.setCreateAccount(sessionUser.getAccount());
		carShipper.setCreateRealName(sessionUser.getRealName());
		int result = namedJdbcTemplate.update(addCarShipper, new BeanPropertySqlParameterSource(carShipper));
		logService.addLogAccount(sessionUser, "添加承运商[" + carShipper.getShipperName() + "]"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 更新承运商
	 * @param carShipper
	 * @return
	 */
	public  AjaxBean updateCarShipper(SystemUser sessionUser,CarShipper carShipper){
		carShipper.setModifyAccount(sessionUser.getAccount());
		carShipper.setModifyRealName(sessionUser.getRealName());
		int result = namedJdbcTemplate.update(updateCarShipper, new BeanPropertySqlParameterSource(carShipper));
		logService.addLogAccount(sessionUser, "更新承运商[" + carShipper.getShipperName() + "]"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
    }

	/**
	 * 删除承运商
	 * @param shipperId
	 * @return
	 */
	public AjaxBean delCarShipper(SystemUser sessionUser,String shipperId){
		int result = jdbcTemplate.update(delCarShipper, shipperId);
		logService.addLogAccount(sessionUser, "删除承运商[" +findShipperById(shipperId).getShipperName()  + "]的"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 承运商批量插入时，获取组织信息
	 * 更具当前登陆组织及其子公司的orgId找出对应的组织名称，组织id，组织地址
	 * @return
	 */
    public List<CarShipper> listAllShipper() {
		List<CarShipper> list = namedJdbcTemplate.query(listAllShipper,new BeanPropertyRowMapper(CarShipper.class));
		return list;
	}

	/**
	 * 根据主键查找承运商
	 * @param shipperId
	 * @return
	 */
	public CarShipper findShipperById(String shipperId) {
		List<CarShipper> list = jdbcTemplate.query(findShipperById,new BeanPropertyRowMapper(CarShipper.class),shipperId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 *查询这个承运商是否存在
	 * @param shipperName
	 * @return
	 */
	public CarShipper listByShipperName(String shipperName) {
		List<CarShipper> list = jdbcTemplate.query(listShipperByShipperName,new BeanPropertyRowMapper(CarShipper.class),shipperName);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
}
