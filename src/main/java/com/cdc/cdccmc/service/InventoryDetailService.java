package com.cdc.cdccmc.service;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.InventoryDifferent;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.dto.CirculateDto;
import com.cdc.cdccmc.domain.dto.InventoryDetailDto;
import com.cdc.cdccmc.domain.dto.InventorySumDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.runnable.ASyncInventoryTask;
import com.cdc.cdccmc.service.basic.AreaService;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 盘点明细
 * 
 * @author shichuang
 * @date 2018/1/29 15:58
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class InventoryDetailService {
	private static final org.slf4j.Logger LOG= org.slf4j.LoggerFactory.getLogger(InventoryDetailService.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private AreaService areaService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private ASyncInventoryTask aSyncInventoryTask;
	@Value("#{sql['insertInventoryDetail']}")
	private String insertInventoryDetail;
	@Value("#{sql['updateInventoryDetailForHandset']}")
	private String updateInventoryDetailForHandset;
	@Value("#{sql['updateCirculateLatestForInventory']}")
	private String updateCirculateLatestForInventory;
	@Value("#{sql['deleteInventoryDetailByEpcAndInventoryId']}")
	private String deleteInventoryDetailByEpcAndInventoryId;
	@Value("#{sql['insertInventoryDetailByParamName']}")
	private String insertInventoryDetailByParamName;

	@Value("#{sql['deleteInventoryDetailByInEpcAndInventoryId']}")
	private String deleteInventoryDetailByInEpcAndInventoryId;
	@Value("#{sql['insertInventoryDetailByPosition']}")
	private String insertInventoryDetailByPosition;
    @Value("#{sql['updateInventoryOverDetail']}")
    private String updateInventoryOverDetail;
    @Value("#{sql['queryInventoryDetailById']}")
    private String queryInventoryDetailById;
    @Value("#{sql['updateInventoryMainFinish']}")
    private String updateInventoryMainFinish;
	@Value("#{sql['queryContainerNotExistsCirulateOrderDeatil']}")
	private String queryContainerNotExistsCirulateOrderDeatil;
	@Value("#{sql['queryInventoryDetailByEpcAndInventoryId']}")
	private String queryInventoryDetailByEpcAndInventoryId;
	@Value("#{sql['updateInventoryNumber']}")
	private String updateInventoryNumber;


    /**
     * 增加盘点单明细-通过流转单明细拼装
     *
     * @param circulateLatests
     * @return
     */
	public void addInventoryDetailByCirculateLatest(SystemUser sessionUser,String inventoryId,List<CirculateDto> circulateLatests)
	{
		if(StringUtils.isNotEmpty(inventoryId) && CollectionUtils.isNotEmpty(circulateLatests))
		{
			// 新增盘点明细
			InventoryDetail inventoryDetail = null;
			Timestamp now = new Timestamp(System.currentTimeMillis());
			LOG.info("---新建盘点单  ["+inventoryId+"]组装盘点明细集合开始，总共明细数量：" + CollectionUtils.size(circulateLatests));
			List<InventoryDetail> inventoryDetails = new ArrayList<>();
			int batchSize = 0;
			int count = 0 ;
			for (CirculateDto circulate : circulateLatests)
			{
				inventoryDetail = new InventoryDetail();
				inventoryDetail.setInventoryDetailId(UUIDUtil.creatUUID());
				inventoryDetail.setInventoryId(inventoryId);
				inventoryDetail.setInventoryAccount(sessionUser.getAccount()); //盘点人账号
				inventoryDetail.setInventoryRealName(sessionUser.getRealName()); //盘点人姓名
				inventoryDetail.setEpcId(circulate.getEpcId());
				inventoryDetail.setIsHaveDifferent(SysConstants.INTEGER_0);//是否有差异。0未知 1 没有差异 2有区域差异 3 器具未扫描到 4 区域内扫描到新器具 
				inventoryDetail.setIsDeal(SysConstants.INTEGER_0); //0未处理 1已处理
				inventoryDetail.setCreateTime(now);
				inventoryDetail.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
				inventoryDetail.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
				inventoryDetail.setCreateAccount(sessionUser.getAccount());
				inventoryDetail.setCreateRealName(sessionUser.getRealName());
				//新建盘点时只设oldArea 在手持机盘点时去更新Area并同时校验是否区域变化
				inventoryDetail.setOldAreaId(circulate.getAreaId());
				inventoryDetail.setOldAreaName(circulate.getAreaName());
				inventoryDetail.setContainerName(circulate.getContainerName());
				inventoryDetail.setContainerCode(circulate.getContainerCode());
				inventoryDetail.setContainerTypeName(circulate.getContainerTypeName());
				inventoryDetail.setContainerTypeId(circulate.getContainerTypeId());
				inventoryDetail.setSystemNumber(SysConstants.INTEGER_1); //需盘点器具数量
				inventoryDetail.setInventoryNumber(SysConstants.INTEGER_0);
				inventoryDetails.add(inventoryDetail);
				//LOG.info("---新建盘点单  inventoryDetails.add "+(count++)+"    inventoryDetail="+ JSON.toJSONString(inventoryDetail));
				//批量插入
				batchSize = CollectionUtils.size(inventoryDetails);
				if(batchSize > SysConstants.MAX_5000) {
					batchInsertInventoryDetail(inventoryDetails, batchSize, count);
					inventoryDetails.clear();
				}
			}
			//补充盘点明细
			batchSize = CollectionUtils.size(inventoryDetails);
			if(batchSize > SysConstants.INTEGER_0) {
				batchInsertInventoryDetail(inventoryDetails, batchSize , count);
			}
			//释放内存
			inventoryDetails = null;
			LOG.info("---新建盘点单  t_inventory_detail批量插入完毕");
		}
	}

	/**
	 * 批量插入盘点单明细数据  SQL上写?号个数匹配
	 * @param inventoryDetails
	 */
	public void batchInsertInventoryDetail(List<InventoryDetail> inventoryDetails, int batchSize, int count)
	{
		if(CollectionUtils.isNotEmpty(inventoryDetails))
		{
			long start = System.currentTimeMillis();
			namedJdbcTemplate.batchUpdate(insertInventoryDetailByParamName, SqlParameterSourceUtils.createBatch(inventoryDetails.toArray()));
			long end = System.currentTimeMillis();
			LOG.info("---新建盘点单  t_inventory_detail批量插入"+SysConstants.MAX_5000+"条完毕，count="+count+"，批量插入盘点明细耗时：" + (end - start));
		}
	}

	/**
	 * 批量插入盘点单明细数据  SQL上写?号个数匹配
	 * @param inventoryDetails
	 */
	public void batchInsertInventoryDetail_old(List<InventoryDetail> inventoryDetails, int batchSize, int count)
	{
		if(CollectionUtils.isNotEmpty(inventoryDetails))
		{
			long start = System.currentTimeMillis();
			jdbcTemplate.batchUpdate(insertInventoryDetailByPosition, new BatchPreparedStatementSetter()
			{
				@Override
				public int getBatchSize()
				{
					return batchSize;
				}
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException
				{
					//inventory_detail_id, inventory_id,
	 				//inventory_account,inventory_real_name, area_id, area_name, inventory_time, epc_id, is_have_different, create_time,
	 				//create_account, create_real_name
					//13-16,container_code,container_type_id,container_type_name,container_name
					ps.setString(1, inventoryDetails.get(i).getInventoryDetailId());
					ps.setString(2, inventoryDetails.get(i).getInventoryId());
					ps.setString(3, inventoryDetails.get(i).getOldAreaId());
					ps.setString(4, inventoryDetails.get(i).getOldAreaName());
					ps.setTimestamp(5, inventoryDetails.get(i).getInventoryTime());
					ps.setString(6, inventoryDetails.get(i).getEpcId());
					ps.setInt(7, inventoryDetails.get(i).getIsHaveDifferent());
					ps.setTimestamp(8, inventoryDetails.get(i).getCreateTime());
					ps.setString(9, inventoryDetails.get(i).getCreateAccount());
					ps.setString(10, inventoryDetails.get(i).getCreateRealName());
					ps.setString(11, inventoryDetails.get(i).getContainerCode());
					ps.setString(12, inventoryDetails.get(i).getContainerTypeId());
					ps.setString(13, inventoryDetails.get(i).getContainerTypeName());
					ps.setString(14, inventoryDetails.get(i).getContainerName());
					ps.setInt(15, inventoryDetails.get(i).getSystemNumber());
					ps.setString(16, inventoryDetails.get(i).getCreateOrgId());
					ps.setString(17, inventoryDetails.get(i).getCreateOrgName());
				}
			});
			long end = System.currentTimeMillis();
			LOG.info("---新建盘点单  t_inventory_detail批量插入"+SysConstants.MAX_5000+"条完毕，count="+count+"，批量插入盘点明细耗时：" + (end - start));
		}
	}

    /**
     * 更新盘点单明细
     * @param sessionUser
     * @param inventoryDetailList
     * @return
     */
    public AjaxBean batchUpdateInventoryDetail(SystemUser sessionUser, List<InventoryDetail> inventoryDetailList) {
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        namedJdbcTemplate.batchUpdate(updateInventoryOverDetail, SqlParameterSourceUtils.createBatch(inventoryDetailList.toArray()));
        return ajaxBean;
    }

    /**
     * 根据盘点单号查询盘点单列表--is_have_different=0
     * @param inventoryId 盘点单号
     * @return
     */
    public List<InventoryDetail> findInventoryDetailById(String inventoryId) {
        List<InventoryDetail> list = jdbcTemplate.query(queryInventoryDetailById, new BeanPropertyRowMapper(InventoryDetail.class), inventoryId);
        return list;
    }

	/**
	 * 根据盘点单号查询盘点单列表--is_have_different=0
	 * @param inventoryId 盘点单号
	 * @return
	 */
	public List<InventoryDetail> findInventoryDetailByIdAndLimit(String inventoryId,Integer start,Integer limit) {
		String sql = "select * from t_inventory_detail where inventory_id = ? limit ?,?";
		List<InventoryDetail> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper(InventoryDetail.class), inventoryId,start,limit);
		return list;
	}

	/**
	 * 统计盘点明细总数
	 * @param inventoryId
	 * @return
	 */
	public Integer countInventoryDetailById(String inventoryId) {
		String sql = "select count(1) from t_inventory_detail where inventory_id = :inventoryId";
		ConcurrentHashMap map = new ConcurrentHashMap();
		map.put("inventoryId", inventoryId);
		return namedJdbcTemplate.queryForObject(sql, map, Integer.class);
	}

    /**
     *  盘点完成
     *
     * @param sessionUser
     * @param inventoryId
     * @return
     */
    public AjaxBean finishInventoryDetail(SystemUser sessionUser, String inventoryId) {
        AjaxBean ajaxBean = new AjaxBean();
        // 找到t_inventory_detail表里is_have_different=0（未知）的记录
//        List<InventoryDetail> list = findInventoryDetailById(inventoryId);
//        if (CollectionUtils.isNotEmpty(list)) {
//            batchUpdateInventoryDetail(sessionUser, list);
//        }
		String sql = "update t_inventory_detail set is_have_different = 3, inventory_time=now()" +
				"where inventory_id = ? and is_have_different = 0";
		jdbcTemplate.update(sql, inventoryId);

        //更新盘点单状态为：   1  盘点完毕
        Integer result = jdbcTemplate.update(updateInventoryMainFinish, sessionUser.getRealName(),
                sessionUser.getCurrentSystemOrg().getOrgName(), sessionUser.getAccount(),
                sessionUser.getCurrentSystemOrg().getOrgId(),inventoryId);
        if (result !=1 ) { //如果更新状态失败
            ajaxBean.setStatus(StatusCode.STATUS_323);
            ajaxBean.setMsg(StatusCode.STATUS_323_MSG);
        }

        ajaxBean.setStatus(StatusCode.STATUS_200);
        ajaxBean.setMsg(StatusCode.STATUS_200_MSG);

        return ajaxBean;
    }


	/**
	 * 分页，盘点明细
	 * @param paging
	 * @param inventoryId 盘点编号
	 * @return
	 */
	public Paging pagingInventoryDetail(Paging paging, String inventoryId, SystemUser sessionUser, Integer isHaveDifferent) {
		String sql = "select d.*,m.inventory_state from t_inventory_detail d, t_inventory_main m where d.inventory_id = m.inventory_id ";
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(inventoryId)) {
			sql += " and d.inventory_id = :inventoryId ";
			paramMap.put("inventoryId", inventoryId);
		}
		if (isHaveDifferent != null){
			sql += "and d.is_have_different = :isHaveDifferent";
			paramMap.put("isHaveDifferent", isHaveDifferent);
		}
		if (StringUtils.isNotBlank(sessionUser.getCurrentSystemOrg().getOrgId())) {
			sql += " and m.inventory_org_id in (  " + sessionUser.getFilialeSystemOrgIds() + " )";
		}
		sql += "  order by d.inventory_id desc,d.container_code asc ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, InventoryDetail.class);
		return paging;
	}

	/**
	 * 分页，盘点统计
	 * @param paging
	 * @param inventoryId 盘点编号
	 * @return
	 */

	public Paging pagingInventoryDetailSum(Paging paging, String inventoryId, SystemUser sessionUser, String differentNumber) {
		StringBuilder sql = new StringBuilder ("select container_code ,container_type_name ,container_name ,SUM(system_number) as allNum , SUM(inventory_number) as actualNum ,(SUM(inventory_number) - SUM(system_number)) as differentNum from t_inventory_detail where 1 = 1");
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(inventoryId)) {
			sql.append( " and inventory_id = :inventoryId ");
			paramMap.put("inventoryId", inventoryId);
		}
		if (StringUtils.isNotBlank(differentNumber)){
			sql.append(" and (select (SUM(inventory_number) - SUM(system_number)) ");
			if(differentNumber.equals("1")){
				sql.append(" > 0");
			}
			if(differentNumber.equals("2")){
				sql.append(" = 0");
			}
			if(differentNumber.equals("3")){
				sql.append(" < 0");
			}
			paramMap.put("differentNumber", differentNumber);
		}
		if (StringUtils.isNotBlank(sessionUser.getCurrentSystemOrg().getOrgId())) {
			sql.append(" and inventory_id in (select inventory_id from t_inventory_main where inventory_org_id in (  " + sessionUser.getFilialeSystemOrgIds() + " )" + ")");
		}
		String groupSql = " GROUP BY container_code, inventory_id";
		paging = baseService.pagingParamMap(paging,  sql.toString() + groupSql, paramMap, InventorySumDto.class);
		Object o = namedJdbcTemplate.queryForObject(sql.toString(), paramMap, new BeanPropertyRowMapper(InventorySumDto.class));
		InventorySumDto sum = new InventorySumDto();
		sum.setAllNum(((InventorySumDto) o).getAllNum());
		sum.setActualNum(((InventorySumDto) o).getActualNum());
		sum.setDifferentNum(((InventorySumDto) o).getDifferentNum());

		sum.setContainerCode("总计");
		List<InventorySumDto> data = (List<InventorySumDto>) paging.getData();
		data.add(sum);
		paging.setData(data);
		return paging;
	}

	/**
	 * 新增盘点单明细
	 * insert into t_inventory_detail( inventory_detail_id, inventory_id, \
	 * inventory_account,inventory_real_name, area_id, area_name, inventory_time, epc_id, is_have_different, create_time, \
	 * create_account, create_real_name )VALUES( :inventoryDetailId ,:inventoryId ,\
	 * :inventoryAccount,:inventoryRealName ,:areaId ,:areaName ,:inventoryTime ,:epcId ,:isHaveDifferent ,:createTime\
	 *  ,:createAccount ,:createRealName )
	 * @param inventoryDetail
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean addInventoryDetail(SystemUser sessionUser, InventoryDetail inventoryDetail) {
		inventoryDetail.setInventoryDetailId(UUIDUtil.creatUUID());
		inventoryDetail.setCreateAccount(sessionUser.getAccount());
		inventoryDetail.setCreateRealName(sessionUser.getRealName());
		int result = namedJdbcTemplate.update(insertInventoryDetail, new BeanPropertySqlParameterSource(inventoryDetail));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 根据盘点单号查询盘点详细列表
	 * @param inventoryId 盘点单号
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InventoryDetail> listInventoryDetailByInventoryId(String inventoryId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "select * from t_inventory_detail where inventory_id = ? ";
		List<InventoryDetail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(InventoryDetail.class),
				inventoryId);
		return list;
	}

	/**
	 * 根据epcId和盘点单号查询明细表
	 * @param epcId
	 * @param inventoryId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public InventoryDetail queryInventoryDetailByepcId(String epcId, String inventoryId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String sql = "select * from t_inventory_detail where inventory_id = ? and epc_id = ?";
		List<InventoryDetail> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(InventoryDetail.class),
				inventoryId, epcId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 更新盘点单明细
	 * @param sessionUser
	 * @param inventoryDetail
	 * @return
	 */
	public AjaxBean updateInventoryDetail(SystemUser sessionUser, InventoryDetail inventoryDetail) {
		inventoryDetail.setCreateAccount(sessionUser.getAccount());
		inventoryDetail.setCreateRealName(sessionUser.getRealName());
		int result = namedJdbcTemplate.update(updateInventoryDetailForHandset, new BeanPropertySqlParameterSource(inventoryDetail));
		return AjaxBean.returnAjaxResult(result);
	}

	public void addInventoryDetailFromHandset(SystemUser sessionUser,String inventoryId, String areaId,List<String> epcIdList) throws Exception{
		Area findArea = areaService.getAreaByAreaId(areaId);
		// 获取所有epcId
		List<String> fullEpcId = getEpcIdList(epcIdList);

		// 新增盘点明细
		for (String epcId : fullEpcId) {
			//根据epcId查明细是否存在
			List<InventoryDetail> details = jdbcTemplate.query(queryInventoryDetailByEpcAndInventoryId,
					new BeanPropertyRowMapper(InventoryDetail.class), inventoryId, epcId);
			Container container = containerService.getContainerByEpcId(epcId);
			//若存在，使用传进来的areaId跟明细中的oldAreaId做比较后，更新明细的areaId
			//20180702 与朱文沟通 这里需要改造  不在当前盘点单中 但是在器具表中存在的就新建盘点到当前仓库中
			// 如果不在器具表中的忽略掉,原因是这个epcid可能不在CMC系统中进行管理
			if (CollectionUtils.isNotEmpty(details))
			{
				InventoryDetail detail = details.get(0);
				//TODO 需要做器具校验 是否在当前仓库 如果不在就设差异是新增器具
				//盘点数据不为空 说明器具肯定在表
				Integer different = findArea.getAreaId().equals(detail.getOldAreaId())
							//is_have_different = 1 【没有差异】盘点区域和旧区域old_area_id 是一致的。
							? Integer.valueOf(InventoryDifferent.NOT_DIFFERENT.getCode())
							//is_have_different = 2 【有区域差异】盘点区域和旧区域old_area_id 是不一致的
							: Integer.valueOf(InventoryDifferent.AREA_DIFFERENT.getCode());
				detail.setIsHaveDifferent(different);
				detail.setInventoryTime(new Timestamp(System.currentTimeMillis()));
				detail.setAreaId(findArea.getAreaId());
				detail.setAreaName(findArea.getAreaName());
				detail.setInventoryAccount(sessionUser.getAccount());
				detail.setInventoryRealName(sessionUser.getRealName());
				detail.setInventoryNumber(SysConstants.INTEGER_1);  //盘点到数量：1。因为一个EPC只可能盘点到一次。
				this.updateInventoryDetail(sessionUser, detail);
			}
			else
			{
				if(null != container)
				{
					// 不存在盘点单中则新增数据，扫描到新器具
					// 新建盘点单的时候是把流转单属于当前收货仓库的器具进行盘点,不存在当前盘点单的器具说明原来不应该在当前仓库所以要新增
					InventoryDetail inventoryDetail = new InventoryDetail();
					inventoryDetail.setInventoryDetailId(UUIDUtil.creatUUID());
					inventoryDetail.setInventoryId(inventoryId);
					inventoryDetail.setInventoryAccount(sessionUser.getAccount()); //盘点人账号
					inventoryDetail.setInventoryRealName(sessionUser.getRealName()); //盘点人姓名
					inventoryDetail.setInventoryTime(new Timestamp(System.currentTimeMillis()));
					inventoryDetail.setEpcId(epcId);
					inventoryDetail.setContainerTypeId(container.getContainerTypeId());
					inventoryDetail.setContainerTypeName(container.getContainerTypeName());
					inventoryDetail.setContainerCode(container.getContainerCode());
					inventoryDetail.setContainerName(container.getContainerName());
					inventoryDetail.setSystemNumber(SysConstants.INTEGER_0); //需盘点数量：0
					inventoryDetail.setInventoryNumber(SysConstants.INTEGER_1); //盘点到数量：1
					inventoryDetail.setIsHaveDifferent(Integer.valueOf(InventoryDifferent.NEW_SCAN.getCode()));
					inventoryDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
					inventoryDetail.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
					inventoryDetail.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
					inventoryDetail.setAreaId(findArea.getAreaId());
					inventoryDetail.setAreaName(findArea.getAreaName());
					this.addInventoryDetail(sessionUser, inventoryDetail);
				}
			}
		}
	}

	/**
	 * 根据epcId修改器具最新流转记录
	 * @param sessionUser
	 * @param detail
	 * @return
	 */
	public AjaxBean updateCirculateLatestForInventory(SystemUser sessionUser, InventoryDetail detail) {
		AjaxBean ajaxBean = circulateService.buildAndInsertCirculateHistoryForModifyArea(sessionUser,detail.getEpcId(),detail.getAreaId());
		if (ajaxBean.getStatus() == StatusCode.STATUS_200){
			int result = jdbcTemplate.update(updateCirculateLatestForInventory,detail.getInventoryDetailId(),detail.getEpcId());
			ajaxBean = AjaxBean.returnAjaxResult(result);
		}
		return ajaxBean;
	}

	// private method
	private List<String> getEpcIdList(List<String> epcIdList) throws Exception {
		if (epcIdList == null)
			throw new Exception("epcId" + StatusCode.STATUS_305_MSG);
		for (String epcId : epcIdList) {
			Container container = containerService.getContainerByEpcId(epcId);
			if (container == null) {
				throw new Exception("该器具" + epcId + "不存在,请联系管理员核实!");
			}
			List<Container> containerList = new ArrayList<>();
			if(container.getIsTray().intValue()==1)
			{//托盘 取出托盘中不在流转单明细的器具 插入流转单明细中
				List<ContainerGroup> containerGroups = containerGroupService.getContainerGroupInfo(container.getEpcId());
				if(CollectionUtils.isNotEmpty(containerGroups))
				{//有组 需要把托盘上所有器具得到
					String groupId = containerGroups.get(0).getGroupId();
					containerList = jdbcTemplate.query(queryContainerNotExistsCirulateOrderDeatil,
							new BeanPropertyRowMapper(Container.class), groupId,groupId);
				}
				if (CollectionUtils.isNotEmpty(containerList)) {
					epcIdList.remove(epcId);
					for (Container container1 : containerList) {
						epcIdList.add(container1.getEpcId());
					}
				}
			}
		}
		return epcIdList;
	}
	/**
	 * [盘点单详情]页面，提交盘点数量按钮监听事件
	 * @param ajaxBean
	 * @param sessionUser
	 * @param inventoryDetailId 盘点明细ID
	 * @param inventoryNumber 盘点到数量
	 * @return
	 */
	public AjaxBean submitInventoryNumber(AjaxBean ajaxBean, SystemUser sessionUser, String inventoryDetailId,Integer inventoryNumber) {
		int result = jdbcTemplate.update(updateInventoryNumber, inventoryNumber,sessionUser.getAccount(),sessionUser.getRealName(),inventoryDetailId);
		return AjaxBean.returnAjaxResult(result);
	}
}

