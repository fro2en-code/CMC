package com.cdc.cdccmc.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.cdc.cdccmc.domain.dto.CirculateDetailDto;
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

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.ClaimDetail;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateDifference;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.AreaService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/**
 * 流转单差异处理
 * 
 * @author Clm
 * @date 2018-01-29
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateDifferenceResultService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerCodeService.class);
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
	@Autowired
	private AreaService areaService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
	
	@Value("#{sql['insertCirculateHistory']}")
	private String insertCirculateHistory;
	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['insertClaimDetail']}")
	private String insertClaimDetail;
	@Value("#{sql['containerInsert']}")
	private String containerInsert;
	@Value("#{sql['updateCirculateDifference']}")
	private String updateCirculateDifference;
	@Value("#{sql['updateCirculateDetail']}")
	private String updateCirculateDetail;
	@Value("#{sql['queryCirculateOrderByOrderCode']}")
	private String queryCirculateOrderByOrderCode;
	@Value("#{sql['updateCirculateDetailDealResult4']}")
	private String updateCirculateDetailDealResult4;
	@Value("#{sql['updateCirculateDetailDealResult2']}")
	private String updateCirculateDetailDealResult2;

	// 流转单差异处理
	public Paging pagingCirculateDetail(Paging paging, SystemUser sessionUser, CirculateDetail circulateDetail, String differenceId) {
		HashMap paramMap = new HashMap();
		StringBuilder sql = new StringBuilder("select d.*,(select is_manual_order from t_circulate_order where d.order_code = order_code) is_manual_order from t_circulate_detail d where order_code in (");
		sql.append(" select order_code from t_circulate_order where is_receive in ('1','2') and target_org_id in ( ");
		sql.append(sessionUser.getFilialeSystemOrgIds());
		sql.append(") ) ");
		if (StringUtils.isNotBlank(circulateDetail.getOrderCode())) {
			sql.append(" and order_code like :orderCode ");
			paramMap.put("orderCode", "%"+circulateDetail.getOrderCode().trim()+"%");
		}
		if (StringUtils.isNotBlank(differenceId)) {
			sql.append(" and deal_result = :dealResult ");
			paramMap.put("dealResult", differenceId);
		}
		sql.append(" order by create_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, CirculateDetailDto.class);
	}
	/**
	 *  【流转单收货详情】页面处理收货差异：EPC覆盖
	 */
	public AjaxBean epcCover(AjaxBean ajaxBean, SystemUser sessionUser, String circulateDetailId, String epcId,
			String newEpcId, CirculateOrder circulateOrder) {

		//旧的器具，本来要发货过来的器具
		Container oldEpcContainer = containerService.getContainerByEpcId(epcId);
		//新的器具，用于代替本来要发货过来的器具
		Container newEpcContainer = containerService.getContainerByEpcId(newEpcId);
		
		if(null == newEpcContainer) {
			ajaxBean.setStatus(StatusCode.STATUS_350);
            ajaxBean.setMsg("EPC编号["+ newEpcId +"]"+StatusCode.STATUS_311_MSG);
            return ajaxBean;
		}
		
		//两个EPC必须是相同的器具代码才能做EPC覆盖处理
		if(!newEpcContainer.getContainerCode().equals(oldEpcContainer.getContainerCode())) {
			ajaxBean.setStatus(StatusCode.STATUS_370);
            ajaxBean.setMsg(StatusCode.STATUS_370_MSG);
            return ajaxBean;
		}
		
        //获取入库区域
        Area defaultArea = areaService.queryDefaultArea();
		// 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
		ajaxBean = buildAndInsertCirculateHistoryForEpcCover(ajaxBean, sessionUser, defaultArea,epcId, newEpcId,circulateOrder, circulateDetailId);
		// 更新器具
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		// 入库以后执行更新t_container表数据中last_org_id
		ajaxBean = updateOrgByEpcId(sessionUser, epcId, circulateOrder, newEpcId);
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		// 操作全部成功以后更新待处理状态
		jdbcTemplate.update(updateCirculateDetail, newEpcId,sessionUser.getAccount(), sessionUser.getRealName(),sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(), circulateDetailId);

		//为收货仓库增加库存：新器具的器具代码
		InventoryHistory ih = new InventoryHistory();
		ih.setContainerCode(newEpcContainer.getContainerCode()); //器具代码
		ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
		ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
		ih.setReceiveNumber(SysConstants.INTEGER_1); //此次收货数量
		ih.setOrderCode(circulateOrder.getOrderCode()); //流转单号
		ih.setCreateAccount(sessionUser.getAccount());
		ih.setCreateRealName(sessionUser.getRealName());
		ih.setIsReceive(true); // true收货，false发货
		ih.setRemark(SysConstants.CIRCULATE_REMARK_EPC_COVER_NEW+"["+newEpcId+"]");
		inventoryHistoryService.updateInventoryLatest(ih);
		
		//为发货仓库增加库存：旧器具的器具代码
		InventoryHistory ih2 = new InventoryHistory();
		ih2.setContainerCode(oldEpcContainer.getContainerCode()); //器具代码
		ih2.setOrgId(circulateOrder.getConsignorOrgId()); //发货仓库ID
		ih2.setOrgName(circulateOrder.getConsignorOrgName()); //发货仓库名称
		ih2.setReceiveNumber(SysConstants.INTEGER_1); //此次收货数量
		ih2.setOrderCode(circulateOrder.getOrderCode()); //流转单号
		ih2.setCreateAccount(sessionUser.getAccount());
		ih2.setCreateRealName(sessionUser.getRealName());
		ih2.setIsReceive(true); // true收货，false发货
		ih2.setRemark(SysConstants.CIRCULATE_REMARK_EPC_COVER_OLD+"["+epcId+"]"); //因为发货仓库在此流转单发货时已扣除该EPC数量，所以EPC覆盖处理时，需返还旧EPC
		inventoryHistoryService.updateInventoryLatest(ih2);
		
		return ajaxBean;
	}

	/**
	 * EPC覆盖处理。包装流转单差异处理
	 * 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
	 */
	@SuppressWarnings("unused")
	public AjaxBean buildAndInsertCirculateHistoryForEpcCover(AjaxBean ajaxBean, SystemUser sessionUser, Area defaultArea,  String oldEpcId,
							String newEpcId,CirculateOrder circulateOrder,String circulateDifferenceId) {
		Container newEpcContainer = containerService.findContainerByEpcId(newEpcId);
		if(!(circulateOrder.getTargetOrgName().equals(sessionUser.getCurrentSystemOrg().getOrgName()))) {
			ajaxBean.setStatus(StatusCode.STATUS_351);
            ajaxBean.setMsg(StatusCode.STATUS_351_MSG+"["+ circulateOrder.getTargetOrgName() +"],请重试！");
			return ajaxBean;
		}
		// 封装Circulate
		Circulate history = new Circulate();
		Timestamp time1 = new Timestamp(new Date().getTime());
		//为了流转记录按照时间显示顺序，所以第一条“流转出库”记录，和第二条“在途”或“入库”记录时间不能完全相同，为了区分排序，暂且加二秒。
		Timestamp time2 = DateUtil.currentAddTwoSecond();
		
		/************** 新EPC：流转入库 ********************/
		history.setCirculateHistoryId(UUIDUtil.creatUUID());
		history.setEpcId(newEpcContainer.getEpcId());
		history.setContainerCode(newEpcContainer.getContainerCode());
		history.setContainerTypeId(newEpcContainer.getContainerTypeId());
		history.setContainerTypeName(newEpcContainer.getContainerTypeName());
		history.setOrderCode(circulateOrder.getOrderCode());
		history.setCirculateState(CirculateState.IN_ORG.getCode()); //流转入库
		history.setCirculateStateName(CirculateState.IN_ORG.getCirculate()); //流转入库
		history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		history.setAreaId(defaultArea.getAreaId());
		history.setAreaName(defaultArea.getAreaName());
		history.setRemark(SysConstants.CIRCULATE_REMARK_EPC_COVER_NEW);
		history.setCreateAccount(sessionUser.getAccount());
		history.setCreateRealName(sessionUser.getRealName());
		history.setFromOrgId(circulateOrder.getConsignorOrgId());
		history.setFromOrgName(circulateOrder.getConsignorOrgName());
		history.setCreateTime(time1);
		/************** 新EPC：流转入库 t_circulate_history ********************/
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(history);
		namedJdbcTemplate.update(insertCirculateHistory, paramSource);
		
		/************** 新EPC：在库 t_circulate_history ********************/
		history.setCirculateHistoryId(UUIDUtil.creatUUID());
		history.setCirculateState(CirculateState.ON_ORG.getCode());
		history.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
		history.setCreateTime(time2);
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(history);
		namedJdbcTemplate.update(insertCirculateHistory, param);
		// 删除新的器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, newEpcId);
		/************** 新EPC：在库 t_circulate_latest ********************/
		namedJdbcTemplate.update(insertCirculateLatest, param);
		
		/************** 更新新器具的最后所在公司last_org_id/last_org_name ********************/
		containerService.updateContaierLastOrgId(sessionUser, newEpcId, circulateOrder.getTargetOrgId(), circulateOrder.getTargetOrgName());
		
		/************** 更新旧器具的最后所在公司last_org_id/last_org_name ********************/
		containerService.updateContaierLastOrgId(sessionUser, oldEpcId, circulateOrder.getConsignorOrgId(), circulateOrder.getConsignorOrgName());
		
		/************** 将旧EPC退回到发货仓库 ********************/
		Container oldEpcContainer = containerService.findContainerByEpcId(oldEpcId);
		history.setCirculateHistoryId(UUIDUtil.creatUUID());
		history.setEpcId(oldEpcContainer.getEpcId());
		history.setContainerCode(oldEpcContainer.getContainerCode());
		history.setContainerTypeId(oldEpcContainer.getContainerTypeId());
		history.setContainerTypeName(oldEpcContainer.getContainerTypeName());
		history.setOrderCode(circulateOrder.getOrderCode());
		history.setCirculateState(CirculateState.ON_ORG.getCode());
		history.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
		history.setCreateAccount(sessionUser.getAccount());
		history.setCreateRealName(sessionUser.getRealName());
		history.setFromOrgId(circulateOrder.getConsignorOrgId());
		history.setFromOrgName(circulateOrder.getConsignorOrgName());
		history.setOrgId(circulateOrder.getConsignorOrgId());
		history.setOrgName(circulateOrder.getConsignorOrgName());
		history.setRemark(SysConstants.CIRCULATE_REMARK_EPC_COVER_OLD); //因为发货仓库在此流转单发货时已扣除该EPC数量，所以EPC覆盖处理时，需返还旧EPC
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(history);
		/************** 旧EPC：在库 t_circulate_history ********************/
		namedJdbcTemplate.update(insertCirculateHistory, params);
		// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, oldEpcId);
		/************** 旧EPC：在库 t_circulate_latest ********************/
		namedJdbcTemplate.update(insertCirculateLatest, params);
		return ajaxBean;
	}

	/* =======================================入库================================= */
	/**
	 * 【流转单收货详情】页面处理收货差异：收货入库
	 * @param ajaxBean
	 * @param sessionUser
	 * @param circulateDetailId
	 * @param epcId
	 * @param defaultArea
	 * @return
	 */
	public AjaxBean inOrgDispose(AjaxBean ajaxBean, SystemUser sessionUser, String circulateDetailId, String epcId,
			Area defaultArea) {
		CirculateDetail cd = queryCirculateDetail(circulateDetailId);
		// 通过OrderCode查Circulate对象
		CirculateOrder circulateOrder = queryCirculateOrder(cd.getOrderCode());
		if(!(circulateOrder.getTargetOrgName().equals(sessionUser.getCurrentSystemOrg().getOrgName()))) {
			ajaxBean.setStatus(StatusCode.STATUS_351);
            ajaxBean.setMsg(StatusCode.STATUS_351_MSG+"["+ circulateOrder.getTargetOrgName() +"],请重试！");
			return ajaxBean;
		}
		// 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
		ajaxBean = buildAndInsertCirculateHistory(ajaxBean, sessionUser, circulateDetailId, defaultArea);
		// 更新器具
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		// 入库以后执行更新t_container表数据中last_org_id
		containerService.updateContaierLastOrgId(sessionUser, epcId, sessionUser.getCurrentSystemOrg().getOrgId(), sessionUser.getCurrentSystemOrg().getOrgName());
		// 操作全部成功以后更新待处理状态
		ajaxBean = updateCirculateDetail(epcId,sessionUser, circulateDetailId);
		
		//为当前仓库增加库存：新器具的器具代码
		Container con = containerService.getContainerByEpcId(epcId);
		InventoryHistory ih = new InventoryHistory();
		ih.setContainerCode(con.getContainerCode()); //器具代码
		ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
		ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
		ih.setReceiveNumber(SysConstants.INTEGER_1); //此次收货数量
		ih.setOrderCode(circulateOrder.getOrderCode()); //流转单号
		ih.setRemark("新增单个器具["+con.getEpcId()+"]");
		ih.setCreateAccount(sessionUser.getAccount());
		ih.setCreateRealName(sessionUser.getRealName());
		ih.setIsReceive(true); // true收货，false发货
		ih.setRemark("流转单收货详情页面处理收货差异：EPC覆盖");
		inventoryHistoryService.updateInventoryLatest(ih);
		
		return ajaxBean;
	}

	/**
	 * 【流转单收货详情】页面处理收货差异：收货入库
	 * 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
	 * @param ajaxBean
	 * @param sessionUser
	 * @param circulateDetailId
	 * @param defaultArea
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateHistory(AjaxBean ajaxBean, SystemUser sessionUser,
			String circulateDetailId, Area defaultArea) {
		CirculateDetail circulateDetail = queryCirculateDetail(circulateDetailId);
		// 通过OrderCode查Circulate对象
		String cOrderCode = circulateDetail.getOrderCode();
		CirculateOrder circulateOrder = queryCirculateOrder(cOrderCode);
		if(!(circulateOrder.getTargetOrgName().equals(sessionUser.getCurrentSystemOrg().getOrgName()))) {
			ajaxBean.setStatus(StatusCode.STATUS_310);
			ajaxBean.setMsg("流转单差异处理仓库必须是流转单收货仓库["+ circulateOrder.getTargetOrgName() +"],请重试！");
			return ajaxBean;
		}
		// 封装Circulate
		Circulate c = new Circulate();
		Timestamp time1 = new Timestamp(new Date().getTime());
		//为了流转记录按照时间显示顺序，所以第一条“流转出库”记录，和第二条“在途”或“入库”记录时间不能完全相同，为了区分排序，暂且加二秒。
		Timestamp time2 = DateUtil.currentAddTwoSecond();
		
		c.setCirculateHistoryId(UUIDUtil.creatUUID());
		c.setEpcId(circulateDetail.getEpcId());
		c.setContainerCode(circulateDetail.getContainerCode());
		c.setContainerTypeId(circulateDetail.getContainerTypeId());
		c.setContainerTypeName(circulateDetail.getContainerTypeName());
		c.setOrderCode(circulateDetail.getOrderCode());
		c.setCirculateState(CirculateState.IN_ORG.getCode());
		c.setCirculateStateName(CirculateState.IN_ORG.getCirculate());
		c.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		c.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		c.setAreaId(defaultArea.getAreaId());
		c.setAreaName(defaultArea.getAreaName());
		c.setRemark(SysConstants.CIRCULATE_REMARK_EPC_RECEIVE); 
		c.setCreateAccount(sessionUser.getAccount());
		c.setCreateRealName(sessionUser.getRealName());
		c.setFromOrgId(circulateOrder.getConsignorOrgId());
		c.setFromOrgName(circulateOrder.getConsignorOrgName());
		c.setCreateTime(time1);
		
		// 插入表 T_CIRCULATE_HISTORY, 流转入库
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(c);
		namedJdbcTemplate.update(insertCirculateHistory, paramSource);

		// 插入表 T_CIRCULATE_HISTORY, 在库
		c.setCirculateHistoryId(UUIDUtil.creatUUID());
		c.setCirculateState(CirculateState.ON_ORG.getCode());
		c.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
		c.setCreateTime(time2);
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(c);
		namedJdbcTemplate.update(insertCirculateHistory, param);
		
		// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, circulateDetail.getEpcId());
		// 新增新的器具最新流转记录 T_CIRCULATE_LATEST
		namedJdbcTemplate.update(insertCirculateLatest, param);
		return ajaxBean;
	}

	// 更新待处理状态
	public AjaxBean updateCirculateDetail(String epcId, SystemUser sessionUser, String circulateDetailId) {
		int result = jdbcTemplate.update(updateCirculateDetailDealResult2, sessionUser.getAccount(),sessionUser.getRealName(),sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(),circulateDetailId);
		return AjaxBean.returnAjaxResult(result);
	}

	// 通过circulateDifferenceId主键查到这个对象
	public CirculateDetail queryCirculateDetail(String circulateDetailId) {
		String sql = "select * from t_circulate_detail where circulate_detail_id =? order by create_time desc";
		List<CirculateDetail> circulateDetailList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(CirculateDetail.class), circulateDetailId);
		if (CollectionUtils.isEmpty(circulateDetailList)) {
			return null;
		}
		return circulateDetailList.get(0);
	}

	// 通过cOrderCode查到这个对象
	public CirculateOrder queryCirculateOrder(String cOrderCode) {
		List<CirculateOrder> circulateList = jdbcTemplate.query(queryCirculateOrderByOrderCode, new BeanPropertyRowMapper(CirculateOrder.class), cOrderCode);
		if (CollectionUtils.isEmpty(circulateList)) {
			return null;
		}
		return circulateList.get(0);
	}

	// 通用的更新t_container表数据(epc覆盖)
	public AjaxBean updateOrgByEpcId(SystemUser sessionUser, String oldEpcId, CirculateOrder circulateOrder,String newEpcId) {
			int result = containerService.updateContaierLastOrgId(sessionUser
					, oldEpcId, circulateOrder.getConsignorOrgId(), circulateOrder.getConsignorOrgName());
			//如果旧器具回退至发货仓库成功，则更新新器具的最后仓库为收货仓库
			if(result == 1) {
				containerService.updateContaierLastOrgId(sessionUser
						, newEpcId, circulateOrder.getTargetOrgId(), circulateOrder.getTargetOrgName());
			}
			return AjaxBean.returnAjaxResult(result);
	}

	/* =======================================索赔================================= */
	/**
	 *  【流转单收货详情】页面处理收货差异：索赔
	 */
	public AjaxBean claimDispose(AjaxBean ajaxBean,SystemUser sessionUser,CirculateDetail diff, String epcId,CirculateOrder order) {
		ClaimDetail claimDetail = new ClaimDetail();
		CirculateDetail cd = queryCirculateDetail(diff.getCirculateDetailId());
		claimDetail.setClaimDetailId(UUIDUtil.creatUUID());
		claimDetail.setClaimType("1");
		claimDetail.setOrderCode(cd.getOrderCode());
		claimDetail.setEpcId(cd.getEpcId());
		claimDetail.setContainerCode(cd.getContainerCode());
		claimDetail.setContainerName(cd.getContainerName());
		claimDetail.setContainerTypeId(cd.getContainerTypeId());
		claimDetail.setContainerTypeName(cd.getContainerTypeName());
		claimDetail.setRemark(cd.getRemark());
		claimDetail.setCreateAccount(sessionUser.getAccount());
		claimDetail.setCreateRealName(sessionUser.getRealName());
		claimDetail.setCreateOrgId(order.getTargetOrgId()); //索赔发起仓库ID
		claimDetail.setCreateOrgName(order.getTargetOrgName()); //索赔发起仓库名称
		// SQL参数组装 T_CIRCULATE_HISTORY
		BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(claimDetail);
		// 新增新的索赔明细记录t_claim_detail
		namedJdbcTemplate.update(insertClaimDetail, paramSource);
		// 更新页面索赔状态
		ajaxBean = updateCirculateDifferenceClaim(ajaxBean,epcId, sessionUser, diff.getCirculateDetailId());
		return ajaxBean;
	}

	// 更新页面索赔状态
	public AjaxBean updateCirculateDifferenceClaim(AjaxBean ajaxBean,String epcId, SystemUser sessionUser, String circulateDetailId) {
		int result = jdbcTemplate.update(updateCirculateDetailDealResult4, sessionUser.getAccount(), sessionUser.getRealName(), sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(),circulateDetailId);
		return AjaxBean.returnAjaxResult(result);
	}
	
    //通过circulateDifferenceId查这个对象的epcId
	public CirculateDetail queryBycirculateDifferenceId(String circulateDetailId) {
		String sql = "select * from t_circulate_detail where circulate_detail_id=?";
		List<CirculateDetail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetail.class), circulateDetailId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
}
