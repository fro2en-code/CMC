package com.cdc.cdccmc.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import java.util.stream.Collectors;

import com.cdc.cdccmc.runnable.ASyncTask;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;

import com.cdc.cdccmc.domain.PurchasePrepare;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.door.DoorScan;
import com.cdc.cdccmc.domain.door.DoorScanReceive;
import com.cdc.cdccmc.domain.door.DoorScanReceiveOrder;
import com.cdc.cdccmc.domain.dto.ContainerForDoorScanDto;
import com.cdc.cdccmc.domain.sys.SystemUser;


/**
 * 器具类型
 * 
 * @author Jerry
 * @date 2018-04-19
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class DoorEquipmentService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoorEquipmentService.class);

    private static final String REMAINING_SQL1 = "select epc_id from t_circulate_detail where order_code = '";
    private static final String REMAINING_SQL2 = "' and epc_id in (";
    private static final String REMAINING_SQL3 = ") and receive_number =0";
    
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private HandsetService handsetService;
	@Autowired
	private PurchasePrepareService purchasePrepareService;
	@Autowired
	private ASyncTask aSyncTask;

	@Value("#{sql['countReceiveForCirculateOrder']}")
	private String countReceiveForCirculateOrder;
	@Value("#{sql['insertDoorScan']}")
	private String insertDoorScan;
	@Value("#{sql['queryCirculateDetailByInEpcId']}")
	private String queryCirculateDetailByInEpcId;
	@Value("#{sql['updateContaierLastOrgId']}")
	private String updateContaierLastOrgId;
	@Value("#{sql['insertDoorScanReceive']}")
	private String insertDoorScanReceive;
	@Value("#{sql['insertDoorScanReceiveOrder']}")
	private String insertDoorScanReceiveOrder;

	public String convertStrListToStrs(List<String> strList)
	{
		StringBuffer strS = new StringBuffer(SysConstants.NULL_STR);
		for (int i=0;i<strList.size();i++)
		{
			strS.append(SysConstants.DYH).append(strList.get(i)).append(SysConstants.DYH);
			if((i+1)<strList.size())
			{
				strS.append(SysConstants.DH);
			}
		}
		return strS.toString();
	}

	/**
	 * UPDATE IN时 条件不能拼单引号 否则会转成'\'这样
	 * @param strList
	 * @return
	 */
	public String convertStrListToStrsForUpdate(List<String> strList)
	{
		StringBuffer strS = new StringBuffer(SysConstants.NULL_STR);
		for (int i=0;i<strList.size();i++)
		{
			strS.append(strList.get(i));
			if((i+1)<strList.size())
			{
				strS.append(SysConstants.DH);
			}
		}
		return strS.toString();
	}

	public void filterStrListDuplication(List<String> strList)
	{
		if(CollectionUtils.isNotEmpty(strList))
		{
			Set<String> set = new HashSet<>(strList);
			strList = new ArrayList<>(set);
		}
	}

    /**
     * 门型设备接口实现-new
     * 门型收货逻辑：
     * 1、门型设备会传参一个epcIdList集合对象作为参数
     * 2、建一个List<String> newEpcIdList对象集合，遍历epcIdList，如果是托盘器具并且是组托状态，获取整托器具的epcId添加到新集合List<String> newEpcIdList，
     * 如果是单个普通器具也添加到新集合List<String> newEpcIdList
     * 3、对新集合List<String> newEpcIdList去掉重复epc对象
     * 4、根据新集合List<String> newEpcIdList里的每个epcId去t_circulate_detail表里查询该epc_id并且receive_number = 0的orderCode
     * 5、获取到所有的orderCode,然后去重得到List<String> orderCodeList
     * 6、遍历List<String> orderCodeList，在每一个循环中，直接调用已有接口：handsetService.inOrgActualScan(sessionUser, orderCode, epcIdList, "");
     * 这里的epcIdList是指把所有的epc都传进去
     * @param ajaxBean
     * @param sessionUser
     * @param epcIdList
     * @return
     */
    public AjaxBean receiveEpc(AjaxBean ajaxBean, SystemUser sessionUser, List<String> epcIdList) {
		//实际进行收货时间
		Timestamp createTime = DateUtil.currentTimestamp();
		
		String epcIds = convertStrListToStrs(epcIdList);
		LOG.info("门型收货 111  epcIds="+epcIds);
		//查到所有器具
		List<ContainerForDoorScanDto> containerForDoorScanDtoList = containerService.queryContainerByEpcIdS(epcIds);
		int resultPurchasePrepare = 0;
		if(epcIdList.size()>containerForDoorScanDtoList.size())
		{   //在采购预入库表中的数据不会同时存在于door_scan表中
			//查询采购预入库器具 只查不在器具表中的EPCID
			//采购表的中器具收货时只更新收货相应字段即可
			resultPurchasePrepare = receiveContainerForPurchasePrepares(sessionUser, epcIds);
		}
		//如果器具都是不存在于t_container && 也没在采购表中，就没必要进行收货，直接忽略即可
		if(CollectionUtils.isEmpty(containerForDoorScanDtoList)) {
			LOG.info("门型收货。["+epcIds+"]器具既不存在于t_container也没在采购表t_purchase_prepare中");
			return ajaxBean;
		}
		//如果是托盘器具并且是组托状态，获取整托器具的epcId
		List<String> containerTrayGroupIdList = containerForDoorScanDtoList.stream().filter(f -> 1 == f.getIsTray().intValue() && 1 == f.getGroupState().intValue()).map(ContainerForDoorScanDto::getGroupId).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(containerTrayGroupIdList))
		{
			List<ContainerForDoorScanDto> dtoList = containerService.queryContainerByGroupIdS(convertStrListToStrs(containerTrayGroupIdList));
			containerForDoorScanDtoList.addAll(dtoList);
			//托盘上的器具全部解托
			for(String trayEpcId : containerTrayGroupIdList) {
				containerGroupService.relieveContainerGroup(sessionUser, trayEpcId);
			}
		}
		List<String> containerEpcIdList = containerForDoorScanDtoList.stream().map(ContainerForDoorScanDto::getEpcId).collect(Collectors.toList());
		//EPC编号去重
		filterStrListDuplication(containerEpcIdList);
		//把epcList转换成适合sql的字符串：'123','456','789'
		epcIds = convertStrListToStrs(containerEpcIdList);
        //LOG.info("门型收货  222 epcIds="+epcIds);
		String queryCirculateDetailSql = queryCirculateDetailByInEpcId + epcIds + ") order by order_code";
		LOG.info("门型收货  222 queryCirculateDetailSql="+queryCirculateDetailSql);
		List<CirculateDetail> circulateDetailList = jdbcTemplate.query(queryCirculateDetailSql, new BeanPropertyRowMapper(CirculateDetail.class));
        LOG.info("门型收货  333 circulateDetailList.size="+circulateDetailList.size());
	/*
		//组装Container对象集合
		List<Container> containerList = new ArrayList<Container>();
		for(ContainerForDoorScanDto d : containerForDoorScanDtoList) {
			Container c = new Container();
			c.setEpcId(d.getEpcId());
			c.setContainerCode(d.getContainerCode());
			c.setContainerName(d.getContainerName());
			c.setContainerTypeId(d.getContainerTypeId());
			c.setContainerTypeName(d.getContainerTypeName());
			c.setIsTray(d.getIsTray());
			containerList.add(c);
		}
		LOG.info("门型收货。刚组装完毕的containerList="+JSONObject.toJSONString(containerList));

		//获取流转单器具明细表t_circulate_detail所有未收货器具(receive_number=0)并且流转单未人工收货(is_manual_receive != '2')
		String queryCirculateDetailSql = queryCirculateDetailByInEpcId + epcIds + ") order by order_code";
		List<CirculateDetail> circulateDetailList = jdbcTemplate.query(queryCirculateDetailSql, new BeanPropertyRowMapper(CirculateDetail.class));

		//为没有被流转单收获掉的器具记录流转记录 buildAndInsertCirculateHistoryForCurrentOrg
		//门型扫描到器具，且器具未被流转单收货
		for(Container c : containerList)
		{
			boolean find = false;
			for(CirculateDetail d : circulateDetailList)
			{
				//如果可以被某个流转单收货
				if(c.getEpcId().equals(d.getEpcId()))
				{
					find = true;
					continue;
				}
			}
			//如果没有被流转单收货，则记录流转记录，因为被门型扫描到了
			if(!find)
			{
				//门型扫描到器具，且器具未被流转单收货
				circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser, c, SysConstants.DEVICE_DOOR_NOT_RECEIVE);

			}
		}
		*/
		List<Container> containerNotInCirculateDetailList = containerService.queryContainerNotInCirculateDetail(epcIds);
        LOG.info("门型收货  444 containerNotInCirculateDetailList.size="+containerNotInCirculateDetailList.size());
		//门型扫描到器具，且器具未被流转单收货
		if(CollectionUtils.isNotEmpty(containerNotInCirculateDetailList))
		{
			circulateService.buildAndBatchInsertCirculateHistoryForCurrentOrg(sessionUser, containerNotInCirculateDetailList, SysConstants.DEVICE_DOOR_NOT_RECEIVE);
		}

		List<Container> containerList = containerService.queryContainerListByEpcIdS(epcIds);
        LOG.info("门型收货  555 queryContainerListByEpcIdS  containerList.size="+containerList.size());
		//把门型收货记录到表：T_DOOR_SCAN_RECEIVE
		//saveScanReceive(sessionUser,containerList,createTime);
		aSyncTask.saveScanReceiveByThread(sessionUser,containerList,createTime);
        LOG.info("门型收货  666 saveScanReceive  end  circulateDetailList="+circulateDetailList.size());
		//如果没有流转单需要对这批EPC进行收货，直接返回
		if(CollectionUtils.isEmpty(circulateDetailList)) {
			return ajaxBean;
		}
		
		//把每个流转单需要收货的器具归类出来
		Map<String,ArrayList<CirculateDetail>> circulateDetailMap = new HashMap<String,ArrayList<CirculateDetail>>();
		for(CirculateDetail d : circulateDetailList) {
			ArrayList<CirculateDetail> detailList = circulateDetailMap.get(d.getOrderCode());
			if(CollectionUtils.isEmpty(detailList)) {
				detailList = new ArrayList<CirculateDetail>();
				detailList.add(d);
				circulateDetailMap.put(d.getOrderCode(), detailList);
				continue;
			}
			detailList.add(d);
		}
        LOG.info("门型收货  777   end");
		//将要插入表T_DOOR_SCAN_RECEIVE的数据集合
		List<DoorScanReceiveOrder> scanReceiveOrderList = new ArrayList<DoorScanReceiveOrder>();
		Iterator<Entry<String, ArrayList<CirculateDetail>>> it = circulateDetailMap.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, ArrayList<CirculateDetail>> entry = it.next();
			//流转单号
			String orderCode = entry.getKey();
			//这个流转单要收货的器具
			ArrayList<CirculateDetail> detailList = entry.getValue();
			//流转单对象
			CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
			/*
			containerEpcIdList = detailList.stream().map(CirculateDetail::getEpcId).collect(Collectors.toList());
			//EPC编号去重
			filterStrListDuplication(containerEpcIdList);
			//把epcList转换成适合sql的字符串：'123','456','789'
			epcIds = convertStrListToStrs(containerEpcIdList);
			List<Container> thisOrderContainerList = containerService.queryContainerInCirculateDetail(epcIds);
			LOG.info("门型收货  aaa   orderCode="+orderCode+"    thisOrderContainerList.size="+thisOrderContainerList.size()+"   end");
			//把这个流转单里要收获的器具转换成Container对象
			*/
			List<Container> thisOrderContainerList = new ArrayList<Container>();
			one:for(CirculateDetail d : detailList) {
				two:for(Container c : containerList) {
					if(d.getEpcId().equals(c.getEpcId())) {
						thisOrderContainerList.add(c);
						break two;
					}
				}
			}
			LOG.info("门型收货  aaa   orderCode="+orderCode+"    thisOrderContainerList.size="+thisOrderContainerList.size()+"   end");
			//实际收货
			handsetService.inOrgDoor(sessionUser, circulateOrder, containerEpcIdList,thisOrderContainerList, createTime);
			LOG.info("门型收货  bbb   orderCode="+orderCode+"    containerEpcIdList.size="+containerEpcIdList.size()+"   end");
			//这个流转单从这个门收到货了，需记录相关收货单：本仓库收货单、其他仓库收货单到表 T_DOOR_SCAN_RECEIVE_ORDER
			DoorScanReceiveOrder o = new DoorScanReceiveOrder();
			o.setDoorScanReceiveOrderId(UUIDUtil.creatUUID());
			o.setOrderCode(orderCode);
			o.setIsOwnOrg(sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())?SysConstants.INTEGER_0:SysConstants.INTEGER_1);
			o.setDoorAccount(sessionUser.getAccount());
			o.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			o.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			o.setCreateTime(createTime);
			scanReceiveOrderList.add(o);
		}
		LOG.info("门型收货  888   scanReceiveOrderList.size="+scanReceiveOrderList.size());
		//批量插入表 T_DOOR_SCAN_RECEIVE_ORDER，区分本仓库收货单、其他仓库收货单
		namedJdbcTemplate.batchUpdate(insertDoorScanReceiveOrder, SqlParameterSourceUtils.createBatch(scanReceiveOrderList.toArray()));
		LOG.info("门型收货  999   end");
		//清空集合，释放内存
		containerEpcIdList = null;
		containerList = null;
		scanReceiveOrderList = null;
		
        return ajaxBean;
    }



    /**
     * 把门型收货记录到表：T_DOOR_SCAN_RECEIVE
     * @param containerEpcIdList 收货门型扫描到的全部器具
     * @param createTime 收货门型扫描到器具的时间
     */
	@Transactional(propagation = Propagation.NESTED)
    public void saveScanReceive(SystemUser sessionUser, List<Container> containerList,Timestamp createTime) {
		List<DoorScanReceive> scanReceiveList = new ArrayList<DoorScanReceive>();
		for (Container c : containerList) {
			DoorScanReceive re = new DoorScanReceive();
			re.setDoorScanReceiveId(UUIDUtil.creatUUID());
			re.setDoorAccount(sessionUser.getAccount());
			re.setDoorRealName(sessionUser.getRealName());
			re.setEpcId(c.getEpcId());
			re.setContainerCode(c.getContainerCode());
			re.setContainerTypeId(c.getContainerTypeId());
			re.setContainerTypeName(c.getContainerTypeName());
			re.setCreateTime(createTime);
			re.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			re.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			scanReceiveList.add(re);
		}
		//批量插入表 T_DOOR_SCAN_RECEIVE
		namedJdbcTemplate.batchUpdate(insertDoorScanReceive, SqlParameterSourceUtils.createBatch(scanReceiveList.toArray()));
	}
    
	/**
	 * 采购表的中器具收货时只更新收货相应字段即可
	 * @param epcIds
	 */
	public int receiveContainerForPurchasePrepares(SystemUser sessionUser,String epcIds)
	{
		int result = 0;
		List<PurchasePrepare> purchasePrepareList = purchasePrepareService.queryPurchasePrepareNotReceiveByIds(epcIds);
		if(CollectionUtils.isNotEmpty(purchasePrepareList))
		{
			List<String> epcIdInPpList = purchasePrepareList.stream().map(PurchasePrepare::getEpcId).collect(Collectors.toList());
			result = purchasePrepareService.receiveContainerForPurchasePrepares(sessionUser, convertStrListToStrs(epcIdInPpList));
		}
		return result;
	}

	public AjaxBean sendEpc(AjaxBean ajaxBean, SystemUser sessionUser, List<String> epcIdList)
	{
		//获取当前时间。保证接下来所有insert语句的createTime都是同一时间
		Timestamp now = new Timestamp(System.currentTimeMillis());
		
		int pageSize = 50;
		StringBuffer epcIdS = new StringBuffer(SysConstants.NULL_STR);
		for (int i=0;i<epcIdList.size();i++)
		{
			epcIdS.append(SysConstants.DYH).append(epcIdList.get(i).toString()).append(SysConstants.DYH);
			if((i+1)<epcIdList.size())
			{
				epcIdS.append(SysConstants.DH);
			}
		}
//		Long beginTime = System.currentTimeMillis();
		//LOG.info("----sendEpc2  begin.time="+beginTime);
		List<ContainerForDoorScanDto> containerTrayList = containerService.queryContainerGroupByEpcIdS(epcIdS.toString());
		//提取已经有组托的托盘
		containerTrayList = containerTrayList.stream().filter(p->1==p.getGroupState().intValue()).collect(Collectors.toList());
		//LOG.info("----sendEpc2  end.time="+(System.currentTimeMillis()-beginTime)+"   containerTrayList.size="+containerTrayList.size());
		if(CollectionUtils.isNotEmpty(containerTrayList))
		{
			sendEpcGroup(sessionUser, containerTrayList, now);
			//获取每一个托上的器具，每个器具如果不是当前仓库，需要移动到当前仓库
			for (ContainerForDoorScanDto dto : containerTrayList) {
				List<Container> containerList = containerGroupService.queryContainerByGroupId(dto.getGroupId());
				if(CollectionUtils.isNotEmpty(containerList)) {
					for (Container container : containerList) {
						circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser,container
								,SysConstants.CIRCULATE_REMARK_DOOR_SEND);
					}
				}
			}
		}
		else
		{//多线程 分组执行
			while (CollectionUtils.isNotEmpty(epcIdList))
			{
				if (epcIdList.size() > pageSize)
				{
					//new MyThread(ajaxBean, sessionUser, epcIdList.subList(0, pageSize)).start();
					//不使用线程
					sendEpcByThread(ajaxBean, sessionUser, epcIdList.subList(0, pageSize),now);
					epcIdList = epcIdList.subList(pageSize, epcIdList.size());
				}
				else
				{
					//new MyThread(ajaxBean, sessionUser, epcIdList).start();
					//不使用线程
					sendEpcByThread(ajaxBean, sessionUser, epcIdList,now);
					epcIdList = null;
				}
			}
			if(CollectionUtils.isNotEmpty(epcIdList)) {
				//每个器具如果不是当前仓库，需要移动到当前仓库
				for (String epcId : epcIdList) {
					circulateService.buildAndInsertCirculateHistoryForCurrentOrg(sessionUser,containerService.getContainerByEpcId(epcId),SysConstants.CIRCULATE_REMARK_DOOR_SEND);
				}
			}
		}
		//不起线程
		//sendEpcByThread(ajaxBean, sessionUser,epcIdList);
		//LOG.info("----sendEpc  end.time="+(System.currentTimeMillis()-beginTime));
		return ajaxBean;
	}

	class MyThread extends Thread
	{
		private AjaxBean ajaxBean;
		private SystemUser sessionUser;
		private List<String> epcIdList;
		public MyThread(AjaxBean ajaxBean, SystemUser sessionUser, List<String> epcIdList)
		{
			this.ajaxBean = ajaxBean;
			this.sessionUser = sessionUser;
			this.epcIdList = epcIdList;
		}
		@Override
		public void run()
		{
			//LOG.info("----MyThread  sendEpcByThread  ThreadName="+this.getName());
			//sendEpcByThread2(ajaxBean, sessionUser,epcIdList);
			//sendEpcByThread(ajaxBean, sessionUser,epcIdList);
		}
	}

	public void sendEpcGroup(SystemUser sessionUser,List<ContainerForDoorScanDto> containerList,Timestamp now)
	{
		containerGroupService.batchInsertDoorScan(convertDoorScanByContainerList(sessionUser,containerList,true,now));//效率更高
	}
	public void sendEpcByThread(AjaxBean ajaxBean, SystemUser sessionUser, List<String> epcIdList,Timestamp now)
	{
		StringBuffer epcIdS = new StringBuffer(SysConstants.NULL_STR);
		for (int i=0;i<epcIdList.size();i++)
		{
			epcIdS.append(SysConstants.DYH).append(epcIdList.get(i).toString()).append(SysConstants.DYH);
			if((i+1)<epcIdList.size())
			{
				epcIdS.append(SysConstants.DH);
			}
		}
		List<ContainerForDoorScanDto> containerList = containerService.queryContainerByEpcIdS(epcIdS.toString());
		if(CollectionUtils.isNotEmpty(containerList))
		{
			containerGroupService.batchInsertDoorScan(convertDoorScanByContainerList(sessionUser, containerList,false,now));//参数位置绑定效率更高
		}
	}

	/**
	 * 组织门型扫描数据 BY 器具列表
	 * @param systemUser
	 * @param containers
	 * @return
	 */
	public List<DoorScan> convertDoorScanByContainerList(SystemUser systemUser,List<ContainerForDoorScanDto> containers,boolean isGroup,Timestamp now)
	{
		Long beginTime = System.currentTimeMillis();
		//LOG.info("----convertDoorScanByContainerList  begin.time="+beginTime);
		if(CollectionUtils.isNotEmpty(containers))
		{
			DoorScan doorScan = null;
			List<DoorScan> doorScans = new ArrayList<>();
			for(ContainerForDoorScanDto container :containers)
			{
				doorScan = new DoorScan();
				doorScan.setContainerCode(container.getContainerCode());
				doorScan.setContainerTypeId(container.getContainerTypeId());
				doorScan.setContainerTypeName(container.getContainerTypeName());
				if(isGroup) {
					doorScan.setIsGroup(SysConstants.INTEGER_1); //是组托
					doorScan.setGroupId(container.getGroupId());
				}else {
					doorScan.setIsGroup(SysConstants.INTEGER_0);
				}
				doorScan.setCreateTime(now);
				doorScan.setDoorAccount(systemUser.getAccount());
				doorScan.setDoorRealName(systemUser.getRealName());
				doorScan.setDoorScanId(UUIDUtil.creatUUID());
				doorScan.setEpcId(container.getEpcId());
				doorScan.setCreateOrgId(systemUser.getCurrentSystemOrg().getOrgId());
				doorScan.setCreateOrgName(systemUser.getCurrentSystemOrg().getOrgName());
				doorScans.add(doorScan);
			}
			//LOG.info("----convertDoorScanByContainerList  end.time="+(System.currentTimeMillis()-beginTime));
			return doorScans;
		}
		return null;
	}
}
