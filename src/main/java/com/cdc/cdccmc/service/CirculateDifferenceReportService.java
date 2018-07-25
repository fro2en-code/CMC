package com.cdc.cdccmc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.circulate.CirculateDifference;
import com.cdc.cdccmc.domain.container.ContainerScrap;
import com.cdc.cdccmc.domain.dto.CirculateDifferenceDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/**
 * 流转单差异报告
 * 
 * @author Clm
 * @date 2018-01-26
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateDifferenceReportService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerCodeService.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 流转单差异报告查询
	public Paging pagingCirculateDifferenceReport(Paging paging, SystemUser sessionUser, String startDate, String endDate,CirculateDifferenceDto circulateDifferenceDto) {
		HashMap paramMap = new HashMap();
		String sIds = sessionUser.getFilialeSystemOrgIds();
		StringBuilder sql = new StringBuilder(
				"select a.*,b.consignor_org_name,b.target_org_name,b.target_time,b.target_real_name from t_circulate_difference a,t_circulate_order b where a.order_code = b.order_code ");
		if (StringUtils.isNotBlank(sIds)) {
			sql.append(" and a.create_org_id in (" + sIds + ") ");
		}
		if (StringUtils.isNotBlank(circulateDifferenceDto.getOrderCode())) {
			sql.append(" and a.order_code =:orderCode ");
			paramMap.put("orderCode", circulateDifferenceDto.getOrderCode().trim());
		}
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and b.target_time >= :startDate ");
			paramMap.put("startDate", startDate.trim());
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and b.target_time <= :endDate ");
			paramMap.put("endDate", endDate.trim());
		}
		sql.append(" order by b.target_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap,CirculateDifferenceDto.class);
	}
}
