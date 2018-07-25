package com.cdc.cdccmc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cdc.cdccmc.common.util.SysConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.common.enums.MaintainState;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Maintain;
import com.cdc.cdccmc.domain.container.ContainerScrap;
import com.cdc.cdccmc.domain.sys.SystemUser;

/**
 * 器具维修service
 * 
 * @author Jerry
 * @date 2018/1/16 10:53
 */

@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class MaintainService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MaintainService.class);
	@Autowired
	private BaseService baseService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private ContainerScrapService containerScrapService;
	
	@Value("#{sql['updateMaintainScrapWayReject']}")
	private String updateMaintainScrapWayReject;
	@Value("#{sql['queryMaintainByEpcIdAndOrgId']}")
	private String queryMaintainByEpcIdAndOrgId;
	@Value("#{sql['finishMaintain']}")
	private String finishMaintain;
	@Value("#{sql['updateMaintainAgreeScrap']}")
	private String updateMaintainAgreeScrap;
	@Value("#{sql['queryMaintainById']}")
	private String queryMaintainById;
	@Value("#{sql['applyMaintain']}")
	private String applyMaintain;
	@Value("#{sql['confirmMaintainLevel']}")
	private String confirmMaintainLevel;
	@Value("#{sql['listApplyMaintainContainer']}")
	private String listApplyMaintainContainer;
	@Value("#{sql['maintainAppraisal']}")
	private String maintainAppraisal;
	@Value("#{sql['queryMaintainId']}")
	private String queryMaintainId;

	/**
	 * 根据ecpId 查询出最后一条该器具的维修记录
	 * 
	 * @param ecpId
	 *            epc编号
	 * @return
	 */
	public Maintain queryLastMaintainByEcpId(String ecpId, String maintainApplyOrgId) {
		List<Maintain> list = jdbcTemplate.query(queryMaintainByEpcIdAndOrgId, new BeanPropertyRowMapper(Maintain.class), ecpId,
				maintainApplyOrgId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 获取当前仓库在库维修状态下，并且维修级别为空的器具
	 * 
	 * @param applyOrgId
	 *            报修公司ID
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Maintain> listMaintainContainer(String applyOrgId) {
		String sql = "SELECT * from T_MAINTAIN where maintain_apply_org_id = ? and maintain_state = ? and (maintain_level IS NULL OR maintain_level = ?)  order by maintain_apply_time desc ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), applyOrgId,
				MaintainState.IN_ORG.getCode(), "");
	}

	/**
	 * 器具维修列表带分页
	 * 
	 * @param paging
	 * @param sessionUser
	 * @param maintain
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Paging pagingMaintain(Paging paging, SystemUser sessionUser, Maintain maintain, String startDate,
			String endDate, String outMode) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from T_MAINTAIN t where 1=1 ");
		if (StringUtils.isNotBlank(outMode)) {
			sql = new StringBuilder(
					"select * from (SELECT m.maintain_id, m.epc_id, m.print_code, m.container_code, m.container_type_id, m.container_type_name, m.maintain_state, m.order_code, m.maintain_level, m.maintain_org_id, m.maintain_org_name, m.maintain_apply_time, m.maintain_apply_account, m.maintain_apply_real_name, m.maintain_apply_org_id, m.maintain_apply_org_name, m.maintain_apply_area_id, m.maintain_apply_area_name, m.maintain_check_time, m.maintain_check_account, m.maintain_check_real_name, m.maintain_check_org_id, m.maintain_check_org_name, m.maintain_finish_time, m.maintain_finish_account, m.maintain_finish_real_name, m.maintain_finish_org_id, m.maintain_finish_org_name FROM t_maintain m, t_maintain_level l WHERE m.maintain_level = l.maintain_level and m.maintain_state ='5' and ((m.maintain_finish_time - m.maintain_apply_time) > l.maintain_hour*3600 )union  SELECT m.maintain_id, m.epc_id, m.print_code, m.container_code, m.container_type_id, m.container_type_name, m.maintain_state, m.order_code, m.maintain_level, m.maintain_org_id, m.maintain_org_name, m.maintain_apply_time, m.maintain_apply_account, m.maintain_apply_real_name, m.maintain_apply_org_id, m.maintain_apply_org_name, m.maintain_apply_area_id, m.maintain_apply_area_name, m.maintain_check_time, m.maintain_check_account, m.maintain_check_real_name, m.maintain_check_org_id, m.maintain_check_org_name, m.maintain_finish_time, m.maintain_finish_account, m.maintain_finish_real_name, m.maintain_finish_org_id, m.maintain_finish_org_name FROM t_maintain m, t_maintain_level l WHERE m.maintain_level = l.maintain_level and m.maintain_state in ('1','2') and ((now() - m.maintain_apply_time) > l.maintain_hour*3600 )) t where 1=1");
		}
		if (StringUtils.isNotBlank(sessionUser.getFilialeSystemOrgIds())) {
			sql.append(" and t.maintain_apply_org_id in(" + sessionUser.getFilialeSystemOrgIds() + ") ");
		}
		if (StringUtils.isNotBlank(startDate)) {
			sql.append(" and t.maintain_apply_time >= :startDate  ");
			paramMap.put("startDate", startDate);
		}
		if (StringUtils.isNotBlank(endDate)) {
			sql.append(" and t.maintain_apply_time <= :endDate ");
			paramMap.put("endDate", endDate);
		}
		if (StringUtils.isNotBlank(maintain.getEpcId())) {
			sql.append(" and t.epc_id = :epcId ");
			paramMap.put("epcId", maintain.getEpcId());
		}
		if (StringUtils.isNotBlank(maintain.getMaintainState())) {
			sql.append(" and t.maintain_state = :maintainState ");
			paramMap.put("maintainState", maintain.getMaintainState());
		}
		if (StringUtils.isNotBlank(maintain.getPrintCode())) {
			sql.append(" and t.print_code = :printCode ");
			paramMap.put("printCode", maintain.getPrintCode());
		}
		sql.append(" order by t.maintain_apply_time desc ");
		return baseService.pagingParamMap(paging, sql.toString(), paramMap, Maintain.class);
	}

	/**
	 * 添加维修/报废器具 1.把对应的器具插入到保修表
	 * 
	 * @param maintain
	 *            maintainState 只能是在库维修或者待报废
	 * @return
	 */
	public AjaxBean addMaintain(Maintain maintain, SystemUser sessionUser) {
		maintain.setMaintainId(UUIDUtil.creatUUID());
		maintain.setMaintainApplyAccount(sessionUser.getAccount());
		maintain.setMaintainApplyRealName(sessionUser.getRealName());
		maintain.setMaintainApplyOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		maintain.setMaintainApplyOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		maintain.setMaintainState(MaintainState.IN_ORG.getCode()); // 设置为“在库维修”
		String sql = "INSERT INTO t_maintain (maintain_id, epc_id, print_code, container_code, container_type_id, container_type_name, maintain_state,maintain_level, maintain_apply_time, maintain_apply_account, maintain_apply_real_name, maintain_apply_org_id, maintain_apply_org_name,maintain_apply_area_id,maintain_apply_area_name) VALUES (:maintainId, :epcId, :printCode, :containerCode, :containerTypeId, :containerTypeName, :maintainState,  :maintainLevel, sysdate(), :maintainApplyAccount, :maintainApplyRealName, :maintainApplyOrgId,:maintainApplyOrgName,:maintainApplyAreaId,:maintainApplyAreaName)";
		int result = namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(maintain));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 根据epc编号和维修状态查找维修库
	 *
	 * @param epcIds
	 *            EPC编号
	 * @param maintainState
	 *            维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
	 * @param applyOrgId
	 *            报修公司ID
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> findMaintainByEpcIdsAndState(String epcIds, String maintainState, String applyOrgId) {
		LOG.info("---findMaintainByEpcIdsAndState   epcIds="+epcIds+"   maintainState="+maintainState+"   applyOrgId="+applyOrgId);
		StringBuffer sql = new StringBuffer("select distinct(epc_id) epcid from t_maintain m")
				.append(" left join ( select epc_id epcId from t_maintain m where epc_id in(")
				.append(epcIds).append(") and m.maintain_state = 1").append(" and maintain_apply_org_id = ?")
				.append(") t2 on m.epc_id = t2.epcId where epc_id in(").append(epcIds).append(")  and maintain_apply_org_id = ?")
				.append(" and t2.epcId is null");
		LOG.info("---findMaintainByEpcIdsAndState  sql="+sql);
		List<String> list = jdbcTemplate.queryForList(sql.toString(), String.class,applyOrgId,applyOrgId);
		LOG.info("---findMaintainByEpcIdsAndState  list="+list);
		StringBuffer sql2 = new StringBuffer("select distinct(epc_id) epcid from t_container c")
				.append(" left join ( select epc_id epcId from t_maintain m where epc_id in(")
				.append(epcIds).append(") ").append(" and maintain_apply_org_id = ?")
				.append(") t2 on c.epc_id = t2.epcId where epc_id in(").append(epcIds).append(")  and t2.epcId is null");
		List<String> list2 = jdbcTemplate.queryForList(sql2.toString(), String.class,applyOrgId);
		LOG.info("---findMaintainByEpcIdsAndState  list2="+list2);
		if(CollectionUtils.isNotEmpty(list))
		{
			if(CollectionUtils.isNotEmpty(list2))
			{
				list.addAll(list2);
			}
			return list;
		}
		else if(CollectionUtils.isNotEmpty(list2))
		{
			return list2;
		}
		return null;
	}

	/**
	 * 根据epc编号和维修状态查找维修库
	 * 
	 * @param epcId
	 *            EPC编号
	 * @param maintainState
	 *            维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
	 * @param applyOrgId
	 *            报修公司ID
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Maintain findMaintainByEpcAndState(String epcId, String maintainState, String applyOrgId) {
		LOG.info("---findMaintainByEpcAndState   epcId="+epcId+"   maintainState="+maintainState+"   applyOrgId="+applyOrgId);
		String sql = "select * from T_MAINTAIN where epc_id =? and maintain_state = ? and maintain_apply_org_id = ?";
		List<Maintain> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), epcId, maintainState,
				applyOrgId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 根据维修状态查找器具列表
	 * 
	 * @param maintainState
	 *            维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
	 * @param applyOrgId
	 *            报修公司ID
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Maintain> listMaintainContainerByState(String maintainState, String applyOrgId) {
		String sql = "select * from T_MAINTAIN where  maintain_state = ? and maintain_apply_org_id = ?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), maintainState, applyOrgId);
	}

	/**
	 * 将维修列表中的待报废器具修改成已报废器具
	 * 
	 * @param maintain
	 * @return
	 */
	public AjaxBean updateMaintainScrapContainer(SystemUser sessionUser, Maintain maintain) {
		String sql = "";
		if (StringUtils.isNotBlank(maintain.getMaintainApplyOrgId())) {// 如果器具报修仓库信息不为空，则要修改这些信息
			sql = "update t_maintain set maintain_apply_time =sysdate() ,maintain_apply_account = :maintainApplyAccount , maintain_apply_real_name = :maintainApplyRealName , maintain_apply_org_id = :maintainApplyOrgId,maintain_apply_org_name = :maintainApplyOrgName, maintain_org_id = :maintainOrgId ,maintain_org_name = :maintainOrgName , maintain_state =:maintainState   WHERE maintain_id = :maintainId ";
		} else {
			sql = "update t_maintain set maintain_state =:maintainState  WHERE maintain_id = :maintainId ";
		}
		int result = this.namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(maintain));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 查询器具在以下状态是否存在。维修状态。1在库维修2 出库维修， 并且报修仓库是当前仓库
	 * 
	 * @param epcId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Maintain existMaintain(String epcId, String maintain_apply_org_id) {
		String sql = "select * from T_MAINTAIN where epc_id =? and maintain_state != '3' and maintain_apply_org_id = ?";
		List<Maintain> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), epcId, maintain_apply_org_id);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 查询维修状态下的器具
	 * 
	 * @param ecpId
	 * @return
	 */
	public Maintain queryMaintainContainer(String epcId) {
		String sql = "select * from T_MAINTAIN where epc_id =? and maintain_state in (?,?) order by maintain_apply_time desc";
		List<Maintain> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Maintain.class), epcId,
				MaintainState.IN_ORG.getCode(), MaintainState.FINISH.getCode());
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * app端维修器具页面之修改维修器具表
	 * 
	 * @param maintain
	 * @return
	 */
