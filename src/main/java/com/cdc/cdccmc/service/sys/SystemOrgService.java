package com.cdc.cdccmc.service.sys;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.enums.OrgEnum;
import com.cdc.cdccmc.common.enums.OrgType;
import com.cdc.cdccmc.common.util.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.LogService;

/**
 * 组织机构
 * 
 * @author ZhuWen
 * @date 2018-01-08
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class SystemOrgService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemOrgService.class);

	@Autowired
	private LogService logService;
	@Autowired
	private BaseService baseService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Value("#{sql['insertSystemOrg']}")
	private String insertSystemOrg;
	@Value("#{sql['updateSystemOrg']}")
	private String updateSystemOrg;
	@Value("#{sql['deleteMemberOrg']}")
	private String deleteMemberOrg;
	@Value("#{sql['queryMemberOrg']}")
	private String queryMemberOrg;
	@Value("#{sql['insertMemberOrg']}")
	private String insertMemberOrg;
	@Value("#{sql['listMemberOrgByAccount']}")
	private String listMemberOrgByAccount;
	@Value("#{sql['listAllFilialeSystemOrg']}")
	private String listAllFilialeSystemOrg;
	@Value("#{sql['querySystemOrgById']}")
	private String querySystemOrgById;
	@Value("#{sql['querySystemOrgByName']}")
	private String querySystemOrgByName;
	@Value("#{sql['querySystemOrgByOrgCode']}")
	private String querySystemOrgByOrgCode;
	@Value("#{sql['listAllSystemOrg']}")
	private String listAllSystemOrg;
	
	/**
	 * 根据登录用户名查询隶属仓库
	 * @param account 登录用户名
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SystemOrg> listMemberOrgByAccount(String account) {
		return jdbcTemplate.query(listMemberOrgByAccount, new BeanPropertyRowMapper(SystemOrg.class),account);
	}

	public SystemOrg findById(String orgId) {
		List<SystemOrg> list = jdbcTemplate.query(querySystemOrgById, new BeanPropertyRowMapper(SystemOrg.class), orgId);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	/**
	 * 根据组织名称查询组织对象
	 * @param orgName
	 * @return
	 */
	public SystemOrg findByOrgName(String orgName) {
		List<SystemOrg> list = jdbcTemplate.query(querySystemOrgByName, new BeanPropertyRowMapper(SystemOrg.class), orgName);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}
	/**
	 * 根据组织代码查询组织对象
	 * @return
	 */
	public SystemOrg findByOrgCode(String orgCode) {
		List<SystemOrg> list = jdbcTemplate.query(querySystemOrgByOrgCode, new BeanPropertyRowMapper(SystemOrg.class), orgCode);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		return list.get(0);
	}

	/**
	 * 返回所有子公司列表
	 * @param orgId 父公司ID
	 * @return
	 */
	public List<SystemOrg> listAllFiliale(String orgId) {
		List<SystemOrg> result = new ArrayList<SystemOrg>();
		@SuppressWarnings("unchecked")
		List<SystemOrg> list = jdbcTemplate.query(listAllFilialeSystemOrg, new BeanPropertyRowMapper(SystemOrg.class),orgId);
		result.addAll(list);
		if (!CollectionUtils.isEmpty(list)) {
			// 递归获得所有下级组织机构
			recursionFiliale(result, list);
		}
		return result;
	}

	/**
	 * 递归获得所有下级组织机构
	 * 
	 * @param result
	 * @param list
	 */
	private void recursionFiliale(List<SystemOrg> result, List<SystemOrg> list) {
		String orgIds = "";
		for (SystemOrg org : list) {
			orgIds += "'" + org.getOrgId() + "',";
		}
		// 去掉尾部逗号
		if (orgIds.endsWith(",")) {
			orgIds = orgIds.substring(0, orgIds.length() - 1);
		}
		List<SystemOrg> filialeList = listFiliale(orgIds);
		// 每一次递归结果往result集合追加
		result.addAll(filialeList);
		// 如果结果集非空，继续递归
		if (!CollectionUtils.isEmpty(filialeList)) {
			recursionFiliale(result, filialeList);
		}
	}

	/**
	 * 返回某些组织ID的下级组织
	 * @param orgIds
	 * @return
	 */
	public List<SystemOrg> listFiliale(String orgIds) {
		String sql = "select o1.* from t_system_org o1,t_system_org o2 "
				+ " where o1.parent_org_id = o2.org_id and o2.org_id in ( " + orgIds + ")";
		List<SystemOrg> list = namedJdbcTemplate.query(sql, new BeanPropertyRowMapper(SystemOrg.class));
		return list;
	}
	/**
	 * 列出当前所选公司的所有子公司列表
	 * @param sessionUser 当前登录用户
	 * @param paging 分页支持
	 * @param orgName 组织机构、仓库ID
	 * @return
	 */
	public Paging pagingSystemOrg(SystemUser sessionUser, Paging paging, String orgName) {
		String sql = " select * from t_system_org where org_id in ( " + sessionUser.getFilialeSystemOrgIds() + " )  ";
		Map paramMap = new HashMap();
		if (StringUtils.isNotBlank(orgName)) {
			sql += " and org_name like :orgName ";
			paramMap.put("orgName", "%" + orgName + "%");
		}
		sql += " order by create_time desc ";
		paging = baseService.pagingParamMap(paging, sql, paramMap, SystemOrg.class);
		return paging;
	}

	/**
	 * 加载当前登录用户的隶属仓库下的所有子公司，包括当前选中仓库。<br/>
	 * 设置systemUser对象的两个字段值：filialeSystemOrgList、filialeSystemOrgIds
	 * 
	 * @param sessionUser
	 *            当前登录用户
	 * @return
	 */
	public SystemUser loadFilialeOrg(SystemUser sessionUser) {
		SystemOrg currentSystemOrg = sessionUser.getCurrentSystemOrg();

		// 加载隶属仓库下的所有子公司，包括当前选中仓库
		List<SystemOrg> allFiliale = listAllFiliale(currentSystemOrg.getOrgId());
		allFiliale.add(currentSystemOrg);
		sessionUser.setFilialeSystemOrgList(allFiliale);

		// 存储session登录用户，所选公司的所有子公司ID拼接出来的字符串，包含当前所选公司
		// 例如： '123','456','789' 其中123是当前所选公司
		String filialeSystemOrgIds = "";
		for (SystemOrg org : allFiliale) {
			filialeSystemOrgIds += "'" + org.getOrgId() + "',";
		}
		if (filialeSystemOrgIds.endsWith(",")) {
			filialeSystemOrgIds = filialeSystemOrgIds.substring(0, filialeSystemOrgIds.length() - 1);
		}
		sessionUser.setFilialeSystemOrgIds(filialeSystemOrgIds);
		return sessionUser;
	}

	/**
	 * 新增一个机构
	 * @param sessionUser 当前登录用户
	 * @param ajaxBean
	 * @param systemOrg  新增机构
	 * @return
	 */
	public AjaxBean addSystemOrg(SystemUser sessionUser, AjaxBean ajaxBean, SystemOrg systemOrg) {
		systemOrg.setOrgId(UUIDUtil.creatUUID());
		systemOrg.setMainOrgId(sessionUser.getCurrentSystemOrg().getMainOrgId()); // 住公司应与当前所选仓库的主公司保持一致
		systemOrg.setCreateAccount(sessionUser.getAccount());
		systemOrg.setCreateRealName(sessionUser.getRealName());
		systemOrg.setOrgTypeName(OrgType.getTypeName(systemOrg.getOrgTypeId()));
		systemOrg.setParentOrgName(findById(systemOrg.getParentOrgId()).getOrgName()); //上级组织名称
		int result = namedJdbcTemplate.update(insertSystemOrg, new BeanPropertySqlParameterSource(systemOrg));
		if (result > 0) { // 如果插入机构成功，重新加载当前登录用户的可见机构列表，应该包含刚新增成功的机构，而不是等待下次登录重新刷新
			logService.addLogAccount(sessionUser, "新增机构["+systemOrg.getOrgName()+"]");
			loadFilialeOrg(sessionUser); // 重新加载登录用户可见机构权限列表
		}
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 编辑一个机构
	 * @param sessionUser 当前登录用户
	 * @param ajaxBean
	 * @param systemOrg 编辑机构
	 * @return
	 */
	public AjaxBean editSystemOrg(SystemUser sessionUser, AjaxBean ajaxBean, SystemOrg systemOrg) {
		systemOrg.setModifyAccount(sessionUser.getAccount());
		systemOrg.setModifyRealName(sessionUser.getRealName());
		systemOrg.setParentOrgName(findById(systemOrg.getParentOrgId()).getOrgName()); //上级组织名称
		systemOrg.setOrgTypeName(OrgType.getTypeName(systemOrg.getOrgTypeId())); //组织类型名称
		int result = namedJdbcTemplate.update(updateSystemOrg, new BeanPropertySqlParameterSource(systemOrg));
		logService.addLogAccount(sessionUser, "编辑机构信息["+systemOrg.getOrgName()+"]");
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 启用或禁用指定机构
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemOrg 指定机构
	 * @return
	 */
	public AjaxBean changeActiveOrg(AjaxBean ajaxBean, SystemUser sessionUser, SystemOrg systemOrg) {
		String sql = "update t_system_org set is_active =?,modify_time=sysdate(),modify_account=?,modify_real_name=? where org_id = ?";
		int result = jdbcTemplate.update(sql, systemOrg.getIsActive(), sessionUser.getAccount(),
				sessionUser.getRealName(), systemOrg.getOrgId());
		logService.addLogAccount(sessionUser, "[" + (systemOrg.getIsActive() == 0 ? "启用" : "禁用") + "]组织机构或仓库：[" 
				+ findById(systemOrg.getOrgId()).getOrgName() + "]" );
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 获取所有组织机构
	 */
	public List<SystemOrg> listAllOrg() {
		return namedJdbcTemplate.query(listAllSystemOrg, new BeanPropertyRowMapper(SystemOrg.class));
	}
    /**
     * excel 批量上传
     * @param sessionUser
     * @param file
     * @return
     */
    public AjaxBean batchUpload(SystemUser sessionUser, File file) {
		//数据校验
		AjaxBean ajaxBean = validExcelData(sessionUser,file.getPath());
		//如果校验不通过，直接返回
		if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		ConcurrentLinkedQueue<SystemOrg> orgList = new ConcurrentLinkedQueue<>();
		//校验通过,构建map数组
		List<Map<Integer, String>> excelList = ajaxBean.getList();
		Map<String,String> orgTypeMap = new HashMap();
		//遍历orgType封装指定对象
		for (OrgType e:OrgType.values()) {
			orgTypeMap.put(e.getTypeName(),e.getTypeId());
		}
		//获取orgNameMap
		for (int i = 0; i < excelList.size(); i++) {
			if(null == excelList.get(i)){ //如果是空行，直接跳过
				continue;
			}
			SystemOrg org = new SystemOrg();
			org.setOrgId(UUIDUtil.creatUUID());
			org.setOrgName(StringUtils.trim(excelList.get(i).get(0)));
			org.setOrgCode(StringUtils.trim(excelList.get(i).get(1)));
			org.setOrgTypeId(orgTypeMap.get(StringUtils.trim(excelList.get(i).get(2))));
			org.setOrgTypeName(StringUtils.trim(excelList.get(i).get(2)));
			org.setContactName(excelList.get(i).get(3));
			org.setContactPhone(excelList.get(i).get(4));
			String orgName = excelList.get(i).get(5);
			SystemOrg findOrg = findByOrgName(orgName);
			org.setParentOrgId(findOrg.getOrgId());
			org.setParentOrgName(findOrg.getOrgName());
			org.setMainOrgId(sessionUser.getCurrentSystemOrg().getMainOrgId());
			org.setCreateAccount(sessionUser.getAccount());
			org.setCreateRealName(sessionUser.getRealName());
			//添加车辆到新增队列
			orgList.add(org);

			//如果达到批量插入条数，就进行批量插入
			if(orgList.size() >= SysConstants.MAX_INSERT_NUMBER){
				this.batchInsertOrg(sessionUser,orgList);
				orgList.clear();
			}
		}
		//如果达到批量插入条数，就进行批量插入
		if(orgList.size() > SysConstants.INTEGER_0){
			this.batchInsertOrg(sessionUser,orgList);
			orgList.clear();
		}
		return ajaxBean;

    }

	/**
	 * excel数据校验
	 * @param path
	 * @return ajaxBean
	 * 		list:excel需要插入的内容
	 * 	    bean:MAP<ORGNAME,ORGID>
	 */
	private AjaxBean validExcelData(SystemUser sessionUser,String path) {
		AjaxBean ajaxBean = new AjaxBean();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,6);
		} catch (Exception e) { //文件批量导入失败！
			LOG.error(StatusCode.STATUS_402_MSG, e);
			logService.addLogError(sessionUser, e, StatusCode.STATUS_402_MSG, null);
			ajaxBean.setStatus(StatusCode.STATUS_402);
			ajaxBean.setMsg(StatusCode.STATUS_402_MSG);
			return ajaxBean;
		}
		if(mapList.size() == 0){ //如果数据一行都没有，未检测到需导入数据，请检查上传文件内数据输入是否正确。
			LOG.error(StatusCode.STATUS_403_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_403);
			ajaxBean.setMsg(StatusCode.STATUS_403_MSG);
			return ajaxBean;
		}
		if (mapList.size()>SysConstants.MAX_UPLOAD_ROWS){
			LOG.error(StatusCode.STATUS_401_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_401);
			ajaxBean.setMsg(StatusCode.STATUS_401_MSG);
			return ajaxBean;
		}
		List<String> errList = new ArrayList<String>();
		LOG.info("本次需批量导入"+mapList.size()+"条组织数据");

		//循环enum,OrgType
		Map<String,String> orgTypeMap = new HashMap<>();
		List<String> orgTypeList = new ArrayList<>();
		for(OrgType o:OrgType.values()){
			orgTypeMap.put(o.getTypeName(),o.getTypeId());
			orgTypeList.add(o.getTypeName());
		}
		int row = 1;
		for(Map<Integer, String> map : mapList){
			row = row + 1;
			if(null == map){  //如果是空行，直接跳过
				continue;
			}
			String orgName = map.get(0);
			if(StringUtils.isBlank(orgName)){
				errList.add("第"+row+"行，组织名称不能为空。");
			}else if(orgName.length() > 50){
				errList.add("第"+row+"行，组织名称["+orgName+"]长度不能超过50个字符。");
			}else {
				SystemOrg findOrg = findByOrgName(orgName);
				if(null != findOrg){
					errList.add("第"+row+"行，组织名称["+orgName+"]已存在，请重试。");
				}else{
					int row2 = 1;
	            	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
	            		if(null != map2 && map.get(0).equals(map2.get(0)) && row != row2){ //如果excel内发现重复EPC编号
	            			errList.add("第"+row+"行，组织名称["+orgName+"]在excel内重复，请核查。");
	            			break two;
	            		}
	            	}
				}
			}

			String orgCode = map.get(1);
			if(StringUtils.isBlank(orgCode)){
				errList.add("第"+row+"行，组织代码不能为空。");
			}
//			else if(!orgCode.matches(SysConstants.REGEX_ORG_CODE)){
//				errList.add("第"+row+"行，组织代码["+orgCode+"]必须为2到7位大写字母或数字。");
//			}
			else {
				SystemOrg findOrg = findByOrgCode(orgCode);
				if(null != findOrg){
					errList.add("第"+row+"行，组织代码["+orgCode+"]已存在，请重试。");
				}else{
					int row2 = 1;
	            	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
	            		if(null != map2 && map.get(1).equals(map2.get(1)) && row != row2){ //如果excel内发现重复EPC编号
	            			errList.add("第"+row+"行，组织代码["+orgCode+"]在excel内重复，请核查。");
	            			break two;
	            		}
	            	}
				}
			}

			String orgTypeName = map.get(2);
			if(StringUtils.isBlank(orgTypeName)){
				errList.add("第"+row+"行，组织类型名称不能为空。");
			} else if (orgTypeName.length() > 20){
				errList.add("第"+row+"行，组织类型名称["+orgTypeName+"]长度不能超过20个字符。");
			} else if (!orgTypeList.contains(orgTypeName)){
				errList.add("第"+row+"行，组织类型名称["+orgTypeName+"]不存在，请核查。");
			}

			String contactName = map.get(3);
			if(StringUtils.isNotBlank(contactName) && contactName.length() > 20){
				errList.add("第"+row+"行，联系人姓名长度不能超过20个字符。");
			}

			String contactPhone = map.get(4);
			if(StringUtils.isNotBlank(contactPhone) && !(contactPhone.matches(SysConstants.REGEX_SHIPPER_CONTAINER_NUMBER))){
				errList.add("第"+row+"行，联系电话只能是数字,空格和'-'的组合且长度为1到20位。");
			}

			String parentOrgName = map.get(5);
			if(StringUtils.isBlank(parentOrgName)){
				errList.add("第"+row+"行，上级组织名称不能为空。");
			}else if(parentOrgName.length() > 50){
				errList.add("第"+row+"行，上级组织名称["+parentOrgName+"]不能超过50个字符。");
			}else{
				SystemOrg findOrg = findByOrgName(parentOrgName);
				if(null == findOrg){
					errList.add("第"+row+"行，上级组织["+parentOrgName+"]不存在，请核查。");
				}else{
					if(findOrg.getOrgId().equals(OrgEnum.VIRTUAL_LOST_ORG.getOrgId())){
						errList.add("第"+row+"行，上级组织不能是["+OrgEnum.VIRTUAL_LOST_ORG.getOrgName()+"]，请修改。");
					}else if(findOrg.getOrgId().equals(OrgEnum.VIRTUAL_SCRAP_ORG.getOrgId())){
						errList.add("第"+row+"行，上级组织不能是["+OrgEnum.VIRTUAL_SCRAP_ORG.getOrgName()+"]，请修改。");
					}else if(findOrg.getOrgId().equals(OrgEnum.VIRTUAL_SELL_ORG.getOrgId())){
						errList.add("第"+row+"行，上级组织不能是["+OrgEnum.VIRTUAL_SELL_ORG.getOrgName()+"]，请修改。");
					}
				}
			}
		}
		if (errList.size() > 0) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setList(errList);
		} else {
			ajaxBean.setList(mapList);
			ajaxBean.setStatus(StatusCode.STATUS_200);
			ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		}
		return ajaxBean;
	}

	private List<String> queryOrgCodes(String orgCodes){
		String sql = "select org_code from T_SYSTEM_ORG where org_code in("+orgCodes+")";
		List<String> list = namedJdbcTemplate.queryForList(sql.toString(),new HashMap<>(),String.class);
		return list;
	}

	private List<Map<String,Object>> queryorgNameAndId(String orgNames){
		String sql = " select org_name AS orgName,org_id as orgId from T_SYSTEM_ORG where org_name in ("+orgNames+") ";
		List<Map<String,Object>> list = namedJdbcTemplate.queryForList(sql,new HashMap<>());
		return list;
	}


    /**
     * 查寻出指定的列名是否存在
     * @param columName
     * @param values
     * @return
     */
	private List<String> queryColumValus(String columName,String values){
		StringBuffer sql = new StringBuffer("select ");
		String whereSql = "";
		switch (columName){
			case "orgName":
				sql.append(" org_name ");
				whereSql = " where org_name in("+values+") ";
				break;
			case "orgCode":
				sql.append(" org_code ");
				whereSql = " where org_code in("+values+") ";
				break;
		}
		sql.append(" from T_SYSTEM_ORG ").append(whereSql);
		List<String> list = namedJdbcTemplate.queryForList(sql.toString(),new HashMap<>(),String.class);
		return list;
	}

	/**
	 * 组织机构批量插入
	 * @param sessionUser 
	 * @param orgList
	 */
	private void batchInsertOrg(SystemUser sessionUser, ConcurrentLinkedQueue<SystemOrg> orgList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(orgList.toArray());
		namedJdbcTemplate.batchUpdate(insertSystemOrg, params);
		logService.addLogAccount(sessionUser, "批量导入机构"+orgList.size()+"条");
		LOG.info("批量导入机构"+orgList.size()+"条");
	}

	/**
	 * 更新指定账号的隶属机构权限
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param systemUser 指定账号
	 * @param orgList 最新隶属机构列表
	 */
	@SuppressWarnings("unchecked")
	public void updateUserMemberOrg(AjaxBean ajaxBean, SystemUser sessionUser,
			SystemUser systemUser, List orgList) {
		List<SystemOrg> memberOrgList = jdbcTemplate.query(queryMemberOrg, new BeanPropertyRowMapper(SystemOrg.class), systemUser.getAccount());
		for(SystemOrg memberOrg : memberOrgList){
			two:for(SystemOrg filialeOrg : sessionUser.getFilialeSystemOrgList()){ 
				if(memberOrg.getOrgId().equals(filialeOrg.getOrgId())){ //如果当前登录用户拥有指定账号的隶属仓库权限，先全部删除，后面insert即可
					jdbcTemplate.update(deleteMemberOrg, systemUser.getAccount(), memberOrg.getOrgId());
					break two;
				}
			}
		}
		//如果新的隶属机构权限不为空，则新增新的隶属机构权限
		if (!CollectionUtils.isEmpty(orgList)) {
			Map<String, ?>[] batchValues = new HashMap[orgList.size()];
			List<String> orgNameList = new ArrayList<String>();
			for (int i = 0; i < orgList.size(); i++) {
				String orgId = orgList.get(i).toString();
				SystemOrg findOrg = findById(orgId);
				Map param = new HashMap();
				param.put("account", systemUser.getAccount());
				param.put("createAccount", sessionUser.getAccount());
				param.put("createRealName", sessionUser.getRealName());
				param.put("realName", systemUser.getRealName());
				param.put("orgName", findOrg.getOrgName());
				orgNameList.add(findOrg.getOrgName());
				param.put("orgId", orgId);
				batchValues[i] = param;
			}
			namedJdbcTemplate.batchUpdate(insertMemberOrg, batchValues);
			logService.addLogAccount(sessionUser,
					"更新账号[" + systemUser.getAccount() + "]的隶属机构列表: " + JSONObject.toJSONString(orgNameList));
		}else{
			logService.addLogAccount(sessionUser,
					"更新账号[" + systemUser.getAccount() + "]的隶属机构列表为空");
		}
	}
}
