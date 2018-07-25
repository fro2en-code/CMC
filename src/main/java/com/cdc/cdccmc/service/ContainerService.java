package com.cdc.cdccmc.service;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cdc.cdccmc.domain.dto.ContainerForDoorScanDto;
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
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.container.ContainerOutmode;
import com.cdc.cdccmc.domain.container.ContainerType;
import com.cdc.cdccmc.domain.dto.ContainerDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.AreaService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/**
 * 器具列表
 * @author ZhuWen
 * @date 2017-12-29
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private AreaService areaService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;

	@Value("#{sql['containerInsert']}")
	private String containerInsert;
	@Value("#{sql['containerBatchInsert']}")
	private String containerBatchInsert;
	@Value("#{sql['insertCirculateHistory']}")
	private String insertCirculateHistory;
	@Value("#{sql['insertCirculateLatest']}")
	private String insertCirculateLatest;
	@Value("#{sql['deleteCirculateLatest']}")
	private String deleteCirculateLatest;
	@Value("#{sql['pagingContainerCurrentOrg']}")
	private String pagingContainerCurrentOrg;
	@Value("#{sql['updateContainer']}")
	private String updateContainer;
	@Value("#{sql['updateContaierLastOrgId']}")
	private String updateContaierLastOrgId;
	@Value("#{sql['queryContainerForDoorScanDtoByEpcIdS']}")
	private String queryContainerForDoorScanDtoByEpcIdS;
	@Value("#{sql['queryContainerForDoorScanDtoByEpcIdS2']}")
	private String queryContainerForDoorScanDtoByEpcIdS2;
	@Value("#{sql['queryContainerGroupForDoorScanDtoByEpcIdS']}")
	private String queryContainerGroupForDoorScanDtoByEpcIdS;
	@Value("#{sql['queryContainerByGroupIdS']}")
	private String queryContainerByGroupIdS;
	@Value("#{sql['updateContainerBelongOrgId']}")
	private String updateContainerBelongOrgId;

	/**
	 * 器具列表和过时器具列表
	 * @param paging
	 * @param container
	 * @param orgId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Paging pagingContainer(Paging paging, Container container, String orgId, String startDate, String endDate) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_container where ( ");
		//隶属仓库或者流转仓库任意一个有值，则是and关系
		if(StringUtils.isNotBlank(container.getLastOrgId()) || StringUtils.isNotBlank(container.getBelongOrgId())) {
			if(StringUtils.isNotBlank(container.getLastOrgId())) {
				sql.append(" last_org_id = '" + container.getLastOrgId() + "' ");
			}
			if(StringUtils.isNotBlank(container.getLastOrgId()) && StringUtils.isNotBlank(container.getBelongOrgId())) {
				sql.append(" and ");
			}
			if(StringUtils.isNotBlank(container.getBelongOrgId())) {
				sql.append(" belong_org_id = '" + container.getBelongOrgId() + "' ");
			}
		}else {
			sql.append(" last_org_id in (" + orgId + ")  or belong_org_id in (" + orgId + ") ");
		}
		sql.append(")");
		if (StringUtils.isNotBlank(container.getEpcId())) {
			sql.append(" and epc_id like :epcId ");
			paramMap.put("epcId","%"+container.getEpcId()+"%");
		}
		if (StringUtils.isNotBlank(container.getContainerTypeId())) {
			sql.append(" and container_type_id = :containerTypeId ");
			paramMap.put("containerTypeId", container.getContainerTypeId());
		}
		if (StringUtils.isNotBlank(container.getContainerCode())){
			sql.append(" and container_code = :containerCode ");
			paramMap.put("containerCode", container.getContainerCode());
		}
		//追加过时查询条件
		sql.append(" and is_outmode= :isOutmode ");
		paramMap.put("isOutmode", container.getIsOutmode());
		
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and modify_time >= :startDate  ");
			paramMap.put("startDate", startDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and modify_time <= :endDate ");
			paramMap.put("endDate", endDate);
		}
		if(container.getIsOutmode()==1){
			sql.append(" order by modify_time desc ");
		}else {
			sql.append(" order by create_time desc ");
		}
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, Container.class);
	}

	/**
	 * 新增器具
	 * @param sessionUser 
	 * @param con
	 * @return
	 */
	public AjaxBean addContainer(SystemUser sessionUser, Container con) {
        //检测containerType是否存在,不存在那么新增一条
		ContainerCode findCode = containerCodeService.queryByContainerCode(con.getContainerCode());
		con.setContainerTypeId(findCode.getContainerTypeId());
		con.setContainerTypeName(findCode.getContainerTypeName());
		con.setContainerName(findCode.getContainerName());
		con.setContainerSpecification(findCode.getContainerSpecification());
		con.setCreateAccount(sessionUser.getAccount());
		con.setCreateRealName(sessionUser.getRealName());
		con.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		con.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		con.setCreateTime(DateUtil.currentTimestamp());
		con.setBelongOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		con.setBelongOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		con.setLastOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		con.setLastOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		con.setIsOutmode(SysConstants.INTEGER_0); //'是否过时，0未过时，1过时',default 0
		con.setVersion(SysConstants.INTEGER_0);
		
		int result = namedJdbcTemplate.update(containerInsert, new BeanPropertySqlParameterSource(con));
		if (result == 0) { //如果器具新增失败
			return AjaxBean.FAILURE();
		}
		Area defaultArea = areaService.queryDefaultArea();
		//如果器具新增成功，则增加新增器具的流转信息
		Circulate circulate = new Circulate();
		circulate.setCirculateState(CirculateState.ON_ORG.getCode().toString());
		circulate.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
		circulate.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		circulate.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		circulate.setAreaId(defaultArea.getAreaId());
		circulate.setAreaName(defaultArea.getAreaName());
		circulate.setCreateAccount(sessionUser.getAccount());
		circulate.setCreateRealName(sessionUser.getRealName());
		circulate.setCirculateHistoryId(UUIDUtil.creatUUID());
		circulate.setEpcId(con.getEpcId());
		circulate.setContainerCode(con.getContainerCode());
		circulate.setContainerTypeId(con.getContainerTypeId());
		circulate.setContainerTypeName(con.getContainerTypeName());
		circulate.setCreateTime(DateUtil.currentTimestamp());
		circulate.setRemark(SysConstants.CIRCULATE_REMARK_ADD_CONTAINER + con.getEpcId());
		// 增加历史流转记录 T_CIRCULATE_HISTORY，在库
		namedJdbcTemplate.update(insertCirculateHistory, new BeanPropertySqlParameterSource(circulate));
		// 删除器具最新流转记录 T_CIRCULATE_LATEST
		jdbcTemplate.update(deleteCirculateLatest, con.getEpcId());
		// 新增器具最新流转记录 T_CIRCULATE_LATEST，在库
		namedJdbcTemplate.update(insertCirculateLatest, new BeanPropertySqlParameterSource(circulate));
		//增加此器具代码库存数量: 对当前仓库的某个器具代码做库存数量的加法（收货）
		InventoryHistory ih = new InventoryHistory();
		ih.setContainerCode(con.getContainerCode()); //器具代码
		ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
		ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
		ih.setReceiveNumber(SysConstants.INTEGER_1); //此次收货数量
		ih.setOrderCode(StringUtils.EMPTY); //流转单号
		ih.setRemark(SysConstants.CIRCULATE_REMARK_ADD_CONTAINER + con.getEpcId());
		ih.setCreateAccount(sessionUser.getAccount());
		ih.setCreateRealName(sessionUser.getRealName());
		ih.setIsReceive(true); // true收货，false发货
		inventoryHistoryService.updateInventoryLatest(ih);
		return AjaxBean.SUCCESS();
	}

	/**
	 * 根据container_code 和last_org_id 查询当前数据是否存在
	 * @param epcId
	 * @return
	 */
	public Container findContainerByEpcId(String epcId) {
		String sql = "select * from t_container where epc_id= ? ";
		List<Container> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Container.class), epcId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 按epcId列表查询器具
	 * @param groupIds  例'1','2'
	 * @return
	 */
	public List<ContainerForDoorScanDto> queryContainerByGroupIdS(String groupIds)
	{
		StringBuffer sql = new StringBuffer(queryContainerByGroupIdS);
		sql.append(groupIds).append(SysConstants.YKH).append(SysConstants.YKH);
		//.append(queryContainerForDoorScanDtoByEpcIdS2).append(epcIds)
		//.append(SysConstants.YKH).append(SysConstants.YKH);
		//LOG.info("---  queryContainerByEpcIdS  SQL="+sql);
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ContainerForDoorScanDto.class));
	}

	/**
	 * 按epcId列表查询器具
	 * @param epcIds  例'1','2'
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ContainerForDoorScanDto> queryContainerByEpcIdS(String epcIds)
	{
		StringBuffer sql = new StringBuffer(queryContainerForDoorScanDtoByEpcIdS);
		sql.append(epcIds).append(SysConstants.YKH).append(SysConstants.YKH).append(SysConstants.STR_D);
				//.append(queryContainerForDoorScanDtoByEpcIdS2).append(epcIds)
				//.append(SysConstants.YKH).append(SysConstants.YKH);
		//LOG.info("---  queryContainerByEpcIdS  SQL="+sql);
		List<ContainerForDoorScanDto> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ContainerForDoorScanDto.class));
		return list;
	}

	/**
	 * 按epcId列表查询器具中的托盘
	 * @param epcIds  例'1','2'
	 * @return
	 */
	public List<ContainerForDoorScanDto> queryContainerGroupByEpcIdS(String epcIds)
	{
		StringBuffer sql = new StringBuffer(queryContainerGroupForDoorScanDtoByEpcIdS);
		sql.append(epcIds).append(SysConstants.YKH).append(SysConstants.YKH).append(SysConstants.STR_D);
		LOG.info("---  queryContainerGroupByEpcIdS  SQL="+sql);
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(ContainerForDoorScanDto.class));
	}

	/**
	 * 更新Container对象,
	 * @param ajaxBean 
	 * @param container
	 * @return
	 */
	public AjaxBean updateContainer(SystemUser sessionUser, AjaxBean ajaxBean, Container container) {
		Integer result = namedJdbcTemplate.update(updateContainer, new BeanPropertySqlParameterSource(container));
		if (result == 0) { //如果未更新成功
			ajaxBean.setStatus(StatusCode.STATUS_304);
			ajaxBean.setMsg(StatusCode.STATUS_304_MSG);
			return ajaxBean;
		}
		return ajaxBean;
	}
	
	/**
	 * 更新Container对象(for盘点新增入库)
	 * @param ajaxBean 
	 * @param container
	 * @return
	 */
	public int updateContaierLastOrgId(SystemUser sessionUser,String epcId,String lastOrgId,String lastOrgName) {
		int result = jdbcTemplate.update(updateContaierLastOrgId, lastOrgId,lastOrgName,epcId);
		return result;
	}

	/**
	 * 器具过时
	 * @param ajaxBean 
	 * @param sessionUser 
	 * @param container ecpId contractNumber 合同号 receiveNumber 领用单号
	 * @return
	 */
	public AjaxBean addOutMode(AjaxBean ajaxBean, SystemUser sessionUser, Container container) {
		Container findContainer = this.findContainerByEpcId(container.getEpcId());
		if(findContainer.getIsOutmode() == SysConstants.INTEGER_1){ //如果该器具已经过时
            ajaxBean.setStatus(StatusCode.STATUS_324);
            ajaxBean.setMsg("epc编号["+container.getEpcId()+"]"+StatusCode.STATUS_324_MSG);
            return ajaxBean;
		}
		String sql = "update t_container set is_outmode=1,contract_number=:contractNumber,receive_number=:receiveNumber,modify_time=sysdate(),modify_account=:modifyAcount,modify_real_name=:modifyRealName ,version="
				+ " (:version +1) where epc_id=:ecpId  AND  version=:version";
		ConcurrentHashMap map = new ConcurrentHashMap();
		map.put("ecpId", container.getEpcId());
		map.put("contractNumber", container.getContractNumber()); //合同号
		map.put("receiveNumber", container.getReceiveNumber()); //领用单号
		map.put("modifyAcount", sessionUser.getAccount());
		map.put("modifyRealName", sessionUser.getRealName());
		map.put("version", container.getVersion());
		Integer result = namedJdbcTemplate.update(sql, map);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 根据epcId查询器具信息
	 * @param epcId
	 * @return
	 */
	public Container getContainerByEpcId(String epcId) {
		String sql = "select * from t_container where  epc_id= ?";
		List<Container> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Container.class), epcId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 器具代码批量导入
	 * @param file
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean batchUpload(File file, SystemUser sessionUser) {
		// 数据校验
		Map<String,ContainerCode> containerCodeMap  = new HashMap();
		Map<String,Integer> epcIdMap = new HashMap<>();
		Long begin = System.currentTimeMillis();
		LOG.info("---batchUploadContainer.batchUpload  begin="+begin);
		AjaxBean ajaxBean = validExcelData(file.getPath(), sessionUser,containerCodeMap,epcIdMap);
		LOG.info("---batchUploadContainer.batchUpload   validExcelData="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
		//TODO YPW  EDIT 把validExcelData弄完了 继续往下看
		if (ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		String orgId = sessionUser.getCurrentSystemOrg().getOrgId();
		String orgName = sessionUser.getCurrentSystemOrg().getOrgName();
		String account = sessionUser.getAccount();
		String realName = sessionUser.getRealName();
		Area defaultArea = areaService.queryDefaultArea();
		List<Map<Integer, String>> maps = ajaxBean.getList();
		List<Container> containerList = new ArrayList<Container>();
		//LinkedList<Container> containerList = new LinkedList<Container>();
		List<Circulate> batchParam = new ArrayList<Circulate>();
		Timestamp time1 = DateUtil.currentTimestamp();
		Container container = null;
		ContainerCode containerCode = null;
		for (Map<Integer, String> map : maps) {
			if(null == map){ //如果是空行，直接跳过
				continue;
			}
			container = new Container();
			container.setEpcType(map.get(0));
			container.setEpcId(map.get(1));
			container.setPrintCode(map.get(2));
			container.setContainerCode(map.get(3));
			containerCode = containerCodeMap.get(map.get(3));
			container.setContainerName(containerCode.getContainerName());
			container.setContainerTypeId(containerCode.getContainerTypeId());
			container.setContainerTypeName(containerCode.getContainerTypeName());
			container.setIsTray(containerCode.getIsTray());
			container.setContainerSpecification(map.get(4));
			container.setContainerTexture(map.get(5));
			if(SysConstants.YES.equals(map.get(6))){ //是否单独成托，0不是，1是
				container.setIsAloneGroup(SysConstants.INTEGER_1);
			}else{
				container.setIsAloneGroup(SysConstants.INTEGER_0);
			}
			container.setBelongOrgId(orgId);
			container.setBelongOrgName(orgName);
			container.setLastOrgId(orgId);
			container.setLastOrgName(orgName);
			container.setCreateAccount(account);
			container.setCreateRealName(realName);
			container.setCreateOrgId(orgId);
			container.setCreateOrgName(orgName);
			containerList.add(container);
			//如果达到批量插入数量，批量新增器具
			if (containerList.size() >= SysConstants.MAX_INSERT_NUMBER) {
				this.batchInsertContainer(sessionUser,containerList);
				logService.addLogAccount(sessionUser, "批量导入器具"+(containerList.size())+"条");
				containerList.clear();
			}
			//批量新增器具流转历史记录、器具最新流转记录
			Circulate circulate = new Circulate();
			circulate.setCirculateState(CirculateState.ON_ORG.getCode().toString());
			circulate.setCirculateStateName(CirculateState.ON_ORG.getCirculate());
			circulate.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			circulate.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			circulate.setAreaId(defaultArea.getAreaId());
			circulate.setAreaName(defaultArea.getAreaName());
			circulate.setCreateAccount(sessionUser.getAccount());
			circulate.setCreateRealName(sessionUser.getRealName());
			circulate.setCirculateHistoryId(UUIDUtil.creatUUID());
			circulate.setEpcId(container.getEpcId());
			circulate.setContainerCode(container.getContainerCode());
			circulate.setContainerTypeId(container.getContainerTypeId());
			circulate.setContainerTypeName(container.getContainerTypeName());
			circulate.setCreateTime(time1);
			circulate.setRemark(SysConstants.CIRCULATE_REMARK_BATCH_ADD_CONTAINER);
			batchParam.add(circulate);

			//删除旧的器具最新流转记录  T_CIRCULATE_LATEST
			jdbcTemplate.update(deleteCirculateLatest, container.getEpcId());
			
			//如果达到批量插入数量，批量新增器具
			if (batchParam.size() >= SysConstants.MAX_INSERT_NUMBER) {
				SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(batchParam.toArray());
				namedJdbcTemplate.batchUpdate(insertCirculateHistory, params); //在库
				namedJdbcTemplate.batchUpdate(insertCirculateLatest, params); //在库
				batchParam.clear();
			}
		}
		
		//把此次批量导入的新器具加入当前仓库的器具代码的库存数量
		buildInventoryHistoryForBatchUpload(sessionUser,containerList);
		
		//如果有未尽器具，再次批量插入
		if (containerList.size() > SysConstants.INTEGER_0) {
			this.batchInsertContainer(sessionUser,containerList);
			logService.addLogAccount(sessionUser, "批量导入器具"+(containerList.size())+"条");
			containerList = null;
		}
		//如果有未尽器具，再次批量插入
		if (CollectionUtils.isNotEmpty(batchParam)) {
			SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(batchParam.toArray());
			namedJdbcTemplate.batchUpdate(insertCirculateHistory, params); //在库
			namedJdbcTemplate.batchUpdate(insertCirculateLatest, params); //在库
			batchParam = null;
		}
		return ajaxBean;
	}
	/**
	 * 分别为每种器具代码增加库存数量
	 * @param sessionUser
	 * @param containerList
	 */
	private void buildInventoryHistoryForBatchUpload(SystemUser sessionUser,List<Container> containerList) {
		//根据器具代码分组，获得器具代码的统计数量,String=器具代码，Integer=器具个数
		ConcurrentMap<String,Integer> map = new ConcurrentHashMap<String,Integer>(); 
		for (Container con : containerList) {
			String containerCode = con.getContainerCode();
			if(map.containsKey(containerCode)) {
				map.put(containerCode, map.get(containerCode) + 1 );
			}else {
				map.put(containerCode, SysConstants.INTEGER_1);
			}
		}
		//分别为每种器具代码增加库存数量
		Iterator<Entry<String, Integer>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Integer> entry = it.next();
			String containerCode = entry.getKey();
			Integer receiveNumber = entry.getValue();
			//对当前仓库的某个器具代码做库存数量的加法（收货）
			InventoryHistory ih = new InventoryHistory();
			ih.setContainerCode(containerCode); //器具代码
			ih.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId()); //收货仓库ID
			ih.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName()); //收货仓库名称
			ih.setReceiveNumber(receiveNumber); //此次收货数量
			ih.setOrderCode(StringUtils.EMPTY); //流转单号
			ih.setRemark("器具代码批量导入");
			ih.setCreateAccount(sessionUser.getAccount());
			ih.setCreateRealName(sessionUser.getRealName());
			ih.setIsReceive(true); // true收货，false发货
			inventoryHistoryService.updateInventoryLatest(ih);
		}
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
			logService.addLogError(systemUser, e, StatusCode.STATUS_402_MSG, null);
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
			String epcType = map.get(0);
			if (StringUtils.isEmpty(epcType)) {
				errMsgList.add("第"+row+"行，EPC类型不能为空。");
			} else if (epcType.length() > 50) {
				errMsgList.add("第"+row+"行，EPC类型["+epcType+"]长度超长。");
			}

			String epcId = map.get(1);
			if (StringUtils.isEmpty(epcId)) {
				errMsgList.add("第"+row+"行，EPC编号不能为空。");
			} else if (epcId.length() > 50) {
				errMsgList.add("第"+row+"行，EPC编号["+epcId+"]长度超长。");
			}else
			{
				if(epcIdMap.containsKey(epcId))
				{
					errMsgList.add("第"+row+"行，EPC编号["+epcId+"]与excel内第"+epcIdMap.get(epcId)+"行重复，请核查。");
				}
				else
				{
					epcIdMap.put(epcId,Integer.valueOf(row));
					Container findContainer = findContainerByEpcId(epcId);
					if(null != findContainer){
						errMsgList.add("第"+row+"行，EPC编号["+epcId+"]在仓库["+findContainer.getBelongOrgName()+"]中已存在，请核查。");
					}
				}
				/* YPW 性能优化  这段放到上面map进行校验excel内部重复
				else{
					int row2 = 1;
	            	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
	            		if(null != map2 && map.get(1).equals(map2.get(1)) && row != row2){ //如果excel内发现重复EPC编号
	            			errMsgList.add("第"+row+"行，EPC编号["+epcId+"]在excel内重复，请核查。");
	            			break two;
	            		}
	            	}
				}
				//YPW 性能优化 END
				*/
			}

			String containerCode = map.get(3);
			if (StringUtils.isEmpty(containerCode)) {
				errMsgList.add("第"+row+"行，器具代码不能为空。");
			} else if (containerCode.length() > 32) {
				errMsgList.add("第"+row+"行，器具代码["+containerCode+"]长度超长。");
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

			String containerSpecification = map.get(4);
			if (StringUtils.isNotBlank(containerSpecification) && containerSpecification.length() > 50) {
				errMsgList.add("第"+row+"行，规格长度超长。");
			}
			String isAloneGroupStr = map.get(6);
			if (StringUtils.isBlank(isAloneGroupStr)) {
				errMsgList.add("第"+row+"行，是否单独成托不能为空。");
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

	/**
	 * 批量插入器具列表
	 * @param containerList
	 */
	public void batchInsertContainer(SystemUser sessionUser, List<Container> containerList) {
//		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(containerList.toArray());
//		namedJdbcTemplate.batchUpdate(containerInsert, params);
		/**/
		Timestamp now = new Timestamp(System.currentTimeMillis());
		jdbcTemplate.batchUpdate(containerBatchInsert, new BatchPreparedStatementSetter()
		{
			@Override
			public int getBatchSize()
			{
				return containerList.size();
			}
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException
			{
//				epc_id, container_name, epc_type, print_code, container_type_id, \
//				container_type_name, container_code, container_specification, container_texture, is_alone_group, belong_org_id,belong_org_name,last_org_id, \
//				last_org_name,create_time, create_account, create_real_name,create_org_id,create_org_name,version,is_tray
				ps.setString(1, containerList.get(i).getEpcId());
				ps.setString(2, containerList.get(i).getContainerName());
				ps.setString(3, containerList.get(i).getEpcType());
				ps.setString(4, containerList.get(i).getPrintCode());
				ps.setString(5, containerList.get(i).getContainerTypeId());
				ps.setString(6, containerList.get(i).getContainerTypeName());
				ps.setString(7, containerList.get(i).getContainerCode());
				ps.setString(8, containerList.get(i).getContainerSpecification());
				ps.setString(9, containerList.get(i).getContainerTexture());
				ps.setInt(10, containerList.get(i).getIsAloneGroup());
				ps.setString(11, containerList.get(i).getBelongOrgId());
				ps.setString(12, containerList.get(i).getBelongOrgName());
				ps.setString(13, containerList.get(i).getLastOrgId());
				ps.setString(14, containerList.get(i).getLastOrgName());
				ps.setTimestamp(15, now);
				ps.setString(16, containerList.get(i).getCreateAccount());
				ps.setString(17, containerList.get(i).getCreateRealName());
				ps.setString(18, containerList.get(i).getCreateOrgId());
				ps.setString(19, containerList.get(i).getCreateOrgName());
				ps.setInt(20, 0);
				ps.setInt(21, containerList.get(i).getIsTray());
			}
		});

	}

	/**
	 * 查器具不在流转单明细中的
	 * @param epcids
	 * @return
	 */
	public List<Container> queryContainerNotInCirculateDetail(String epcids)
	{
		StringBuffer sql = new StringBuffer("select * from t_container c LEFT JOIN (select distinct(epc_id) epcid2 from t_circulate_detail where epc_id in(");
			sql.append(epcids).append(") ) AS t  ON (t.epcid2 = c.epc_id ) where c.epc_id in( ")
			.append(epcids).append(") and t.epcid2 is null");
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(Container.class));
	}

	/**
	 * 查器具在流转单明细中的
	 * @param epcids
	 * @return
	 */
	public List<Container> queryContainerInCirculateDetail(String epcids)
	{
		StringBuffer sql = new StringBuffer("select * from t_container c LEFT JOIN (select distinct(epc_id) epcid2 from t_circulate_detail where epc_id in(");
		sql.append(epcids).append(") ) AS t  ON (t.epcid2 = c.epc_id ) where c.epc_id in( ")
				.append(epcids).append(") and t.epcid2 is not null");
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(Container.class));
	}

	/**
	 * 按epcId列表查询器具
	 * @param epcids  例'1','2'
	 * @return
	 */
	public List<Container> queryContainerListByEpcIdS(String epcids)
	{
		StringBuffer sql = new StringBuffer("select * from t_container c where epc_id in (");
		sql.append(epcids).append(SysConstants.YKH);
		return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(Container.class));
	}

}
