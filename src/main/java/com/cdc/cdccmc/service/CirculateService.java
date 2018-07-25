package com.cdc.cdccmc.service;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.door.DoorScanReceive;
import com.cdc.cdccmc.domain.door.DoorScanReceiveOrder;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.runnable.ASyncTask;
import com.cdc.cdccmc.service.basic.AreaService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author ZhuWen
 * @date 2018年1月19日 器具流转记录
 * @author ZhuWen
 * @date 2018-01-09
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateService {
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
	private AreaService areaService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private DoorEquipmentService doorEquipmentService;
	@Autowired
	private ASyncTask aSyncTask;

	@Value("#{sql['containerSum1']}")
	private String containerSum1;
	@Value("#{sql['containerSum2']}")
	private String containerSum2;
	@Value("#{sql['containerSum3']}")
	private String containerSum3;
	@Value("#{sql['containerSum4']}")
	private String containerSum4;
	@Value("#{sql['insertCirculateHistory']}")
	private String insertCirculateHistory;
	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['updateCirculateSql']}")
	private String updateCirculateSql;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['queryCirculateLatestByEpcId']}")
	private String queryCirculateLatestByEpcId;
	@Value("#{sql['updateInventoryDetailIsDeal']}")
	private String updateInventoryDetailIsDeal;
	@Value("#{sql['updateContaierLastOrgId']}")
	private String updateContaierLastOrgId;
	@Value("#{sql['insertDoorScanReceiveHistory']}")
	private String insertDoorScanReceiveHistory;
	@Value("#{sql['insertDoorScanReceiveOrderHistory']}")
	private String insertDoorScanReceiveOrderHistory;
	@Value("#{sql['queryDoorScanReceive']}")
	private String queryDoorScanReceive;
	@Value("#{sql['queryDoorScanReceiveOrder']}")
	private String queryDoorScanReceiveOrder;
	@Value("#{sql['deleteDoorScanReceive']}")
	private String deleteDoorScanReceive;
	@Value("#{sql['deleteDoorScanReceiveOrder']}")
	private String deleteDoorScanReceiveOrder;
	@Value("#{sql['updateCirculateReceiveNumber']}")
	private String updateCirculateReceiveNumber;

	/**
	 * 器具最新流转状态列表
	 */
	public Paging pagingCirculateLatest(Paging paging, Circulate circulate, String code, String orgId) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_circulate_latest c where 1=1 ");
		if (StringUtils.isNotBlank(code)) {
			sql.append(" and c.circulate_state = :circulateState ");
			paramMap.put("circulateState", code);
		}
		if (StringUtils.isNotBlank(circulate.getEpcId())) {
			sql.append(" and c.epc_id like :epcId ");
			paramMap.put("epcId", "%"+circulate.getEpcId().trim()+"%");
		}
		if (StringUtils.isNotBlank(circulate.getContainerTypeId())) {
			sql.append(" and c.container_type_id = :containerTypeId ");
			paramMap.put("containerTypeId", circulate.getContainerTypeId().trim());
		}
		if (StringUtils.isNotBlank(circulate.getOrderCode())) {
			sql.append(" and c.order_code like :orderCode ");
			paramMap.put("orderCode", "%"+ circulate.getOrderCode().trim()+"%");
		}
		if (StringUtils.isNotBlank(circulate.getContainerCode())) {
			sql.append(" and c.container_code = :containerCode ");
			paramMap.put("containerCode", circulate.getContainerCode().trim());
		}
		if (StringUtils.isNotBlank(orgId)) {
			sql.append(" and c.org_id = :orgId ");
			paramMap.put("orgId", orgId);
		}
		sql.append(" order by create_time desc ");
		LOG.info("---pagingCirculate  sql="+sql);
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, Circulate.class);
	}

	/**
	 * 器具流转历史查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Paging pagingCirculateHistory(Paging paging, SystemUser sessionUser, Circulate circulate, String startDate,
			String endDate) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_circulate_history where 1=1 ");
		if (StringUtils.isNotBlank(circulate.getOrderCode())) {
			sql.append(" and order_code like :orderCode ");
			paramMap.put("orderCode", "%" + circulate.getOrderCode() + "%");
		}
		if (StringUtils.isNotBlank(circulate.getEpcId())) {
			sql.append(" and epc_id like :epcId ");
			paramMap.put("epcId", "%"+ circulate.getEpcId().trim()+"%");
		}
		if (StringUtils.isNotBlank(circulate.getContainerCode())) {
			sql.append(" and container_code = :containerCode ");
			paramMap.put("containerCode", circulate.getContainerCode().trim());
		}
		if (StringUtils.isNotBlank(circulate.getContainerTypeId())) {
			sql.append(" and container_type_id = :containerTypeId ");
			paramMap.put("containerTypeId", circulate.getContainerTypeId().trim());
		}
		if (StringUtils.isNotBlank(circulate.getCirculateState())) {
			sql.append(" and circulate_state = :circulateState ");
			paramMap.put("circulateState", circulate.getCirculateState().trim());
		}
		if (StringUtils.isNotBlank(circulate.getOrgId())) {
			sql.append(" and org_id = :orgId ");
			paramMap.put("orgId", circulate.getOrgId().trim());
		}
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and create_time >= :startDate ");
			paramMap.put("startDate", startDate.trim());
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and create_time <= :endDate ");
			paramMap.put("endDate", endDate.trim());
		}
		sql.append(" order by create_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, Circulate.class);
	}


	/**
	 * 获取指定仓库下所有器具
	 * 
	 * @param orgId
	 *            指定仓库ID
	 * @return
	 */
	public List<Circulate> listOrgAllContainer(String orgId) {
		String sql = "select * from t_circulate_latest where org_id = ?";
		List<Circulate> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Circulate.class), orgId);
		return list;
	}

	/*
	 * 查询器具流转最新记录-epcId
	 *
	 * @param epcId
	 *
	 * @return
	 */
	public Circulate getCirculateLatestByEpcId(String epcId) {
		String sql = "select * from t_circulate_latest where epc_id = ?";
		List<Circulate> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Circulate.class), epcId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	/*
	 * 查询器具是否在本仓库
	 * 
	 * @param orgId 指定仓库ID
	 * 
	 * @param epcId
	 * 
	 * @return
	 */
	public Circulate queryCirculateLatestByEpcId(String epcId, String orgId) {
		String sql = "select * from t_circulate_latest where epc_id = ? and org_id = ?";
		List<Circulate> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Circulate.class), epcId, orgId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	/**
	 * 为某个器具移动到当前仓库。修改流转记录即可：t_circulate_history, t_circulate_latest
	 * @param sessionUser
	 * @param con 
	 * @param remark 流转备注 t_circulate_history.remark
	 * @return
	 */
	public void buildAndInsertCirculateHistoryForCurrentOrg(SystemUser sessionUser,
			Container con, String remark) {
		// 判断器具是否属于当前仓库下的器具，并且流转状态为在库状态！
		Circulate lastCirculate = queryCirculateLatestByEpcId(con.getEpcId(), sessionUser.getCurrentSystemOrg().getOrgId());
		//如果不是这种情况，则直接返回，不需要额外流转处理：没有流转记录，或者流转记录不在当前仓库或者不是“在库”状态
		if (!(lastCirculate == null || !lastCirculate.getOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId()) 
				|| !lastCirculate.getCirculateState().equals(CirculateState.ON_ORG.getCode()))  ) {
			return;
		}
		//获取默认区域
		Area defaultArea = areaService.queryDefaultArea();
		//新增一条流转记录：使该器具在当前仓库，并且是“在库”状态。
		Circulate history = new Circulate();
		history.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
		history.setEpcId(con.getEpcId()); // EPC编号
		history.setContainerCode(con.getContainerCode()); // 器具代码
		history.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
		history.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
		history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 操作公司ID
		history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 操作公司名称
		history.setCreateAccount(sessionUser.getAccount()); // 操作人
		history.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
		history.setRemark(remark);
		// 设置第一条流转记录的流转类型：在库
		history.setCirculateState(CirculateState.ON_ORG.getCode()); // 流转状态ID
		history.setCirculateStateName(CirculateState.ON_ORG.getCirculate()); // 流转状态名称
		history.setCreateTime(new Timestamp(new Date().getTime()));
		//设置默认区域
		history.setAreaId(defaultArea.getAreaId()); 
		history.setAreaName(defaultArea.getAreaName());
		
		//组织SQL参数
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(history);
		
		//插入表t_circulate_history
		namedJdbcTemplate.update(insertCirculateHistory, param);
		// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, con.getEpcId());
		// 新增新的器具最新流转记录 T_CIRCULATE_LATEST
		namedJdbcTemplate.update(insertCirculateLatest, param);
		
		//修改器具的最后所在仓库值
		jdbcTemplate.update(updateContaierLastOrgId, sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(),con.getEpcId());
	}


	/**
	 * 为批量器具移动到当前仓库。修改流转记录即可：t_circulate_history, t_circulate_latest
	 * @param sessionUser
	 * @param containers
	 * @param remark 流转备注 t_circulate_history.remark
	 * @return
	 */
	public void buildAndBatchInsertCirculateHistoryForCurrentOrg(SystemUser sessionUser,
															List<Container> containers, String remark) {
        LOG.info("---buildAndBatchInsertCirculateHistoryForCurrentOrg  "+System.currentTimeMillis()+"  begin");
		// 判断器具是否属于当前仓库下的器具，并且流转状态为在库状态！
		List<Container> containerList = new ArrayList<>();
		for(Container con : containers)
		{
			Circulate lastCirculate = queryCirculateLatestByEpcId(con.getEpcId(), sessionUser.getCurrentSystemOrg().getOrgId());
			//如果不是这种情况，则直接返回，不需要额外流转处理：没有流转记录，或者流转记录不在当前仓库或者不是“在库”状态
			if (!(lastCirculate == null || !lastCirculate.getOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())
					|| !lastCirculate.getCirculateState().equals(CirculateState.ON_ORG.getCode()))) {
				continue;
			}
			containerList.add(con);
		}
		//获取默认区域
		Area defaultArea = areaService.queryDefaultArea();
		List<Circulate> circulates = new ArrayList<>();
		Timestamp now = new Timestamp(System.currentTimeMillis());
		for(Container con:containerList)
		{
			//新增一条流转记录：使该器具在当前仓库，并且是“在库”状态。
			Circulate history = new Circulate();
			history.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
			history.setEpcId(con.getEpcId()); // EPC编号
			history.setContainerCode(con.getContainerCode()); // 器具代码
			history.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
			history.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
			history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 操作公司ID
			history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 操作公司名称
			history.setCreateAccount(sessionUser.getAccount()); // 操作人
			history.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
			history.setRemark(remark);
			// 设置第一条流转记录的流转类型：在库
			history.setCirculateState(CirculateState.ON_ORG.getCode()); // 流转状态ID
			history.setCirculateStateName(CirculateState.ON_ORG.getCirculate()); // 流转状态名称
			history.setCreateTime(now);
			//设置默认区域
			history.setAreaId(defaultArea.getAreaId());
			history.setAreaName(defaultArea.getAreaName());
			circulates.add(history);
		}

		//插入表t_circulate_history
		batchInsertCirculateHistory(circulates);

		// 新增新的器具最新流转记录 T_CIRCULATE_LATEST
		batchInsertCirculateLatest(circulates);

		// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
		List<String> epcids = circulates.stream().map(Circulate::getEpcId).collect(Collectors.toList());
		deleteCirculateLatestByEpcids(epcids);

		//修改器具的最后所在仓库值
		//jdbcTemplate.update(updateContaierLastOrgId, sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(),con.getEpcId());
		updateContainerLastOrgIdByEpcids(sessionUser,epcids);
        LOG.info("---buildAndBatchInsertCirculateHistoryForCurrentOrg  "+System.currentTimeMillis()+"   end");
	}

	public void updateContainerLastOrgIdByEpcids(SystemUser sessionUser,List<String> epcids)
	{
		StringBuffer sql = new StringBuffer("update T_CONTAINER set last_org_id = ?,last_org_name=? where epc_id in (");
		sql.append(doorEquipmentService.convertStrListToStrs(epcids)).append(SysConstants.YKH);
		jdbcTemplate.update(sql.toString(), sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName());
	}

	public void deleteCirculateLatestByEpcids(List<String> epcids)
	{
		StringBuffer sql = new StringBuffer("delete from T_CIRCULATE_LATEST where epc_id in (");
		sql.append(doorEquipmentService.convertStrListToStrs(epcids)).append(SysConstants.YKH);
		jdbcTemplate.update(sql.toString());
	}

	public void batchInsertCirculateHistory(List<Circulate> circulates)
	{
		jdbcTemplate.batchUpdate(insertCirculateHistory, new BatchPreparedStatementSetter()
		{
			@Override
			public int getBatchSize()
			{
				return circulates.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException
			{
				/**
				 * circulate_history_id, epc_id, container_code,\
				 *  container_type_id, container_type_name, order_code, circulate_state, circulate_state_name, org_id,\
				 *   org_name, from_org_id, from_org_name, target_org_id, target_org_name, area_id, area_name,\
				 *  remark,  create_time, create_account, create_real_name
				 */
				ps.setString(1, circulates.get(i).getCirculateHistoryId());
				ps.setString(2, circulates.get(i).getEpcId());
				ps.setString(3, circulates.get(i).getContainerCode());
				ps.setString(4, circulates.get(i).getContainerTypeId());
				ps.setString(5, circulates.get(i).getContainerTypeName());
				ps.setString(6, circulates.get(i).getOrderCode());
				ps.setString(7, circulates.get(i).getCirculateState());
				ps.setString(8, circulates.get(i).getCirculateStateName());
				ps.setString(9, circulates.get(i).getOrgId());
				ps.setString(10, circulates.get(i).getOrgName());
				ps.setString(11, circulates.get(i).getFromOrgId());
				ps.setString(12, circulates.get(i).getFromOrgName());
				ps.setString(13, circulates.get(i).getTargetOrgId());
				ps.setString(14, circulates.get(i).getTargetOrgName());
				ps.setString(15, circulates.get(i).getAreaId());
				ps.setString(16, circulates.get(i).getAreaName());
				ps.setString(17, circulates.get(i).getRemark());
				ps.setTimestamp(18, circulates.get(i).getCreateTime());
				ps.setString(19, circulates.get(i).getCreateAccount());
				ps.setString(20, circulates.get(i).getCreateRealName());
			}
		});
	}

	public void batchInsertCirculateLatest(List<Circulate> circulates)
	{
		jdbcTemplate.batchUpdate(insertCirculateLatest, new BatchPreparedStatementSetter()
		{
			@Override
			public int getBatchSize()
			{
				return circulates.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException
			{
				/**
				 * circulate_history_id, epc_id, container_code\
				 * , container_type_id, container_type_name, order_code, circulate_state, circulate_state_name, org_id\
				 * , org_name, from_org_id, from_org_name, target_org_id, target_org_name, area_id, area_name, remark \
				 * , create_time, create_account, create_real_name
				 */
				ps.setString(1, circulates.get(i).getCirculateHistoryId());
				ps.setString(2, circulates.get(i).getEpcId());
				ps.setString(3, circulates.get(i).getContainerCode());
				ps.setString(4, circulates.get(i).getContainerTypeId());
				ps.setString(5, circulates.get(i).getContainerTypeName());
				ps.setString(6, circulates.get(i).getOrderCode());
				ps.setString(7, circulates.get(i).getCirculateState());
				ps.setString(8, circulates.get(i).getCirculateStateName());
				ps.setString(9, circulates.get(i).getOrgId());
				ps.setString(10, circulates.get(i).getOrgName());
				ps.setString(11, circulates.get(i).getFromOrgId());
				ps.setString(12, circulates.get(i).getFromOrgName());
				ps.setString(13, circulates.get(i).getTargetOrgId());
				ps.setString(14, circulates.get(i).getTargetOrgName());
				ps.setString(15, circulates.get(i).getAreaId());
				ps.setString(16, circulates.get(i).getAreaName());
				ps.setString(17, circulates.get(i).getRemark());
				ps.setTimestamp(18, circulates.get(i).getCreateTime());
				ps.setString(19, circulates.get(i).getCreateAccount());
				ps.setString(20, circulates.get(i).getCreateRealName());
			}
		});
	}

	/**
	 * 为库内移位记录器具流转历史
	 * 
	 * @param ajaxBean
	 * @param sessionUser
	 * @param epcIdList
	 * @param areaId
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateHistoryForMove(AjaxBean ajaxBean, SystemUser sessionUser,
			List<String> epcIdList, String areaId) {
		Area area = areaService.getAreaByAreaId(areaId);
		List<Circulate> batchParam = new ArrayList<Circulate>();
		for (String epcId : epcIdList) {
			Container container = containerService.getContainerByEpcId(epcId);
			if (null == container) {
				ajaxBean.setStatus(StatusCode.STATUS_311);
				ajaxBean.setMsg("器具[" + epcId + "]" + StatusCode.STATUS_311_MSG);
				return ajaxBean;
			}
			// 因为器具流转历史表和器具最新流转记录表是同一个bean,添加数据的时候要保证两个表的数据一致
			Circulate history = new Circulate();
			history.setCirculateState(CirculateState.ON_ORG.getCode().toString());
			history.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
			history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			history.setAreaId(area.getAreaId());
			history.setAreaName(area.getAreaName());
			history.setCreateAccount(sessionUser.getAccount());
			history.setCreateRealName(sessionUser.getRealName());
			history.setCreateTime(DateUtil.currentTimestamp());
			history.setCirculateHistoryId(UUIDUtil.creatUUID());
			history.setEpcId(epcId);
			history.setContainerCode(container.getContainerCode());
			history.setContainerTypeId(container.getContainerTypeId());
			history.setContainerTypeName(container.getContainerTypeName());
			history.setRemark(SysConstants.CIRCULATE_REMARK_MOVE);
			batchParam.add(history);
			// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
			jdbcTemplate.update(deleteCirculateLatest, epcId);
			// 批量插入
			if (batchParam.size() >= SysConstants.MAX_INSERT_NUMBER) {
				SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(batchParam.toArray());
				namedJdbcTemplate.batchUpdate(insertCirculateHistory, params);
				logService.addLogAccount(sessionUser, "器具流转历史表批量插入" + (batchParam.size()) + "条");
				// 新增新的器具最新流转记录 T_CIRCULATE_LATEST
				namedJdbcTemplate.batchUpdate(insertCirculateLatest, params);
				logService.addLogAccount(sessionUser, "批量插入最新流转记录" + batchParam.size() + "条");
				batchParam.clear();
			}
		}
		// 批量插入
		if (batchParam.size() > SysConstants.INTEGER_0) {
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(batchParam.toArray());
			namedJdbcTemplate.batchUpdate(insertCirculateHistory, params);
			logService.addLogAccount(sessionUser, "器具流转历史表批量插入" + (batchParam.size() + "条"));
			// 新增新的器具最新流转记录 T_CIRCULATE_LATEST
			namedJdbcTemplate.batchUpdate(insertCirculateLatest, params);
			logService.addLogAccount(sessionUser, "批量插入最新流转记录" + batchParam.size() + "条");
			batchParam.clear();
		}
		return ajaxBean;
	}

	/**
	 * 手持端--新增器具流转历史记录，更新器具最新流转记录
	 * 
	 * @param sessionUser
	 * @param Container
	 * @param circulateOrder
	 */
	public void updateOrInsertCirculateHistoryForReceive(SystemUser sessionUser, List<Container> containerList, CirculateOrder circulateOrder,String deviceRemark) {
		//如果没有器具集合，则不需要记录流转历史
		if(CollectionUtils.isEmpty(containerList)) {
			return;
		}
		StringBuffer deleteCirculateLatestSql = new StringBuffer("delete from T_CIRCULATE_LATEST where epc_id in(");
		List<String> containerEpcIdList = containerList.stream().map(Container::getEpcId).collect(Collectors.toList());
		//EPC编号去重
		doorEquipmentService.filterStrListDuplication(containerEpcIdList);
		//把epcList转换成适合sql的字符串：'123','456','789'
		String epcIds = doorEquipmentService.convertStrListToStrs(containerEpcIdList);
		deleteCirculateLatestSql.append(epcIds).append(SysConstants.YKH);
		//jdbcTemplate.update(deleteCirculateLatestSql.toString());
		LOG.info("---门型收货  bbb----111---3-----1    deleteCirculateLatestSql="+deleteCirculateLatestSql);
		// 批量删除旧器具的最新流转记录 T_CIRCULATE_LATEST
//		jdbcTemplate.batchUpdate(deleteCirculateLatest, new BatchPreparedStatementSetter() {
//			@Override
//			public int getBatchSize() {
//				return containerList.size();
//			}
//			@Override
//			public void setValues(PreparedStatement ps, int i) throws SQLException {
//				ps.setString(1, containerList.get(i).getEpcId());
//			}
//		});
		
		Area area = areaService.queryDefaultArea();
		Timestamp time1 = new Timestamp(new Date().getTime());
		Timestamp time2 = DateUtil.currentAddFourSecond();
		//“流转入库”对象集合
		List<Circulate> circulateOne = new ArrayList<Circulate>();
		//“在库”对象集合
		List<Circulate> circulateTwo = new ArrayList<Circulate>();
		//托盘器具集合
		List<Container> trayContainerList = new ArrayList<Container>();
		//为每个器具封装流转记录对象
		for (Container con : containerList) {
			// 第一条记录流转状态：流转入库
			Circulate history = new Circulate();
			history.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
			history.setEpcId(con.getEpcId()); // EPC编号
			history.setContainerCode(con.getContainerCode()); // 器具代码
			history.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
			history.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
			history.setOrderCode(circulateOrder.getOrderCode()); // 包装流转单单据编号
			history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 操作公司ID
			history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 操作公司名称
			history.setFromOrgId(circulateOrder.getConsignorOrgId()); // 来源公司ID
			history.setFromOrgName(circulateOrder.getConsignorOrgName()); // 来源公司名称
			history.setAreaId(area.getAreaId());// 区域ID
			history.setAreaName(area.getAreaName());// 区域名称
			history.setCreateAccount(sessionUser.getAccount()); // 操作人
			history.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
			history.setCirculateState(CirculateState.IN_ORG.getCode()); // 流转状态ID
			history.setCirculateStateName(CirculateState.IN_ORG.getCirculate()); // 流转状态名称
			history.setRemark(deviceRemark);
			history.setCreateTime(time1);
			// 第二条记录流转状态：在库
			Circulate historyAndLatest = new Circulate();
			historyAndLatest.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
			historyAndLatest.setEpcId(con.getEpcId()); // EPC编号
			historyAndLatest.setContainerCode(con.getContainerCode()); // 器具代码
			historyAndLatest.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
			historyAndLatest.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
			historyAndLatest.setOrderCode(circulateOrder.getOrderCode()); // 包装流转单单据编号
			historyAndLatest.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 操作公司ID
			historyAndLatest.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 操作公司名称
			historyAndLatest.setFromOrgId(circulateOrder.getConsignorOrgId()); // 来源公司ID
			historyAndLatest.setFromOrgName(circulateOrder.getConsignorOrgName()); // 来源公司名称
			historyAndLatest.setTargetOrgId(circulateOrder.getTargetOrgId());
			historyAndLatest.setTargetOrgName(circulateOrder.getTargetOrgName());
			historyAndLatest.setAreaId(area.getAreaId());// 区域ID
			historyAndLatest.setAreaName(area.getAreaName());// 区域名称
			historyAndLatest.setCreateAccount(sessionUser.getAccount()); // 操作人
			historyAndLatest.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
			historyAndLatest.setCirculateState(CirculateState.ON_ORG.getCode());// 流转状态ID
			historyAndLatest.setCirculateStateName(CirculateState.ON_ORG.getCirculate()); // 流转状态名称
			historyAndLatest.setRemark(deviceRemark);
			historyAndLatest.setCreateTime(time2);
			//把对象放入集合
			circulateOne.add(history);
			circulateTwo.add(historyAndLatest);
			//识别托盘器具
			if(con.getIsTray() == SysConstants.INTEGER_1) {
				trayContainerList.add(con);
			}
		}

		StringBuffer updateContaierLastOrgIdSql = new StringBuffer("update T_CONTAINER set last_org_id = ?,last_org_name=? where epc_id in(");
		updateContaierLastOrgIdSql.append(epcIds).append(SysConstants.YKH);
		jdbcTemplate.update(updateContaierLastOrgIdSql.toString(),sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName());
		LOG.info("---门型收货  bbb----111---3-----2   updateContaierLastOrgIdSql="+updateContaierLastOrgIdSql);
		//更新器具最后所在仓库。t_container表last_org_id字段 
//		jdbcTemplate.batchUpdate(updateContaierLastOrgId, new BatchPreparedStatementSetter() {
//			@Override
//			public int getBatchSize() {
//				return containerList.size();
//			}
//			@Override
//			public void setValues(PreparedStatement ps, int i) throws SQLException {
//				ps.setString(1, sessionUser.getCurrentSystemOrg().getOrgId());
//				ps.setString(2, sessionUser.getCurrentSystemOrg().getOrgName());
//				ps.setString(3, containerList.get(i).getEpcId());
//			}
//		});
		/*
		// 批量更新最新流转记录 T_CIRCULATE_HISTORY，流转出库
		namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(circulateOne.toArray()));
		LOG.info("---门型收货  bbb----111---3-----3  ");
		// 批量更新最新流转记录 T_CIRCULATE_HISTORY，在途
		namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(circulateTwo.toArray()));
		LOG.info("---门型收货  bbb----111---3-----4  ");
		// 批量更新历史流转记录 T_CIRCULATE_LATEST，在途
		namedJdbcTemplate.batchUpdate(insertCirculateLatest, SqlParameterSourceUtils.createBatch(circulateTwo.toArray()));
		LOG.info("---门型收货  bbb----111---3-----5  ");
		*/
		aSyncTask.insertCirculateHistoryAndLatestByThread(circulateOne,circulateTwo,deleteCirculateLatestSql.toString());
		//对托盘器具进行解托 
		if(CollectionUtils.isNotEmpty(trayContainerList)) {
			for (Container trayContainer : trayContainerList) {
				containerGroupService.relieveContainerGroup(sessionUser,trayContainer.getEpcId());
			}
		}
		LOG.info("---门型收货  bbb----111---3-----3  ");
		//清空，释放内存
		circulateOne = null;
		circulateTwo = null;
		trayContainerList = null;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void insertCirculateHistoryAndLatest(List<Circulate> circulateOne,List<Circulate> circulateTwo,String deleteCirculateLatestSql)
	{
		jdbcTemplate.update(deleteCirculateLatestSql.toString());
		// 批量更新最新流转记录 T_CIRCULATE_HISTORY，流转出库
		namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(circulateOne.toArray()));
		LOG.info("---门型收货  bbb----111---3-----3  ");
		// 批量更新最新流转记录 T_CIRCULATE_HISTORY，在途
		namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(circulateTwo.toArray()));
		LOG.info("---门型收货  bbb----111---3-----4  ");
		// 批量更新历史流转记录 T_CIRCULATE_LATEST，在途
		LOG.info("---insertCirculateHistoryAndLatest   circulateTwo="+ JSON.toJSONString(circulateTwo));
		namedJdbcTemplate.batchUpdate(insertCirculateLatest, SqlParameterSourceUtils.createBatch(circulateTwo.toArray()));
	}

	/**
	 * 根据epcId修改器具最新流转记录(盘点差异处理新增入库)
	 * 
	 * @param epcId
	 * @param circulate
	 * @return
	 */
	public AjaxBean addCirculate(SystemUser sessionUser, AjaxBean ajaxBean, String inventoryId,
			String epcId, Container container, InventoryDetail iDetail, InventoryMain inventoryMain) {
   	 	//新增流转记录
   	    Circulate circulate = new Circulate();
		circulate.setCirculateState(CirculateState.ON_ORG.getCode().toString()); // 在库
		circulate.setCirculateStateName(CirculateState.ON_ORG.getCirculate()); // 在库
		circulate.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		circulate.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		circulate.setAreaId(iDetail.getAreaId());
		circulate.setAreaName(iDetail.getAreaName());
		circulate.setCreateAccount(sessionUser.getAccount());
		circulate.setCreateRealName(sessionUser.getRealName());
		circulate.setCirculateHistoryId(UUIDUtil.creatUUID());
		circulate.setEpcId(container.getEpcId());
		circulate.setContainerCode(container.getContainerCode());
		circulate.setContainerTypeId(container.getContainerTypeId());
		circulate.setContainerTypeName(container.getContainerTypeName());
		circulate.setCreateTime(DateUtil.currentTimestamp());
		circulate.setRemark(SysConstants.CIRCULATE_REMARK_INVENTORY_ADD_NEW);
		// 更新器具最后所在仓库
		containerService.updateContaierLastOrgId(sessionUser, container.getEpcId(), inventoryMain.getInventoryOrgId(),
				inventoryMain.getInventoryOrgName());
		// 增加历史流转记录 T_CIRCULATE_HISTORY，在库
		namedJdbcTemplate.update(insertCirculateHistory, new BeanPropertySqlParameterSource(circulate));
		// 删除器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, container.getEpcId());
		// 新增器具最新流转记录 T_CIRCULATE_LATEST，在库
		namedJdbcTemplate.update(insertCirculateLatest, new BeanPropertySqlParameterSource(circulate));
		// 更新盘点单差异状态为：1已处理
		jdbcTemplate.update(updateInventoryDetailIsDeal, inventoryId.trim(), circulate.getEpcId());
		return ajaxBean;
	}

	/**
	 * 为修正区域记录器具流转历史
	 * 
	 * @param sessionUser
	 * @param areaId
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateHistoryForModifyArea(SystemUser sessionUser, String epcId, String areaId) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		Area area = areaService.getAreaByAreaId(areaId);

		Container container = containerService.getContainerByEpcId(epcId);
		if (null == container) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("器具[" + epcId + "]" + StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		// 因为器具流转历史表和器具最新流转记录表是同一个bean,添加数据的时候要保证两个表的数据一致
		Circulate history = new Circulate();
		history.setCirculateState(CirculateState.ON_ORG.getCode().toString());
		history.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
		history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		history.setAreaId(area.getAreaId());
		history.setAreaName(area.getAreaName());
		history.setCreateAccount(sessionUser.getAccount());
		history.setCreateRealName(sessionUser.getRealName());
		history.setCreateTime(DateUtil.currentTimestamp());
		history.setCirculateHistoryId(UUIDUtil.creatUUID());
		history.setEpcId(epcId);
		history.setContainerCode(container.getContainerCode());
		history.setContainerTypeId(container.getContainerTypeId());
		history.setContainerTypeName(container.getContainerTypeName());
		history.setRemark(SysConstants.CIRCULATE_REMARK_INVENTORY_MODIFY_AREA);

		// 新增新的器具最新流转记录 T_CIRCULATE_HISTORY，在库
		namedJdbcTemplate.update(insertCirculateHistory, new BeanPropertySqlParameterSource(history));
		// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, epcId);
		// 新增新的器具最新流转记录 T_CIRCULATE_LATEST，在库
		namedJdbcTemplate.update(insertCirculateLatest, new BeanPropertySqlParameterSource(history));
		return ajaxBean;
	}

	/**
	 * [收货门型监控]页面，移除按钮点击事件
	 * 数据移除到history历史表里：
	 * T_DOOR_SCAN_RECEIVE 移动到 T_DOOR_SCAN_RECEIVE_HISTORY
	 * T_DOOR_SCAN_RECEIVE_ORDER 移动到 T_DOOR_SCAN_RECEIVE_ORDER_HISTORY
	 * @param sessionUser
	 * @param doorAccount
	 * @param createTime
	 * @return
	 */
	public AjaxBean removeDoorReceive(SystemUser sessionUser, String doorAccount, String createTime) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(doorAccount)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("参数[门型账号]"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(createTime)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("参数[创建时间]"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//T_DOOR_SCAN_RECEIVE 移动到 T_DOOR_SCAN_RECEIVE_HISTORY
		List<DoorScanReceive> doorScanReceiveList = jdbcTemplate.query(queryDoorScanReceive, new BeanPropertyRowMapper(DoorScanReceive.class), doorAccount, createTime);
		namedJdbcTemplate.batchUpdate(insertDoorScanReceiveHistory, SqlParameterSourceUtils.createBatch(doorScanReceiveList.toArray()));
		jdbcTemplate.update(deleteDoorScanReceive, doorAccount, createTime);
		
		//T_DOOR_SCAN_RECEIVE_ORDER 移动到 T_DOOR_SCAN_RECEIVE_ORDER_HISTORY
		List<DoorScanReceiveOrder> doorScanReceiveOrderList = jdbcTemplate.query(queryDoorScanReceiveOrder, new BeanPropertyRowMapper(DoorScanReceiveOrder.class), doorAccount, createTime);
		namedJdbcTemplate.batchUpdate(insertDoorScanReceiveOrderHistory, SqlParameterSourceUtils.createBatch(doorScanReceiveOrderList.toArray()));
		jdbcTemplate.update(deleteDoorScanReceiveOrder, doorAccount, createTime);
		return ajaxBean;
	}


	/**
	 * 当应发数于应收数相等时，更新收货数为1
	 * @param orderCode
	 * @param containerCodes
	 */
	public void updateReceiveNumber(String orderCode, String containerCodes) {
		jdbcTemplate.update(updateCirculateReceiveNumber + containerCodes + SysConstants.YKH, orderCode);
	}
}
