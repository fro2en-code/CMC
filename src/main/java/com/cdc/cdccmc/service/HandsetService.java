package com.cdc.cdccmc.service;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.ContainerCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 手持机端Service
 * 
 * @author 75645
 *
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class HandsetService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HandsetService.class);
	
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate; 
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
	@Autowired
	private ContainerCodeService containerCodeService;

	@Value("#{sql['queryContainerTypeByName']}")
	private String queryContainerTypeByName;
	@Value("#{sql['queryContainerAboutReceive0']}")
	private String queryContainerAboutReceive0;
	@Value("#{sql['updateReceiveInfoForCirculateDetail_inOrgWebManualOrder']}")
	private String updateReceiveInfoForCirculateDetail_inOrgWebManualOrder;
	@Value("#{sql['updateContainerBelongOrgId']}")
	private String updateContainerBelongOrgId;
	@Value("#{sql['insertCirculateDetailReceive']}")
	private String insertCirculateDetailReceive;
	@Value("#{sql['updateReceiveInfoForCirculateDetail_inOrgWebOrder']}")
	private String updateReceiveInfoForCirculateDetail_inOrgWebOrder;
	@Value("#{sql['queryContainerAboutReceive']}")
	private String queryContainerAboutReceive;

	/**
	 * 门型收货
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public void inOrgDoor(SystemUser sessionUser, CirculateOrder circulateOrder,
									List<String> epcIdList, List<Container> circulateDetailList, Timestamp createTime) {
		LOG.info("门型收货  bbb----111  ");
		//对t_circulate_detail流转单器具明细表进行收货操作
		circulateOrderService.updateReceiveInfoForCirculateDetail_inOrgActualScan(circulateOrder,epcIdList,circulateDetailList, sessionUser, createTime, StringUtils.EMPTY,SysConstants.DEVICE_DOOR);
		LOG.info("门型收货  bbb----222  ");
		//对t_circulate_order流转单主单信息表进行收货标记操作
		circulateOrderService.updateReceiveInfoForCirculateOrder_inOrgDoor(circulateOrder.getOrderCode(), sessionUser);
		LOG.info("门型收货  bbb----end  ");
	}

	/**
	 * 实收入库--
	 * 点击“实收入库”按钮后，参数epcIdList 里的每个epcId，把t_circulate_detail里面所有receive_number=0的记录都修改以下字段：
	 * deal_result=5,receive_number=1
	 * 另外更新处理人信息：
	 * `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
	 * `receive_org_id` varchar(32) DEFAULT NULL COMMENT '收货组织ID',
	 * `receive_org_name` varchar(50) DEFAULT NULL COMMENT '收货组织名称',
	 * `receive_account` varchar(50) DEFAULT NULL COMMENT '收货账号',
	 * `receive_real_name` varchar(20) DEFAULT NULL COMMENT '收货账号的姓名'
	 * 另外修改所有receive_number=0的未收货器具更新字段：
	 * `remark` varchar(20) DEFAULT NULL COMMENT '差异备注，仅用于人工收货时',
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @param differenceRemark
	 *            差异备注
	 * @return
	 */
	public AjaxBean inOrgActualScan(SystemUser sessionUser, String orderCode,
									@RequestParam(value = "epcIdList[]") List<String> epcIdList, String differenceRemark,String deviceRemark) {
		AjaxBean ajaxBean = new AjaxBean();
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		// 当包装流转单的收货仓库不是当前所选仓库时不能收货
		if (!circulateOrder.getTargetOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())) {
			ajaxBean.setStatus(StatusCode.STATUS_315);
			ajaxBean.setMsg(StatusCode.STATUS_315_MSG);
			return ajaxBean;
		}
		//包装流转单尚未发货，不能进行收货！
		if(circulateOrder.getPrintNumber() < SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_354);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_354_MSG);
			return ajaxBean;
		}
		// 获取包装流转单详情
		List<CirculateDetail> circulateDetail = circulateOrderService.queryCirculateDetailByOrderCode(orderCode);
		if (CollectionUtils.isEmpty(circulateDetail)) {
			ajaxBean.setStatus(StatusCode.STATUS_333);
			ajaxBean.setMsg(StatusCode.STATUS_333_MSG); //包装流转单数据不存在！请核查！
			return ajaxBean;
		}
		List<Container> circulateDetailList = new ArrayList<Container>();
		for(String epc : epcIdList) {
			circulateDetailList.add(containerService.getContainerByEpcId(epc));
		}
		//对t_circulate_detail流转单器具明细表进行收货操作
		circulateOrderService.updateReceiveInfoForCirculateDetail_inOrgActualScan(circulateOrder,epcIdList, circulateDetailList, sessionUser, DateUtil.currentTimestamp(), differenceRemark,deviceRemark);
		//对t_circulate_order流转单主单信息表进行收货标记操作
		circulateOrderService.updateReceiveInfoForCirculateOrder_inOrgActualScan(orderCode, sessionUser, differenceRemark);
		return ajaxBean;
	}

	/**
	 * 照单全收
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public AjaxBean inOrgAll(SystemUser sessionUser, String orderCode,String deviceRemark) {
		AjaxBean ajaxBean = new AjaxBean();
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		// 当包装流转单的收货仓库不是当前所选仓库时不能收货
		if (!circulateOrder.getTargetOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())) {
			ajaxBean.setStatus(StatusCode.STATUS_315);
			ajaxBean.setMsg(StatusCode.STATUS_315_MSG);
			return ajaxBean;
		}
		//包装流转单尚未发货，不能进行收货！
		if(circulateOrder.getPrintNumber() < SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_354);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_354_MSG);
			return ajaxBean;
		}
		// 获取包装流转单详情
		List<CirculateDetail> circulateDetail = circulateOrderService.queryCirculateDetailByOrderCode(orderCode);
		if (CollectionUtils.isEmpty(circulateDetail)) {
			ajaxBean.setStatus(StatusCode.STATUS_333);
			ajaxBean.setMsg(StatusCode.STATUS_333_MSG); //包装流转单数据不存在！请核查！
			return ajaxBean;
		}
		//获取这个单子上所有未收货（receive_number = 0）的器具列表
		List<Container> circulateDetailList = jdbcTemplate.query(queryContainerAboutReceive0, new BeanPropertyRowMapper(Container.class), orderCode);
		String epcIds = "";
		//批量更新流转记录
		batchUpdateCirculates(sessionUser, deviceRemark, circulateOrder, circulateDetailList);
		//收货后，如果是托盘，则解托
		for (Container container : circulateDetailList) {
			epcIds = epcIds + "'" +container.getEpcId() +"'," ;
			//如果是托盘，则解托
			if(container.getIsTray() == SysConstants.INTEGER_1) {
				containerGroupService.relieveContainerGroup(sessionUser, container.getEpcId());
			}
		}
		//组装InventoryLatest对象，更新表t_inventory_latest，新记录插入历史表t_inventory_history
		inventoryHistoryService.buildInventoryLatest(sessionUser, orderCode, deviceRemark, epcIds);
		//更新t_circulate_detail 流转单器具明细表收货状态
		circulateOrderService.updateReceiveInfoForCirculateDetail_inOrgAll(orderCode,sessionUser);
		// 更新t_circulate_order主表状态为：2 已全部收货
		circulateOrderService.updateCirculateOrderReceiverMsg(orderCode, sessionUser, SysConstants.STRING_2,StringUtils.EMPTY);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setList(circulateDetail);
		ajaxBean.setBean(circulateOrder);
		return ajaxBean;
	}

	/**
	 * 批量更新流转记录
	 * @param sessionUser
	 * @param deviceRemark
	 * @param circulateOrder
	 * @param circulateDetailList
	 */
	private void batchUpdateCirculates(SystemUser sessionUser, String deviceRemark, CirculateOrder circulateOrder, List<Container> circulateDetailList) {
		circulateService.updateOrInsertCirculateHistoryForReceive(sessionUser, circulateDetailList, circulateOrder,deviceRemark);
		// 如果是销售出库，那么收货的时候，需更新器具的隶属仓库 t_container表belong_org_id字段为收货仓库
		if (CirculateState.SELL.getCode().equals(circulateOrder.getTradeTypeCode()) && CollectionUtils.isNotEmpty(circulateDetailList)) { // 如果是销售出库
			updateContainerBelongOrgId(sessionUser, circulateDetailList);
		}
	}

	/**
	 * 如果是销售出库，那么收货的时候，需更新器具的隶属仓库 t_container表belong_org_id字段为收货仓库
	 * @param ajaxBean
	 * @param detailList
	 * @param sessionUser
	 * @param circulateOrder
	 * @return
	 */
	public void updateContainerBelongOrgId(SystemUser sessionUser, List<Container> circulateDetailList) {
		List<Container> paramList = new ArrayList<Container>();
		for (Container con : circulateDetailList) {
			Container c = new Container();
			c.setBelongOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			c.setBelongOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			c.setModifyAccount(sessionUser.getAccount());
			c.setModifyRealName(sessionUser.getRealName());
			c.setModifyOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			c.setModifyOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			c.setEpcId(con.getEpcId());
			paramList.add(c);
		}
		namedJdbcTemplate.batchUpdate(updateContainerBelongOrgId, SqlParameterSourceUtils.createBatch(paramList.toArray()));
	}
	/**
	 * web端，【收货】页面，手工流转单收货入库，根据器具代码输入收货数量。与非手工流转单收货逻辑有差别。
	 * @param sessionUser
	 * @param orderCode
	 * @param detailList
	 * @return
	 */
	public void inOrgWebManualOrder(SystemUser sessionUser, String orderCode, List<CirculateDetail> detailList) {
		//组装SQL需要的参数
		for (CirculateDetail c : detailList) {
			c.setReceiveAccount(sessionUser.getAccount());
			c.setReceiveRealName(sessionUser.getRealName());
			c.setReceiveOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			c.setReceiveOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			c.setOrderCode(orderCode);
			
			//对当前仓库的某个器具代码做库存数量的加法（收货）
			InventoryHistory ih = new InventoryHistory();
			ih.setContainerCode(c.getContainerCode()); //器具代码
			ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
			ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
			ih.setReceiveNumber(c.getReceiveNumber()); //此次收货数量
			ih.setOrderCode(orderCode); //流转单号
			ih.setRemark("web");
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			ih.setIsReceive(true); // true收货，false发货
			LOG.info("更新库存--["+orderCode+"]手工单收货。仓库：[" + ih.getOrgId() + "]["+ih.getOrgName()+"]器具代码：["+ih.getContainerCode()+"]+["+ih.getReceiveNumber()+"]-["+ih.getSendNumber()+"]=["+ih.getInOrgNumber()+"]备注：["+ih.getRemark()+"]");
			inventoryHistoryService.updateInventoryLatest(ih);
		}
		//更新 t_circulate_detail器具明细表收货状态
		namedJdbcTemplate.batchUpdate(updateReceiveInfoForCirculateDetail_inOrgWebManualOrder, SqlParameterSourceUtils.createBatch(detailList.toArray()));
		//对t_circulate_order流转单主单信息表进行收货标记操作
		circulateOrderService.updateReceiveInfoForCirculateOrder_inOrgWebManualOrder(orderCode, sessionUser);
	}


	/**
	 * web端，【收货】页面，非手工流转单收货入库，根据器具代码输入收货数量。按手工流转单逻辑基础上新增新逻辑
	 * @param sessionUser
	 * @param orderCode
	 * @param detailList
	 * @return
	 */
	public void inOrgWebOrder(SystemUser sessionUser, String orderCode, List<CirculateDetail> detailList) {
		//组装SQL需要的参数
		for (CirculateDetail c : detailList) {
			c.setReceiveAccount(sessionUser.getAccount());
			c.setReceiveRealName(sessionUser.getRealName());
			c.setReceiveOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			c.setReceiveOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			c.setOrderCode(orderCode);

			//对当前仓库的某个器具代码做库存数量的加法（收货）
			InventoryHistory ih = new InventoryHistory();
			ih.setContainerCode(c.getContainerCode()); //器具代码
			ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
			ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
			ih.setReceiveNumber(c.getReceiveNumber()); //此次收货数量
			ih.setOrderCode(orderCode); //流转单号
			ih.setRemark("web");
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			ih.setIsReceive(true); // true收货，false发货
			LOG.info("更新库存--["+orderCode+"]手工单收货。仓库：[" + ih.getOrgId() + "]["+ih.getOrgName()+"]器具代码：["+ih.getContainerCode()+"]+["+ih.getReceiveNumber()+"]-["+ih.getSendNumber()+"]=["+ih.getInOrgNumber()+"]备注：["+ih.getRemark()+"]");
			inventoryHistoryService.updateInventoryLatest(ih);
		}
		//更新 t_circulate_detail器具明细表收货状态
		namedJdbcTemplate.batchUpdate(updateReceiveInfoForCirculateDetail_inOrgWebOrder, SqlParameterSourceUtils.createBatch(detailList.toArray()));
		//对t_circulate_order流转单主单信息表进行收货标记操作
		circulateOrderService.updateReceiveInfoForCirculateOrder_inOrgWebManualOrder(orderCode, sessionUser);
		//将数据插入 t_circulate_detail_receive
		insertCirculateDetailReceive(sessionUser, orderCode, detailList);
		//更新表circulate_order的is_circulate_detail_receive字段
		circulateOrderService.updateCirculateDetailReceive(orderCode);
		//过滤出完全收货的 container code，并转换为where in中的形式
		String containerCodes = detailList.stream()
				.filter(d -> d.getSendNumber() == d.getReceiveNumber())
				.map(d -> "'" + d.getContainerCode() + "'")
				.collect(Collectors.joining(","));
		if (StringUtils.isNotBlank(containerCodes)) {
			//更新表 T_CIRCULATE_DETAIL，当实收数量和实发数量相等时将收货数量设置为1
			circulateService.updateReceiveNumber(orderCode, containerCodes);
			//批量更新流转记录
			CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
			List fullReceiveDetailList = jdbcTemplate.query(queryContainerAboutReceive + containerCodes + SysConstants.YKH + SysConstants.YKH,
					new BeanPropertyRowMapper(Container.class), orderCode);
			batchUpdateCirculates(sessionUser, SysConstants.DEVICE_WEB, circulateOrder, fullReceiveDetailList);
		}
	}

	/**
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @param circulateDetailList
	 */
	public void insertCirculateDetailReceive(SystemUser sessionUser, String orderCode, List<CirculateDetail> circulateDetailList) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		jdbcTemplate.batchUpdate(insertCirculateDetailReceive, new BatchPreparedStatementSetter()
		{
			@Override
			public int getBatchSize()
			{
				return circulateDetailList.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException
			{
				ContainerCode containerCode = containerCodeService.queryByContainerCode(circulateDetailList.get(i).getContainerCode());

				ps.setString(1, UUIDUtil.creatUUID());
				ps.setString(2, orderCode);
				ps.setString(3, circulateDetailList.get(i).getContainerCode());
				ps.setString(4, containerCode.getContainerName());
				ps.setString(5, containerCode.getContainerTypeId());
				ps.setString(6, containerCode.getContainerTypeName());
				ps.setInt(7, circulateDetailList.get(i).getPlanNumber());
				ps.setInt(8, circulateDetailList.get(i).getSendNumber());
				ps.setInt(9, circulateDetailList.get(i).getReceiveNumber());
				ps.setInt(10, circulateDetailList.get(i).getReceiveNumber() - circulateDetailList.get(i).getSendNumber());
				ps.setTimestamp(11, now);
				ps.setString(12, sessionUser.getCurrentSystemOrg().getOrgId());
				ps.setString(13, sessionUser.getCurrentSystemOrg().getOrgName());
				ps.setString(14, sessionUser.getAccount());
				ps.setString(15, sessionUser.getRealName());
			}
		});
	}

}
