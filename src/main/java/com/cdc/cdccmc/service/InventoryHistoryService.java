package com.cdc.cdccmc.service;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cdc.cdccmc.domain.Area;

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

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.CmcException;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/** 
 * 盘点单信息
 * @author shichuang
 * @date 2018-01-29
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class InventoryHistoryService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InventoryHistoryService.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
    private SystemOrgService systemOrgService;

	@Value("#{sql['queryInventoryLatest']}")
	private String queryInventoryLatest;
	@Value("#{sql['updateInventoryLatest']}")
	private String updateInventoryLatest;
	@Value("#{sql['insertInventoryHistory']}")
	private String insertInventoryHistory;
	@Value("#{sql['insertInventoryLatest']}")
	private String insertInventoryLatest;
	@Value("#{sql['queryMaxInventoryLatestId']}")
	private String queryMaxInventoryLatestId;
	
	@Autowired
	private InventoryHistoryService inventoryHistoryService;

	/**
	 * 当前库存页面
	 * 
	 * @param paging
	 * @param sessionUser
	 * @param codeId
     * @param containerCodeId
     * @param containerTypeId
     * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Paging pagingInventoryLatest(Paging paging, SystemUser sessionUser, String containerCodeId, String containerTypeId, String targetOrgId) {
		String sql = "select * from t_inventory_latest t where 1=1";
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(targetOrgId)) {
			sql += " and t.org_id = :targetOrgId ";
			paramMap.put("targetOrgId", targetOrgId);
		}else {
			//如果没有仓库名称查询条件，那么默认是当前公司和子公司的数据可以看
			sql += " and t.org_id in ( "+sessionUser.getFilialeSystemOrgIds()+" ) ";
		}
		if (StringUtils.isNotBlank(containerCodeId)) {
			sql += " and t.container_code = :containerCodeId ";
			paramMap.put("containerCodeId", containerCodeId);
		}
		if (StringUtils.isNotBlank(containerTypeId)) {
			sql += " and t.container_type_id = :containerTypeId ";
			paramMap.put("containerTypeId", containerTypeId);
		}
		sql += " order by create_time desc ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, InventoryHistory.class);
		return paging;
	}
	/**
	 * 【库存历史】列表查询
	 * @param paging
	 * @param sessionUser
	 * @param containerCodeId
	 * @param containerTypeId
	 * @param targetOrgId
	 * @return
	 */
	public Paging pagingInventoryHistory(Paging paging, SystemUser sessionUser, String containerCodeId,
			String containerTypeId, String targetOrgId) {
		String sql = "select * from t_inventory_history t where 1=1";
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(targetOrgId)) {
			sql += " and t.org_id = :targetOrgId ";
			paramMap.put("targetOrgId", targetOrgId);
		}else {
			//如果没有仓库名称查询条件，那么默认是当前公司和子公司的数据可以看
			sql += " and t.org_id in ( "+sessionUser.getFilialeSystemOrgIds()+" ) ";
		}
		if (StringUtils.isNotBlank(containerCodeId)) {
			sql += " and t.container_code = :containerCodeId ";
			paramMap.put("containerCodeId", containerCodeId);
		}
		if (StringUtils.isNotBlank(containerTypeId)) {
			sql += " and t.container_type_id = :containerTypeId ";
			paramMap.put("containerTypeId", containerTypeId);
		}
		sql += " order by create_time desc ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, InventoryHistory.class);
		return paging;
	}
	
	/**
	 * 根据orgId和器具代码获取这个仓库最新的某个器具代码的库存数量对象
	 * @param orgId
	 * @param containerCode
	 * @return
	 */
	public InventoryHistory queryInventoryLatest(String orgId, String containerCode) {
		List<InventoryHistory> list = jdbcTemplate.query(queryInventoryLatest, new BeanPropertyRowMapper(InventoryHistory.class), orgId,containerCode);
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}

	/**<pre>
	 * 对当前仓库的某个器具代码做库存数量的收货发货（加法、减法）
	 * 注意：此方法的synchronized 和参数的InventoryHistory类（已重写此类的equals和hashcode方法）一起作用于并发锁，不可去掉。
	 * </pre>
	 * @param inventoryHistory
	 * @return AjaxBean
	 */
	public synchronized AjaxBean updateInventoryLatest(InventoryHistory inventoryHistory) {
		LOG.info("更新库存--开始。单号["+inventoryHistory.getOrderCode()+"]仓库：[" + inventoryHistory.getOrgId() + "]["+inventoryHistory.getOrgName()+"]器具代码：["+inventoryHistory.getContainerCode()+"]+["+inventoryHistory.getReceiveNumber()+"]-["+inventoryHistory.getSendNumber()+"]备注：["+inventoryHistory.getRemark()+"]");
		String real_orderCode = inventoryHistory.getOrderCode();
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(inventoryHistory.getOrderCode());
		if(null != circulateOrder && SysConstants.STRING_1.equals(circulateOrder.getIsManualOrder()))
		{
			real_orderCode +=SysConstants.MANUAL_ORDER;
		}
		ContainerCode code = containerCodeService.queryByContainerCode(inventoryHistory.getContainerCode());
		List<InventoryHistory> list = jdbcTemplate.query(queryInventoryLatest, new BeanPropertyRowMapper(InventoryHistory.class), inventoryHistory.getOrgId(),inventoryHistory.getContainerCode());
		if(CollectionUtils.isEmpty(list)) {
			//如果是发货，则没有库存数量就要终止发货
			if(!inventoryHistory.getIsReceive()) {
				AjaxBean ajaxBean = AjaxBean.FAILURE();
				ajaxBean.setMsg("["+inventoryHistory.getOrgName()+"]仓库的["+inventoryHistory.getContainerCode()+"]器具代码库存为0，流转单["+inventoryHistory.getOrderCode()+"]发货失败！");
				return ajaxBean;
			}
			//如果当前仓库的这种器具代码没有历史库存，则初始化为0
			//组装insert对象，插入表t_inventory_history和t_inventory_latest
			InventoryHistory ih = new InventoryHistory();
			ih.setOrgId(inventoryHistory.getOrgId());
			ih.setOrgName(inventoryHistory.getOrgName());
			ih.setOrderCode(real_orderCode);
			ih.setContainerCode(code.getContainerCode());
			ih.setContainerName(code.getContainerName());
			ih.setContainerTypeId(code.getContainerTypeId());
			ih.setContainerTypeName(code.getContainerTypeName());
			ih.setCreateTime(DateUtil.currentTimestampLess2Second()); //初始化为0的时间尽量不要跟下面的Insert语句的时间一致，以免排序造成错觉
			ih.setCreateAccount(inventoryHistory.getCreateAccount());
			ih.setCreateRealName(inventoryHistory.getCreateRealName());
			ih.setSendNumber(SysConstants.INTEGER_0); //初始化发货数量0
			ih.setReceiveNumber(SysConstants.INTEGER_0); //初始化收货数量0
			ih.setInOrgNumber(SysConstants.INTEGER_0); //初始化库存数量0
			ih.setRemark("初始化库存为0");
			//设置自增主键的值
			BigInteger inventoryLatestId = jdbcTemplate.queryForObject(queryMaxInventoryLatestId, BigInteger.class);
			ih.setInventoryLatestId(inventoryLatestId);
			//更新表t_inventory_latest
			namedJdbcTemplate.update(insertInventoryLatest, new BeanPropertySqlParameterSource(ih));
			//把新记录插入历史表t_inventory_history
			namedJdbcTemplate.update(insertInventoryHistory, new BeanPropertySqlParameterSource(ih));
			//获取最新值
			list = jdbcTemplate.query(queryInventoryLatest, new BeanPropertyRowMapper(InventoryHistory.class), inventoryHistory.getOrgId(),inventoryHistory.getContainerCode());
		}
		//组装update对象
		InventoryHistory oldLatest = list.get(0);

		inventoryHistory.setContainerName(code.getContainerName());
		inventoryHistory.setContainerTypeId(code.getContainerTypeId());
		inventoryHistory.setContainerTypeName(code.getContainerTypeName());
		inventoryHistory.setInventoryLatestId(oldLatest.getInventoryLatestId()); //update主键
		inventoryHistory.setCreateTime(DateUtil.currentTimestamp());
		
		//收货
		if(inventoryHistory.getIsReceive()) {
			//新的在库数量 = 旧的在库数量 + 收货数量
			Integer newInOrgNumber = new BigDecimal(oldLatest.getInOrgNumber()).add(new BigDecimal(inventoryHistory.getReceiveNumber())).intValue();
			inventoryHistory.setInOrgNumber(newInOrgNumber);
			inventoryHistory.setSendNumber(SysConstants.INTEGER_0); //发货数量
		}else { //发货
			//如果库存数量不足，则终止发货
			if(oldLatest.getInOrgNumber() < inventoryHistory.getSendNumber()) {
				AjaxBean ajaxBean = AjaxBean.FAILURE();
				ajaxBean.setMsg("["+inventoryHistory.getOrgName()+"]仓库中["+inventoryHistory.getContainerCode()+"]器具代码库存数量为["+oldLatest.getInOrgNumber()+"]，实际需要发货["+inventoryHistory.getSendNumber()+"]，库存不足，发货失败！");
				return ajaxBean;
			}
			//新的在库数量 = 旧的在库数量  - 收货数量
			Integer newInOrgNumber = new BigDecimal(oldLatest.getInOrgNumber()).subtract(new BigDecimal(inventoryHistory.getSendNumber())).intValue();
			inventoryHistory.setInOrgNumber(newInOrgNumber);
			inventoryHistory.setReceiveNumber(SysConstants.INTEGER_0); //收货数量
		}
		inventoryHistory.setOrderCode(real_orderCode);
		//更新表t_inventory_latest
		namedJdbcTemplate.update(updateInventoryLatest, new BeanPropertySqlParameterSource(inventoryHistory));
		//把新记录插入历史表t_inventory_history
		namedJdbcTemplate.update(insertInventoryHistory, new BeanPropertySqlParameterSource(inventoryHistory));
		LOG.info("更新库存--完毕。单号["+inventoryHistory.getOrderCode()+"]仓库：[" + inventoryHistory.getOrgId() + "]["+inventoryHistory.getOrgName()+"]器具代码：["+inventoryHistory.getContainerCode()+"]+["+inventoryHistory.getReceiveNumber()+"]-["+inventoryHistory.getSendNumber()+"]=["+inventoryHistory.getInOrgNumber()+"]备注：["+inventoryHistory.getRemark()+"]");
		return AjaxBean.SUCCESS();
	}
	/**
	 * 组装InventoryLatest对象，更新表t_inventory_latest，新记录插入历史表t_inventory_history
	 * @param epcIds 值格式为：'123','456','789'
	 */
	public void buildInventoryLatest(SystemUser sessionUser,String orderCode,String deviceRemark, String epcIds) {
		//更新收货仓库的器具代码库存数量，更新表t_inventory_history, t_inventory_latest
		if(epcIds.endsWith(",")) {
			epcIds = epcIds.substring(0, epcIds.length() - 1 );
		}
		//查出此次收货中的器具代码的数量
		String sumSql = "select container_code ,count(receive_number) receive_number from t_circulate_detail where order_code ='" +orderCode+ "' and epc_id in (" + epcIds + ") group by container_code";
		List<InventoryHistory> list = jdbcTemplate.query(sumSql,new BeanPropertyRowMapper(InventoryHistory.class));
		for (InventoryHistory ih : list) {
			//对当前仓库的某个器具代码做库存数量的加法（收货）
			ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
			ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
			ih.setOrderCode(orderCode); //流转单号
			ih.setRemark(deviceRemark); //值有：web、app、手持机、door
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			ih.setIsReceive(true); // true收货，false发货
			this.updateInventoryLatest(ih);
		}
	}
	/**
	 * [库存历史]页面，库存调整
	 * @param ajaxBean
	 * @param sessionUser
	 * @param orgId 仓库
	 * @param containerCode 器具代码
	 * @param adjustNumber 加减库存数量
	 * @param adjustRadio 1：加数量   0：减数量
	 * @param remark 加减库存备注
	 * @return
	 */
	public AjaxBean adjustNumber(AjaxBean ajaxBean, SystemUser sessionUser, String orgId, String containerCode,
			Integer adjustNumber, Integer adjustRadio, String remark) {
		//增加此器具代码库存数量: 对当前仓库的某个器具代码做库存数量的加法（收货）
		InventoryHistory ih = new InventoryHistory();
		ih.setContainerCode(containerCode); //器具代码
		ih.setOrgId(orgId); //仓库ID
		ih.setOrgName(systemOrgService.findById(orgId).getOrgName()); //仓库名称
		ih.setRemark(remark);
		ih.setCreateAccount(sessionUser.getAccount());
		ih.setCreateRealName(sessionUser.getRealName());
		//  0：减数量  == false发货
		if(SysConstants.INTEGER_0 == adjustRadio) {
			ih.setIsReceive(false); // false发货
			ih.setSendNumber(adjustNumber); //此次发货数量
		}else {
			ih.setIsReceive(true); // true收货
			ih.setReceiveNumber(adjustNumber); //此次收货数量
		}
		LOG.info("更新库存--[库存历史]页面，库存调整。仓库：[" + ih.getOrgId() + "]["+ih.getOrgName()+"]器具代码：["+ih.getContainerCode()+"]+["+ih.getReceiveNumber()+"]-["+ih.getSendNumber()+"]=["+ih.getInOrgNumber()+"]备注：["+ih.getRemark()+"]");
		return inventoryHistoryService.updateInventoryLatest(ih);
	}
	/**
	 * 
	 * @param file 上传的excel,xlsx格式
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean batchInitInOrgNumber(File file, SystemUser sessionUser) {
		// 数据校验
		Map<String,ContainerCode> containerCodeMap  = new HashMap();
		Map<String,Integer> epcIdMap = new HashMap<>();
		Long begin = System.currentTimeMillis();
		LOG.info("---batchInitInOrgNumber  begin="+begin);
		AjaxBean ajaxBean = validExcelData(file.getPath(), sessionUser,containerCodeMap,epcIdMap);
		LOG.info("---batchInitInOrgNumber   validExcelData="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
		//如果校验失败，直接返回
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		
		List<Map<Integer, String>> maps = ajaxBean.getList();
		List<InventoryHistory> ihHistoryList = new ArrayList<InventoryHistory>();
		List<InventoryHistory> ihLatestList2Insert = new ArrayList<InventoryHistory>();
		List<InventoryHistory> ihLatestList2Update = new ArrayList<InventoryHistory>();
		Timestamp now = DateUtil.currentTimestamp();
		ContainerCode containerCode = null;
		
		for (Map<Integer, String> map : maps) {
			if(null == map){ //如果是空行，直接跳过
				continue;
			}
			containerCode = containerCodeMap.get(map.get(0));
			
			//初始化器具代码的库存数量
			InventoryHistory ih = new InventoryHistory();
			ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
			ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
			ih.setContainerCode(map.get(0)); //器具代码
			ih.setContainerName(containerCode.getContainerName()); //器具名称
			ih.setContainerTypeId(containerCode.getContainerTypeId());
			ih.setContainerTypeName(containerCode.getContainerTypeName());
			ih.setSendNumber(SysConstants.INTEGER_0); //此次发货数量
			ih.setReceiveNumber(SysConstants.INTEGER_0); //此次收货数量
			ih.setInOrgNumber(Integer.valueOf(map.get(1))); //在库数量
			ih.setRemark(SysConstants.INIT_IN_ORG_NUMBER);
			ih.setCreateTime(now);
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			
			//组装批量参数list
			InventoryHistory inventoryLatest = queryInventoryLatest(sessionUser.getCurrentSystemOrg().getOrgId(), map.get(0));
			//如果当前仓库的此类器具代码库存数据已存在，则更新
			if(null != inventoryLatest) {
				ih.setInventoryLatestId(inventoryLatest.getInventoryLatestId());
				ihLatestList2Update.add(ih); //更新: t_inventory_latest
			}else { //否则，则新增此类器具代码的库存数量到当前仓库
				ih.setInventoryLatestId(jdbcTemplate.queryForObject(queryMaxInventoryLatestId, BigInteger.class));
				ihLatestList2Insert.add(ih); //新增: t_inventory_latest
			}
			ihHistoryList.add(ih); //新增：t_inventory_history
			
			//如果达到批量插入数量，批量新增器具
			if (CollectionUtils.size(ihHistoryList) >= SysConstants.MAX_INSERT_NUMBER) {
				namedJdbcTemplate.batchUpdate(updateInventoryLatest, 
						SqlParameterSourceUtils.createBatch(ihLatestList2Update.toArray())); //更新: t_inventory_latest
				namedJdbcTemplate.batchUpdate(insertInventoryLatest, 
						SqlParameterSourceUtils.createBatch(ihLatestList2Insert.toArray())); //新增: t_inventory_latest
				namedJdbcTemplate.batchUpdate(insertInventoryHistory, 
						SqlParameterSourceUtils.createBatch(ihHistoryList.toArray())); //新增: t_inventory_history
				//清空列表
				ihLatestList2Update.clear();
				ihLatestList2Insert.clear();
				ihHistoryList.clear();
			}
		}
		//如果还有剩余数据未批量更新
		if (CollectionUtils.size(ihHistoryList) > SysConstants.INTEGER_0) {
			namedJdbcTemplate.batchUpdate(updateInventoryLatest, 
					SqlParameterSourceUtils.createBatch(ihLatestList2Update.toArray())); //更新: t_inventory_latest
			namedJdbcTemplate.batchUpdate(insertInventoryLatest, 
					SqlParameterSourceUtils.createBatch(ihLatestList2Insert.toArray())); //新增: t_inventory_latest
			namedJdbcTemplate.batchUpdate(insertInventoryHistory, 
					SqlParameterSourceUtils.createBatch(ihHistoryList.toArray())); //新增: t_inventory_history
			//清空列表
			ihLatestList2Update.clear();
			ihLatestList2Insert.clear();
			ihHistoryList.clear();
		}
		return ajaxBean;
	
	}


	/**
	 * excel批量导入数据检测
	 * @param path
	 * @param systemUser
	 * @return
	 */
	private AjaxBean validExcelData(String path, SystemUser systemUser,Map<String,ContainerCode> containerCodeMap,Map<String,Integer> epcIdMap) {
		AjaxBean ajaxBean = new AjaxBean();
		List<String> errMsgList = new ArrayList<String>();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,8);
		} catch (Exception e) {
			LOG.error(StatusCode.STATUS_402_MSG, e);
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
		int row = 1;
		for (Map<Integer, String> map : mapList) {
			row++;
			if(null == map){  //如果是空行，直接跳过
				continue;
			}
			String containerCode = map.get(0);
			if (StringUtils.isEmpty(containerCode)) {
				errMsgList.add("第"+row+"行，器具代码不能为空。");
			} else {
				//ypw 性能优化
				ContainerCode findCode = null;
				if(!containerCodeMap.containsKey(containerCode))
				{
					findCode = containerCodeService.queryByContainerCode(containerCode);
					containerCodeMap.put(containerCode,findCode);
				}
				else {
					findCode = containerCodeMap.get(containerCode);
				}
				//ypw 性能优化 END
				if (null == findCode) {
					errMsgList.add("第" + row + "行，器具代码[" + containerCode + "]不存在，请核查。");
				} else if (findCode.getIsActive().equals(SysConstants.INTEGER_1)) {
					errMsgList.add("第" + row + "行，器具代码[" + containerCode + "]已禁用，请修改为其它器具代码。");
				}
			}

			String inOrgNumber = map.get(1);
			if (StringUtils.isBlank(inOrgNumber)) {
				errMsgList.add("第"+row+"行，在库数量不能为空。");
			} else if (!(inOrgNumber.matches(SysConstants.REGEX_IN_ORG_NUM))) {
				errMsgList.add("第"+row+"行，在库数量必须是数字，且长度小于等于8位。");
			}
			if(errMsgList.size() > 10) {
				break;
			}
		}

		if (CollectionUtils.isNotEmpty(errMsgList)) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setList(errMsgList);
		} else {
			ajaxBean.setList(mapList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		}
		return ajaxBean;
	}

}