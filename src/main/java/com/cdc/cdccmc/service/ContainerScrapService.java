package com.cdc.cdccmc.service;

import java.util.List;

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

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.container.ContainerScrap;
import com.cdc.cdccmc.domain.sys.SystemUser;

/**
 * 器具报废
 * @author Administrator
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerScrapService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerTypeService.class);
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['insertContainerScrap']}")
	private String insertContainerScrap;
	@Value("#{sql['queryContainerScrapByEpcIsOutCreateOrgId']}")
	private String queryContainerScrapByEpcIsOutCreateOrgId;

	/**
	 * 查询 T_CONTAINER_SCRAP 报废表里是否存在该报废器具，并且该器具尚未报废出库 ,即 is_out = 1
	 * @param epcId
	 * @param isOut 是否已报废出库,0已出库，1未出库
	 * @param createOrgId 创建组织ID
	 * @return
	 */
	public ContainerScrap findScrapByEpcAndIsOut(String epcId, String isOut, String createOrgId) {
		List<ContainerScrap> list = jdbcTemplate.query(queryContainerScrapByEpcIsOutCreateOrgId, new BeanPropertyRowMapper(ContainerScrap.class), epcId,
				isOut, createOrgId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 新增器具报废信息
	 * @param containerScrap
	 * @return
	 */
	public AjaxBean addContainerScrap(SystemUser sessionUser, ContainerScrap containerScrap) {
		int result = this.namedJdbcTemplate.update(insertContainerScrap, new BeanPropertySqlParameterSource(containerScrap));
		return AjaxBean.returnAjaxResult(result);
	}
}
