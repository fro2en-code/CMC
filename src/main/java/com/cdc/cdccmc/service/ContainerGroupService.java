package com.cdc.cdccmc.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.cdc.cdccmc.domain.*;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.door.DoorScan;
import com.cdc.cdccmc.domain.door.DoorScanGroup;
import com.cdc.cdccmc.domain.door.DoorScanGroupResult;
import com.cdc.cdccmc.domain.dto.ContainerGroupDto;

import com.cdc.cdccmc.service.sys.SystemUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.util.CmcException;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemUser;

/**
 * 器具组托和解托
 *
 * @author ZhuWen
 * @date 2018-01-17
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerGroupService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerGroupService.class);
	@Autowired
	private ContainerService containerService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private DoorEquipmentService doorEquipmentService;
	@Autowired
	private SystemUserService systemUserService;

	@Value("#{sql['updateContainerGroupSetGroupState1']}")
	private String updateContainerGroupSetGroupState1;
	@Value("#{sql['queryContainerGroupByEpcAndState']}")
	private String queryContainerGroupByEpcAndState;
	@Value("#{sql['deleteContainerByEpcId']}")
	private String deleteContainerByEpcId;
	@Value("#{sql['updateContainerGroupNumber']}")
	private String updateContainerGroupNumber;
	@Value("#{sql['updateContainerGroupNumberNew']}")
	private String updateContainerGroupNumberNew;
    @Value("#{sql['queryDoorScanGroupList']}")
    private String queryDoorScanGroupList;
    @Value("#{sql['queryDoorScanGroupDetail']}")
    private String queryDoorScanGroupDetail;
    @Value("#{sql['queryContainerGroupForApp']}")
    private String queryContainerGroupForApp;
    @Value("#{sql['queryContainerGroupByGrupId']}")
    private String queryContainerGroupByGrupId;
    @Value("#{sql['queryDoorScanGroupDetailList']}")
    private String queryDoorScanGroupDetailList;
	@Value("#{sql['queryContainerByGroupId']}")
	private String queryContainerByGroupId;
	@Value("#{sql['queryContainerByDoorScan']}")
	private String queryContainerByDoorScan;
	@Value("#{sql['removeDoorScan']}")
	private String removeDoorScan;
	@Value("#{sql['removeDoorScanByGroupId']}")
	private String removeDoorScanByGroupId;
	@Value("#{sql['queryDoorScanByGroupId']}")
	private String queryDoorScanByGroupId;
	@Value("#{sql['insertDoorScanByValues']}")
	private String insertDoorScanByValues;
	@Value("#{sql['removeCirculateDetail']}")
	private String removeCirculateDetail;
	@Value("#{sql['deleteGroupByEpcIdAndGroupId']}")
	private String deleteGroupByEpcIdAndGroupId;
	@Value("#{sql['sumGroupByGroupId']}")
	private String sumGroupByGroupId;
	@Value("#{sql['updateGroupNumberByGroupId']}")
	private String updateGroupNumberByGroupId;
	@Value("#{sql['queryContainerNotExistsCirulateOrderDeatil']}")
	private String queryContainerNotExistsCirulateOrderDeatil;
	@Value("#{sql['insertDoorScan']}")
	private String insertDoorScan;
	@Value("#{sql['queryContainerForReBindResult']}")
	private String queryContainerForReBindResult;
	@Value("#{sql['queryContainerForReBindResult2']}")
	private String queryContainerForReBindResult2;
	@Value("#{sql['queryDoorScanReceiveList']}")
	private String queryDoorScanReceiveList;
	@Value("#{sql['queryDoorScanReceiveDetail']}")
	private String queryDoorScanReceiveDetail;
	@Value("#{sql['queryDoorScanReceiveOrdersDetail']}")
	private String queryDoorScanReceiveOrdersDetail;

	/**
	 * 分组查看门型扫描数据 FOR APP
	 * @param doorId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AjaxBean queryDoorScanGroupInfo(String doorId)
    {
    	AjaxBean ajaxBean = new AjaxBean();
        List<DoorScanGroup> doorScanGroups = jdbcTemplate.query(queryDoorScanGroupList,
                new BeanPropertyRowMapper(DoorScanGroup.class), doorId);
		List<DoorScanGroup> doorScanGroupsResult = new ArrayList<>();
		doorScanGroupsResult.addAll(doorScanGroups);
        for(DoorScanGroup doorScanGroup:doorScanGroups)
        {
			List<DoorScanGroupResult> doorScanGroupResults = null;
        	if(0==doorScanGroup.getIsGroup().intValue())
        	{//非托盘
				doorScanGroupResults = jdbcTemplate.query(queryDoorScanGroupDetail,
						new BeanPropertyRowMapper(DoorScanGroupResult.class), doorId, doorScanGroup.getCreateTime());
			}
			else
			{//托盘 查托盘组明细
				doorScanGroupResults = jdbcTemplate.query(queryContainerGroupForApp,
						new BeanPropertyRowMapper(DoorScanGroupResult.class),  doorScanGroup.getGroupId());
			}
			if (CollectionUtils.isNotEmpty(doorScanGroupResults))
			{
				doorScanGroup.setDoorScanGroupResultList(doorScanGroupResults);
			}
			else
			{
				doorScanGroupsResult.remove(doorScanGroup);
			}
        }
		ajaxBean.setList(doorScanGroupsResult);
        return ajaxBean;
    }

    /**
	 * 分组查看门型扫描数据 FOR APP
	 * @param doorId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AjaxBean queryDoorScanReceive(String doorId)
    {
    	AjaxBean ajaxBean = new AjaxBean();
        List<DoorScanReceive> doorScanReceiveList = jdbcTemplate.query(queryDoorScanReceiveList,
                new BeanPropertyRowMapper(DoorScanReceive.class), doorId);
		List<DoorScanReceive> doorScanGroupsResult = new ArrayList<>();
		doorScanGroupsResult.addAll(doorScanReceiveList);
        for(DoorScanReceive doorScanReceive:doorScanReceiveList)
        {
			List<DoorScanGroupResult> doorScanGroupResults = null;
			doorScanGroupResults = jdbcTemplate.query(queryDoorScanReceiveDetail,
					new BeanPropertyRowMapper(DoorScanGroupResult.class), doorId, doorScanReceive.getCreateTime());
			List<DoorScanReceiveOrder> doorScanOrderCodeList = jdbcTemplate.query(queryDoorScanReceiveOrdersDetail,
					new BeanPropertyRowMapper(DoorScanReceiveOrder.class), doorId, doorScanReceive.getCreateTime());

			doorScanReceive.setDoorScanReceiveOwnOrderList(doorScanOrderCodeList.stream()
					.filter(m->m.getIsOwnOrg() == 0).map(m->m.getOrderCode()).collect(Collectors.toList()));
			doorScanReceive.setDoorScanReceiveOtherOrderList(doorScanOrderCodeList.stream()
					.filter(m->m.getIsOwnOrg() == 1).map(m->m.getOrderCode()).collect(Collectors.toList()));
			if (CollectionUtils.isNotEmpty(doorScanGroupResults))
			{
				doorScanReceive.setDoorScanGroupResultList(doorScanGroupResults);
				doorScanReceive.setCount(doorScanGroupResults.stream().mapToInt(m->m.getContainerCount()).sum());
			}
			else
			{
				doorScanGroupsResult.remove(doorScanReceive);
			}
        }
		ajaxBean.setList(doorScanGroupsResult);
        return ajaxBean;
    }

	/**
	 * 门型扫描-添加按钮
	 * 1.添加时要创建这个流转单明细（器具）.
	 * 2.操作回退时删除流转单明细,并且把door_scan表的上一次修改的数据恢复到数据库中
	 * 3.移除只是把door_scan表的数据物理清除
	 * @param session
	 * @param sessionUser
	 * @param doorAccount
	 * @param cirulateOrderCode
	 * @param groupId
	 * @param scanTime
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
    public AjaxBean doorScanAdd(HttpSession session, SystemUser sessionUser, String doorAccount, String cirulateOrderCode, String groupId, String scanTime)
    {
    	AjaxBean ajaxBean = new AjaxBean();
		List<DoorScan> doorScans = queryDoorSacnDetailList(doorAccount,groupId,scanTime);
		if(CollectionUtils.isNotEmpty(doorScans))
		{
			//session里存储门型添加的数据 只保留上一次操作的记录 回退时把这个数据再插回door_scan
			session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD, doorScans);
			//session里存储门型添加的数据所关联的流转单orderCode 回退时使用清除明细数据
			session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE, cirulateOrderCode);
			List<Container> containerList = null;
			if (StringUtils.isEmpty(groupId) || StringUtils.isBlank(groupId) || StringUtils.equals(groupId, "null"))
			{//非托盘 用epcid查器具
				containerList = jdbcTemplate.query(queryContainerByDoorScan,
						new BeanPropertyRowMapper(Container.class), doorAccount, scanTime);
				//jdbcTemplate.update(removeDoorScan, doorAccount, scanTime);
			}
			else
			{//托盘通过group_id去查托盘组
				containerList = jdbcTemplate.query(queryContainerByGroupId,
						new BeanPropertyRowMapper(Container.class), groupId);
				//jdbcTemplate.update(removeDoorScanByGroupId, groupId);
			}
			if (CollectionUtils.isNotEmpty(containerList))
			{
				//如果是维修出库，需校验器具是否都是“维修中”
				ajaxBean = circulateOrderDeliveryService.checkContainExistNew(ajaxBean, circulateOrderService.queryCirculateOrderByOrderCode(cirulateOrderCode), containerList, sessionUser.getCurrentSystemOrg().getOrgId());
				if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
					return ajaxBean;
				}
				try
				{
					circulateOrderDeliveryService.buildAndInsertCirculateDetail(null, sessionUser
							, cirulateOrderCode, containerList,doorScans.get(0).getCreateTime());
					if (StringUtils.isBlank(groupId) || StringUtils.equals(groupId, "null"))
					{//非托盘 用epcid查器具
						jdbcTemplate.update(removeDoorScan, doorAccount, scanTime);
					}
					else
					{//托盘通过group_id去查托盘组
						jdbcTemplate.update(removeDoorScanByGroupId, groupId);
					}
					List<String> epcIdList = containerList.stream().map(Container::getEpcId).collect(Collectors.toList());
					session.setAttribute(SysConstants.SESSION_DOORSCAN_ADD_EPCIDS, epcIdList);
				}
				catch (Exception e)
				{
					ajaxBean = AjaxBean.FAILURE();
					ajaxBean.setMsg("创建流转明细单异常!");
					e.printStackTrace();
					return ajaxBean;
				}
			}
			ajaxBean = AjaxBean.SUCCESS();
			return ajaxBean;
		}
		else
		{
			ajaxBean = AjaxBean.FAILURE();
			ajaxBean.setMsg("没有可添加的数据,可能数据已经添加了!");
			return ajaxBean;
		}
    }

	/**
	 * 门型扫描-移除按钮
	 * 移除现在没有要求回退所以不用放入session
	 * 也不用记录操作日志
	 * @param session
	 * @param sessionUser
	 * @param doorAccount
	 * @param groupId
	 * @param scanTime
	 * @return
	 */
    public AjaxBean doorScanRemove(HttpSession session, SystemUser sessionUser,String doorAccount,String groupId,Date scanTime)
    {
		if(StringUtils.isEmpty(groupId) || StringUtils.equals(groupId, "null"))
		{//非托盘
			jdbcTemplate.update(removeDoorScan,doorAccount,scanTime);
		}
		else
		{//托盘
			jdbcTemplate.update(removeDoorScanByGroupId,groupId);
		}
        return AjaxBean.SUCCESS();
    }

    /**
     * 查询门型扫描数据详细列表
     * @param doorAccount
     * @param groupId
     * @param scanTime
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<DoorScan> queryDoorSacnDetailList(String doorAccount,String groupId,String scanTime) {
    	List<DoorScan> list;
    	if(StringUtils.isEmpty(groupId) || StringUtils.isBlank(groupId) || StringUtils.equals(groupId, "null"))	{
    		LOG.info("isBlank = true; groupId == " +groupId);
    		list = jdbcTemplate.query(queryDoorScanGroupDetailList,
					new BeanPropertyRowMapper(DoorScan.class), doorAccount, 0, scanTime);
		} else {
    		LOG.info("isBlank = false; groupId == " +groupId);
    		list = jdbcTemplate.query(queryContainerGroupByGrupId,
					new BeanPropertyRowMapper(DoorScan.class), groupId);
		}
    	return list;
    }

	/**
	 * 门型扫描-回退按钮
	 * @param session
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public AjaxBean rollBackDoorScan(HttpSession session)
	{
		AjaxBean ajaxBean = null;
		List<DoorScan> doorScans = new ArrayList<>();
		String cirulateOrderCode = null;
		try
		{
			List sessionCache = (List) session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD);
			cirulateOrderCode = (String)session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_ORDERCODE);
			if(CollectionUtils.isNotEmpty(sessionCache) && StringUtils.isNotEmpty(cirulateOrderCode))
			{
				CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(cirulateOrderCode);
				if(null != circulateOrder && circulateOrder.getPrintNumber().intValue()>0)
				{
					ajaxBean = AjaxBean.FAILURE();
					ajaxBean.setMsg("流转单["+cirulateOrderCode+"]已经发货,不能回退数据!");
					return ajaxBean;
				}
				doorScans.addAll(sessionCache);
				//回退后初始化 不设null 防止APP再次点击回退报异常
				try
				{
					batchInsertDoorScanNew(doorScans);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					ajaxBean = AjaxBean.FAILURE();
					ajaxBean.setMsg("门型扫描数据提交至数据库异常!");
					return ajaxBean;
				}
				try
				{
					List<String> epcIdList = (List)session.getAttribute(SysConstants.SESSION_DOORSCAN_ADD_EPCIDS);
					StringBuffer sql = new StringBuffer("delete from t_circulate_detail where order_code = ? and epc_id in (");
					sql.append(doorEquipmentService.convertStrListToStrs(epcIdList)).append(SysConstants.YKH);
					jdbcTemplate.update(sql.toString(), cirulateOrderCode);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					ajaxBean = AjaxBean.FAILURE();
					ajaxBean.setMsg("回退门型扫描数据过程中,清除流转单明细数据异常!");
					return ajaxBean;
				}
				//回退后清空session
				systemUserService.clearDoorScanSessionCache(session);
			}
			else
			{
				ajaxBean = AjaxBean.FAILURE();
				ajaxBean.setMsg("没有可回退的数据!");
				return ajaxBean;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ajaxBean = AjaxBean.FAILURE();
			ajaxBean.setMsg("获取用户上次添加的门型数据异常!");
			return ajaxBean;
		}
		return AjaxBean.SUCCESS();
	}

	/**
	 * 批量插入门型扫描数据  SQL上写?号个数匹配
	 * @param doorScans
	 */
	public void batchInsertDoorScan(List<DoorScan> doorScans)
	{
		if(CollectionUtils.isNotEmpty(doorScans))
		{
			jdbcTemplate.batchUpdate(insertDoorScanByValues, new BatchPreparedStatementSetter()
			{
				@Override
				public int getBatchSize()
				{
					return doorScans.size();
				}
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException
				{
					//door_scan_id, door_account, door_real_name, epc_id,\
					//group_id, container_code, container_type_id, container_type_name, is_tray, create_time, create_org_id, create_org_name
					ps.setString(1, doorScans.get(i).getDoorScanId());
					ps.setString(2, doorScans.get(i).getDoorAccount());
					ps.setString(3, doorScans.get(i).getDoorRealName());
					ps.setString(4, doorScans.get(i).getEpcId());
					ps.setString(5, doorScans.get(i).getGroupId());
					ps.setString(6, doorScans.get(i).getContainerCode());
					ps.setString(7, doorScans.get(i).getContainerTypeId());
					ps.setString(8, doorScans.get(i).getContainerTypeName());
					ps.setInt(9, doorScans.get(i).getIsGroup());
					ps.setTimestamp(10, doorScans.get(i).getCreateTime());
					ps.setString(11, doorScans.get(i).getCreateOrgId());
					ps.setString(12, doorScans.get(i).getCreateOrgName());
				}
			});
		}
	}
	/**
	 * 批量插入门型扫描数据 要求SQL上拼写上字段名
	 * @param doorScans
	 */
	public void batchInsertDoorScanNew(List<DoorScan> doorScans)
	{
		if(CollectionUtils.isNotEmpty(doorScans))
		{
			namedJdbcTemplate.batchUpdate(insertDoorScan, SqlParameterSourceUtils.createBatch(doorScans.toArray()));
		}
	}

    /**
	 * 列出当前所选仓库的包含所有子公司的所有组托，支持分页
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Paging pagingAllGroup(Paging paging, SystemUser sessionUser, String epcId,
			String groupState) {
		Map paramMap = new HashMap();
		String sql = "select g.*,l.org_name,l.org_id from T_CONTAINER_GROUP  g LEFT JOIN t_circulate_latest l on g.epc_id = l.epc_id where g.group_id in ( ";
		sql += " select group_id from T_CONTAINER_GROUP ";
		if(StringUtils.isNotBlank(epcId)) {
			sql += " where epc_id like '%"+epcId+"%'  ";
		}
		sql += " )  ";
		if (StringUtils.isNotBlank(groupState)) {
			sql += " and g.group_state = :groupState ";
			paramMap.put("groupState", groupState);
		}
		sql += " order by g.create_time desc,g.group_id ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, ContainerGroupDto.class);
		return paging;
	}

	/**
	 * 对指定器具进行整托解托
	 *
	 * @param sessionUser
	 *            当前登录用户
	 * @param groupIdList
	 *            指定器具的组托识别号
	 * @return
	 */
	public AjaxBean relieveGroup(AjaxBean ajaxBean, SystemUser sessionUser, List<String> groupIdList) throws CmcException {
		for (String epcId : groupIdList) {
			ajaxBean = relieveContainerGroup(sessionUser,epcId);
			if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
				//如果是此器具被之前选中的托盘器具提前解托，则此时解托失败不算真正失败。
				if(ajaxBean.getStatus() == StatusCode.STATUS_361) {
					ajaxBean.setStatus(StatusCode.STATUS_200);
					ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
					return ajaxBean;
				}
				//如果某个器具没有解托成功，则抛出异常使事物回滚
				throw new CmcException(ajaxBean.getStatus()+"",ajaxBean.getMsg());
			}
		}
		return ajaxBean;
	}

	/**
	 * 对器具(非托盘)进行单个解托操作,此器具已经是组托状态
	 *
	 * @param sessionUser
	 * @param epcId
	 * @return
	 */
	public AjaxBean unbindOneContainer(SystemUser sessionUser, String epcId) {
		// 获取该器具在组托表的信息
		List<ContainerGroup> containerGroup = jdbcTemplate.query(queryContainerGroupByEpcAndState,
				new BeanPropertyRowMapper(ContainerGroup.class), epcId);
		for (ContainerGroup cg : containerGroup) {
			if(cg.getGroupState() != 1) {
				// 将器具进行解托
				int result = jdbcTemplate.update(updateContainerGroupSetGroupState1, sessionUser.getAccount(), sessionUser.getRealName(),
						containerGroup.get(0).getGroupId(), epcId);
				return AjaxBean.returnAjaxResult(result);
			}
		}
		return null;
	}

	/**
	 * 解托
	 * @param sessionUser
	 * @param epcId
	 * @return
	 */
	public AjaxBean relieveContainerGroup(SystemUser sessionUser,String epcId)
	{
		AjaxBean ajaxBean = new AjaxBean();
		// 获取器具信息
		Container container = containerService.getContainerByEpcId(epcId);
		if (null == container) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("该器具不存在,请联系管理员核实!");
			return ajaxBean;
		}
		//将器具移动到当前仓库
		circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser,container,SysConstants.CIRCULATE_REMARK_RELIEVE_GROUP);
		// 根据epcId查询该器具是否处于组托状态
		if (containerGroupStatus(epcId) == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_361);
			ajaxBean.setMsg(StatusCode.STATUS_361_MSG);
			return ajaxBean;
		}
		ContainerGroup containerGroup = getGroupByEpcId(epcId);
		// 判断器具的属性，如果是托盘,并且是已组托状态
		if (container.getIsTray().intValue()==1 && containerGroup != null) {
			//进行整托解托
//			unbindAllContainerFromApp(sessionUser, container);
			String sql = "update t_container_group set group_state = '1', modify_time = sysdate(),modify_account=?, modify_real_name =? where group_id = ?";
			jdbcTemplate.update(sql,sessionUser.getAccount(),sessionUser.getRealName(), containerGroup.getGroupId());
			//2.解托成功后将托盘放入ajaxBean的bean中
			ajaxBean.setBean(container);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg("解托成功!");
			return ajaxBean;
		}
		// 如果是单个器具则对单个器具进行解托 进行物理删除组托记录
		jdbcTemplate.update(deleteGroupByEpcIdAndGroupId,containerGroup.getGroupId(),epcId);
		//记录用户的删除操作
		logService.addLogAccount(sessionUser, " ["+sessionUser.getRealName()+"]在组托["+containerGroup.getGroupId()+"]上解托单个器具["+epcId+"]，隶属托盘为["+containerGroup.getGroupEpcId()+"]，原操作人为["+containerGroup.getCreateRealName()+"]");
		//重新统计这一托的器具个数
		Integer newGroupNumber = jdbcTemplate.queryForObject(sumGroupByGroupId, Integer.class, containerGroup.getGroupId());
		jdbcTemplate.update(updateGroupNumberByGroupId, newGroupNumber,sessionUser.getAccount(),sessionUser.getRealName(),containerGroup.getGroupId());
		//取托盘
		container = containerService.getContainerByEpcId(containerGroup.getGroupEpcId());
		//获得该托所有器具
        String sql = "select * from t_container_group where group_id=?";
		List<ContainerGroup> groupList = jdbcTemplate.query(sql,new BeanPropertyRowMapper(ContainerGroup.class), containerGroup.getGroupId());
		ajaxBean.setList(groupList);
		ajaxBean.setBean(container);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg("解托成功!");
		return ajaxBean;
	}

	/**
	 * 更新组托数量设create操作人
	 * @param groupId
	 * @param count
	 * @return
	 */
	public void updateContainerGroupByGroupId(SystemUser sessionUser,Integer count,String groupId) {
		// 更新组托数量
		jdbcTemplate.update(updateContainerGroupNumberNew, count,sessionUser.getAccount(),sessionUser.getRealName(),groupId);
	}

	/**
	 * 对器具进行整托解托操作
	 *
	 * @param sessionUser
	 * @param
	 * @return
	 */
	public AjaxBean unbindWholeGrouop(SystemUser sessionUser, String groupId) {
		String relieveSql = "update T_CONTAINER_GROUP set group_state = 1,modify_time=sysdate(),modify_account=?, modify_real_name=? where group_id =?";
		int result = jdbcTemplate.update(relieveSql, sessionUser.getAccount(), sessionUser.getRealName(), groupId);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 查询器具组托状态,根据epcId查询该器具是否已经组托 结果等于1则代表已组托 为0则未组托
	 *
	 * @param epcId
	 * @return
	 */
	public Integer containerGroupStatus(String epcId) {
		String sql = "select count(*) from t_container_group where epc_id = :epcId and group_state = :groupState";
		ConcurrentHashMap map = new ConcurrentHashMap();
		map.put("epcId", epcId);
		map.put("groupState", 0);
		return namedJdbcTemplate.queryForObject(sql, map, Integer.class);
	}

	/**
	 * 根据组托epcId查询到已组托状态下的器具集合
	 *
	 * @param groupEpcId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ContainerGroup> getContainerGroupInfo(String groupEpcId) {
		String sql = "select * from t_container_group where group_epc_id = ? and group_state =?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerGroup.class), groupEpcId,
				SysConstants.STRING_0);
	}

	/**
	 * 根据epcId查询到该器具“已组托”状态的这一托器具
	 * @param epcId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ContainerGroup> getContainerGroupByEpcId(String epcId) {
		String sql = "select * from t_container_group where group_id = ( select group_id from t_container_group where epc_id = ? and group_state =0) and group_state =0";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerGroup.class), epcId);
	}

	/**
	 * 根据组托识别号group_id查询所有器具信息t_container表
	 * @param groupId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Container> queryContainerByGroupId(String groupId) {
		String sql = "select * from t_container where epc_id in ( select epc_id from t_container_group where group_id = ? and group_state = 0 )";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Container.class), groupId);
	}

	/**
	 * 1.通过epcId组托groupId
	 * 2.通过groupId关联出在组托上的器具epcId
	 * 3.通过epcId关联出器具列表
	 * @param epcId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Container> queryContainerByEpcId(String epcId) {
		String sql = "select * from t_container where epc_id in (select epc_id from t_container_group where group_id = ( select group_id from t_container_group where epc_id = ? and group_state =0) and group_state =0)";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Container.class), epcId);
	}

	/**
	 * 新增组托表
	 *
	 * @param groupId
	 *            组托识别号
	 * @param groupEpcId
	 *            组托EPC编号
	 * @param groupType
	 *            组托EPC器具关键字
	 * @param groupNumber
	 *            器具组托个数
	 * @param containerGroup
	 * @param container
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean addContainerGroup(String groupId, String groupEpcId, String groupType, Integer groupNumber,
			ContainerGroup containerGroup, Container container, SystemUser sessionUser) {
		containerGroup.setContainerGroupId(UUIDUtil.creatUUID());
		containerGroup.setGroupId(groupId);
		containerGroup.setEpcId(container.getEpcId());
		containerGroup.setContainerCode(container.getContainerCode());
		containerGroup.setContainerTypeId(container.getContainerTypeId());
		containerGroup.setContainerTypeName(container.getContainerTypeName());
		containerGroup.setContainerName(container.getContainerName());
		containerGroup.setGroupEpcId(groupEpcId);
		containerGroup.setGroupType(groupType);
		containerGroup.setGroupNumber(groupNumber);
		containerGroup.setGroupState(0);
		containerGroup.setCreateAccount(sessionUser.getAccount());
		containerGroup.setCreateRealName(sessionUser.getRealName());
		containerGroup.setVersion(0);
		String sql = "INSERT INTO t_container_group (container_group_id, group_id, epc_id, container_code, container_type_id, container_type_name, container_name, group_epc_id, group_type, group_number, group_state, create_time, create_account, create_real_name, modify_time, modify_account, modify_real_name)"
				+ "values(:containerGroupId ,:groupId ,:epcId ,:containerCode ,:containerTypeId ,:containerTypeName ,:containerName ,:groupEpcId ,:groupType ,:groupNumber ,:groupState ,sysdate() ,:createAccount,:createRealName ,:modifyTime ,:modifyAccount ,:modifyRealName)";
		int result = namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(containerGroup));
		return AjaxBean.SUCCESS();
	}

	/**
	 * app端创建器具组托
	 *
	 * @param groupId
	 *            组托识别号
	 * @param groupEpcId
	 *            组托EPC编号
	 * @param groupType
	 *            组托EPC器具关键字
	 * @param sessionUser
	 * @param containerList
	 *            要进行组托的器具List
	 */
	public void createContainerGroupFromApp(String groupId, String groupEpcId, String groupType, SystemUser sessionUser,
			List<Container> containerList) {
		int groupNumber = 0;
		List<ContainerGroup> containerGroupList = getContainerGroupInfo(groupEpcId);
		if (containerGroupList.size() > 0) {
			groupNumber = containerGroupList.size() + containerList.size();
			jdbcTemplate.update(updateGroupNumberByGroupId,groupNumber,sessionUser.getAccount(),sessionUser.getRealName(), groupId);
		} else {
			groupNumber = containerList.size();
		}
		for (Container container : containerList) {
			ContainerGroup containerGroup = new ContainerGroup();
			this.addContainerGroup(groupId, groupEpcId, groupType, groupNumber, containerGroup, container,
					sessionUser);
		}
	}

	/**
	 * app端解托所有器具
	 * @param sessionUser
	 * @param container
	 */
	public void unbindAllContainerFromApp(SystemUser sessionUser, Container container) {
		// 找到该托盘所在组托的所有器具信息
		List<ContainerGroup> containerGroup = this.getContainerGroupInfo(container.getEpcId());// 托盘的epcId就是这个组托的groupEpcId
		// 循环输出器具组托的epcId,并对其进行解绑操作
		for (ContainerGroup group : containerGroup) {
			this.unbindOneContainerNew(sessionUser, group.getEpcId());
		}
	}

	/**
	 * 逻辑解托
	 * @param sessionUser
	 * @param epcId
	 * @return
	 */
	public AjaxBean unbindOneContainerNew(SystemUser sessionUser, String epcId)
	{
		// 获取该器具在组托表的信息
		List<ContainerGroup> containerGroup = jdbcTemplate.query(queryContainerGroupByEpcAndState,
				new BeanPropertyRowMapper(ContainerGroup.class), epcId);
		int result = jdbcTemplate.update(updateContainerGroupSetGroupState1, sessionUser.getAccount(), sessionUser.getRealName(),
				containerGroup.get(0).getGroupId(), epcId);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 根据epc_id查询出同一托的所有器具
	 */
	public ContainerGroup getGroupByEpcId(String epcId){
		String sql = "SELECT * FROM t_container_group WHERE epc_id = ? and group_state = ?";
		List<ContainerGroup> groups = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerGroup.class), epcId,SysConstants.STRING_0);
		if (groups==null||groups.size()==0){
			return null;
		}else {
			return groups.get(0);
		}
	}

	/**
	 * 获取某个器具的组托状态
	 * @param ajaxBean
	 * @param con 该器具的最后一次流转记录
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean getContainerInfoAndGroupStatus(AjaxBean ajaxBean, Container con, SystemUser sessionUser) {
		//新增一条流转记录：使该器具在当前仓库，并且是“在库”状态。
		circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser,con,SysConstants.CIRCULATE_REMARK_APP_CONTAINER_GROUP_SHOW);
		//查询该器当前具有无组托信息
		List<ContainerGroup> findGroupList = getContainerGroupByEpcId(con.getEpcId());
		//如果是已组托状态
		if(CollectionUtils.isNotEmpty(findGroupList)) {
			ajaxBean.setBean(findGroupList.get(0).getGroupEpcId());
			ajaxBean.setList(findGroupList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
		}else {
			//该器具或托盘未组托！无相关组托明细。
			ajaxBean.setStatus(StatusCode.STATUS_360);
			ajaxBean.setMsg(StatusCode.STATUS_360_MSG);
		}
		return ajaxBean;
	}

	/**
	 * 创建器具组托
	 *
	 * @param sessionUser
	 * @param epcIdList
	 * @return
	 */
	public AjaxBean createContainerGroup(SystemUser sessionUser, List<String> epcIdList) {
		AjaxBean ajaxBean = new AjaxBean();
		List<Container> containerList = new ArrayList<Container>();//所有器具
		List<String> trayEpcIdList = new ArrayList<String>();//托盘epcid列表，用于返回信息
		List<Container> trayList = new ArrayList<Container>();//托盘
		List<Container> containerExceptTrayList = new ArrayList<Container>();//器具（不包含托盘）
		String groupId = "";// 生成组托识别号
		String groupEpcId = "";// 组托EPC编号
		String groupType = "托盘";// 组托EPC器具关键字
		if (CollectionUtils.isNotEmpty(epcIdList)) {
			// 初始化托盘和器具列表
			for (String epcId : epcIdList) {
				// 获取器具信息
				Container container = containerService.getContainerByEpcId(epcId);
				if (null == container) {
					ajaxBean.setStatus(StatusCode.STATUS_201);
					ajaxBean.setMsg("该器具" + epcId + "不存在,请联系管理员核实!");
					return ajaxBean;
				}
				// 如果器具不在当前库 就移到当前库
				circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser,container
						,SysConstants.CIRCULATE_REMARK_ADD_GROUP);
				Integer status = this.containerGroupStatus(epcId);
				containerList.add(container);
				//如果是托盘
				if (SysConstants.INTEGER_1 == container.getIsTray()) {//1为托盘
					trayEpcIdList.add(epcId);
					trayList.add(container);
				} else {
					containerExceptTrayList.add(container);
					//如果是组托状态
					if (SysConstants.INTEGER_1 == status) {
						// 组托成功后解托原来的在托器具（托盘除外）
						// 如果是单个器具则对单个器具进行解托 进行物理删除组托记录
						//单个解托后返回这个托上剩余器具个数
						ContainerGroup containerGroup = getGroupByEpcId(container.getEpcId());
						//如果是已组托，才有必要调用解托接口
						if(null != containerGroup) {
							relieveContainerGroup(sessionUser,epcId);
						}
					}
				}
			}

			Integer trayNum = trayList.size();
			if (SysConstants.INTEGER_0.equals(trayNum)) {
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("未识别到托盘，组托失败！");
				return ajaxBean;
			}
			if (SysConstants.INTEGER_1 < trayNum) {
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("识别到多个托盘，组托失败！");
				ajaxBean.setList(trayEpcIdList);
				return ajaxBean;
			}

			if (trayList != null && trayList.size() > 0) {
				Container trayContrainer = trayList.get(0);
				groupEpcId = trayContrainer.getEpcId();
				// 判断器托盘具是否已经组托
				Integer trayStatus = this.containerGroupStatus(trayContrainer.getEpcId());
				List<ContainerGroup> oldContainerGroupList = this.getContainerGroupByEpcId(trayContrainer.getEpcId());
				ContainerGroup trayContrainerGroup = new ContainerGroup();
				if (oldContainerGroupList.size() > 0) {
					trayContrainerGroup = oldContainerGroupList.get(0);
				}

				if (SysConstants.INTEGER_1 != trayStatus) {//创建组托
					groupId = UUIDUtil.creatUUID();
					this.createContainerGroupFromApp(groupId, groupEpcId, groupType, sessionUser, containerList);
					// 添加日志，[李四]在组托[组托识别号]上追加组托器具[3]个，隶属托盘为[XXXX]
					logService.addLogAccountAboutEpc(sessionUser,sessionUser.getRealName()+"在组托["+groupId+"]上追加组托器具["+containerList.size()+"]个,隶属托盘为["+groupEpcId+"]", groupEpcId);
				} else {//已经组托，进行追加组托
					groupId = trayContrainerGroup.getGroupId();
					this.createContainerGroupFromApp(groupId, groupEpcId, groupType, sessionUser, containerExceptTrayList);
					// 添加日志 “[李四]在组托[组托识别号]上追加组托器具[3]个，隶属托盘为[XXXX]，原操作人为[张三]”
					logService.addLogAccountAboutEpc(sessionUser,sessionUser.getRealName()+"在组托["+groupId+"]上追加组托器具["+containerExceptTrayList.size()+"]个,隶属托盘为["+groupEpcId+"],原操作人为["+trayContrainerGroup.getCreateAccount()+"]", groupEpcId);
				}
			}
		}

		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg("识别到托盘：" + groupEpcId + " 组托成功!");
		return ajaxBean;
	}

	/**
	 * APP-门型器具绑定-器具重绑
	 * @param sessionUser
	 * @param orderCode
	 * @param epcId
	 * @return
	 */
	public AjaxBean containerReBind(SystemUser sessionUser,String orderCode,String epcId)
	{
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setMsg("绑定成功!");
		Container container = containerService.getContainerByEpcId(epcId);
		if(null != container)
		{
			List<DoorScanGroupResult> doorScanGroupResults = null;
			List<Container> containerList = null;
			List<CirculateDetail> circulateDetails = null;
			if(1==container.getIsTray().intValue())
			{//托盘
				containerList = queryContainerByCheckIsTrayAndNotExistsCod(container);
			}
			else
			{//单个器具
				//判断器具是否已经绑定过流转单
				circulateDetails = circulateOrderService.queryCirculateDetailByEpcId(orderCode,epcId);
				if(CollectionUtils.isEmpty(circulateDetails))
				{
					containerList = new ArrayList<>();
					containerList.add(container);
				}
			}
			if (CollectionUtils.isNotEmpty(containerList))
			{
				// 检测器具是否存在,例如是维修出库，那么维修表里是否存在该器具的维修状态为：在库维修
				ajaxBean = circulateOrderDeliveryService.checkContainExist(ajaxBean
						, circulateOrderService.queryCirculateOrderByOrderCode(orderCode), containerList,
						sessionUser.getCurrentSystemOrg().getOrgId());
				if (!StatusCode.STATUS_200.equals(ajaxBean.getStatus()))
				{
					return ajaxBean;
				}
				try
				{
					Timestamp createTime = new Timestamp(System.currentTimeMillis());
					circulateOrderDeliveryService.buildAndInsertCirculateDetail(null, sessionUser
							, orderCode, containerList,createTime);
					for(Container container1:containerList)
					{
						//如果被扫描的这个器具不存在于当前仓库，则需要移动到当前仓库
						circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser, container1
								,SysConstants.CIRCULATE_REMARK_APP_RE_BIND);
					}
				}
				catch (Exception e)
				{
					ajaxBean.setStatus(StatusCode.STATUS_201);
					ajaxBean.setMsg("创建流转明细单异常!");
					e.printStackTrace();
				}
				//LOG.info("---containerReBind  本次重绑共添加器具数量="+containerList.size()+"   添加的epcid="+containerList.stream().map(Container::getEpcId).collect(Collectors.toList()));
			}
			//组织重绑的返回结果
			//统计返回结果 1.单个器具直接把epcid放入epcidList 2.托盘需要去查组托下器具在流转单明细中的器具列表
			List<String> epcIdList = null;
			if(1==container.getIsTray().intValue())
			{//托盘
				circulateDetails = circulateOrderService.queryCirculateDetailFromContainGroupEpcId(container.getEpcId());
				if(CollectionUtils.isNotEmpty(circulateDetails))
				{
					epcIdList = circulateDetails.stream().map(CirculateDetail::getEpcId).collect(Collectors.toList());
				}
			}
			else
			{
				epcIdList = Arrays.asList(container.getEpcId());
			}
			if(CollectionUtils.isNotEmpty(epcIdList))
			{
				//LOG.info("---containerReBind  添加结束后流转单中器具数量="+epcIdList.size()+"   器具的epcid="+epcIdList);
				String epcIds = doorEquipmentService.convertStrListToStrs(epcIdList);
				doorScanGroupResults = queryContainerForReBindResult(epcIds);
				ajaxBean.setList(doorScanGroupResults);
			}
		}
		else
		{
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("该器具" + epcId + "不存在,请联系管理员核实!");
		}
		return ajaxBean;
	}

	public List<DoorScanGroupResult> queryContainerForReBindResultBefore(String epcId)
	{
		return queryContainerForReBindResult(SysConstants.DYH+epcId+SysConstants.DYH);
	}

	public List<DoorScanGroupResult> queryContainerForReBindResult(String epcIds)
	{
		StringBuffer sql = new StringBuffer(queryContainerForReBindResult);
		sql.append(epcIds).append(queryContainerForReBindResult2);
		List<DoorScanGroupResult> doorScanGroupResults = jdbcTemplate.query(sql.toString(),
				new BeanPropertyRowMapper(DoorScanGroupResult.class));
		return doorScanGroupResults;
	}

	/**
	 * 判断器具是托盘就通过groupId查托上所有器具
	 * 然后过滤出不存在于流转单明细中的器具列表
	 * @param container
	 * @return
	 */
	public List<Container> queryContainerByCheckIsTrayAndNotExistsCod(Container container)
	{
		List<Container> containerList = new ArrayList<>();
		if(1==container.getIsTray().intValue())
		{//托盘 取出托盘中不在流转单明细的器具 插入流转单明细中
			List<ContainerGroup> containerGroups = getContainerGroupByEpcId(container.getEpcId());
			if(CollectionUtils.isNotEmpty(containerGroups))
			{//有组 需要把托盘上所有器具得到
				String groupId = containerGroups.get(0).getGroupId();
				containerList = jdbcTemplate.query(queryContainerNotExistsCirulateOrderDeatil,
						new BeanPropertyRowMapper(Container.class), groupId,groupId);
			}
		}
		return containerList;
	}

	/**
	 * 判断器具是托盘
	 * 1.通过epcId组托groupId
	 * 2.通过groupId关联出在组托上的器具epcId
	 * 3.通过epcId关联出器具列表
	 * @param container
	 * @return
	 */
	public List<Container> queryContainerByCheckIsTray(Container container)
	{
		List<Container> containerList = new ArrayList<>();
		if(1==container.getIsTray().intValue())
		{//托盘 取出托盘中不在流转单明细的器具 插入流转单明细中
			/*
			List<ContainerGroup> containerGroups = getContainerGroupInfo(container.getEpcId());
			if(CollectionUtils.isNotEmpty(containerGroups))
			{//有组 需要把托盘上所有器具得到
				String groupId = containerGroups.get(0).getGroupId();
				containerList = queryContainerByGroupId(groupId);
			}
			*/
			containerList = queryContainerByEpcId(container.getEpcId());
		}
		return containerList;
	}
}