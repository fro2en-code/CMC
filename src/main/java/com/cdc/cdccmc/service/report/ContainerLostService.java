package com.cdc.cdccmc.service.report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.cdc.cdccmc.common.enums.ClaimType;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.ClaimDetail;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerLost;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.LogService;

/**
 * 器具丢失
 * 
 * @author Clm
 * @date 2018/1/23
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerLostService {

	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ContainerService containerService;
	
	@Value("#{sql['insertContainerLost']}")
	private String insertContainerLost;
	@Value("#{sql['insertClaimDetail']}")
	private String insertClaimDetail;
	@Value("#{sql['updateInventoryDetailIsDeal']}")
	private String updateInventoryDetailIsDeal;
	@Value("#{sql['updateContainerLostIsClaim']}")
	private String updateContainerLostIsClaim;
	@Value("#{sql['queryContainerLostById']}")
	private String queryContainerLostById;
	@Value("#{sql['queryContainerLostByEpcAndIsOut']}")
	private String queryContainerLostByEpcAndIsOut;
	/**
	 * 新增丢失器具
	 * @param sessionUser 
	 * @param ajaxBean 通用返回
	 * @param containerLost 器具实例
	 * @return
	 */
	public AjaxBean addContainerLost(SystemUser sessionUser, AjaxBean ajaxBean,String inventoryId, String epcId,String lostRemark) {
    	//新增器具
    	ContainerLost containerLost = new ContainerLost();
    	//根据epc编码获取器具信息
    	Container container = containerService.findContainerByEpcId(epcId);
    	//器具信息
    	containerLost.setContainerLostId(UUIDUtil.creatUUID());
    	containerLost.setEpcId(container.getEpcId());
    	containerLost.setPrintCode(container.getPrintCode());
    	containerLost.setContainerTypeId(container.getContainerTypeId());
    	containerLost.setContainerTypeName(container.getContainerTypeName());
    	containerLost.setContainerCode(container.getContainerCode());
    	containerLost.setContainerName(container.getContainerName());
    	containerLost.setInventoryId(inventoryId);
    	containerLost.setIsOut("1");
    	containerLost.setIsClaim(0);
    	containerLost.setLostRemark(ClaimType.INVENTORY_LOSS.getClaimTypeId());
    	containerLost.setCreateAccount(sessionUser.getAccount());
    	containerLost.setCreateRealName(sessionUser.getRealName());
    	containerLost.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
    	containerLost.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
    	containerLost.setLostRemark(lostRemark);
		int result = namedJdbcTemplate.update(insertContainerLost, new BeanPropertySqlParameterSource(containerLost));
		if(result == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg("差异状态更新成功");
			return ajaxBean;
		}
		int i = jdbcTemplate.update(updateInventoryDetailIsDeal, containerLost.getInventoryId().trim(), containerLost.getEpcId());
		if (i == 0) {
			logService.addLogAccount(sessionUser, "差异状态更新失败");
			ajaxBean.setStatus(StatusCode.STATUS_309);
			ajaxBean.setMsg(StatusCode.STATUS_309_MSG);
			return ajaxBean;
		}
		return ajaxBean;
	}
	
	/**
	 * 检查 T_CONTAINER_LOST 丢失表里是否存在该丢失器具
	 * @param epcId
	 * @param isOut 是否已丢失出库,0已出库，1未出库
	 * @param createOrgId 创建丢失器具的公司ID
	 * @return
	 */
	public ContainerLost findLostByEpcAndIsOut(String epcId, String isOut,String createOrgId) {
		List<ContainerLost> list = jdbcTemplate.query(queryContainerLostByEpcAndIsOut, new BeanPropertyRowMapper(ContainerLost.class), epcId,isOut,createOrgId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	
	
	//器具丢失查询
	public Paging pagingLostContainer(Paging paging, SystemUser sessionUser, ContainerLost containerLost,
			String startDate, String endDate) {
		HashMap paramMap = new HashMap();
		String lIds= sessionUser.getFilialeSystemOrgIds();
		StringBuilder sql = new StringBuilder("select * from t_container_lost where 1=1 ");
		if(StringUtils.isNotBlank(lIds)){
			sql.append(" and create_org_id in ("+lIds+")" );
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
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, ContainerLost.class);
		return resultPaging;
	}
	
	//通过containerLostId查询ContainerLost对象
	public ContainerLost queryContainerLostById(String containerLostId) {
		List<ContainerLost> list = jdbcTemplate.query(queryContainerLostById, new BeanPropertyRowMapper(ContainerLost.class), containerLostId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	//索赔设置
	public AjaxBean setIsClaim(AjaxBean ajaxBean,SystemUser sessionUser, ContainerLost containerLost, ClaimDetail claimDetail) {
        //先进行索赔明细添加
		claimDetail.setClaimDetailId(UUIDUtil.creatUUID());
		claimDetail.setClaimType(SysConstants.STRING_2); //索赔来源，1：流转单差异索赔，2：盘点丢失索赔
		claimDetail.setOrderCode(containerLost.getOrderCode());
		claimDetail.setInventoryId(containerLost.getInventoryId()); //设置盘点单号
		claimDetail.setEpcId(containerLost.getEpcId());
		claimDetail.setContainerCode(containerLost.getContainerCode());
		claimDetail.setContainerName(containerLost.getContainerName());
		claimDetail.setContainerTypeId(containerLost.getContainerTypeId());
		claimDetail.setContainerTypeName(containerLost.getContainerTypeName());
		claimDetail.setRemark(containerLost.getLostRemark());
		claimDetail.setCreateAccount(sessionUser.getAccount());
		claimDetail.setCreateRealName(sessionUser.getRealName());
		claimDetail.setCreateOrgId(containerLost.getCreateOrgId());
		claimDetail.setCreateOrgName(containerLost.getCreateOrgName());
		int result = namedJdbcTemplate.update(insertClaimDetail, new BeanPropertySqlParameterSource(claimDetail));
		if(result == 0){
			return AjaxBean.returnAjaxResult(result);
		}
        //设置为：已索赔
		result = this.jdbcTemplate.update(updateContainerLostIsClaim, containerLost.getContainerLostId());
		return AjaxBean.returnAjaxResult(result);
	}
	
}
