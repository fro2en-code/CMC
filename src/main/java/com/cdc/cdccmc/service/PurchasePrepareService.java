package com.cdc.cdccmc.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;

import com.cdc.cdccmc.service.sys.SystemUserService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import com.cdc.cdccmc.CdccmcApplication;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.PurchaseInOrgDetail;
import com.cdc.cdccmc.domain.PurchaseInOrgMain;
import com.cdc.cdccmc.domain.PurchasePrepare;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.dto.PurchaseSumDto;
import com.cdc.cdccmc.domain.print.PruchaseInbound;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.AreaService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/**
 * 采购预备表
 * @author Administrator
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class PurchasePrepareService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PurchasePrepareService.class); 
	private static final String EXCHANGE_NAME = "inbound_exchange_print";
	//private static final String QUEUE_NAME = "inbound_direct_queue_";

	@Autowired
	private BaseService baseService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private SystemUserService systemUserService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ContainerTypeService containerTypeService;
	@Autowired
	private LogService logService;

	@Value("#{sql['insertPurchaseInOrgMain']}")
	private String insertPurchaseInOrgMain;
	@Value("#{sql['insertPurchaseInOrgDetail']}")
	private String insertPurchaseInOrgDetail;
	@Value("#{sql['insertCirculateHistory']}")
	private String insertCirculateHistory;
	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['insertPurchasePrepare']}")
	private String insertPurchasePrepare;
	@Value("#{sql['updatePurchasePrepare']}")
	private String updatePurchasePrepare;
	@Value("#{sql['updatePurchaseInOrgMainPrintNumber']}")
	private String updatePurchaseInOrgMainPrintNumber;
	@Value("#{sql['queryPurchasePrepareByIds']}")
	private String queryPurchasePrepareByIds;
    @Value("#{sql['receiveContainerForPurchasePrepares']}")
    private String receiveContainerForPurchasePrepares;

	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.port}")
	private Integer port;
	@Value("${spring.rabbitmq.username}")
	private String userName;
	@Value("${spring.rabbitmq.password}")
	private String pwd;

	/**
	 * 查未收货的采购单器具-条件EPCID和isRecevie=0
	 * @param epcIds
	 * @return
	 */
	public List<PurchasePrepare> queryPurchasePrepareNotReceiveByIds(String epcIds)
	{
		StringBuffer sql = new StringBuffer(queryPurchasePrepareByIds);
		sql.append(epcIds).append(SysConstants.YKH);
		List<PurchasePrepare> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(PurchasePrepare.class));
		return list;
	}

	/**
	 * 采购单收货-更新收货信息
	 * @param sessionUser
	 * @param epcIds
	 * @return
	 */
	public int receiveContainerForPurchasePrepares(SystemUser sessionUser,String epcIds)
    {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        StringBuffer sql = new StringBuffer(receiveContainerForPurchasePrepares);
        sql.append(epcIds).append(SysConstants.YKH);
        int result = jdbcTemplate.update(sql.toString(),SysConstants.STRING_1,now,sessionUser.getAccount()
		  ,sessionUser.getRealName(),sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName());
        return result;
    }

	/**
	 * 采购预备表翻页，只能看当前仓库新建的数据
	 * @param sessionUser
	 * @param paging
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Paging pagingPurchasePrepare(SystemUser sessionUser, Paging paging,String startDate, String endDate) {
		Map paramMap = new HashMap();
		StringBuilder sql = new StringBuilder("select * from T_PURCHASE_PREPARE where create_org_id='");
		sql.append(sessionUser.getCurrentSystemOrg().getOrgId());
		sql.append("' ");
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and receive_time >= :startDate");
			paramMap.put("startDate", startDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and receive_time <= :endDate");
			paramMap.put("endDate", endDate);
		}
		// 当日期搜索条件不为空时,按照入库时间排序
		if (StringUtils.isNotBlank(startDate) || StringUtils.isNotBlank(endDate)) {
			sql.append(" order by in_org_time desc");
		} else {// 否则按照创建时间排序
			sql.append(" order by create_time desc");
		}
		paging = baseService.pagingParamMap(paging, sql.toString(), paramMap, PurchasePrepare.class);
		return paging;
	}

	/**
	 * 采购预备表excel 批量上传
	 * @param sessionUser
	 * @param file
	 * @return
	 */
	public AjaxBean batchUpload(SystemUser sessionUser, File file) {
		AjaxBean ajaxBean = validExcelData(sessionUser, file.getPath());
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) { //如果校验失败，直接返回。
			return ajaxBean;
		}
		ConcurrentLinkedQueue<PurchasePrepare> purchaseList = new ConcurrentLinkedQueue<>();
		// 校验通过,构建map数组
		List<Map<Integer, String>> excelList = ajaxBean.getList();
		// 获取orgNameMap
		for (int i = 0; i < excelList.size(); i++) {
			if(null == excelList.get(i)){ //如果是空行，直接跳过
				continue;
			}
			PurchasePrepare purchase = new PurchasePrepare();
			purchase.setEpcType(StringUtils.trim(excelList.get(i).get(0))); //EPC类型
			purchase.setEpcId(StringUtils.trim(excelList.get(i).get(1))); //EPC编号
			purchase.setPrintCode(StringUtils.trim(excelList.get(i).get(2))); //印刷编号
			purchase.setContainerCode(excelList.get(i).get(3)); //器具代码
//			String containerTypeName = excelList.get(i).get(4); //器具类型
//			purchase.setContainerTypeId(containerTypeService.findOrInsertContainerType(sessionUser, containerTypeName).getContainerTypeId());
//			purchase.setContainerTypeName(containerTypeName); //器具类型名称
//			purchase.setContainerName(StringUtils.trim(excelList.get(i).get(5))); //器具名称
			purchase.setContainerSpecification(StringUtils.trim(excelList.get(i).get(4))); //规格
			purchase.setContainerTexture(StringUtils.trim(excelList.get(i).get(5))); //材质
			String isAloneGroupStr = excelList.get(i).get(6);
			if(StringUtils.equals(isAloneGroupStr, SysConstants.YES)){ //是否单独成托，0不是，1是
				purchase.setIsAloneGroup(SysConstants.INTEGER_1);
			}else{
				purchase.setIsAloneGroup(SysConstants.INTEGER_0);
			}
			purchase.setContainerName(containerCodeService.queryByContainerCode(excelList.get(i).get(3)).getContainerName());
			purchase.setContainerTypeName(containerCodeService.queryByContainerCode(excelList.get(i).get(3)).getContainerTypeName());
			purchase.setContainerTypeId(containerCodeService.queryByContainerCode(excelList.get(i).get(3)).getContainerTypeId());
			purchase.setIsReceive(SysConstants.STRING_0); //收货状态。0未收货，1已收货
			purchase.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			purchase.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			purchase.setCreateAccount(sessionUser.getAccount());
			purchase.setCreateRealName(sessionUser.getRealName());
			// 添加到新增队列
			purchaseList.add(purchase);

			// 如果达到批量插入条数，就进行批量插入
			if (purchaseList.size() >= SysConstants.MAX_INSERT_NUMBER) {
				this.batchInsertPurchase(sessionUser,purchaseList);
				purchaseList.clear();
			}
		}
		if (purchaseList.size() > SysConstants.INTEGER_0) {
			this.batchInsertPurchase(sessionUser,purchaseList);
			purchaseList.clear();
		}
		return ajaxBean;

	}

	/**
	 * excel数据校验
	 * @param path
	 * @return ajaxBean list:excel需要插入的内容 bean:MAP<containTypeName,containTypeId>
	 */
	private AjaxBean validExcelData(SystemUser sessionUser, String path) {
		AjaxBean ajaxBean = new AjaxBean();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,9);
		} catch (Exception e) { // 文件批量导入失败！
			LOG.error(StatusCode.STATUS_402_MSG, e);
			logService.addLogError(sessionUser, e, StatusCode.STATUS_402_MSG, null);
			ajaxBean.setStatus(StatusCode.STATUS_402);
			ajaxBean.setMsg(StatusCode.STATUS_402_MSG);
			return ajaxBean;
		}
		if (mapList.size() == 0) { // 如果数据一行都没有，未检测到需导入数据，请检查上传文件内数据输入是否正确。
			LOG.error(StatusCode.STATUS_403_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_403);
			ajaxBean.setMsg(StatusCode.STATUS_403_MSG);
			return ajaxBean;
		}

		if (mapList.size() > SysConstants.MAX_UPLOAD_ROWS) {
			LOG.error(StatusCode.STATUS_401_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_401);
			ajaxBean.setMsg(StatusCode.STATUS_401_MSG);
			return ajaxBean;
		}

		List<String> errList = new ArrayList<String>();
		LOG.info("本次需批量导入" + mapList.size() + "条组织数据");

		int row = 1;
		for (Map<Integer, String> map : mapList) {
			row = row + 1;
			if(null == map){  //如果是空行，直接跳过
				continue;
			}
			String epcType = map.get(0);
			if (StringUtils.isBlank(epcType)) {
				errList.add("第" + row + "行，EPC类型不能为空。");
			} else if(epcType.length()>50){
				errList.add("第" + row + "行，EPC类型["+epcType+"]长度不能超过50个字符。");
			}

			String epcId = map.get(1);
			if (StringUtils.isBlank(epcId)) {
				errList.add("第" + row + "行，EPC编号不能为空。");
			}else if (epcId.length() > 50) {
				errList.add("第" + row + "行，EPC编号["+epcId+"]长度不能超过50个字符。");
			} else if(null != containerService.findContainerByEpcId(epcId)){
				errList.add("第" + row + "行，EPC编号["+epcId+"]在仓库["+containerService.findContainerByEpcId(epcId).getBelongOrgName()+"]中已存在。");
			} else if (null != this.queryPurchasePrepareByEpcId(epcId)){
				errList.add("第" + row + "行，EPC编号["+epcId+"]在采购预备表中已存在。");
			} else {
				int row2 = 1;
				two:for(Map<Integer, String> map2 : mapList){
					row2 = row2 + 1;
					if(null != map2 && map.get(1).equals(map2.get(1)) && row != row2){ //如果excel内发现重复车牌号
						errList.add("第"+row+"行，EPC编号["+epcId+"]在excel内重复，请核查。");
						break two;
					}
				}
			}

//			String printCode = map.get(2);
//			if (StringUtils.isBlank(printCode)) {
//				errList.add("第" + row + "行，印刷编号不能为空。");
//			}
//			if (printCode.length() > 20) {
//				errList.add("第" + row + "行，印刷编号["+printCode+"]长度不能超过20个字符。");
//			}

			String containerCode = map.get(3);
			if (StringUtils.isBlank(containerCode)) {
				errList.add("第" + row + "行，器具代码不能为空。");
			} else if (containerCode.length() > 32) {
				errList.add("第" + row + "行，器具代码["+containerCode+"]长度不能超过32个字符。");
			}else {
				ContainerCode findCode = containerCodeService.queryByContainerCode(containerCode);
				if(null == findCode){ //如果器具代码不存在，则报错
					errList.add("第" + row + "行，器具代码["+containerCode+"]不存在，请核查。");
				}
			}

//			String containTypeName = map.get(4);
//			if (StringUtils.isBlank(containTypeName)) {
//				errList.add("第" + row + "行，器具类型不能为空。");
//			}
//			if (containTypeName.length() > 50) {
//				errList.add("第" + row + "行，器具类型长度不能超过50个字符。");
//			}
//
//			String containerName = map.get(5);
//			if (StringUtils.isBlank(containerName)) {
//				errList.add("第" + row + "行，器具名称不能为空。");
//			}else if (containerName.length() > 50) {
//				errList.add("第" + row + "行，器具名称["+containerName+"]长度不能超过50个字符。");
//			}

			String containerSpecification = map.get(4);
			if(StringUtils.isNotBlank(containerSpecification) && containerSpecification.length() > 50){
				errList.add("第" + row + "行，规格["+containerSpecification+"]长度不能超过50个字符。");
			}
			String containerTexture = map.get(5);
			if(StringUtils.isNotBlank(containerTexture) && containerTexture.length() > 20){
				errList.add("第" + row + "行，材质["+containerTexture+"]长度不能超过20个字符。");
			}
			String isAloneGroup = map.get(6);
			if (StringUtils.isBlank(isAloneGroup)) {
				errList.add("第" + row + "行，是否单独成托不能为空。");
			}else if (isAloneGroup.length() > 1) {
				errList.add("第" + row + "行，是否单独成托不能超过1个字符。");
			}
			if(errList.size() > 10) {
				break;
			}
		}
		if (CollectionUtils.isNotEmpty(errList)) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setList(errList);
		} else {
			ajaxBean.setList(mapList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		}
		return ajaxBean;
	}

	/**
	 * 批量插入
	 * @param prepareList
	 */
	private void batchInsertPurchase(SystemUser sessionUser, ConcurrentLinkedQueue<PurchasePrepare> prepareList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(prepareList.toArray());
		namedJdbcTemplate.batchUpdate(insertPurchasePrepare, params);
		logService.addLogAccount(sessionUser, "采购预备表批量导入"+(prepareList.size())+"条");
	}

	/**
	 * 采购入库表分页
	 * @param purchaseInOrgMainId
	 */
	public Paging pagingPurchaseInOrg(SystemUser sessionUser, Paging paging, String startDate, String endDate,
									  String purchaseInOrgMainId) {
		Map paramMap = new HashMap();
		StringBuilder sql = new StringBuilder("select * from T_PURCHASE_IN_ORG_MAIN where create_org_id in ( ");
		sql.append(sessionUser.getFilialeSystemOrgIds());
		sql.append(" ) ");
		if (StringUtils.isNotBlank(purchaseInOrgMainId)) {
			sql.append(" and purchase_in_org_main_id like :purchaseInOrgMainId ");
			paramMap.put("purchaseInOrgMainId", "%" + purchaseInOrgMainId.trim() + "%");
		}
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and create_time >= :startDate");
			paramMap.put("startDate", startDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and create_time <= :endDate");
			paramMap.put("endDate", endDate);
		}
		sql.append(" order by create_time desc ");
		paging = baseService.pagingParamMap(paging, sql.toString(), paramMap, PurchaseInOrgMain.class);
		return paging;
	}

	/**
	 * 入库单详情查询
	 * @param purchaseInOrgMainId 入库单号
	 * @return paging
	 */
	public List<PurchaseInOrgDetail> queryCirculateDetail(String purchaseInOrgMainId) {
		String sql = "select * from t_purchase_in_org_detail where purchase_in_org_main_id= ? ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(PurchaseInOrgDetail.class), purchaseInOrgMainId);
	}

	/**
	 * 器具统计列表
	 * @return 返回一个每个器具代码有多少个器具数量的统计列表
	 */
	public List<PurchaseSumDto> buildPurchaseSumDtoByPurchaseInOrgDetail(List<PurchaseInOrgDetail> piodList) {
		//组装器具统计需要的数据
		ConcurrentHashMap<String, PurchaseSumDto> map = new ConcurrentHashMap<String, PurchaseSumDto>();
		for (PurchaseInOrgDetail pl : piodList) {
			String containerCode = pl.getContainerCode();
			if(map.containsKey(containerCode)){
				PurchaseSumDto dto = map.get(containerCode);
				dto.setPurchaseCount(dto.getPurchaseCount() + 1);
				map.put(containerCode, dto);
			}else{
				PurchaseSumDto dto = new PurchaseSumDto();
				dto.setPurchaseCount(1);
				dto.setContainerCode(pl.getContainerCode());
				dto.setContainerName(pl.getContainerName());
                dto.setContainerSpecification(pl.getContainerSpecification());
				map.put(containerCode, dto);
			}
		}
		//组装器具统计列表
		List<PurchaseSumDto> purchaseSumList = new ArrayList<PurchaseSumDto>();
		Iterator<Entry<String, PurchaseSumDto>> it = map.entrySet().iterator();
		while(it.hasNext()){
			purchaseSumList.add(it.next().getValue());
		}
		return purchaseSumList;
	}
	/**
	 * 入库单器具明细查询
	 * @param purchaseInOrgMainId 入库单号
	 * @return
	 */
	public PurchaseInOrgMain queryPurchaseInOrgDetailByMainId(String purchaseInOrgMainId) {
		String sql = "select * from t_purchase_in_org_main where purchase_in_org_main_id= ?";
		List<PurchaseInOrgMain> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PurchaseInOrgMain.class), purchaseInOrgMainId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 采购预备表查询，根据EPC编号
	 * @param epcId EPC编号
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public PurchasePrepare queryPurchasePrepareByEpcId(String epcId) {
		String sql = "select * from t_purchase_prepare where epc_id = ?";
		List<PurchasePrepare> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(PurchasePrepare.class), epcId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据ecpId删除指定的器具
	 *
	 * @param sessionUser
	 * @param epcId
	 * @return ajaxBean
	public AjaxBean deleteOrderByEpcId(SystemUser sessionUser, String epcId) {
	String sql = "DELETE from t_purchase_in_org_detail where epc_id=:epcId";
	Map map = new HashMap();
	map.put("epcId", epcId);
	int count = namedJdbcTemplate.update(sql, map);
	if (count > 0)
	return AjaxBean.SUCCESS();
	else
	return AjaxBean.FAILURE();
	}*/

	/**
	 * 打印包装入库单
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param purchaseInOrgMainId 包装入库单单号
	 * @return
	 */
	public AjaxBean printInbound(AjaxBean ajaxBean,SystemUser sessionUser, String purchaseInOrgMainId){
		//包装流转单不能为空
		if(StringUtils.isBlank(purchaseInOrgMainId)){
			ajaxBean.setMsg("包装入库单单号"+StatusCode.STATUS_305_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		List<PurchaseInOrgDetail> detailList = this.queryCirculateDetail(purchaseInOrgMainId);
		List<PurchaseSumDto> sumDtoList = this.buildPurchaseSumDtoByPurchaseInOrgDetail(detailList);
		if(CollectionUtils.isEmpty(detailList)){
			ajaxBean.setStatus(StatusCode.STATUS_341);
			ajaxBean.setMsg("[" + purchaseInOrgMainId + "]"+StatusCode.STATUS_341_MSG);
			return ajaxBean;
		}
		LOG.info("开始打印入库单，单号为：["+purchaseInOrgMainId+"]");
		//组装包装流转单DTO
		PurchaseInOrgMain main = this.queryPurchaseInOrgDetailByMainId(purchaseInOrgMainId);
		if (main==null){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg(StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
    	if(!sessionUser.getCurrentSystemOrg().getOrgId().equals(main.getCreateOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_364);
			ajaxBean.setMsg(StatusCode.STATUS_364_MSG);
			return ajaxBean;
    	}
		PruchaseInbound inbound = new PruchaseInbound();
		inbound.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		inbound.setTitle("包装入库单");
		inbound.setBarCode(purchaseInOrgMainId);//条形码
		inbound.setInBoundName(main.getCreateOrgName());//收获方
		inbound.setConsignorOrgName(main.getConsignorOrgName());//送货方


		//表格
		List<Map<String,String>> list = new ArrayList<>();
		Integer i =0;

		for (PurchaseSumDto o :sumDtoList){
			Map<String,String> map = new HashMap<String, String>();
			map.put("id",String.valueOf(i+1));
			map.put("code",o.getContainerCode());
			map.put("name",o.getContainerName());
			map.put("size",o.getContainerSpecification());
			map.put("planCount",o.getPurchaseCount().toString());
			map.put("receiveCount",o.getPurchaseCount().toString());//实收
			map.put("sendCount","");
			map.put("remark","");
			list.add(map);
			i++;
		}
		inbound.setDetails(list);
		inbound.setPeopleId(sessionUser.getAccount());
		inbound.setPeopleName(sessionUser.getRealName());
		inbound.setPrintTime(DateUtil.currentTimestamp().toString());

		//底部表格
		inbound.setDescription(StringUtils.EMPTY);//特别描述
		inbound.setCreateRealName("");
		inbound.setCreateTime("");
		inbound.setOrgId(main.getCreateOrgId());
		String jsonString = JSON.toJSONString(inbound, SerializerFeature.WriteNullStringAsEmpty);
		LOG.info(jsonString);
		//不使用MQ直接调用打印
		//InboundPrint inboundPrint = new InboundPrint();
		//inboundPrint.print(jsonString);
//		rabbitTemplate.convertAndSend("inboundExchange","topic.inbound.test",jsonString);
		/* 使用MQ异步打印 因为服务端要部署在云平台上 链接不到本地打印机 所以走MQ*/
		if(StringUtils.isNotBlank(jsonString)){
			try {
				LOG.info("初始化mq连接IP["+host+":"+port+"]");
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost(host);
				factory.setPort(port);
				factory.setUsername(userName);
				factory.setPassword(pwd);
				String printName = systemUserService.getUserPrintCode(sessionUser);
				if(StringUtils.isEmpty(printName))
				{
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
				// 声明该channel是direct类型
				LOG.info("MQ创建交换机"+EXCHANGE_NAME);
				channel.exchangeDeclare(EXCHANGE_NAME, "direct",true);
				//LOG.info("MQ创建队列名" + queueName);
				//channel.queueDeclare(queueName,true,false,false,null);
				//LOG.info("队列绑定交换机");
				//channel.queueBind(queueName,EXCHANGE_NAME,"");
				LOG.info("MQ绑定路由:"+orgId);
				// 将消息发送给exchange
				channel.basicPublish(EXCHANGE_NAME, orgId, null, jsonString.getBytes());

				//发送成功,把打印次数加1;
				LOG.info("入库单队列发送完成，打印次数自增");
				jdbcTemplate.update(updatePurchaseInOrgMainPrintNumber, purchaseInOrgMainId);
				logService.addLogAccount(sessionUser,"打印采购入库单[" + main.getPurchaseInOrgMainId()+ "]");

				channel.close();
				conn.close();
			} catch (IOException e) {
				LOG.error("发送MQ异常",e);
				ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
				ajaxBean.setMsg(StatusCode.STATUS_367_MSG);
			} catch (TimeoutException e) {
				LOG.error("发送MQ异常:连接超时",e);
				ajaxBean.setStatus(AjaxBean.FAILURE().getStatus());
				ajaxBean.setMsg(StatusCode.STATUS_367_MSG);
			}


		}




		return ajaxBean;
	}

	/**
	 * 生成新采购入库单，以及入库单明细
	 * @param ajaxBean
	 * @param sessionUser
	 * @param purchaseInOrgMain
	 * @param epcIdList
	 * @return
	 */
	public AjaxBean createPurchaseInOrg(AjaxBean ajaxBean,SystemUser sessionUser, PurchaseInOrgMain purchaseInOrgMain,
										List<String> epcIdList) {
		//创建采购入库单单号，创建规则与包装流转单相同
		String orderCode = circulateOrderService.createOrderCode(sessionUser);
		//新增采购入库单主表 t_purchase_in_org_main
		PurchaseInOrgMain main = buildAndInsertPurchaseInOrgMain(ajaxBean,orderCode,sessionUser,purchaseInOrgMain,epcIdList);
		if(StatusCode.STATUS_200 != ajaxBean.getStatus()){
			return ajaxBean;
		}
		//获取采购预备表里的器具集合，插入器具表t_container
		ConcurrentHashMap<String, PurchasePrepare> prepareMap = batchInsertContainerForPrepare(sessionUser, epcIdList,main);
		//组装并插入包装入库单器具明细表 t_purchase_in_org_detail
		buildAndInsertPurchaseInOrgDetail(prepareMap,orderCode,sessionUser,purchaseInOrgMain,epcIdList);
		//更新采购预备表t_purchase_prepare里的采购入库单信息
		updatePurchasePrepareInOrg(ajaxBean,sessionUser,orderCode,prepareMap);
		//新增首次EPC器具的流转记录：入库，新增表 t_circulate_history、t_circulate_detail
		buildAndInsertCirculateHistory(ajaxBean,sessionUser,orderCode,main,prepareMap);
		ajaxBean.setBean(main);
		return ajaxBean;
	}

	/**
	 * 更新采购预备表t_purchase_prepare里的采购入库单信息
	 * @param ajaxBean
	 * @param sessionUser
	 * @param orderCode
	 */
	private void updatePurchasePrepareInOrg(AjaxBean ajaxBean,SystemUser sessionUser, String orderCode,ConcurrentHashMap<String, PurchasePrepare> prepareMap) {
		Iterator<Entry<String, PurchasePrepare>> it = prepareMap.entrySet().iterator();
		List<PurchasePrepare> prepareList = new ArrayList<PurchasePrepare>();
		while(it.hasNext()){
			PurchasePrepare p = it.next().getValue();
			PurchasePrepare param = new PurchasePrepare();
			param.setEpcId(p.getEpcId());
			param.setPurchaseInOrgMainId(orderCode); //采购入库单
			param.setInOrgAccount(sessionUser.getAccount());
			param.setInOrgRealName(sessionUser.getRealName());
			param.setInOrgOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			param.setInOrgOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			prepareList.add(param);
			if(prepareList.size() >= SysConstants.MAX_INSERT_NUMBER){
				SqlParameterSource[] batchParam = SqlParameterSourceUtils.createBatch(prepareList.toArray());
				namedJdbcTemplate.batchUpdate(updatePurchasePrepare, batchParam);
				prepareList.clear();
			}
		}
		if(prepareList.size() > SysConstants.INTEGER_0){
			SqlParameterSource[] batchParam = SqlParameterSourceUtils.createBatch(prepareList.toArray());
			namedJdbcTemplate.batchUpdate(updatePurchasePrepare, batchParam);
		}
		//释放内存
		prepareList = null;
	}

	/**
	 * 生成新采购入库单时，新增器具流转记录：器具流转历史表 T_CIRCULATE_HISTORY，器具最新流转记录表 T_CIRCULATE_LATEST
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	public AjaxBean buildAndInsertCirculateHistory(AjaxBean ajaxBean,SystemUser sessionUser,String orderCode
			,PurchaseInOrgMain main,ConcurrentHashMap<String, PurchasePrepare> prepareMap){
		Iterator<Entry<String, PurchasePrepare>> it = prepareMap.entrySet().iterator();
		Area defaultArea = areaService.queryDefaultArea();

		Timestamp time1 = new Timestamp(new Date().getTime());
		Timestamp time2 = DateUtil.currentAddTwoSecond();
		while (it.hasNext()) {
			Entry<String, PurchasePrepare> entry = it.next();
			PurchasePrepare p = entry.getValue();
			Circulate history = new Circulate();
			history.setCirculateHistoryId(UUIDUtil.creatUUID()); //流转历史ID
			history.setEpcId(p.getEpcId()); //EPC编号
			history.setContainerCode(p.getContainerCode()); //器具代码
			history.setContainerTypeId(p.getContainerTypeId()); //器具类型ID
			history.setContainerTypeName(p.getContainerTypeName()); //器具类型名称
			history.setOrderCode(orderCode); //包装流转单单据编号
			history.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //操作公司ID
			history.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //操作公司名称
			history.setCreateAccount(sessionUser.getAccount()); //操作人
			history.setCreateRealName(sessionUser.getRealName()); //操作人的姓名
			history.setCreateTime(time1); //创建时间
			//更新from来源仓库
			history.setFromOrgId(main.getConsignorOrgId()); //送货方组织ID
			history.setFromOrgName(main.getConsignorOrgName()); //送货方组织名称
			//器具流转状态为：采购入库
			history.setCirculateState(CirculateState.PURCHASE_IN_ORG.getCode()); //采购入库
			history.setCirculateStateName(CirculateState.PURCHASE_IN_ORG.getCirculate()); //采购入库
			//入库区域为默认收货区域
			history.setAreaId(defaultArea.getAreaId());
			history.setAreaName(defaultArea.getAreaName());
			history.setRemark(""); 

			Circulate onOrgCirculate = new Circulate();
			onOrgCirculate.setCirculateHistoryId(UUIDUtil.creatUUID()); //流转历史ID
			onOrgCirculate.setEpcId(p.getEpcId()); //EPC编号
			onOrgCirculate.setContainerCode(p.getContainerCode()); //器具代码
			onOrgCirculate.setContainerTypeId(p.getContainerTypeId()); //器具类型ID
			onOrgCirculate.setContainerTypeName(p.getContainerTypeName()); //器具类型名称
			onOrgCirculate.setOrderCode(orderCode); //包装流转单单据编号
			onOrgCirculate.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //操作公司ID
			onOrgCirculate.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //操作公司名称
			//更新from来源仓库
			onOrgCirculate.setFromOrgId(main.getConsignorOrgId()); //来源公司ID
			onOrgCirculate.setFromOrgName(main.getConsignorOrgName()); //来源公司名称
			onOrgCirculate.setCreateAccount(sessionUser.getAccount()); //操作人
			onOrgCirculate.setCreateRealName(sessionUser.getRealName()); //操作人的姓名
			onOrgCirculate.setCreateTime(time2);
			onOrgCirculate.setCirculateState(CirculateState.ON_ORG.getCode()); //在库
			onOrgCirculate.setCirculateStateName(CirculateState.ON_ORG.getCirculate()); //在库
			onOrgCirculate.setRemark(SysConstants.CIRCULATE_REMARK_PURCHASE);
			//入库区域为默认收货区域
			onOrgCirculate.setAreaId(defaultArea.getAreaId());
			onOrgCirculate.setAreaName(defaultArea.getAreaName());

			BeanPropertySqlParameterSource[] batchValues = new BeanPropertySqlParameterSource[2];
			batchValues[0] = new BeanPropertySqlParameterSource(history);
			batchValues[1] = new BeanPropertySqlParameterSource(onOrgCirculate);
			//新增新的器具最新流转记录  T_CIRCULATE_HISTORY，在库
			namedJdbcTemplate.batchUpdate(insertCirculateHistory, batchValues);
			//删除旧的器具最新流转记录  T_CIRCULATE_LATEST
			jdbcTemplate.update(deleteCirculateLatest, p.getEpcId());
			//新增新的器具最新流转记录  T_CIRCULATE_LATEST，在库
			namedJdbcTemplate.update(insertCirculateLatest, batchValues[1]);
		}
		return ajaxBean;
	}

	/**
	 * 组装并插入包装入库单器具明细表 t_purchase_in_org_detail
	 * @return
	 */
	private void buildAndInsertPurchaseInOrgDetail(ConcurrentHashMap<String, PurchasePrepare> prepareMap,
												   String orderCode,SystemUser sessionUser, PurchaseInOrgMain purchaseInOrgMain,List<String> epcIdList){
		//新增采购入库单器具明细表
		List<PurchaseInOrgDetail> detailList = new ArrayList<PurchaseInOrgDetail>();
		for (String epcId : epcIdList) {
			PurchasePrepare p = prepareMap.get(epcId);
			PurchaseInOrgDetail detail = new PurchaseInOrgDetail();
			detail.setPurchaseInOrgDetailId(UUIDUtil.creatUUID());
			detail.setPurchaseInOrgMainId(orderCode);
			detail.setEpcId(epcId);
			detail.setEpcType(p.getEpcType());
			detail.setContainerName(p.getContainerName());
			detail.setPrintCode(p.getPrintCode());
			detail.setContainerTypeId(p.getContainerTypeId());
			detail.setContainerTypeName(p.getContainerTypeName());
			detail.setContainerCode(p.getContainerCode());
			detail.setIsAloneGroup(p.getIsAloneGroup());
			detail.setContainerSpecification(p.getContainerSpecification());
			detail.setPlanNumber(SysConstants.INTEGER_1);
			detail.setSendNumber(SysConstants.INTEGER_1);
			detail.setReceiveNumber(SysConstants.INTEGER_1);
			detail.setCreateAccount(sessionUser.getAccount());
			detail.setCreateRealName(sessionUser.getRealName());
			detail.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			detail.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			detailList.add(detail);
			//如果达到批量插入条数，则执行批量插入
			if(detailList.size() >= SysConstants.MAX_INSERT_NUMBER){
				SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(detailList.toArray());
				namedJdbcTemplate.batchUpdate(insertPurchaseInOrgDetail, params);
				detailList.clear();
			}
		}
		//如果尚未批量插入，执行批量插入
		if(detailList.size() > SysConstants.INTEGER_0){
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(detailList.toArray());
			namedJdbcTemplate.batchUpdate(insertPurchaseInOrgDetail, params);
		}
	}

	/**
	 * 创建新的包装入库单。组装并插入包装入库单主单 t_purchase_in_org_main
	 * @return
	 */
	private PurchaseInOrgMain buildAndInsertPurchaseInOrgMain(AjaxBean ajaxBean,String orderCode,SystemUser sessionUser, PurchaseInOrgMain purchaseInOrgMain,
															  List<String> epcIdList){
		//获取发货方信息
		SystemOrg consignorOrg = systemOrgService.findById(purchaseInOrgMain.getConsignorOrgId());
		PurchaseInOrgMain inOrgMain = new PurchaseInOrgMain();
		inOrgMain.setPurchaseInOrgMainId(orderCode);
		inOrgMain.setInOrgNumber(epcIdList.size()); //器具入库总数
		inOrgMain.setPrintNumber(0); //打印次数
		inOrgMain.setInOrgRemark(purchaseInOrgMain.getInOrgRemark()); //入库备注
		inOrgMain.setConsignorOrgId(consignorOrg.getOrgId()); //发货方仓库ID
		inOrgMain.setConsignorOrgName(consignorOrg.getOrgName());  //发货方仓库名称
		inOrgMain.setCreateAccount(sessionUser.getAccount());
		inOrgMain.setCreateRealName(sessionUser.getRealName());
		inOrgMain.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		inOrgMain.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		
		try {
			//insert包装入库单
			int insertResult = namedJdbcTemplate.update(insertPurchaseInOrgMain, new BeanPropertySqlParameterSource(inOrgMain));
			if(insertResult == 0){
				ajaxBean.setStatus(StatusCode.STATUS_336);
				ajaxBean.setMsg(StatusCode.STATUS_336_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.info(e.getMessage(),e);
			ajaxBean.setStatus(StatusCode.STATUS_336);
			ajaxBean.setMsg(StatusCode.STATUS_336_MSG);
		}
		return inOrgMain;
	}

	/**
	 * 把采购预备表里的器具首次批量插入器具表
	 * @param sessionUser
	 * @param epcIdList
	 * @return
	 */
	private ConcurrentHashMap<String, PurchasePrepare> batchInsertContainerForPrepare(SystemUser sessionUser,List<String> epcIdList, PurchaseInOrgMain purchaseInOrgMain){
		ConcurrentHashMap<String, PurchasePrepare> map = new ConcurrentHashMap<String, PurchasePrepare>();
		//器具表新增这些采购预备表里的器具明细
		List<Container> containerList = new ArrayList<Container>();
		for (String epcId : epcIdList) {
			PurchasePrepare p = queryPurchasePrepareByEpcId(epcId);
			map.put(p.getEpcId(), p);
			Container container = new Container();
			container.setEpcId(epcId);
			container.setEpcType(p.getEpcType());
			container.setContainerTypeId(p.getContainerTypeId());
			container.setPrintCode(p.getPrintCode());
			container.setContainerName(p.getContainerName()); //器具名称
			container.setContainerTypeId(p.getContainerTypeId());
			container.setContainerTypeName(p.getContainerTypeName());
			container.setContainerCode(p.getContainerCode());
			container.setContainerSpecification(p.getContainerSpecification()); //规格
			container.setContainerTexture(p.getContainerTexture()); //材质
			container.setIsAloneGroup(p.getIsAloneGroup());
			container.setIsOutmode(SysConstants.INTEGER_0); //是否过时，0未过时，1过时
			container.setCreateAccount(sessionUser.getAccount());
			container.setCreateRealName(sessionUser.getRealName());
			container.setLastOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			container.setLastOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			container.setBelongOrgId(sessionUser.getCurrentSystemOrg().getOrgCode());
			container.setBelongOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			container.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			container.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			container.setIsTray(0);
			container.setVersion(SysConstants.INTEGER_0);
			containerList.add(container);
			//如果达到批量插入条数，则执行批量插入
			if(containerList.size() >= SysConstants.MAX_INSERT_NUMBER){
				containerService.batchInsertContainer(sessionUser,containerList);
				logService.addLogAccount(sessionUser, "创建新的采购入库单["+purchaseInOrgMain.getPurchaseInOrgMainId()+"]，从采购预备表批量导入器具到器具列表"+(containerList.size())+"条");
				containerList.clear();
			}
		}
		//如果尚未批量插入，执行批量插入
		if(containerList.size() > SysConstants.INTEGER_0){
			containerService.batchInsertContainer(sessionUser,containerList);
			logService.addLogAccount(sessionUser, "创建新的采购入库单["+purchaseInOrgMain.getPurchaseInOrgMainId()+"]，从采购预备表批量导入器具到器具列表"+(containerList.size())+"条");
			containerList = null;
		}
		return map;
	}
}