package com.cdc.cdccmc.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import com.cdc.cdccmc.domain.*;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.door.DoorScanGroupResult;
import com.cdc.cdccmc.domain.door.DoorScanReceiveOrder;
import com.cdc.cdccmc.service.sys.SystemOrgService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.DealResult;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.LoginController;
import com.cdc.cdccmc.domain.dto.CirculateDetailDto;
import com.cdc.cdccmc.domain.dto.ContainerDto;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.domain.print.PritCirculateOrder;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.CarService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;
import com.cdc.cdccmc.service.sys.SystemUserService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class CirculateOrderService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(LoginController.class);
	private static final String EXCHANGE_NAME = "order_exchange_print";
	//private static final String QUEUE_NAME = "order_direct_queue_";


	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private HandsetService handsetService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private CarService carService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
	@Autowired
	private DoorEquipmentService doorEquipmentService;

	@Value("#{sql['queryGroupEpcs']}")
	private String queryGroupEpcs;
	@Value("#{sql['pagingContainerCurrentOrg']}")
	private String pagingContainerCurrentOrg;
	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['insertCirculateHistory']}")
	private String insertCirculateHistory;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['updateContaierLastOrgId']}")
	private String updateContaierLastOrgId;
	@Value("#{sql['updateMaintainState']}")
	private String updateMaintainState;
	@Value("#{sql['updateScrapIsOut']}")
	private String updateScrapIsOut;
	@Value("#{sql['updateLostIsOut']}")
	private String updateLostIsOut;
	@Value("#{sql['insertContainerSell']}")
	private String insertContainerSell;
	@Value("#{sql['insertCirculateOrder']}")
	private String insertCirculateOrder;
	@Value("#{sql['updateCirculateOrderPrintNumber']}")
	private String updateCirculateOrderPrintNumber;
	@Value("#{sql['updateCirculateOrderIsReceive']}")
	private String updateCirculateOrderIsReceive;
	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private Integer port;
	@Value("${spring.rabbitmq.username}")
	private String userName;
	@Value("${spring.rabbitmq.password}")
	private String pwd;
	@Value("#{sql['removeEpcFromCirculateOrder']}")
	private String removeEpcFromCirculateOrder;
    @Value("#{sql['updateCirculateOrderPrintOrderTime']}")
    private String updateCirculateOrderPrintOrderTime;
	@Value("#{sql['queryCirculateOrderByDoorAccount']}")
	private String queryCirculateOrderByDoorAccount;
	@Value("#{sql['updateCirculateOrderLeaveTime']}")
	private String updateCirculateOrderLeaveTime;
	@Value("#{sql['queryContainerByGroupId']}")
	private String queryContainerByGroupId;
	@Value("#{sql['removeEpcByGroupId']}")
	private String removeEpcByGroupId;
	@Value("#{sql['queryCirculateDetailByEpcId']}")
	private String queryCirculateDetailByEpcId;
	@Value("#{sql['updateReceiveInfoForCirculateDetail_inOrgActualScan']}")
	private String updateReceiveInfoForCirculateDetail_inOrgActualScan;
	@Value("#{sql['updateReceiveInfoForCirculateDetail_inOrgAll']}")
	private String updateReceiveInfoForCirculateDetail_inOrgAll;
	@Value("#{sql['countReceiveForCirculateDetail']}")
	private String countReceiveForCirculateDetail;
	@Value("#{sql['queryCirculateDetaiByGroupId']}")
	private String queryCirculateDetaiByGroupId;
	@Value("#{sql['updateCirculateOrderReceiverForDoor']}")
	private String updateCirculateOrderReceiverForDoor;
	@Value("#{sql['queryCirculateDetailFromContainGroupEpcId']}")
	private String queryCirculateDetailFromContainGroupEpcId;
    @Value("#{sql['insertCirculateDetail']}")
    private String insertCirculateDetail;
    @Value("#{sql['updateReceiveInfoForCirculateOrder_inOrgWebManualOrder']}")
    private String updateReceiveInfoForCirculateOrder_inOrgWebManualOrder;
	@Value("#{sql['updateContainerBelongOrgId']}")
	private String updateContainerBelongOrgId;
	@Value("#{sql['invalidOrder']}")
	private String invalidOrder;

	/**
	 * 1.通过epcId查组托groupId
	 * 2.通过groupId关联出在组托上的器具epcId
	 * 3.通过epcId关联出流转单明细列表
	 * @param epcId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<CirculateDetail> queryCirculateDetailFromContainGroupEpcId(String epcId)
	{
		return jdbcTemplate.query(queryCirculateDetailFromContainGroupEpcId, new BeanPropertyRowMapper(CirculateDetail.class), epcId);
	}

	/**
	 * 通过EPCID查找流转单器具
	 * @param orderCode
	 * @param epcId
	 * @return
	 */
	public List<CirculateDetail> queryCirculateDetailByEpcId(String orderCode,String epcId)
	{
		return jdbcTemplate.query(queryCirculateDetailByEpcId,
				new BeanPropertyRowMapper(Container.class), orderCode,epcId);
	}

	/**
	 * 流转单-器具重绑-解绑
	 * @param sessionUser
	 * @param circulateOrderCode
	 * @param epcId
	 * @return
	 */
	public AjaxBean relieveContainer(SystemUser sessionUser, String circulateOrderCode,String epcId)
	{
		AjaxBean ajaxBean = null;
		List<Container> containerList = new ArrayList<>();
		Container container = containerService.getContainerByEpcId(epcId);
		if(null == container)
		{
			ajaxBean = AjaxBean.FAILURE();
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("该器具" + epcId + "不存在,请联系管理员核实!");
			return ajaxBean;
		}

		//如果被扫描的这个器具不存在于当前仓库，则需要移动到当前仓库
		circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser, container,SysConstants.CIRCULATE_REMARK_APP_RELIEVE_GROUP);

		String groupId = null;
		List<ContainerGroup> containerGroups = null;
		// 根据epcId查询该器具是否处于组托状态
		if (1==container.getIsTray().intValue())
		{//托盘
			containerGroups = containerGroupService.getContainerGroupInfo(container.getEpcId());
			if(CollectionUtils.isNotEmpty(containerGroups))
			{//有组 需要把托盘上所有器具得到
				groupId = containerGroups.get(0).getGroupId();
				containerList = jdbcTemplate.query(queryContainerByGroupId,
						new BeanPropertyRowMapper(Container.class), groupId);
			}
		}
		if(CollectionUtils.isEmpty(containerList))
		{
			containerList.add(container);
		}
		for(Container containerObj:containerList)
		{
			try
			{
				//记录操作日志  [xx]把器具[epcId]从流转单[XXX]上解绑
				StringBuffer msg = new StringBuffer("[");
				msg.append(sessionUser.getRealName()).append("]把器具[").append(containerObj.getEpcId()).append("]从流转单[")
						.append(circulateOrderCode).append("]上解绑");
				logService.addLogAccountAboutEpc(sessionUser,msg.toString(),epcId);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ajaxBean = AjaxBean.FAILURE();
				ajaxBean.setMsg("器具重绑-解绑记录操作日志发生异常!");
			}
		}
		try
		{
			List<DoorScanGroupResult> doorScanGroupResults = new ArrayList<>();
			if(containerList.size()>1)
			{//按托盘删除
				doorScanGroupResults = jdbcTemplate.query(queryCirculateDetaiByGroupId, new BeanPropertyRowMapper(DoorScanGroupResult.class),
						circulateOrderCode,groupId);
				jdbcTemplate.update(removeEpcByGroupId, circulateOrderCode,groupId);
			}
			else
			{//单个直接删除
				jdbcTemplate.update(removeEpcFromCirculateOrder, circulateOrderCode,epcId);
				DoorScanGroupResult doorScanGroupResult = new DoorScanGroupResult();
				doorScanGroupResult.setContainerCode(container.getContainerCode());
				doorScanGroupResult.setContainerCount(1);
				doorScanGroupResult.setContainerName(container.getContainerTypeName());
				doorScanGroupResults.add(doorScanGroupResult);
			}
			ajaxBean = AjaxBean.SUCCESS();
			ajaxBean.setList(doorScanGroupResults);
			ajaxBean.setMsg("解绑成功!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ajaxBean = AjaxBean.FAILURE();
			ajaxBean.setMsg("器具重绑-解绑发生异常!");
		}
		return ajaxBean;
	}


    /**
     * 更新流转单车辆离开时间
     *
     * @param orderCode
     * @param sessionUser
     */
    public AjaxBean updateLeaveTimeByOrderCode(SystemUser sessionUser, String orderCode)
	{
		AjaxBean ajaxBean = null;
    	try
		{
			jdbcTemplate.update(updateCirculateOrderLeaveTime, orderCode);
			ajaxBean = AjaxBean.SUCCESS();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ajaxBean = AjaxBean.FAILURE();
			ajaxBean.setMsg("更新车辆离开时间异常!");
		}
		return ajaxBean;
    }

	/**
	 * 通过门型设备账号查找未打印过的流转单
	 * @param doorAccount
	 * @return
	 */
	public AjaxBean queryCirculateOrderByDoorAccount(String doorAccount)
	{
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(jdbcTemplate.query(queryCirculateOrderByDoorAccount,
				new BeanPropertyRowMapper(CirculateOrder.class), doorAccount));
		return ajaxBean;
	}

	/**
	 * 包装流转单单号生成、包装入库单单号生成
	 *
	 * @param sessionUser
	 * @return
	 */
	public synchronized String createOrderCode(SystemUser sessionUser) {
		Integer serialNumber = 0; // 流水编号

		// 查询最新的流水编号
		String todayDateNo = DateUtil.today_yyyy_MM_dd();
		String querySerialNumberSql = "select * from T_SERIAL_NUMBER where date_no = '" + todayDateNo + "'";
		List<Map<String, Object>> serialNumberList = namedJdbcTemplate.queryForList(querySerialNumberSql,
				new HashMap());
		if (CollectionUtils.isEmpty(serialNumberList)) { // 如果没有查到今天的流水编号，则新增一条今天的,初始流水号为1
			serialNumber = 1;
			String insertSql = "INSERT INTO t_serial_number (date_no, SERIAL_NUMBER) VALUES (?, ?)";
			jdbcTemplate.update(insertSql, todayDateNo, serialNumber);
		} else { // 若找到今天的流水编号，则更新+1
			serialNumber = new Integer(serialNumberList.get(0).get("SERIAL_NUMBER").toString()) + 1;
			String updateSql = "update t_serial_number set SERIAL_NUMBER=? where date_no=?";
			jdbcTemplate.update(updateSql, serialNumber, todayDateNo);
		}

		int length = 4; // 流水编号长度为固定4位，不足位的前面补0
		String serialNumberStr = serialNumber.toString();
		int forLength = length - serialNumberStr.length();
		for (int i = 0; i < forLength; i++) {
			if (serialNumberStr.length() > length) {
				break;
			}
			serialNumberStr = "0" + serialNumberStr;
		}

		// 包装流转单单据编号，生成规则：仓库代码+日期年月日+四位流水号，例子：CMCSP201712250001
		String orderCode = sessionUser.getCurrentSystemOrg().getOrgCode() + DateUtil.today_yyyyMMdd() + serialNumberStr;

		CirculateOrder order = queryCirculateOrderByOrderCode(orderCode);
		while (null != order) {
			LOG.info("包装流转单单号[" + orderCode + "]已经存在，创建包装流转单单号失败！继续尝试生成单号...");
			orderCode = createOrderCode(sessionUser);
			order = queryCirculateOrderByOrderCode(orderCode);
		}

		LOG.info("新的包装流转单单号[" + orderCode + "]创建成功！");
		return orderCode;
	}

	/**
	 * 打印包装流转单
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 *            当前登录用户
	 * @param orderCode
	 *            包装流转单单号
	 * @return
	 */
	public AjaxBean printCirculateOrder(AjaxBean ajaxBean, SystemUser sessionUser, String orderCode,HttpSession session) {
		// 包装流转单不能为空
		if (StringUtils.isBlank(orderCode)) {
			ajaxBean.setMsg("包装流转单单号" + StatusCode.STATUS_305_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		LOG.info("开始打印包装流转单，单号为：[" + orderCode + "]");
		// 组装包装流转单DTO
		CirculateOrder order = this.queryCirculateOrderByOrderCode(orderCode);
		if (order == null) {
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg(StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		LOG.info("["+sessionUser.getRealName()+"]打印包装流转单["+orderCode+"]流转类别["+order.getTradeTypeName()+"]");
		List<CirculateDetail> detailList = this.queryCirculateDetailByOrderCode(orderCode);
		List<EpcSumDto> epcSumDtoList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(order);
		//打印后对应流转单的缓存清空 不能再回退
		systemUserService.clearDoorScanSessionCacheByOrderCode(session,orderCode);
		//打印后对应流转单的缓存清空 不能再回退 end
		PritCirculateOrder orderDto = new PritCirculateOrder();
		orderDto.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		orderDto.setTitle("包装流转单");
		orderDto.setBarCode(orderCode);// 条形码
		orderDto.setSendLocation(order.getConsignorOrgName());// 发货地点 --> 发货组织名称
		//orderDto.setReceiveLocation(order.getTargetOrgName());// 收货地点 --> 收货组织名称
		String receiveLocation = systemOrgService.findById(order.getTargetOrgId()).getOrgCode();
		if(StringUtils.isNotEmpty(receiveLocation))
		{
			receiveLocation +="-"+order.getTargetOrgName();
		}
		else
		{
			receiveLocation =order.getTargetOrgName();
		}
		orderDto.setReceiveLocation(receiveLocation);// 收货地点 --> 收货组织名称
		orderDto.setTransactionType(order.getTradeTypeName());// 交易类型 --> 交易类型名称
		// 发货组织类别ID，设置为YES即勾选
		switch (order.getConsignorOrgTypeName()) {
			case "车间":
				// 车间
				orderDto.setWsSendFlag("YES");
				orderDto.setWsSendCode(order.getConsignorWayOut());
				break;
			case "CMC":
				// cmc
				orderDto.setCmcSendFlag("YES");
				orderDto.setWsSendCode(order.getConsignorWayOut());
				break;
			case "DC":
				orderDto.setDcSendFlag("YES");
				orderDto.setWsSendCode(order.getConsignorWayOut());
				break;
			case "供应商":
				orderDto.setProviderSendFlag("YES");
				orderDto.setWsSendCode(order.getConsignorWayOut());
//				orderDto.setWsSendCode(systemOrgService.findById(order.getConsignorOrgId()).getOrgCode());
				break;
			case "维修工厂":
				orderDto.setSendByShopFlag("YES");
				break;
			default:
				orderDto.setOtherSend("YES");
		}
		// 收货组织类别ID，设置为YES即勾选
		switch (order.getTargetOrgTypeName()) {
			case "车间":
				// 车间
				orderDto.setWsReceiveFlag("YES");
				orderDto.setWsReceiveCode(order.getTargetWayOut());
				break;
			case "CMC":
				// cmc
				orderDto.setCmcReceiveFlag("YES");
				orderDto.setWsReceiveCode(order.getTargetWayOut());
				break;
			case "DC":
				orderDto.setDcReceiveFlag("YES");
				orderDto.setWsReceiveCode(order.getTargetWayOut());
				break;
			case "供应商":
				orderDto.setProviderReceiveFlag("YES");
				orderDto.setWsReceiveCode(order.getTargetWayOut());
//				orderDto.setWsReceiveCode(systemOrgService.findById(order.getTargetOrgId()).getOrgCode());
				break;
			case "维修工厂":
				orderDto.setReceiveByShopFlag("YES");
				break;
			default:
				orderDto.setOtherReceive("YES");
		}
		// 交易类别
		// 仓库调拨 = 出库
		// 维修转移 = 维修
		// 料架修改出库 = 维修
		// 报废出库 = 报废
		// 新包装入库 = 入库
		switch (order.getTradeTypeName()) {
			case "流转出库":
				orderDto.setOutByWarehouse("YES");
				break;
			case "维修出库":
				orderDto.setOutByRepair("YES");
				break;
			case "报废出库":
				orderDto.setOutByScrap("YES");
				break;
			case "采购入库":
				orderDto.setOutByNewPackage("YES");
				break;
			default:
				orderDto.setOtherOut("YES");
		}

		// 表格
		List<Map<String, Object>> list = new ArrayList<>();
		Integer i = 0;

		for (EpcSumDto o : epcSumDtoList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", String.valueOf(i + 1));
			map.put("code", o.getContainerCode().toString());
			map.put("name", o.getContainerName());
			map.put("containerSpecification", o.getContainerSpecification());
			map.put("planNumber", o.getPlanNumber());
			map.put("sendNumber", o.getSendNumber());
			//未收货的实收数量显示空字符串  收货的显示实收数量
			map.put("receiveNumber", SysConstants.STRING_0.equals(order.getIsReceive())?SysConstants.NULL_STR:o.getReceiveNumber());
//			map.put("size", o.getContainerSpecification());
//			map.put("planCount", o.getEpcCount().toString());
//			map.put("sendCount", o.getEpcCount().toString());
			map.put("receiveCount", ""); // 人工确认实收数量
			map.put("remark", "");
			list.add(map);
			i++;
		}
		orderDto.setDetails(list);
		orderDto.setPeopleId(sessionUser.getAccount());
		orderDto.setPeopleName(sessionUser.getRealName());
		orderDto.setPrintTime(DateUtil.currentTimestamp().toString());

		// 底部表格
		orderDto.setDescription(order.getSpecialDescription());// 特别描述
		orderDto.setTransportCompany(order.getShipperName());// 运输公司
		orderDto.setCarNo(order.getCarNo());// 车牌
		String jsonString = JSON.toJSONString(orderDto, SerializerFeature.WriteNullStringAsEmpty);
		LOG.info("发送的消息为"+jsonString);
		//不使用MQ直接调用打印
		//OrderPrint orderPrint = new OrderPrint();
		//orderPrint.print(jsonString);
		//rabbitTemplate.convertAndSend("orderExchange", "topic.order." + orderDto.getOrgId(), jsonString);
		/* 使用MQ异步打印 因为服务端要部署在云平台上 链接不到本地打印机 所以走MQ*/
		if (StringUtils.isNotBlank(jsonString)){
			try {
				LOG.info("初始化mq连接IP["+host+":"+port+"]");
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(host);
				factory.setPort(port);
				factory.setUsername(userName);
				factory.setPassword(pwd);
				if (StringUtils.isBlank(sessionUser.getCurrentSystemOrg().getOrgId())) {
					LOG.error("包装流转单打印失败: ordId is null.");
					ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
					ajaxBean.setMsg("包装流转单打印失败: ordId is null.");
					return ajaxBean;
				}
				String printName = systemUserService.getUserPrintCode(sessionUser);
				if(StringUtils.isEmpty(printName)) {
					LOG.info("---printCirculateOrder  printName="+printName);
					ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
					ajaxBean.setMsg("无法打印,您的帐号在仓库:"+sessionUser.getCurrentSystemOrg().getOrgName()+"中没有配置对应打印机!");
					return ajaxBean;
				}
				// MQ处理 routeKey
				String orgId = sessionUser.getCurrentSystemOrg().getOrgId()+printName;
				//String queueName = QUEUE_NAME + orgId;
				// 创建connection
				Connection conn = factory.newConnection();
				// 创建channel
				Channel channel = conn.createChannel();
				// 声明该channel是fanout类型
				LOG.info("创建交换机"+EXCHANGE_NAME);
				channel.exchangeDeclare(EXCHANGE_NAME, "direct",true);
				//LOG.info("创建队列"+ queueName);
				//channel.queueDeclare(queueName,true,false,false,null);
//				LOG.info("队列绑定交换机");
//				channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
				LOG.info("MQ绑定路由:"+orgId);
				// 将消息发送给exchange
				channel.basicPublish(EXCHANGE_NAME, orgId, null, jsonString.getBytes());


				ajaxBean.setStatus(StatusCode.STATUS_200);
				ajaxBean.setMsg("[" + orderCode + "]包装流转单单号打印成功");

				channel.close();
				conn.close();
			} catch (IOException e) {
				LOG.error("发送MQ异常，捕获到IOException异常："+e.getMessage(),e);
				ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
				ajaxBean.setMsg(StatusCode.STATUS_367_MSG);
			} catch (TimeoutException e) {
				LOG.error("发送MQ异常:连接超时，捕获到TimeoutException异常："+e.getMessage(),e);
				ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
				ajaxBean.setMsg(StatusCode.STATUS_367_MSG);
			} catch (Exception e) {
				LOG.error("发送MQ异常，捕获到Exception异常："+e.getMessage(),e);
				ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
				ajaxBean.setMsg(StatusCode.STATUS_367_MSG);
			}
		}
		//只有打印成才记录操作日志
		logService.addLogAccount(sessionUser, "["+sessionUser.getRealName()+"]打印包装流转单["+orderCode+"]流转类别["+order.getTradeTypeName()+"]");
		return ajaxBean;
	}

	public void addCirculateOrderPrintNumber(String orderCode) {
		LOG.info("包装流转单["+orderCode+"]打印次数自增");
		jdbcTemplate.update(updateCirculateOrderPrintNumber, orderCode);
	}

	/**
	 * 暂且保留 根据车牌号查询是否存在这辆车的流转单
	 *
	 * @param carNo
	 * @return public Integer queryCountByCarNo(String carNo) { String sql = "select
	 *         count(1) num from T_CIRCULATE_ORDER where car_no in (" + carNo + ")
	 *         "; ConcurrentHashMap map = new ConcurrentHashMap(); map.put("carNo",
	 *         carNo); return namedJdbcTemplate.queryForObject(sql, map,
	 *         Integer.class); }
	 */

	/**
	 * 流转单列表页面查询
	 *
	 * @param paging
	 * @param circulateOrder
	 * @param orgIds
	 * @return
	 */
	public Paging pagingCirculateOrder(Paging paging, CirculateOrder circulateOrder, SystemUser sessionUser) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from T_CIRCULATE_ORDER where 1=1 ");
		//如果是非总公司仓库，才有必要加上子仓库过滤
		if(!SysConstants.STRING_100.equals(sessionUser.getCurrentSystemOrg().getOrgId())
				&& !(StringUtils.isNotBlank(circulateOrder.getConsignorOrgId()) && StringUtils.isNotBlank(circulateOrder.getTargetOrgId()))) {
			sql.append(" and ( ");
			if (StringUtils.isBlank(circulateOrder.getConsignorOrgId())) {
				sql.append(" consignor_org_id in (" + sessionUser.getFilialeSystemOrgIds() + ")");
			}
			if (StringUtils.isBlank(circulateOrder.getConsignorOrgId()) && StringUtils.isBlank(circulateOrder.getConsignorOrgId())) {
				sql.append(" or ");
			}
			if (StringUtils.isBlank(circulateOrder.getTargetOrgId())) {
				sql.append(" target_org_id in (" + sessionUser.getFilialeSystemOrgIds() + ")");
			}
			sql.append(" ) ");
		}
		if (StringUtils.isNotBlank(circulateOrder.getOrderCode())) {
			sql.append(" and order_code like :orderCode ");
			paramMap.put("orderCode", "%"+circulateOrder.getOrderCode()+"%");
		}
		if (StringUtils.isNotBlank(circulateOrder.getCarNo())) {
			sql.append(" and car_no like :carNo ");
			paramMap.put("carNo", "%"+circulateOrder.getCarNo()+"%");
		}
		if (StringUtils.isNotBlank(circulateOrder.getConsignorOrgId())) {
			sql.append(" and consignor_org_id = :consignorOrgId ");
			paramMap.put("consignorOrgId", circulateOrder.getConsignorOrgId());
		}
		if (StringUtils.isNotBlank(circulateOrder.getTargetOrgId())) {
			sql.append(" and target_org_id = :targetOrgId ");
			paramMap.put("targetOrgId", circulateOrder.getTargetOrgId());
		}
		if (StringUtils.isNotBlank(circulateOrder.getTradeTypeCode())) {
			sql.append(" and trade_type_code = :tradeTypeCode ");
			paramMap.put("tradeTypeCode", circulateOrder.getTradeTypeCode());
		}
		if (StringUtils.isNotBlank(circulateOrder.getIsReceive())) {
			sql.append(" and is_receive = :isReceive ");
			paramMap.put("isReceive", circulateOrder.getIsReceive());
		}
		sql.append(" order by create_time desc ");
		LOG.info("[包装流转单]页面SQL= " + sql.toString());
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, CirculateOrder.class);
	}

	/**
	 * 流转单列表页面查询，仅查询发货仓库为：当前选择仓库，并且是手工流转单
	 * @param paging
	 * @param orderCode
	 * @param sessionUser
	 * @return
	 */
	public Paging pagingManualCirculateOrderCurrentOrg(Paging paging, String orderCode, SystemUser sessionUser) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from T_CIRCULATE_ORDER where is_manual_order = '1' and consignor_org_id = '"
				+ sessionUser.getCurrentSystemOrg().getOrgId() + "'");
		if (StringUtils.isNotBlank(orderCode)) {
			sql.append(" and order_code like :orderCode ");
			paramMap.put("orderCode", "%"+orderCode+"%");
		}
		sql.append(" order by create_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, CirculateOrder.class);
	}

	/**
	 * 流转单列表页面查询，仅查询收货仓库为：当前选择仓库
	 *
	 * @param paging
	 * @param detail
	 * @return
	 */
	public Paging pagingCirculateOrderReceiveCurrentOrg(Paging paging, CirculateDetail detail, SystemUser sessionUser) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from T_CIRCULATE_ORDER where is_manual_receive != 2 and print_number > 0 and target_org_id = '"
				+ sessionUser.getCurrentSystemOrg().getOrgId() + "'");
		if (StringUtils.isNotBlank(detail.getOrderCode())) {
			sql.append(" and order_code like :orderCode ");
			paramMap.put("orderCode", "%" + detail.getOrderCode() + "%");
		}
		sql.append(" order by create_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, CirculateOrder.class);
	}

	/**
	 * 流转单详情列表页面查询
	 *
	 * @param orderCode
	 * @return
	 */
	public List<CirculateDetail> queryCirculateDetailByOrderCode(String orderCode) {
		String sql = "select * from T_CIRCULATE_DETAIL where order_code= ? order by create_time ASC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetail.class), orderCode);
	}

	/**
	 * 流转单详情列表页面查询
	 *
	 * @param orderCode
	 * @return
	 */
	public List<CirculateDetailDto> listCirculateDetailByOrderCode(String orderCode) {
		String sql = "select * from T_CIRCULATE_DETAIL where order_code= ? order by create_time ASC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetailDto.class), orderCode);
	}

	/**
	 * 流转单详情列表页面查询，按照创建时间倒叙排序
	 *
	 * @param orderCode
	 * @return
	 */
	public List<CirculateDetail> queryCirculateDetailByOrderCodeInCreateTimeDesc(String orderCode) {
		String sql = "select * from T_CIRCULATE_DETAIL where order_code= ? order by create_time DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetail.class), orderCode);
	}

	/**
	 * 创建包装流转单
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 * @param targetOrg
	 * @return
	 */
	public AjaxBean createCirculateOrder(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg targetOrg,
										 CirculateOrder circulateOrder) {
		// 设置出库类别
		circulateOrder.setTradeTypeName(CirculateState.getCirculate(circulateOrder.getTradeTypeCode().trim()));
		// 创建包装流转单单号
		String orderCode = circulateOrderService.createOrderCode(sessionUser);
		circulateOrder.setIsManualOrder(SysConstants.STRING_0); //普通流转单
		// 流转类型名称，比如维修、流转、销售、报废，具体查看CirculateState.java
		String tradeTypeName = CirculateState.getCirculate(circulateOrder.getTradeTypeCode());
		circulateOrder.setOrderCode(orderCode); // 设置包装流转单单号
		circulateOrder.setTradeTypeName(tradeTypeName); // 设置流转类型名称，也就是出库类别，比如：维修、报废、流转
		// 设置创建人
//		circulateOrder.setConsignorAccount(sessionUser.getAccount()); // 发货人账号
//		circulateOrder.setConsignorRealName(sessionUser.getRealName()); // 发货人姓名
		circulateOrder.setCreateAccount(sessionUser.getAccount()); // 创建人账号
		circulateOrder.setCreateRealName(sessionUser.getRealName()); // 创建人姓名
		// 设置发货组织
		SystemOrg currentOrg = sessionUser.getCurrentSystemOrg();
		circulateOrder.setConsignorOrgId(currentOrg.getOrgId()); // 发货公司ID
		circulateOrder.setConsignorOrgName(currentOrg.getOrgName()); // 发货公司名称
		circulateOrder.setConsignorOrgTypeId(currentOrg.getOrgTypeId()); // 发货组织类别ID
		circulateOrder.setConsignorOrgTypeName(currentOrg.getOrgTypeName()); // 发货组织类别名称，比如:
		// CMC、DC、供应商、仓库，参考OrgType.java
		circulateOrder.setCreateAccount(sessionUser.getAccount()); // 发货人账号
		circulateOrder.setCreateRealName(sessionUser.getRealName()); // 发货人真实姓名
		// 设置收货组织
		circulateOrder.setTargetOrgId(targetOrg.getOrgId()); // 发货公司ID
		circulateOrder.setTargetOrgName(targetOrg.getOrgName()); // 发货公司名称
		circulateOrder.setTargetOrgTypeId(targetOrg.getOrgTypeId()); // 发货组织类别ID
		circulateOrder.setTargetOrgTypeName(targetOrg.getOrgTypeName()); // 收货组织类别名称，比如:CMC、DC、供应商、仓库，参考OrgType.java
		circulateOrder.setPrintNumber(0); //打印次数
		// 设置承运商
		Car car = carService.findCarByCarNo(circulateOrder.getCarNo());
		if (null != car) {
			circulateOrder.setShipperId(car.getShipperId());
			circulateOrder.setShipperName(car.getShipperName());
		}
		// 是否已入库。0未入库，1已入库。收货时会更新为1已入库
		circulateOrder.setIsReceive(SysConstants.STRING_0);
		//人工收货状态。0未收货, 1已机器收货，2已人工收货
		circulateOrder.setIsManualReceive(SysConstants.STRING_0);

		// 新增包装流转单
		int insertResult = namedJdbcTemplate.update(insertCirculateOrder,
				new BeanPropertySqlParameterSource(circulateOrder));
		if (insertResult == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_306);
			ajaxBean.setMsg("新增包装流转单失败！");
			return ajaxBean;
		}
		ajaxBean.setBean(orderCode);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg("创建包装流转单成功！");
		return ajaxBean;
	}
	/**
	 * 【手工流转单】页面，创建一个流转单主单。专门针对像供应商这种原始的手工出库单，无法具体到个体器具，即无法记录epc编号的出库
	 * @param ajaxBean
	 * @param sessionUser
	 * @param targetOrg
	 * @param circulateOrder
	 * @return
	 */
	public AjaxBean createCirculateOrderForAsn(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg targetOrg,
			CirculateOrder circulateOrder) {
		circulateOrder.setIsManualOrder(SysConstants.STRING_1); //手工流转单
		// 设置出库类别
		circulateOrder.setTradeTypeName(CirculateState.getCirculate(circulateOrder.getTradeTypeCode().trim()));
		// 流转类型名称，比如维修、流转、销售，具体查看CirculateState.java
		String tradeTypeName = CirculateState.getCirculate(circulateOrder.getTradeTypeCode());
		circulateOrder.setTradeTypeName(tradeTypeName); // 设置流转类型名称，也就是出库类别，比如：维修、报废、流转
		// 设置发货人
		circulateOrder.setConsignorAccount(sessionUser.getAccount()); // 发货人账号
		circulateOrder.setConsignorRealName(sessionUser.getRealName()); // 发货人姓名
		circulateOrder.setCreateAccount(sessionUser.getAccount()); // 创建人账号
		circulateOrder.setCreateRealName(sessionUser.getRealName()); // 创建人姓名
		// 设置发货组织
		SystemOrg currentOrg = sessionUser.getCurrentSystemOrg();
		circulateOrder.setConsignorOrgId(currentOrg.getOrgId()); // 发货公司ID
		circulateOrder.setConsignorOrgName(currentOrg.getOrgName()); // 发货公司名称
		circulateOrder.setConsignorOrgTypeId(currentOrg.getOrgTypeId()); // 发货组织类别ID
		circulateOrder.setConsignorOrgTypeName(currentOrg.getOrgTypeName()); // 发货组织类别名称，比如:
		//创建日期和时间
		circulateOrder.setCreateAccount(sessionUser.getAccount()); // 发货人账号
		circulateOrder.setCreateRealName(sessionUser.getRealName()); // 发货人真实姓名
		// 设置收货组织
		circulateOrder.setTargetOrgId(targetOrg.getOrgId()); // 发货公司ID
		circulateOrder.setTargetOrgName(targetOrg.getOrgName()); // 发货公司名称
		circulateOrder.setTargetOrgTypeId(targetOrg.getOrgTypeId()); // 发货组织类别ID
		circulateOrder.setTargetOrgTypeName(targetOrg.getOrgTypeName()); // 收货组织类别名称，比如:CMC、DC、供应商、仓库，参考OrgType.java
		circulateOrder.setPrintNumber(0);
		// 设置承运商
		Car car = carService.findCarByCarNo(circulateOrder.getCarNo());
		if (null != car) {
			circulateOrder.setShipperId(car.getShipperId());
			circulateOrder.setShipperName(car.getShipperName());
		}
		// 是否已入库。0未入库，1已入库。收货时会更新为1已入库
		circulateOrder.setIsReceive(SysConstants.STRING_0);
		//人工收货状态。0未收货, 1已机器收货，2已人工收货
		circulateOrder.setIsManualReceive(SysConstants.STRING_0);

		// 新增包装流转单
		int insertResult = namedJdbcTemplate.update(insertCirculateOrder,
				new BeanPropertySqlParameterSource(circulateOrder));
		if (insertResult == 0) {
			ajaxBean.setStatus(StatusCode.STATUS_306);
			ajaxBean.setMsg("新增包装流转单失败！");
			return ajaxBean;
		}
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg("创建包装流转单成功！");
		return ajaxBean;
	}

	/**
	 * 根据单号查询
	 *
	 * @param orderCode
	 * @return
	 */
	public CirculateOrder queryCirculateOrderByOrderCode(String orderCode) {
		String sql = "select * from T_CIRCULATE_ORDER where order_code=?";
		List<CirculateOrder> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateOrder.class), orderCode);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 添加单个器具epc到包装流转单明细
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 * @param orderCode
	 *            包装流转单单号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean addEpcToCirculateOrder(AjaxBean ajaxBean, SystemUser sessionUser, String orderCode,
										   Container findContainer, CirculateOrder circulateOrder) {
		// 要添加的器具必须是当前仓库下的器具，并且流转状态为在库状态！
		String sql = pagingContainerCurrentOrg + " and t.epc_id = :epcId ";
		Map paramMap = new HashMap();
		paramMap.put("orgId", sessionUser.getCurrentSystemOrg().getOrgId());
		paramMap.put("epcId", findContainer.getEpcId());
		List<ContainerDto> dtoList = namedJdbcTemplate.query(sql, paramMap,
				new BeanPropertyRowMapper(ContainerDto.class));
		if (CollectionUtils.isEmpty(dtoList)) {
			ajaxBean.setStatus(StatusCode.STATUS_337);
			ajaxBean.setMsg("[" + findContainer.getEpcId() + "]" + StatusCode.STATUS_337_MSG);
			return ajaxBean;
		}

		// 如果该器具是托盘且是组托，则添加一托器具，否则添加单个器具
		List<Container> containerList = new ArrayList<Container>();
		if (SysConstants.STRING_1.equals(findContainer.getContainerTypeId())) { // 如果该器具是“托盘”
			// 如果该器具是组托 ,则添加所有组托内EPC器具到该流转单
			List<ContainerGroup> groupList = jdbcTemplate.query(queryGroupEpcs,
					new BeanPropertyRowMapper(ContainerGroup.class), findContainer.getEpcId());
			if (CollectionUtils.isNotEmpty(groupList)) {
				for (ContainerGroup group : groupList) {
					if(group.getContainerTypeName().equals("托盘") || group.getGroupState() != 1) {
						containerList.add(containerService.getContainerByEpcId(group.getEpcId())); // 添加一托器具

					}
				}
			} else {
				containerList.add(findContainer); // 添加单个器具
			}
		} else {
			containerList.add(findContainer); // 添加单个器具
		}
		// 检测器具是否存在,例如是维修出库，那么维修表里是否存在
		ajaxBean = circulateOrderDeliveryService.checkContainExist(ajaxBean, circulateOrder, containerList,
				sessionUser.getCurrentSystemOrg().getOrgId());
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}

		// 组装和新增包装流转单详细表 T_CIRCULATE_DETAIL
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		ajaxBean = circulateOrderDeliveryService.buildAndInsertCirculateDetail(ajaxBean, sessionUser, orderCode,
				containerList,createTime);
		return ajaxBean;
	}

	/**
	 * 根据orderCode和epcId获取包装流转单明细信息
	 *
	 * @param orderCode
	 * @param epcId
	 * @return
	 */
	public CirculateDetail queryCirculateDetailByEpcAndOrderCode(String orderCode, String epcId) {
		String sql = "select * from T_CIRCULATE_DETAIL where order_code= ? and epc_id = ?";
		List<CirculateDetail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetail.class),
				orderCode, epcId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 【实收入库】对t_circulate_detail流转单器具明细表进行收货操作
	 * 点击“照单全收”按钮后，把t_circulate_detail里面所有receive_number=0的记录都修改以下字段：deal_result=5,receive_number=1.
	 * 另外更新处理人信息:
	 * deal_account` varchar(50) DEFAULT NULL COMMENT '差异处理人账号',
	 * deal_real_name` varchar(20) DEFAULT NULL COMMENT '差异处理人的姓名',
	 * deal_time` datetime DEFAULT NULL COMMENT '差异处理时间',
	 * deal_org_id` varchar(32) DEFAULT NULL COMMENT '差异处理操作组织ID',
	 * deal_org_name` varchar(50) DEFAULT NULL COMMENT '差异处理操作组织名称',
	 *
	 * @param orderCode
	 * @param epcId
	 * @param sessionUser 门型账号
	 * @param receiveNumber
	 *            照单全收数量
	 */
	public void updateReceiveInfoForCirculateDetail_inOrgActualScan(CirculateOrder circulateOrder, List<String> epcIdList, List<Container> containerList, SystemUser sessionUser, Timestamp createTime, String differenceRemark,String deviceRemark) {
		//如果epc列表不为空，则进行收货操作
		if(CollectionUtils.isNotEmpty(epcIdList)) {
//			List<CirculateDetail> params = new ArrayList<CirculateDetail>();
			String epcIds = doorEquipmentService.convertStrListToStrs(epcIdList);
			StringBuffer updateSql = new StringBuffer("update T_CIRCULATE_DETAIL set deal_result = ?,")
			.append(" receive_number = ? , receive_time = ? , receive_org_id = ?,  receive_org_name = ?,")
			.append(" receive_account = ? , receive_real_name = ?, remark = ?")
			.append(" where order_code = ? and epc_id in(").append(epcIds).append(") and receive_number =0");
			String dealResult = SysConstants.NULL_STR;
			Integer receiveNumber = 0;
			String remark = (StringUtils.isNotEmpty(differenceRemark) && StringUtils.isNotBlank(differenceRemark))?differenceRemark:SysConstants.NULL_STR;
			if(sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())) {
				receiveNumber=SysConstants.INTEGER_1; //收货数量=1。只有门型所在的仓库才计算收货，其他仓库只是标记收货信息
				dealResult=DealResult.NO_DIFFERENCE.getDifferenceId(); //差异处理结果=5。1待处理 2收货入库 3EPC覆盖 4索赔 5无差异
			}else {
				receiveNumber=SysConstants.INTEGER_0; //收货数量=0。其他仓库只是标记收货信息
				dealResult=DealResult.UN_DISPOSE.getDifferenceId(); //差异处理结果=1。1待处理 2收货入库 3EPC覆盖 4索赔 5无差异
			}
//			for (Container con : containerList) {
////				epcIds.append("'").append(con.getEpcId()).append("',");
//				CirculateDetail detail = new CirculateDetail();
//				detail.setReceiveAccount(sessionUser.getAccount());
//				detail.setReceiveRealName(sessionUser.getRealName());
//				detail.setReceiveOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
//				detail.setReceiveOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
//				detail.setOrderCode(circulateOrder.getOrderCode());
//				detail.setEpcId(con.getEpcId());
//				detail.setReceiveTime(createTime); //收货时间
//				detail.setRemark(StringUtils.isNotBlank(differenceRemark)?differenceRemark:"");
//				//判断流转单的收货仓库是不是门型所在仓库
//				if(sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())) {
//					detail.setReceiveNumber(SysConstants.INTEGER_1); //收货数量=1。只有门型所在的仓库才计算收货，其他仓库只是标记收货信息
//					detail.setDealResult(DealResult.NO_DIFFERENCE.getDifferenceId()); //差异处理结果=5。1待处理 2收货入库 3EPC覆盖 4索赔 5无差异
//				}else {
//					detail.setReceiveNumber(SysConstants.INTEGER_0); //收货数量=0。其他仓库只是标记收货信息
//					detail.setDealResult(DealResult.UN_DISPOSE.getDifferenceId()); //差异处理结果=1。1待处理 2收货入库 3EPC覆盖 4索赔 5无差异
//				}
//				params.add(detail);
				//LOG.info("门型收货  bbb----111---0  detail="+detail);
//			}
			LOG.info("门型收货  bbb----111---0  sessionUser.getCurrentSystemOrg().getOrgId()="+sessionUser.getCurrentSystemOrg().getOrgId()
					+"   circulateOrder.getTargetOrgId()="+circulateOrder.getTargetOrgId());
			//批量更新收货：t_circulate_detail

			jdbcTemplate.update(updateSql.toString(),
					dealResult,receiveNumber,createTime,sessionUser.getCurrentSystemOrg().getOrgId()
					,sessionUser.getCurrentSystemOrg().getOrgName(),sessionUser.getAccount()
					,sessionUser.getRealName(),remark,circulateOrder.getOrderCode());
			//SqlParameterSource[] batchParams = SqlParameterSourceUtils.createBatch(params.toArray());
			//namedJdbcTemplate.batchUpdate(updateReceiveInfoForCirculateDetail_inOrgActualScan, batchParams);
			LOG.info("门型收货  bbb----111---1  ");
			//只有门型所在的仓库才计算库存数量
			if(sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())) {
				//收货后，计算该仓库的流转单对应器具代码的库存数量。组装InventoryLatest对象，更新表t_inventory_latest，新记录插入历史表t_inventory_history
				inventoryHistoryService.buildInventoryLatest(sessionUser, circulateOrder.getOrderCode(), deviceRemark, epcIds);
			}
			LOG.info("门型收货  bbb----111---2  ");
			//批量更新流转记录
			circulateService.updateOrInsertCirculateHistoryForReceive(sessionUser, containerList, circulateOrder,deviceRemark);
			LOG.info("门型收货  bbb----111---3  ");
			// 如果是销售出库，那么收货的时候，需更新器具的隶属仓库 t_container表belong_org_id字段为收货仓库
			if (CirculateState.SELL.getCode().equals(circulateOrder.getTradeTypeCode())) { // 如果是销售出库
				handsetService.updateContainerBelongOrgId(sessionUser, containerList);
			}
			LOG.info("门型收货  bbb----111---4  ");
		}
	}
	
	/**
	 * 【照单全收】对t_circulate_detail流转单器具明细表进行收货操作
	 * 点击“照单全收”按钮后，把t_circulate_detail里面所有receive_number=0的记录都修改以下字段：deal_result=5,receive_number=1.
	 * 另外更新处理人信息:
	 * deal_account` varchar(50) DEFAULT NULL COMMENT '差异处理人账号',
	 * deal_real_name` varchar(20) DEFAULT NULL COMMENT '差异处理人的姓名',
	 * deal_time` datetime DEFAULT NULL COMMENT '差异处理时间',
	 * deal_org_id` varchar(32) DEFAULT NULL COMMENT '差异处理操作组织ID',
	 * deal_org_name` varchar(50) DEFAULT NULL COMMENT '差异处理操作组织名称',
	 *
	 * @param orderCode
	 * @param epcId
	 * @param sessionUser
	 * @param receiveNumber
	 *            照单全收数量
	 */
	public void updateReceiveInfoForCirculateDetail_inOrgAll(String orderCode, SystemUser sessionUser) {
		CirculateDetail detail = new CirculateDetail();
		detail.setDealResult(DealResult.NO_DIFFERENCE.getDifferenceId()); //差异处理结果。1待处理 2收货入库 3EPC覆盖 4索赔 5无差异
		detail.setReceiveNumber(SysConstants.INTEGER_1); //收货数量
		detail.setReceiveAccount(sessionUser.getAccount());
		detail.setReceiveRealName(sessionUser.getRealName());
		detail.setReceiveOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		detail.setReceiveOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		detail.setOrderCode(orderCode);
		namedJdbcTemplate.update(updateReceiveInfoForCirculateDetail_inOrgAll, new BeanPropertySqlParameterSource(detail));
	}

	/**
	 * 门型收货，可以无限次数反复收货
	 * 门型收货，对t_circulate_order流转单主单信息表进行收货标记操作
	 */
	public void updateReceiveInfoForCirculateOrder_inOrgDoor(String orderCode, SystemUser sessionUser) {
		List<Integer> countList = jdbcTemplate.queryForList(countReceiveForCirculateDetail, Integer.class, orderCode,orderCode);
		LOG.info("门型收货  bbb----222---1  ");
		String isReceive = null;
		//如果receive_number = 1的数量与这个流转单所有epc编号个数相符，则认为：2 已全部收货
		if((1==countList.size() && countList.get(0).intValue() != SysConstants.INTEGER_0) || (countList.get(0).intValue() == countList.get(1).intValue())) {
			isReceive = "2";
			// 更新t_circulate_order主表状态为：2 已全部收货
			circulateOrderService.updateCirculateOrderReceiverMsg(orderCode, sessionUser, isReceive,StringUtils.EMPTY);
			LOG.info("门型收货  bbb----222---2a  ");
		}else {
			//如果第一位是0，则是：未收货
			if(countList.get(0).intValue() == SysConstants.INTEGER_0) {
				isReceive = "0"; //未收货的话对主单表 t_circulate_order什么都不做
			}else {
				isReceive = "1"; //已部分收货
				//如果是部分收货的话，门型不能设置is_manual_receive=2以及其他receive字段，要留给工人补充收货
				jdbcTemplate.update(updateCirculateOrderReceiverForDoor, isReceive,orderCode);
			}
			LOG.info("门型收货  bbb----222---2b  ");
		}
	}
	/**
	 * 人工收货，只能对某个单子进行一次收货
	 * @param orderCode
	 * @param sessionUser
	 * @param differenceRemark 收货差异备注，人工输入
	 */
	public void updateReceiveInfoForCirculateOrder_inOrgActualScan(String orderCode, SystemUser sessionUser,  String differenceRemark) {
		List<Integer> countList = jdbcTemplate.queryForList(countReceiveForCirculateDetail, Integer.class, orderCode,orderCode);
		String isReceive = "1";
		//如果receive_number = 1的数量与这个流转单所有epc编号个数相符，则认为：2 已全部收货
		if(1==countList.size() || (countList.get(0).intValue() == countList.get(1).intValue())) {
			isReceive = "2";
		}
		// 更新t_circulate_order主表状态isReceive
		circulateOrderService.updateCirculateOrderReceiverMsg(orderCode, sessionUser, isReceive,differenceRemark);
	}

	/**
	 * web端，【收货】页面，手工流转单收货，更新t_circulate_order信息
	 * @param orderCode
	 * @param sessionUser
	 * @param empty
	 */
	public void updateReceiveInfoForCirculateOrder_inOrgWebManualOrder(String orderCode, SystemUser sessionUser) {
		EpcSumDto dto = jdbcTemplate.queryForObject(updateReceiveInfoForCirculateOrder_inOrgWebManualOrder, new BeanPropertyRowMapper<>(EpcSumDto.class),orderCode);
		//1是部分收货 2全部收货
		String isReceive = SysConstants.STRING_1;
		//if(java.math.BigDecimal.valueOf(dto.getSendNumber()).equals(java.math.BigDecimal.valueOf(dto.getReceiveNumber()))) {
		//收货有可能多于发货数量 所以多收应该算为全收
		if(dto.getReceiveNumber().intValue()>=dto.getSendNumber().intValue()) {
			isReceive = SysConstants.STRING_2;
		}
		// 更新t_circulate_order主表状态isReceive
		circulateOrderService.updateCirculateOrderReceiverMsg(orderCode, sessionUser, isReceive, StringUtils.EMPTY);
	}

	/**
	 * 更新主表收货状态
	 *
	 * @param orderCode
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateCirculateOrderIsReceive(String orderCode, SystemUser sessionUser, String isReceive) {
		Map map = new HashMap();
		map.put("isReceive", isReceive);
		map.put("modifyAccount", sessionUser.getAccount());
		map.put("modifyRealName", sessionUser.getRealName());
		map.put("orderCode", orderCode);
		namedJdbcTemplate.update(updateCirculateOrderIsReceive, map);
	}

	/**
	 * 照单全收更新主表状态
	 * 更新主单信息 t_circulate_order 字段：
	 * is_receive = 2已全部收货
	 * is_manual_receive=2已人工收货
	 * target_account` varchar(50) DEFAULT NULL COMMENT '收货人',
	 * target_real_name` varchar(20) DEFAULT NULL COMMENT '收货人的姓名',
	 * target_time` datetime DEFAULT NULL COMMENT '收货日期',
	 * remark 入库备注
	 *  @param orderCode
	 *  @param sessionUser
	 * @param orderCode
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateCirculateOrderReceiverMsg(String orderCode, SystemUser sessionUser, String isReceive,String remark) {
		String sql = "update T_CIRCULATE_ORDER set is_receive = :isReceive , is_manual_receive = :isManualReceive , target_account = :targetAccount, target_real_name = :targetRealName, target_time = now(),remark = :remark where order_code = :orderCode";
		Map map = new HashMap();
		map.put("isReceive", isReceive);
		map.put("isManualReceive", SysConstants.STRING_2);
		map.put("targetAccount", sessionUser.getAccount());
		map.put("targetRealName", sessionUser.getRealName());
		map.put("orderCode", orderCode);
		map.put("remark", StringUtils.isNotBlank(remark)?remark:"");
		namedJdbcTemplate.update(sql, map);
	}

	/**
	 * 根据epcId查询包装流转单详情表中数据
	 *
	 * @return
	 */
	public CirculateDetail queryLastCirculateDetailByEpcId(String epcId) {
		String sql = "select * from T_CIRCULATE_DETAIL where epc_id= ? order by create_time desc limit 1";
		List<CirculateDetail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateDetail.class), epcId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateHistory(AjaxBean ajaxBean, CirculateOrder circulateOrder,
												   List<CirculateDetail> detailList, SystemUser sessionUser) {
		Integer size = detailList.size();

		Timestamp time1 = new Timestamp(new Date().getTime());
		// 为了流转记录按照时间显示顺序，所以第一条“流转出库”记录，和第二条“在途”记录时间不能完全相同，为了区分排序，暂且加一秒。
		Timestamp time2 = DateUtil.currentAddTwoSecond();
		
		List<Circulate> historyList = new ArrayList<Circulate>();
		List<Circulate> latestList = new ArrayList<Circulate>();

		for (int i = 0; i < size; i++) {
			CirculateDetail con = detailList.get(i);
			Circulate history = new Circulate();
			history.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
			history.setEpcId(con.getEpcId()); // EPC编号
			history.setContainerCode(con.getContainerCode()); // 器具代码
			history.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
			history.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
			history.setOrderCode(circulateOrder.getOrderCode()); // 包装流转单单据编号
			history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 操作公司ID
			history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 操作公司名称
			history.setTargetOrgId(circulateOrder.getTargetOrgId()); // 目标公司ID
			history.setTargetOrgName(circulateOrder.getTargetOrgName()); // 目标公司名称
			history.setCreateAccount(sessionUser.getAccount()); // 操作人
			history.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
			history.setRemark(SysConstants.CIRCULATE_REMARK_PRINT_AND_SEND);
			// 设置第一条流转记录的流转类型：流转出库
			history.setCirculateState(circulateOrder.getTradeTypeCode()); // 流转状态ID
			history.setCirculateStateName(circulateOrder.getTradeTypeName()); // 流转状态名称
			history.setCreateTime(time1);

			Circulate historyAndLatest = new Circulate();
			historyAndLatest.setCirculateHistoryId(UUIDUtil.creatUUID()); // 流转历史ID
			historyAndLatest.setEpcId(con.getEpcId()); // EPC编号
			historyAndLatest.setContainerCode(con.getContainerCode()); // 器具代码
			historyAndLatest.setContainerTypeId(con.getContainerTypeId()); // 器具类型ID
			historyAndLatest.setContainerTypeName(con.getContainerTypeName()); // 器具类型名称
			historyAndLatest.setOrderCode(circulateOrder.getOrderCode()); // 包装流转单单据编号
			historyAndLatest.setOrgId(circulateOrder.getTargetOrgId()); // 操作公司ID
			historyAndLatest.setOrgName(circulateOrder.getTargetOrgName()); // 操作公司名称
			historyAndLatest.setCreateAccount(sessionUser.getAccount()); // 操作人
			historyAndLatest.setCreateRealName(sessionUser.getRealName()); // 操作人的姓名
			history.setRemark(SysConstants.CIRCULATE_REMARK_PRINT_AND_SEND);
			historyAndLatest.setCreateTime(time2);	
			// 设置第二条流转记录的流转类型：在途
			historyAndLatest.setCirculateState(CirculateState.ON_WAY.getCode()); // 在途
			historyAndLatest.setCirculateStateName(CirculateState.ON_WAY.getCirculate()); // 在途
			// 如果是“在途”数据，需记录来源公司和目标公司
			historyAndLatest.setFromOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); // 来源公司ID
			historyAndLatest.setFromOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); // 来源公司名称
			historyAndLatest.setTargetOrgId(circulateOrder.getTargetOrgId());  // 目标公司ID
			historyAndLatest.setTargetOrgName(circulateOrder.getTargetOrgName()); // 目标公司名称
			
			//组装SQL批量插入参数
			historyList.add(history);
			latestList.add(historyAndLatest);
			
			// 删除旧的器具最新流转记录 T_CIRCULATE_LATEST
			jdbcTemplate.update(deleteCirculateLatest, con.getEpcId());
			
			if(historyList.size() >= SysConstants.MAX_INSERT_NUMBER) {
				// 新增新的器具最新流转记录 T_CIRCULATE_HISTORY，流转出库
				namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(historyList.toArray()));
				// 新增新的器具最新流转记录 T_CIRCULATE_HISTORY，在途 
				namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(latestList.toArray()));
				// 新增新的器具最新流转记录 T_CIRCULATE_LATEST，在途
				namedJdbcTemplate.batchUpdate(insertCirculateLatest, SqlParameterSourceUtils.createBatch(latestList.toArray()));
				historyList.clear();
				latestList.clear();
			}
		}
		//如果有剩余的器具未被插入完毕
		if(historyList.size() > SysConstants.INTEGER_0) {
			// 新增新的器具最新流转记录 T_CIRCULATE_HISTORY，流转出库
			namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(historyList.toArray()));
			// 新增新的器具最新流转记录T_CIRCULATE_HISTORY，在途 
			namedJdbcTemplate.batchUpdate(insertCirculateHistory, SqlParameterSourceUtils.createBatch(latestList.toArray()));
			// 新增新的器具最新流转记录 T_CIRCULATE_LATEST，在途
			namedJdbcTemplate.batchUpdate(insertCirculateLatest, SqlParameterSourceUtils.createBatch(latestList.toArray()));
			//清空队列，释放内存。
			historyList = null;
			latestList = null;
		}
		return ajaxBean;
	}

	/**
	 * 更新器具的最后所在公司,即last_org_id字段
	 *
	 * @param ajaxBean
	 * @param targetOrgId
	 *            目的地仓库
	 * @return
	 */
	public AjaxBean updateContainerLastOrgId(SystemUser sessionUser, AjaxBean ajaxBean,
											 List<CirculateDetail> detailList, String targetOrgId, String targetOrgName) {
		Integer size = detailList.size();
		for (int i = 0; i < size; i++) {
			jdbcTemplate.update(updateContaierLastOrgId, targetOrgId, targetOrgName,
					detailList.get(i).getEpcId());
		}
		return ajaxBean;
	}

	/**
	 * 如果是维修出库，更新器具维修表T_MAINTAIN里的维修状态maintain_state为：2维修出库
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 *            当前登录用户
	 * @return
	 */
	public AjaxBean updateMaintainState(AjaxBean ajaxBean, List<CirculateDetail> detailList, SystemUser sessionUser,
										CirculateOrder circulateOrder) {
		Integer size = detailList.size();
		for (int i = 0; i < size; i++) {
			jdbcTemplate.update(updateMaintainState, circulateOrder.getOrderCode(),
					circulateOrder.getTargetOrgId(), circulateOrder.getTargetOrgName(), detailList.get(i).getEpcId(),
					sessionUser.getCurrentSystemOrg().getOrgId());
		}
		return ajaxBean;
	}

	/**
	 * 如果是报废出库，更新器具报废表T_CONTAINER_SCRAP出库状态（is_out）为：0已出库
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 *            当前登录用户
	 * @return
	 */
	public AjaxBean updateContainerScrapIsOut(AjaxBean ajaxBean, List<CirculateDetail> detailList,
											  SystemUser sessionUser, String orderCode) {
		Integer size = detailList.size();
		for (int i = 0; i < size; i++) {
			jdbcTemplate.update(updateScrapIsOut, orderCode, detailList.get(i).getEpcId(),
					sessionUser.getCurrentSystemOrg().getOrgId());
		}
		return ajaxBean;
	}

	/**
	 * 如果是丢失出库，更新器具丢失表T_CONTAINER_LOST出库状态（is_out）为：0已出库
	 *
	 * @param ajaxBean
	 * @param sessionUser
	 *            当前登录用户
	 * @return
	 */
	public AjaxBean updateContainerLostIsOut(AjaxBean ajaxBean, List<CirculateDetail> detailList,
											 SystemUser sessionUser, String orderCode) {
		Integer size = detailList.size();
		for (int i = 0; i < size; i++) {
			jdbcTemplate.update(updateLostIsOut, orderCode, detailList.get(i).getEpcId(),
					sessionUser.getCurrentSystemOrg().getOrgId());

		}
		return ajaxBean;
	}

	/**
	 * 在打印流转单之前对流转单做业务逻辑处理，比如发货和校验
	 * @param ajaxBean
	 * @param orderCode
	 * @param circulateOrder
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean dealContainerPrePrintCirculateOrder(AjaxBean ajaxBean, String orderCode,CirculateOrder circulateOrder, SystemUser sessionUser) {
		// 如果打印次数大于1，则不再记录流转历史等
		if (circulateOrder.getPrintNumber() >= SysConstants.INTEGER_1) {
			LOG.info("[" + orderCode + "]包装流转单打印次数[" + circulateOrder.getPrintNumber() + "]大于1，不再记录流转历史！");
			return ajaxBean;
		}

		//更新流转单首次打印时间，发货人、发货时间
		jdbcTemplate.update(updateCirculateOrderPrintOrderTime, sessionUser.getAccount(), sessionUser.getRealName(), orderCode);
		
		// 获取包装流转单明细器具
		List<CirculateDetail> detailList = this.queryCirculateDetailByOrderCode(orderCode);
		if (CollectionUtils.isEmpty(detailList)) {
			ajaxBean.setStatus(StatusCode.STATUS_339);
			ajaxBean.setMsg("[" + orderCode + "]" + StatusCode.STATUS_339_MSG);
			return ajaxBean;
		}
		//不管是不是手工流转单，发货时候都要从仓库库存数量里减掉单子上器具代码发货的个数，涉及到两个表t_inventory_history，t_inventory_latest
		List<EpcSumDto> epcSumList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(circulateOrder);
		for (EpcSumDto dto : epcSumList) {
			//对当前仓库的某个器具代码做库存数量的加法（收货）
			InventoryHistory ih = new InventoryHistory();
			ih.setContainerCode(dto.getContainerCode()); //器具代码
			ih.setOrgId(circulateOrder.getConsignorOrgId()); //发货仓库ID
			ih.setOrgName(circulateOrder.getConsignorOrgName()); //发货仓库名称
			ih.setSendNumber(dto.getSendNumber()); //此次发货数量，单个器具
			ih.setOrderCode(orderCode); //流转单号
			ih.setRemark("打印发货");
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			ih.setIsReceive(false); // true收货，false发货
			LOG.info("更新库存--首次打印流转单["+orderCode+"]，器具发货。仓库：[" + ih.getOrgId() + "]["+ih.getOrgName()+"]器具代码：["+ih.getContainerCode()+"]+["+ih.getReceiveNumber()+"]-["+ih.getSendNumber()+"]=["+ih.getInOrgNumber()+"]备注：["+ih.getRemark()+"]");
			//更新器具代码的库存数量，更新表t_inventory_history和t_inventory_latest
			ajaxBean = inventoryHistoryService.updateInventoryLatest(ih);
			if(ajaxBean.getStatus() != StatusCode.STATUS_200) { //如果报错了，比如库存数量不足，直接返回，终止发货
				return ajaxBean;
			}
		}
				
    	//如果是手工流转单，就不必要记录EPC流转记录，因为手工流转单不具体到个体器具，所以没有EPC
    	if(StringUtils.equals(circulateOrder.getIsManualOrder(), SysConstants.STRING_1)) {
    		return ajaxBean;
    	}

		// 新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
		ajaxBean = circulateOrderService.buildAndInsertCirculateHistory(ajaxBean, circulateOrder, detailList,
				sessionUser);
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) { // 如果流转记录新增失败
			return ajaxBean;
		}
		// 更新器具的最后所在公司
		ajaxBean = updateContainerLastOrgId(sessionUser, ajaxBean, detailList, circulateOrder.getTargetOrgId(),
				circulateOrder.getTargetOrgName());

		if (CirculateState.MAINTAIN.getCode().equals(circulateOrder.getTradeTypeCode())) { // 如果是维修出库
			// 更新器具维修表里的维修状态为：维修出库
			ajaxBean = updateMaintainState(ajaxBean, detailList, sessionUser, circulateOrder);
		}
		return ajaxBean;
	}

	/**
	 * 获取当前仓库下的流转单，app页面【创建流转单】
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean listCirculateOrderForCurrentOrg(AjaxBean ajaxBean,SystemUser sessionUser) {
		String sql = "select * from t_circulate_order where consignor_org_id =? and print_number = 0 and is_manual_order != 1 and is_invalid = 0 order by create_time desc";
		List<CirculateOrder> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateOrder.class),sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean.setList(list);
		return ajaxBean;
	}
	/**
	 * 绑定门型、重绑按钮点击事件，app页面【创建流转单】
	 * @param sessionUser
	 * @param orderCode 流转单号
	 * @param doorAccount 被绑定的门型账号
	 * @return
	 */
	public AjaxBean bindDoorForCirculateOrder(SystemUser sessionUser, String orderCode,
			String doorAccount) {
		SystemUser door = systemUserService.querySystemUserByAccount(doorAccount);
		String sql = "update t_circulate_order set door_account=?,door_real_name=? where order_code=?";
		int result = jdbcTemplate.update(sql,door.getAccount(),door.getRealName(),orderCode);
		return AjaxBean.returnAjaxResult(result);
	}
	/**
	 * 获取当前仓库门型设备账号列表，app页面【创建流转单】
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean listDoorForCurrentOrg(AjaxBean ajaxBean,SystemUser sessionUser) {
		String sql = "select * from t_system_user where account in (select account from t_system_userorg where org_id=? and account in (select account from t_system_user where is_door = 1 and is_delete = 0 and is_active = 0 ))" ;
		List<SystemUser> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(SystemUser.class),sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean.setList(list);
		return ajaxBean;
	}
    /**
     * 返回web端【包装流转单】页面【查看明细】弹出框内容，包含器具个数统计列表
     * @param orderCode
     * @return
     */
    public AjaxBean circulateOrderDetail(String orderCode)
    {
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        Map beanMap = new HashMap();
        //包装流转单对像
        CirculateOrder circulateOrder = queryCirculateOrderByOrderCode(orderCode);
        beanMap.put("circulateOrder",circulateOrder);
        //包装流转单，器具明细列表
        List<CirculateDetail> circulateDetailList = circulateOrderService.queryCirculateDetailByOrderCode(orderCode);
        //包装流转单，器具统计列表
        List<EpcSumDto> epcSumList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(circulateOrder);
        beanMap.put("epcSumList",epcSumList);
        ajaxBean.setStatus(StatusCode.STATUS_200);
        ajaxBean.setList(circulateDetailList);
        ajaxBean.setBean(beanMap);
        return ajaxBean;
    }

	/**
	 * 更新车辆到达时间
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public AjaxBean updateCarArriveTime(SystemUser sessionUser, String orderCode) {
		AjaxBean ajaxBean = new AjaxBean();
		String sql = "update t_circulate_order set car_arrive_time=now() where order_code=?";
		int result = jdbcTemplate.update(sql, orderCode);
		return ajaxBean.returnAjaxResult(result);
	}

	/**
	 * 更新装货完了时间
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public AjaxBean updateLoadingEndTime(SystemUser sessionUser, String orderCode) {
		AjaxBean ajaxBean = new AjaxBean();
		String sql = "update t_circulate_order set loading_end_time=now() where order_code=?";
		int result = jdbcTemplate.update(sql, orderCode);
		return ajaxBean.returnAjaxResult(result);
	}

	/**
	 * 点击修改按钮，修改司机姓名、司机联系方式。
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public AjaxBean updateDriverMsg(SystemUser sessionUser, String orderCode, String driverName, String driverPhone) {
		AjaxBean ajaxBean = new AjaxBean();
		String sql = "update t_circulate_order set driver_name=?,driver_phone=? where order_code=?";
		int result = jdbcTemplate.update(sql, driverName, driverPhone, orderCode);
		return ajaxBean.returnAjaxResult(result);
	}

	/**
	 * 点击修改按钮，修改车牌号。
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	public AjaxBean updateCarNo(SystemUser sessionUser, String orderCode, String carNo) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(carNo)) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("车牌号不能为空。");
			return ajaxBean;
		}
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		if(null != circulateOrder.getPrintNumber() && circulateOrder.getPrintNumber() > 0) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("流转单["+orderCode+"]已发货，无法修改车牌号。");
			return ajaxBean;
		}
		Car car = carService.findCarByCarNo(carNo);
		if(null != car && null != car.getShipperId()) {
			String sql = "update t_circulate_order set car_no =?,shipper_id=?,shipper_name=? where order_code = ?";
			jdbcTemplate.update(sql, car.getCarNo(),car.getShipperId(),car.getShipperName(),orderCode);
		}else {
			String sql = "update t_circulate_order set car_no =? where order_code = ?";
			jdbcTemplate.update(sql, carNo,orderCode);
		}
		return ajaxBean;
	}
	/**
	 * 点击修改按钮，修改特别描述。
	 * @param sessionUser
	 * @param specialDescription
	 * @return
	 */
	public AjaxBean updateSpecialDescription(SystemUser sessionUser, String specialDescription,String orderCode) {
		String sql = "update t_circulate_order set special_description =? where order_code = ?";
		int result = jdbcTemplate.update(sql, specialDescription,orderCode);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * web端手工流转单页面，为手工流转单添加仅有器具代码，没有epc编号的器具明细
	 * @param ajaxBean
	 * @param sessionUser
	 * @param orderCode
	 * @param circulateDetailList
	 * @return
	 */
	public AjaxBean addCirculateDetailForAsn(AjaxBean ajaxBean, SystemUser sessionUser, CirculateOrder circulateOrder,
			List<CirculateDetail> circulateDetailList) {
		for (CirculateDetail circulateDetail : circulateDetailList) {
			if(StringUtils.isBlank(circulateDetail.getContainerCode())) {
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("器具代码不允许为空。");
				return ajaxBean;
			}
			if(null == circulateDetail.getSendNumber() || circulateDetail.getSendNumber() <= 0 ) {
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("器具代码为["+circulateDetail.getContainerCode()+"]，实发数量不允许为空，并且必须是整数且大于0。");
				return ajaxBean;
			}
			ContainerCode code = containerCodeService.queryByContainerCode(circulateDetail.getContainerCode());
			circulateDetail.setContainerName(code.getContainerName());
			circulateDetail.setContainerTypeId(code.getContainerTypeId());
			circulateDetail.setContainerTypeName(code.getContainerTypeName());
		}
		//删除手工流转单的旧器具明细
		String sql = "delete from t_circulate_detail where order_code = ?";
		jdbcTemplate.update(sql, circulateOrder.getOrderCode());
		//新增包装流转单详细 T_CIRCULATE_DETAIL
		ajaxBean = buildAndInsertCirculateDetailForAsn(ajaxBean,sessionUser,circulateOrder.getOrderCode(),circulateDetailList);
		return ajaxBean;
	}

	/**
	 * 组建并新增包装流转单详细表  T_CIRCULATE_DETAIL
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param orderCode 包装流转单单号
	 * @param containerList 包装流转单详细里的器具列表
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateDetailForAsn(AjaxBean ajaxBean,SystemUser sessionUser,String orderCode,List<CirculateDetail> circulateDetailList){
		Integer size = circulateDetailList.size();
		Integer sequenceNo = 0;
		List<CirculateDetail> batchParam = new ArrayList<CirculateDetail>();
		for (int i = 0; i < size; i++) {
			//组装insert对象
			CirculateDetail detail = circulateDetailList.get(i);
			detail.setCirculateDetailId(UUIDUtil.creatUUID());
			detail.setOrderCode(orderCode); //包装流转单单号
//			detail.setSequenceNo((sequenceNo+i+1)+""); //序列号
			detail.setPlanNumber(detail.getSendNumber()); //计划数量，暂时与实发数量相同即可
			detail.setReceiveNumber(SysConstants.INTEGER_0);//实收数量
			detail.setCreateAccount(sessionUser.getCreateAccount());
			detail.setCreateRealName(sessionUser.getRealName()); //创建账号
			detail.setDealResult(DealResult.UN_DISPOSE.getDifferenceId()); //初始化为：1待处理
			detail.setCreateTime(DateUtil.currentTimestamp());
			batchParam.add(detail);
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
	 * 【包装流转单】页面【作废】按钮点击，作废一个流转单，不可以恢复。
	 * @param orderCode
	 * @return
	 */
	public AjaxBean invalidOrder(AjaxBean ajaxBean,String orderCode) {
		CirculateOrder order = queryCirculateOrderByOrderCode(orderCode);
		if(order.getPrintNumber() > SysConstants.INTEGER_0) {
			ajaxBean.setStatus(StatusCode.STATUS_366);
			ajaxBean.setMsg("["+orderCode+"]"+StatusCode.STATUS_366_MSG);
			return ajaxBean;
		}
		int result = jdbcTemplate.update(invalidOrder, orderCode);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 是否为普通单人工输入实收数量，如果是那么T_CIRCULATE_DETAIL_RECEIVE表就由这个流转单号的收货明细。0否，1是
	 * @param orderCode
	 */
	public void updateCirculateDetailReceive(String orderCode) {
		jdbcTemplate.update("update t_circulate_order set is_circulate_detail_receive=1 where order_code=?", orderCode);
	}
}