//	public AjaxBean confirmMaintainLevel(SystemUser sessionUser, Maintain maintain) {
//		int result = this.namedJdbcTemplate.update(confirmMaintainLevel, new BeanPropertySqlParameterSource(maintain));
//		return AjaxBean.returnAjaxResult(result);
//	}

	/**
	 * 维修完成
	 * 
	 * @param sessionUser
	 * @param maintain
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean maintainFinsh(SystemUser sessionUser, Maintain maintain) {
		Map map = new HashMap();
		map.put("maintainId", maintain.getMaintainId());
		map.put("version", maintain.getVersion());
		map.put("newVersion", maintain.getVersion() + 1);
		map.put("maintainFinishAccount", sessionUser.getAccount());
		map.put("maintainFinishRealName", sessionUser.getRealName());
		map.put("maintainFinishOrgId", sessionUser.getCurrentSystemOrg().getOrgId());
		map.put("maintainFinishOrgName", sessionUser.getCurrentSystemOrg().getOrgName());
		int result = namedJdbcTemplate.update(finishMaintain, map);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 同意报废
	 * 
	 * @param sessionUser
	 * @param maintain
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean maintainScrap(SystemUser sessionUser, Maintain maintain) {
		Map map = new HashMap();
		map.put("maintainId", maintain.getMaintainId());
		map.put("version", maintain.getVersion());
		map.put("newVersion", maintain.getVersion() + 1);
		map.put("scrapAccount", sessionUser.getAccount());
		map.put("scrapRealName", sessionUser.getRealName());
		map.put("scrapWayId", maintain.getScrapWayId());
		map.put("scrapWayName", maintain.getScrapWayName());
		int count = namedJdbcTemplate.update(updateMaintainAgreeScrap, map);
		if (count == 1) {
			maintain = this.queryMaintainById(maintain.getMaintainId());
			ContainerScrap containerScrap = new ContainerScrap();
			containerScrap.setContainerScrapId(UUIDUtil.creatUUID());
			containerScrap.setEpcId(maintain.getEpcId());
			containerScrap.setPrintCode(maintain.getPrintCode());
			containerScrap.setContainerCode(maintain.getContainerCode());
			containerScrap.setContainerTypeId(maintain.getContainerTypeId());
			containerScrap.setContainerTypeName(maintain.getContainerTypeName());
			containerScrap.setIsOut("1");
			containerScrap.setScrapTime(maintain.getScrapTime());
			containerScrap.setScrapAccount(maintain.getScrapAccount());
			containerScrap.setScrapRealName(maintain.getScrapRealName());
			containerScrap.setScrapWayId(maintain.getScrapWayId());
			containerScrap.setScrapWayName(maintain.getScrapWayName());
			containerScrap.setOrderCode(maintain.getOrderCode());
			containerScrap.setCreateTime(maintain.getScrapTime());
			containerScrap.setCreateAccount(maintain.getScrapAccount());
			containerScrap.setCreateRealName(maintain.getScrapRealName());
			containerScrap.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			containerScrap.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			containerScrapService.addContainerScrap(sessionUser, containerScrap);
			return AjaxBean.SUCCESS();
		}
		return AjaxBean.FAILURE();
	}

	/**
	 * 根据maintainId查询maintain信息
	 * 
	 * @param maintainId
	 *            return
	 */
	public Maintain queryMaintainById(String maintainId) {
		List<Maintain> list = jdbcTemplate.query(queryMaintainById, new BeanPropertyRowMapper(Maintain.class), maintainId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	
	/**
	 * 报修器具 1.把对应的器具插入到报修表
	 * @return
	 */
	public AjaxBean repairMaintain(Maintain maintain, SystemUser sessionUser) {
		maintain.setMaintainApplyBadReason(maintain.getMaintainApplyBadReason());
		maintain.setMaintainId(UUIDUtil.creatUUID());
		maintain.setMaintainApplyAccount(sessionUser.getAccount());
		maintain.setMaintainApplyRealName(sessionUser.getRealName());
		maintain.setMaintainApplyOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		maintain.setMaintainApplyOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		// 设置维修状态
		maintain.setMaintainState(MaintainState.IN_ORG.getCode()); // 设置为“在库维修”
		int result = namedJdbcTemplate.update(applyMaintain, new BeanPropertySqlParameterSource(maintain));
		return AjaxBean.returnAjaxResult(result);
	}

 	/**
	 * 维修鉴定
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean maintainAppraisal(AjaxBean ajaxBean,SystemUser sessionUser, Maintain maintain) {
		//如果维修ID为空，则需要从当前仓库找到这个EPC，获取维修ID
		if(StringUtils.isBlank(maintain.getMaintainId())) {
			List<Maintain> list = jdbcTemplate.query(queryMaintainId,new BeanPropertyRowMapper(Maintain.class), sessionUser.getCurrentSystemOrg().getOrgId(),maintain.getEpcId());
			if (CollectionUtils.isEmpty(list)) {
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("鉴定失败！["+maintain.getEpcId()+"]器具在当前仓库未报修！");
				return ajaxBean;
			}
			//获取维修ID
			maintain.setMaintainId(list.get(0).getMaintainId());
		}
		int result = jdbcTemplate.update(maintainAppraisal, maintain.getMaintainLevel(),sessionUser.getAccount(),sessionUser.getRealName(),sessionUser.getCurrentSystemOrg().getOrgId(),sessionUser.getCurrentSystemOrg().getOrgName(),maintain.getMaintainId());
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 维修完成
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean maintainFinish(SystemUser sessionUser, Maintain maintain) {
		Map map = new HashMap();
		map.put("maintainId", maintain.getMaintainId());
		map.put("maintainFinishAccount", sessionUser.getAccount());
		map.put("maintainFinishRealName", sessionUser.getRealName());
		map.put("maintainFinishOrgId", sessionUser.getCurrentSystemOrg().getOrgId());
		map.put("maintainFinishOrgName", sessionUser.getCurrentSystemOrg().getOrgName());
		map.put("maintainFinishBadReason", maintain.getMaintainFinishBadReason().trim());
		map.put("maintainFinishSolution", maintain.getMaintainFinishSolution().trim());
		int result = namedJdbcTemplate.update(finishMaintain, map);
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 列出当前仓库下所有报修器具，维修级别为空的，尚未鉴定维修级别的，此接口用于app端【器具报修】页面列表请求
	 * @param sessionUser
	 * @return
	 */
	public AjaxBean listApplyMaintainContainer(AjaxBean ajaxBean,SystemUser sessionUser) {
		List<Maintain> list = jdbcTemplate.query(listApplyMaintainContainer, new BeanPropertyRowMapper(Maintain.class),sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean.setList(list);
		return ajaxBean;
	}
}
