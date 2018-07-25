package com.cdc.cdccmc.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.domain.dto.CirculateDto;
import com.cdc.cdccmc.runnable.ASyncInventoryTask;
import com.cdc.cdccmc.runnable.ASyncTask;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.InventoryDifferent;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.ContextLoader;

/** 
 * 盘点单信息
 * @author shichuang
 * @date 2018-01-29
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class InventoryMainService {
	private static final org.slf4j.Logger LOG= org.slf4j.LoggerFactory.getLogger(InventoryMainService.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private InventoryDetailService inventoryDetailService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
    private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private ASyncInventoryTask aSyncTask;


	@Value("#{sql['insertInventoryMain']}")
	private String insertInventoryMain;
	@Value("#{sql['insertDetialSql']}")
	private String insertDetialSql;
	@Value("#{sql['updateInventoryDetail']}")
	private String updateInventoryDetail;
	@Value("#{sql['updateInventoryMainFinish']}")
	private String updateInventoryMainFinish;
	@Value("#{sql['insertSerialNumberPd']}")
	private String insertSerialNumberPd;
	@Value("#{sql['updateSerialNumberPd']}")
	private String updateSerialNumberPd;
	@Value("#{sql['queryInventoryMainById']}")
	private String queryInventoryMainById;
	@Value("#{sql['queryInventoryLatestByOrgId']}")
	private String queryInventoryLatestByOrgId;
	
	/**
	 * 获取盘点主单信息
	 * @param paging
	 * @param inventoryMain
	 * @return
	 */
	public Paging queryInventoryMainList(SystemUser sessionUser, Paging paging,InventoryMain inventoryMain,String startDate,String endDate) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_inventory_main where inventory_org_id in ( ");
		sql.append(sessionUser.getFilialeSystemOrgIds());
		sql.append(" ) ");
		if(StringUtils.isNotBlank(inventoryMain.getInventoryId())){
			sql.append(" and inventory_id like :inventoryId ");
			paramMap.put("inventoryId", "%"+inventoryMain.getInventoryId()+"%");
		}
		if(StringUtils.isNotBlank(inventoryMain.getInventoryOrgId())){
			sql.append(" and inventory_org_id = :inventoryOrgId ");
			paramMap.put("inventoryOrgId", inventoryMain.getInventoryOrgId());
		}
		if(StringUtils.isNotBlank(startDate)){
			sql.append(" and create_time > :startDate ");
			paramMap.put("startDate", startDate.trim());
		}
		if(StringUtils.isNotBlank(endDate)){
			sql.append(" and create_time < :endDate ");
			paramMap.put("endDate", endDate.trim());
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, InventoryMain.class);
		return resultPaging;
	}
	
	/**
	 * 查询盘点仓库下状态为“盘点中”盘点单
	 * @param inventoryOrg 盘点仓库
	 * @return
	 */
	public InventoryMain listNotFinishInventoryMain(SystemOrg inventoryOrg ) {
		String sql = "select * from t_inventory_main where inventory_state = 0 and inventory_org_id =?";
		List<InventoryMain> list = jdbcTemplate.query(sql,new BeanPropertyRowMapper(InventoryMain.class),inventoryOrg.getOrgId());
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

    public AjaxBean startInventoryByThread(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg inventoryOrg,String contactName,String contactPhone) {
        //创建盘点单
        LOG.info("---新建盘点单-多线程   新建盘点单t_inventory_main开始");
        ajaxBean = addInventoryMain(ajaxBean, sessionUser, inventoryOrg,contactName,contactPhone);
        //LOG.info("---startInventory   ajaxBean="+JSON.toJSONString(ajaxBean));
        if(200 != ajaxBean.getStatus().intValue()) {
            LOG.info("---新建盘点单-多线程    新建盘点单t_inventory_main失败");
            return ajaxBean;
        }
        InventoryMain inventoryMain = (InventoryMain)ajaxBean.getBean();
        //LOG.info("---startInventory   inventoryMain.getInventoryId()="+inventoryMain.getInventoryId());
        LOG.info("---新建盘点单-多线程    ["+inventoryMain.getInventoryId()+"]新建盘点单t_inventory_main完毕");
        ajaxBean.setBean(null);

        //查询关联的t_circulate_latest
        LOG.info("---新建盘点单-多线程   查询关联的t_circulate_latest开始");
        long start = System.currentTimeMillis();
        Integer count = circulateOrderDeliveryService.countCirculateLatestByOrgId(inventoryOrg.getOrgId()
                ,CirculateState.ON_ORG.getCode());
        int forCount = count / 5000;
        if(count<5000 || count % 5000>0)
        {
            forCount ++;
        }
        for(int i=0;i<forCount;i++)
        {
			aSyncTask.addInventoryDetailByCirculateLatestByThread(sessionUser,inventoryMain,inventoryOrg.getOrgId(),CirculateState.ON_ORG.getCode(),i*5000,5000);
        }
        /*
        List<CirculateDto> circulates = circulateOrderDeliveryService.queryCirculateLatestByOrgIdLimit(inventoryOrg.getOrgId()
                ,CirculateState.ON_ORG.getCode(),i*5000,5000);
        long end = System.currentTimeMillis();
        LOG.info("---新建盘点单-多线程   查询关联的t_circulate_latest完毕，耗时：" + (end - start));

        if(CollectionUtils.isNotEmpty(circulates)) {
            //生成盘点单明细 t_inventory_detail，有多少个EPC，就有多少条记录
            inventoryDetailService.addInventoryDetailByCirculateLatest(sessionUser, inventoryMain.getInventoryId(), circulates);
        }
        LOG.info("---新建盘点单-多线程    ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量开始");
        addInventoryDetailAboutNoEPC(sessionUser,inventoryMain,circulates);
        LOG.info("---新建盘点单-多线程    ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量完毕");
        */
        return ajaxBean;
    }

    @Transactional(propagation = Propagation.NESTED)
    public void addInventoryDetailByCirculateLatest(SystemUser sessionUser,InventoryMain inventoryMain,String orgId,String state,Integer start,Integer limit)
    {
        try
        {
            List<CirculateDto> circulates = circulateOrderDeliveryService.queryCirculateLatestByOrgIdLimit(orgId
                    ,state,start,limit);
            long end = System.currentTimeMillis();
            LOG.info("---新建盘点单-多线程   查询关联的t_circulate_latest完毕，耗时：" + (end - start));

            if(CollectionUtils.isNotEmpty(circulates)) {
                //生成盘点单明细 t_inventory_detail，有多少个EPC，就有多少条记录
                inventoryDetailService.addInventoryDetailByCirculateLatest(sessionUser, inventoryMain.getInventoryId(), circulates);
            }
            LOG.info("---新建盘点单-多线程    ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量开始");
            addInventoryDetailAboutNoEPC(sessionUser,inventoryMain,circulates);
            LOG.info("---新建盘点单-多线程    ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量完毕");
        }
        catch (Exception e)
        {
            LOG.info("Thread="+Thread.currentThread().getName()+"   orgId="+orgId+"   state="+state
					+"   start="+start+"    limit="+limit+"   异常信息：" + e.toString());
        }
    }

	/**
	 * 启动盘点
	 * 1.生成盘点单 t_inventory_main
	 * 2.生成盘点单明细 t_inventory_detail
	 *   t_circulate_latest表里符合以下条件的数  1) org_id = 当前仓库 2) circulate_state = '10' (在库)
	 *   固化到表t_inventory_detail，t_inventory_detail数据值设置：
	 *   1) is_have_different = 0 未知
	 *   2）inventory_time = now() 当前时间
	 *   更新t_inventory_detail其它字段：
	 * `  create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	 *   `create_account` varchar(50) DEFAULT NULL COMMENT '创建账号',
	 *   `create_real_name` varchar(20) DEFAULT NULL COMMENT '创建账号的姓名',
	 *   `create_org_id` varchar(32) NOT NULL COMMENT '创建组织ID',
	 *   `create_org_name` varchar(50) NOT NULL COMMENT '创建组织名称',
	 * @param ajaxBean
	 * @param sessionUser
	 * @param inventoryOrg
	 * @return
	 */
	public AjaxBean startInventory(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg inventoryOrg,String contactName,String contactPhone) {
		//创建盘点单
		LOG.info("---新建盘点单   新建盘点单t_inventory_main开始");
		ajaxBean = addInventoryMain(ajaxBean, sessionUser, inventoryOrg,contactName,contactPhone);
		//LOG.info("---startInventory   ajaxBean="+JSON.toJSONString(ajaxBean));
		if(200 != ajaxBean.getStatus().intValue()) {
			LOG.info("---新建盘点单   新建盘点单t_inventory_main失败");
			return ajaxBean;
		}
		InventoryMain inventoryMain = (InventoryMain)ajaxBean.getBean();
		//LOG.info("---startInventory   inventoryMain.getInventoryId()="+inventoryMain.getInventoryId());
		LOG.info("---新建盘点单   ["+inventoryMain.getInventoryId()+"]新建盘点单t_inventory_main完毕");
		ajaxBean.setBean(null);
		
		//查询关联的t_circulate_latest 
		LOG.info("---新建盘点单  查询关联的t_circulate_latest开始  inventoryOrg.getOrgId()="+inventoryOrg.getOrgId()
		+"   CirculateState.ON_ORG.getCode()="+CirculateState.ON_ORG.getCode());
		long start = System.currentTimeMillis();
        List<CirculateDto> circulates = circulateOrderDeliveryService.queryCirculateLatestByOrgId(inventoryOrg.getOrgId()
                ,CirculateState.ON_ORG.getCode());
		long end = System.currentTimeMillis();
		LOG.info("---新建盘点单  查询关联的t_circulate_latest完毕，耗时：" + (end - start));
		
        if(CollectionUtils.isNotEmpty(circulates)) {
			//生成盘点单明细 t_inventory_detail，有多少个EPC，就有多少条记录
			inventoryDetailService.addInventoryDetailByCirculateLatest(sessionUser, inventoryMain.getInventoryId(), circulates);
		}
		LOG.info("---新建盘点单   ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量开始");
        addInventoryDetailAboutNoEPC(sessionUser,inventoryMain,circulates);
		LOG.info("---新建盘点单   ["+inventoryMain.getInventoryId()+"]新建盘点单增加无EPC号的器具需盘点数量完毕");
		return ajaxBean;
	}
	/**
	 * <pre>
	 * 为没有EPC的器具数量每种器具代码插入一条需盘点数量的记录。
	 * 计算方式为：	
	 *  当前库存的该类器具代码在库数量 (t_inventory_latest)
	 * 	- 有EPC的器具最新流转状态当前仓库且在库状态的该类器具代码代码在库数量(t_circulate_latest)
	 * 	= 无EPC的需盘点器具数量(如果此数量为0，则不插入记录)
	 * </pre>
	 * @param ajaxBean
	 * @param sessionUser
	 * @param inventoryOrg
	 * @param contactName
	 * @param contactPhone
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void addInventoryDetailAboutNoEPC(SystemUser sessionUser, InventoryMain inventoryMain,List<CirculateDto> circulates) {
		List<InventoryHistory> ihList = jdbcTemplate.query(queryInventoryLatestByOrgId, new BeanPropertyRowMapper(InventoryHistory.class),inventoryMain.getInventoryOrgId());
		if(CollectionUtils.isNotEmpty(ihList)) {
			Map<String,Integer> map = new HashMap<String,Integer>();
			Map<String,CirculateDto> circulateDtoMap = new HashMap<String,CirculateDto>();
			if(CollectionUtils.isNotEmpty(circulates)) {
				for (CirculateDto c : circulates) {
					if(map.containsKey(c.getContainerCode())) {
						map.put(c.getContainerCode(), map.get(c.getContainerCode() + 1));
					}else {
						map.put(c.getContainerCode(), 1);
						circulateDtoMap.put(c.getContainerCode(), c);
					}
				}
			}
			// 新增盘点明细，没有EPC卡的器具
			InventoryDetail inventoryDetail = null;
			Timestamp now = new Timestamp(System.currentTimeMillis());
			List<InventoryDetail> inventoryDetails = new ArrayList<>();
			for (InventoryHistory ih : ihList) {
				String containerCode = ih.getContainerCode();
				//如果无EPC的器具数量大于0，则需要盘点
				int noEpcNumber = ih.getInOrgNumber();
				if(null != map.get(containerCode)) {
					noEpcNumber = new BigDecimal(ih.getInOrgNumber()).subtract(new BigDecimal(map.get(containerCode))).intValue();
				}
				if(noEpcNumber > SysConstants.INTEGER_0) {
					CirculateDto c = circulateDtoMap.get(ih.getContainerCode());
					inventoryDetail = new InventoryDetail();
					inventoryDetail.setInventoryDetailId(UUIDUtil.creatUUID());
					inventoryDetail.setInventoryId(inventoryMain.getInventoryId());
					inventoryDetail.setInventoryAccount(sessionUser.getAccount()); //盘点人账号
					inventoryDetail.setInventoryRealName(sessionUser.getRealName()); //盘点人姓名
					inventoryDetail.setIsHaveDifferent(SysConstants.INTEGER_0);//是否有差异。0未知 1 没有差异 2有区域差异 3 器具未扫描到 4 区域内扫描到新器具 
					inventoryDetail.setIsDeal(SysConstants.INTEGER_0); //0未处理 1已处理
					inventoryDetail.setCreateTime(now);
					inventoryDetail.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
					inventoryDetail.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
					inventoryDetail.setCreateAccount(sessionUser.getAccount());
					inventoryDetail.setCreateRealName(sessionUser.getRealName());
					//设置器具代码、器具类型等
					if(null != c) {
						inventoryDetail.setContainerName(c.getContainerName());
						inventoryDetail.setContainerCode(c.getContainerCode());
						inventoryDetail.setContainerTypeName(c.getContainerTypeName());
						inventoryDetail.setContainerTypeId(c.getContainerTypeId());
					}else {
						ContainerCode findCode = containerCodeService.queryByContainerCode(containerCode);
						inventoryDetail.setContainerName(findCode.getContainerName());
						inventoryDetail.setContainerCode(findCode.getContainerCode());
						inventoryDetail.setContainerTypeName(findCode.getContainerTypeName());
						inventoryDetail.setContainerTypeId(findCode.getContainerTypeId());
					}
					inventoryDetail.setSystemNumber(noEpcNumber); //需盘点器具数量
					inventoryDetail.setInventoryNumber(SysConstants.INTEGER_0);
					inventoryDetails.add(inventoryDetail);
				}
			}
			//把每一种器具代码的没有EPC的器具数量都插入为一条需要盘点的记录
			if(CollectionUtils.isNotEmpty(inventoryDetails)) {
				inventoryDetailService.batchInsertInventoryDetail(inventoryDetails, CollectionUtils.size(inventoryDetails),0);
			}
		}
	}

	/**
	 * 新增盘点单
	 * @param ajaxBean 
	 * @param ajaxBean
	 * @param sessionUser
	 * @param inventoryOrg 盘点仓库
	 * @return 
	 */
	public AjaxBean addInventoryMain(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg inventoryOrg,String contactName,String contactPhone) {
		//新增盘点单
		InventoryMain inventoryMain = new InventoryMain();
		inventoryMain.setInventoryTime(new Timestamp(System.currentTimeMillis()));
		inventoryMain.setCreateTime(new Timestamp(System.currentTimeMillis()));
		inventoryMain.setVersion(0);
		inventoryMain.setContactName(contactName);
		inventoryMain.setContactPhone(contactPhone);
		inventoryMain.setInventoryState(0); //状态默认设置为“盘点中”
		inventoryMain.setInventoryId(createInventoryId(inventoryOrg,sessionUser)); //盘点单号
		inventoryMain.setInventoryOrgId(inventoryOrg.getOrgId()); //盘点仓库ID
		inventoryMain.setInventoryOrgName(inventoryOrg.getOrgName()); //盘点仓库名称
		inventoryMain.setCreateAccount(sessionUser.getAccount());
		inventoryMain.setCreateRealName(sessionUser.getRealName());
		inventoryMain.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		inventoryMain.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		int result = namedJdbcTemplate.update(insertInventoryMain, new BeanPropertySqlParameterSource(inventoryMain));
		ajaxBean = AjaxBean.returnAjaxResult(result);
		ajaxBean.setBean(inventoryMain);
		return ajaxBean;
	}

    /**
     * 创建指定仓库的盘点编号
     * @param org 指定仓库
     * @return
     */
	public synchronized String createInventoryId(SystemOrg org, SystemUser sessionUser){
		Integer serialNumber = 0; //流水编号
		//查询最新的流水编号
		String todayDateNo = DateUtil.today_yyyy_MM_dd();
		String querySerialNumberSql = "select * from t_serial_number_pd where date_no = '"+todayDateNo+"'";
		List<Map<String, Object>> serialNumberList = namedJdbcTemplate.queryForList(querySerialNumberSql, new HashMap());
		if(CollectionUtils.isEmpty(serialNumberList)){ //如果没有查到今天的流水编号，则新增一条今天的,初始流水号为1
			serialNumber = 1;
			jdbcTemplate.update(insertSerialNumberPd, todayDateNo,serialNumber);
		}else{ //若找到今天的流水编号，则更新+1
			serialNumber = new Integer(serialNumberList.get(0).get("SERIAL_NUMBER").toString()) + 1;
			jdbcTemplate.update(updateSerialNumberPd, serialNumber,todayDateNo);
		}
		
		int length = 4;  //流水编号长度为固定4位，不足位的前面补0
		String serialNumberStr = serialNumber.toString();
		int forLength = length - serialNumberStr.length();
		for (int i = 0; i < forLength; i++) {
			if(serialNumberStr.length() > length){
				break;
			}
			serialNumberStr = "0" + serialNumberStr;
		}

		//盘点单单号，生成规则：仓库代码+PD(盘点拼音首字母大写)+日期年月日+四位流水号，例子：CMCSP-PD-201712250001
		String inventoryId = org.getOrgCode() + "-PD-" + DateUtil.today_yyyyMMdd() + serialNumberStr;
		
		InventoryMain main = findInventoryMainById(inventoryId);
		while(null != main){
			LOG.info("盘点单单号["+inventoryId+"]已经存在，创建盘点单单号失败！继续尝试生成单号...");
			inventoryId = createInventoryId(org,sessionUser);
			main = findInventoryMainById(inventoryId);
		}
		LOG.info("新的盘点单单号["+inventoryId+"]创建成功！");
		return inventoryId;
	}
	/**
	 * 根据盘点单号查询盘点单对象
	 * @param inventoryId 盘点单号
	 * @return
	 */
	public InventoryMain findInventoryMainById(String inventoryId){
		List<InventoryMain> list = jdbcTemplate.query(queryInventoryMainById, new BeanPropertyRowMapper(InventoryMain.class), inventoryId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 盘点完成
	 * 1、先过滤出来盘点单明细中盘点区域
	 * 2、在根据盘点仓库和区域 分别查出 盘点明细 和 流转信息
	 * 3、然后匹配出来盘点和流转信息匹配结果
	 * 4、更新盘点单状态
	 * @param ajaxBean 
	 * @param sessionUser
	 * @param inventoryMain
	 * @return
	 */
	public AjaxBean finishInventoryMain(AjaxBean ajaxBean, SystemUser sessionUser,InventoryMain inventoryMain){
		//获取盘点单器具详细
		List<InventoryDetail> inventoryDetailList = inventoryDetailService.listInventoryDetailByInventoryId(inventoryMain.getInventoryId());
		//获取指定仓库所有器具列表
		List<Circulate> latestCirculateList = circulateService.listOrgAllContainer(inventoryMain.getInventoryOrgId());
		List<InventoryDetail> inventoryDetailParamList = new ArrayList<InventoryDetail>();
		//比较“盘点单器具详细”和“指定仓库所有器具列表”，生成盘点差异结果
		for(InventoryDetail inventoryDetail : inventoryDetailList){
			boolean find = false;
			InventoryDetail inventoryDetaiParam = new InventoryDetail();
			inventoryDetaiParam.setInventoryDetailId(inventoryDetail.getInventoryDetailId());
			two:for(Circulate latest :latestCirculateList){
				if(latest.getEpcId().equals(inventoryDetail.getEpcId())){//同一个器具
					//如果器具在途，不进行比对，作为“未扫描到器具”处理
					if(CirculateState.ON_WAY.getCode().equals(latest.getCirculateState())){
						find = false;
						break two;
					}
					if(latest.getAreaId().equals(inventoryDetail.getAreaId())){ //如果盘点结果为区域一致
						inventoryDetaiParam.setIsHaveDifferent(Integer.valueOf(InventoryDifferent.NOT_DIFFERENT.getCode())); //没有差异
					}else{  //如果盘点结果为区域不同
						inventoryDetaiParam.setIsHaveDifferent(Integer.valueOf(InventoryDifferent.AREA_DIFFERENT.getCode())); //区域差异
					}
					inventoryDetaiParam.setOldAreaId(latest.getAreaId()); //旧区域ID
					inventoryDetaiParam.setOldAreaName(latest.getAreaName()); //旧区域名称
					find = true;
					break two; //找到了该器具，就结束查找和比较
				}
			}
			if(!find){ //如果没找到该器具，则为：扫描到新器具
				inventoryDetaiParam.setIsHaveDifferent(Integer.valueOf(InventoryDifferent.NEW_SCAN.getCode())); //扫描到新器具
				inventoryDetaiParam.setOldAreaId(StringUtils.EMPTY); //旧区域ID
				inventoryDetaiParam.setOldAreaName(StringUtils.EMPTY); //旧区域名称
			}
			inventoryDetaiParam.setAreaId(inventoryDetail.getAreaId()); //盘点区域ID
			inventoryDetaiParam.setAreaName(inventoryDetail.getAreaName()); //盘点区域名称
			inventoryDetailParamList.add(inventoryDetaiParam);
			//如果列表数量已经达到批量插入数，则执行批量插入
			if(inventoryDetailParamList.size() >= SysConstants.MAX_INSERT_NUMBER){
				namedJdbcTemplate.batchUpdate(updateInventoryDetail, SqlParameterSourceUtils.createBatch(inventoryDetailParamList.toArray()));
				inventoryDetailParamList.clear();
			}
		}
		//如果列表数量大于0，则执行批量插入
		if(inventoryDetailParamList.size() > SysConstants.INTEGER_0){
			namedJdbcTemplate.batchUpdate(updateInventoryDetail, SqlParameterSourceUtils.createBatch(inventoryDetailParamList.toArray()));
			inventoryDetailParamList = null;
		}
		
		//反过来再比较一遍，查找状态为“未扫描到”的器具
		List<InventoryDetail> insertParamList = new ArrayList<InventoryDetail>();
		for(Circulate latest :latestCirculateList){
			boolean find = false;
			two:for(InventoryDetail inventoryDetail : inventoryDetailList){
				if(latest.getEpcId().equals(inventoryDetail.getEpcId())){//同一个器具
					if(CirculateState.ON_WAY.getCode().equals(latest.getCirculateState())){
						find = false; //如果器具在途，不进行比对，作为“未扫描到器具”处理
						break two;
					}
					find = true;
					break two;
				}
			}
			if(!find){ //如果没找到该器具，则为“未扫描到”的器具,不属于任何盘点区域
				InventoryDetail detail = new InventoryDetail();
				detail.setInventoryDetailId(UUIDUtil.creatUUID());
				detail.setInventoryId(inventoryMain.getInventoryId());
				detail.setOldAreaId(latest.getAreaId());
				detail.setOldAreaName(latest.getAreaName());
				detail.setEpcId(latest.getEpcId());
				detail.setContainerCode(latest.getContainerCode());
				ContainerCode cc = containerCodeService.queryByContainerCode(latest.getContainerCode());
				if (cc != null) {
					detail.setContainerName(containerCodeService.queryByContainerCode(latest.getContainerCode()).getContainerName());
				} else {
					detail.setContainerName("");
				}
				detail.setContainerTypeName(latest.getContainerTypeName());
				detail.setIsHaveDifferent(3);
				detail.setCreateAccount(sessionUser.getAccount());
				detail.setCreateRealName(sessionUser.getCreateRealName());
				detail.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
				detail.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
				insertParamList.add(detail);
			}
			//如果列表数量已经达到批量插入数，则执行批量插入
			if(insertParamList.size() >= SysConstants.MAX_INSERT_NUMBER){
				namedJdbcTemplate.batchUpdate(insertDetialSql, SqlParameterSourceUtils.createBatch(insertParamList.toArray()));
				insertParamList.clear();
			}
		}
		//如果列表数量大于0，则执行批量插入
		if(insertParamList.size() > SysConstants.INTEGER_0){
			namedJdbcTemplate.batchUpdate(insertDetialSql, SqlParameterSourceUtils.createBatch(insertParamList.toArray()));
			insertParamList = null;
		}
		
		//更新盘点单状态为：   1  盘点完毕
		Integer result = jdbcTemplate.update(updateInventoryMainFinish, sessionUser.getRealName(), sessionUser.getCurrentSystemOrg().getOrgName(), inventoryMain.getInventoryId());
		if(result !=1 ){ //如果更新状态失败
			ajaxBean.setStatus(StatusCode.STATUS_323);
			ajaxBean.setMsg(StatusCode.STATUS_323_MSG);
		}
		return ajaxBean;  
	}
}