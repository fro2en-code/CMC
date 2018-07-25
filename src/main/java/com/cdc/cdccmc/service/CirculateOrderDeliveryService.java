package com.cdc.cdccmc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.cdc.cdccmc.domain.*;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.dto.CirculateDto;
import com.cdc.cdccmc.service.basic.ContainerCodeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.DealResult;
import com.cdc.cdccmc.common.enums.IsOut;
import com.cdc.cdccmc.common.enums.MaintainState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.domain.sys.SystemUser;

@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateOrderDeliveryService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateOrderDeliveryService.class);
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private MaintainService maintainService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private DoorEquipmentService doorEquipmentService;
	
	@Value("#{sql['updateContaierLastOrgId']}")
	private String updateContaierLastOrgId;
	/**
	 * 新增包装流转单
	 */
	@Value("#{sql['insertCirculateOrder']}")
	private String insertCirculateOrder;
	/**
	 * 新增包装流转单详细表
	 */
	@Value("#{sql['insertCirculateDetail']}")
	private String insertCirculateDetail;
	@Value("#{sql['updateMaintainState']}")
	private String updateMaintainState;
	@Value("#{sql['updateScrapIsOut']}")
	private String updateScrapIsOut;
	@Value("#{sql['updateLostIsOut']}")
	private String updateLostIsOut;
	@Value("#{sql['insertContainerSell']}")
	private String insertContainerSell;
	@Value("#{sql['removeEpcFromCirculateOrder']}")
	private String removeEpcFromCirculateOrder;
    @Value("#{sql['queryCirculateLatestByOrgId']}")
    private String queryCirculateLatestByOrgId;
    @Value("#{sql['queryEpcSumDtoForCirculateDetail']}")
    private String queryEpcSumDtoForCirculateDetail;
    @Value("#{sql['queryCirculateDetailReceive']}")
    private String queryCirculateDetailReceive;

    /**
     * 通过仓库ID和流转单明细状态 10在库 来查询流转单LATEST
     * @param orgId
     * @param circulateState
     * @return
     */
	public List<CirculateDto> queryCirculateLatestByOrgId(String orgId, String circulateState)
	{
		List<CirculateDto> circulates = jdbcTemplate.query(queryCirculateLatestByOrgId, new BeanPropertyRowMapper(CirculateDto.class), orgId,circulateState);
		return circulates;
	}

	/**
	 * 通过仓库ID和流转单明细状态 10在库 来查询流转单LATEST
	 * @param orgId
	 * @param circulateState
	 * @return
	 */
	public List<CirculateDto> queryCirculateLatestByOrgIdLimit(String orgId, String circulateState,Integer start,Integer limit)
	{
		String sql = new String("select *,(select container_name from t_container_code c where c.container_code = l.container_code) container_name " +
				"from t_circulate_latest l where org_id = ? and circulate_state = ? limit ?,?");
		List<CirculateDto> circulates = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDto.class), orgId,circulateState,start,limit);
		return circulates;
	}
	/**
	 * 如果是报废出库，更新器具报废表T_CONTAINER_SCRAP出库状态（is_out）为：0已出库
	 * @param ajaxBean
	 * @param containerList 报废出库的器具列表
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	public AjaxBean updateContainerScrapIsOut(AjaxBean ajaxBean,List<Container> containerList,SystemUser sessionUser,String orderCode){
		Integer size = containerList.size();
		for (int i = 0; i < size; i++) {
			Container con = containerList.get(i);
			jdbcTemplate.update(updateScrapIsOut, orderCode,con.getEpcId(),sessionUser.getCurrentSystemOrg().getOrgId());
		}
		return ajaxBean;
	}

	/**
	 * 统计流转单明细总数
	 * @param orderCode
	 * @return
	 */
	public Integer countCirculateDetailByOrderCode(String orderCode) {
		String sql = "select count(*) from t_circulate_detail where order_code= :orderCode";
		ConcurrentHashMap map = new ConcurrentHashMap();
		map.put("orderCode", orderCode);
		return namedJdbcTemplate.queryForObject(sql, map, Integer.class);
	}

	/**
	 * 统计流转单明细总数
	 * @param orgId
	 * @return
	 */
	public Integer countCirculateLatestByOrgId(String orgId,String state) {
		String sql = "select count(*) from t_circulate_latest where org_id = :orgId and circulate_state = :state";
		ConcurrentHashMap map = new ConcurrentHashMap();
		map.put("orgId", orgId);
		map.put("state", state);
		return namedJdbcTemplate.queryForObject(sql, map, Integer.class);
	}

	/**
	 * 组建并新增包装流转单详细表  T_CIRCULATE_DETAIL
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param orderCode 包装流转单单号
	 * @param containerList 包装流转单详细里的器具列表
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateDetail(AjaxBean ajaxBean,SystemUser sessionUser,String orderCode,List<Container> containerList,Timestamp createTime){
		Integer size = containerList.size();
		Integer sequenceNo = countCirculateDetailByOrderCode(orderCode);
		List<CirculateDetail> batchParam = new ArrayList<CirculateDetail>();
		List<String> epcIdList = containerList.stream().map(Container::getEpcId).collect(Collectors.toList());
		StringBuffer deleteSql = new StringBuffer("delete from t_circulate_detail where order_code =? and epc_id in(").append(doorEquipmentService.convertStrListToStrs(epcIdList)).append(")");
		LOG.info("---buildAndInsertCirculateDetail  deleteSql="+deleteSql);
		//TODO 性能优化-添加流转单前要把原始数据清掉 重新插入 改成批量删除
		jdbcTemplate.update(deleteSql.toString(), orderCode);

		for (int i = 0; i < size; i++) {
			//器具对象
			Container container = containerList.get(i);
			//组装insert对象
			CirculateDetail detail = new CirculateDetail();
			detail.setCirculateDetailId(UUIDUtil.creatUUID());
			detail.setOrderCode(orderCode); //包装流转单单号
			detail.setSequenceNo((sequenceNo+i+1)+""); //序列号
			detail.setEpcId(container.getEpcId()); //epcId
			detail.setContainerCode(container.getContainerCode()); //器具代码
			detail.setContainerName(container.getContainerName()); //器具名称
			detail.setContainerTypeId(container.getContainerTypeId());  //器具类型ID
			detail.setContainerTypeName(container.getContainerTypeName()); //器具类型名称
			detail.setContainerSpecification(container.getContainerSpecification()); //规格
			detail.setPlanNumber(1); //计划数量
			detail.setSendNumber(1); //实发数量
			//detail.setCreateTime(createTime);//创建明细的时间要合门型扫描的时间一致 否则回退时区分不出是回退同一流转单下的哪批数据
			detail.setReceiveNumber(SysConstants.INTEGER_0);//实收数量
			detail.setCreateAccount(sessionUser.getCreateAccount());
			detail.setCreateRealName(sessionUser.getRealName()); //创建账号
			detail.setDealResult(DealResult.UN_DISPOSE.getDifferenceId()); //初始化为：1待处理
			detail.setCreateTime(DateUtil.currentTimestamp());
			batchParam.add(detail);
			//从包装流转单里删除单个器具
			//TODO 性能优化-添加流转单前要把原始数据清掉 重新插入 改成批量删除
			//jdbcTemplate.update(removeEpcFromCirculateOrder, orderCode, container.getEpcId());

			if(batchParam.size() >= SysConstants.MAX_INSERT_NUMBER){ //如果满足批量插入条数，则做批量插入操作
				namedJdbcTemplate.batchUpdate(insertCirculateDetail, SqlParameterSourceUtils.createBatch(batchParam.toArray()));
				batchParam.clear();
			}
		}
		if(batchParam.size() > SysConstants.INTEGER_0){ //如果还有剩余未插入的，再次执行批量插入
			namedJdbcTemplate.batchUpdate(insertCirculateDetail, SqlParameterSourceUtils.createBatch(batchParam.toArray()));
			batchParam = null;
		}
		return ajaxBean;
	}

	/**
	 * <pre>
	 * 检查器具是否存在:
	 * 如果包装流转单出库类别是：维修出库。检查 T_MAINTAIN 维修表里是否存在维修状态为：在库维修   的器具
	 * </pre>
	 * @param ajaxBean
	 * @param circulateOrder 包装流转单
	 * @param containerList 组装成包装流转单详细的器具列表
	 * @param currentOrgId 当前登录用户的当前的选择仓库
	 * @return
	 */
	public AjaxBean checkContainExistNew(AjaxBean ajaxBean,CirculateOrder circulateOrder,List<Container> containerList,String currentOrgId) {
		//如果流转类型是：流转出库的器具不需要检查特殊的其它表，只需要检查t_container器具表即可
		//维修出库
		if(CirculateState.MAINTAIN.getCode().equals(circulateOrder.getTradeTypeCode())){
			//检查 T_MAINTAIN 维修表里是否存在维修状态为：在库维修   的器具
			//TODO 性能优化-添加流转单
			List<String> epcIdList = containerList.stream().map(Container::getEpcId).collect(Collectors.toList());
			StringBuffer maintainStates = new StringBuffer( SysConstants.DYH).append(MaintainState.OUT_ORG.getCode()).append(SysConstants.DYH).append(SysConstants.DH)
					.append(SysConstants.DYH).append(MaintainState.FINISH.getCode()).append(SysConstants.DYH);
			List<String> epcIds = maintainService.findMaintainByEpcIdsAndState(doorEquipmentService.convertStrListToStrs(epcIdList)
					, maintainStates.toString(),currentOrgId);
			if(CollectionUtils.isNotEmpty(epcIds))
			{
				ajaxBean.setStatus(StatusCode.STATUS_311);
				ajaxBean.setMsg("EPC编号["+doorEquipmentService.convertStrListToStrsForUpdate(epcIds)+"]不是维修状态为“在库维修”的器具。");
				return ajaxBean;
			}
		}
		return ajaxBean;
	}

	/**
	 * <pre>
	 * 检查器具是否存在:
	 * 如果包装流转单出库类别是：维修出库。检查 T_MAINTAIN 维修表里是否存在维修状态为：在库维修   的器具
	 * </pre>
	 * @param ajaxBean
	 * @param circulateOrder 包装流转单
	 * @param containerList 组装成包装流转单详细的器具列表
	 * @param currentOrgId 当前登录用户的当前的选择仓库
	 * @return
	 */
	public AjaxBean checkContainExist(AjaxBean ajaxBean,CirculateOrder circulateOrder,List<Container> containerList,String currentOrgId) {
		//如果流转类型是：流转出库的器具不需要检查特殊的其它表，只需要检查t_container器具表即可
		//维修出库
		if(CirculateState.MAINTAIN.getCode().equals(circulateOrder.getTradeTypeCode())){
			//检查 T_MAINTAIN 维修表里是否存在维修状态为：在库维修   的器具
			for (int i = 0; i < containerList.size(); i++) {
				Container container = containerList.get(i);
				Maintain maintain = maintainService.findMaintainByEpcAndState(container.getEpcId(), MaintainState.IN_ORG.getCode(),currentOrgId);
				if(null == maintain){ //如果维修表里不存在“在库维修”的器具，则包装流转单不成立
					ajaxBean.setStatus(StatusCode.STATUS_311);
					ajaxBean.setMsg("EPC编号["+container.getEpcId()+"]不是维修状态为“在库维修”的器具。");
					return ajaxBean;
				}
			}
		}
		return ajaxBean;
	}
	/**
	 * 组装器具统计列表
	 * @param circulateOrder
	 * @return 返回一个每个器具代码有多少个器具数量的统计列表
	 */
	public List<EpcSumDto> buildEpcSumDtoByOrderCode(CirculateOrder circulateOrder) {
		List<EpcSumDto> epcSumList = null;
		//如果是普通流转单（非手工单）并且使用了手工输入实收数量的方式（web端收货页面收货）
		if(SysConstants.STRING_1.equals(circulateOrder.getIsCirculateDetailReceive())) {
			epcSumList = jdbcTemplate.query(queryCirculateDetailReceive, new BeanPropertyRowMapper(EpcSumDto.class),circulateOrder.getOrderCode());
		}else {
			epcSumList = jdbcTemplate.query(queryEpcSumDtoForCirculateDetail, new BeanPropertyRowMapper(EpcSumDto.class),circulateOrder.getOrderCode());	
		}
		int allSendNumber = 0 ;
		int allReceiveNumber = 0;
		LOG.info("---buildEpcSumDtoByOrderCode   epcSumList.size="+epcSumList.size());
		if(CollectionUtils.isNotEmpty(epcSumList)) {
			for (EpcSumDto e : epcSumList) {
				ContainerCode code = containerCodeService.queryByContainerCode(e.getContainerCode());
				if(null != code)
				{
					e.setContainerName(code.getContainerName());
				}
				else
				{
					e.setContainerName(SysConstants.NULL_STR);
				}
				e.setDifferentNumber(e.getReceiveNumber() - e.getSendNumber());
				allSendNumber += e.getSendNumber();
				allReceiveNumber += e.getReceiveNumber();
			}
		}
		int allDifferentNumber = allReceiveNumber - allSendNumber;
		for(EpcSumDto e : epcSumList) {
			e.setAllSendNumber(allSendNumber);  //流转单全部实发数量
			e.setAllReceiveNumber(allReceiveNumber);  //流转单全部实收数量
			e.setAllDifferentNumber(allDifferentNumber);
		}
		return epcSumList;
	}


}
