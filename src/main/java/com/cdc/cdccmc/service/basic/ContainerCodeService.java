package com.cdc.cdccmc.service.basic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import org.springframework.util.CollectionUtils;

import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.container.ContainerType;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.ContainerTypeService;
import com.cdc.cdccmc.service.LogService;

/**
 * 器具代码
 * @author Clm
 * @date 2018-01-04
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerCodeService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerCodeService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ContainerTypeService containerTypeService;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	@Value("#{sql['containerCodeInsert']}")
	private String containerCodeInsert;
	@Value("#{sql['queryContainerTypeByName']}")
	private String queryContainerTypeByName;
	@Value("#{sql['insertContainerType']}")
	private String insertContainerType;
	@Value("#{sql['insertContainerCode']}")
	private String insertContainerCode;
	@Value("#{sql['updateContainerCodeActive']}")
	private String updateContainerCodeActive;
	@Value("#{sql['listContainerCode']}")
	private String listContainerCode;
	@Value("#{sql['listActiveContainerCode']}")
	private String listActiveContainerCode;

	/**
	 * 用户操作器具代码管理列表查询/器具代码查询
	 */
	public Paging pagingContainerCode(Paging paging, SystemUser sessionUser, ContainerCode containerCode) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		StringBuilder sql = new StringBuilder("select * from t_container_code where 1=1 ");
		if(StringUtils.isNotBlank(containerCode.getContainerCode())){
			sql.append(" and container_code like :containerCode ");
			paramMap.put("containerCode", "%"+containerCode.getContainerCode().trim()+"%");
		}
		sql.append(" order by create_time desc ");
		Paging resultPaging = baseService.pagingParamMap(paging, sql.toString(), paramMap, ContainerCode.class);
		return resultPaging;
	}

	/**
	 * 新增器具代码
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param containerCode 新的器具代码
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AjaxBean addContainerCode(AjaxBean ajaxBean, SystemUser sessionUser, ContainerCode containerCode) {
		List<ContainerCode> codeList = jdbcTemplate.query(queryContainerTypeByName, new BeanPropertyRowMapper(ContainerCode.class), containerCode.getContainerTypeName());
		String containerTypeId = UUIDUtil.creatUUID();
		if(CollectionUtils.isEmpty(codeList)){ //如果器具类型不存在，则新增
			ConcurrentHashMap paramMap = new ConcurrentHashMap();
			paramMap.put("containerName", containerCode.getContainerName());
			paramMap.put("containerTypeId", containerTypeId);
			paramMap.put("containerTypeName", containerCode.getContainerTypeName());
			paramMap.put("createOrgId", sessionUser.getCurrentSystemOrg().getOrgId());
			paramMap.put("createOrgName", sessionUser.getCurrentSystemOrg().getOrgName());
			paramMap.put("createAccount", sessionUser.getAccount());
			paramMap.put("createRealName", sessionUser.getRealName());

			//新增器具类型
			int result = namedJdbcTemplate.update(insertContainerType, paramMap);
			if(result == 0){ //器具类型新增失败，直接返回，终止新增器具代码
				ajaxBean.setMsg(StatusCode.STATUS_330_MSG);
				ajaxBean.setStatus(StatusCode.STATUS_330);
				return ajaxBean;
			}
		}else{
			containerTypeId = codeList.get(0).getContainerTypeId();  //如果器具类型已存在，则获取UUID
		}
		containerCode.setContainerTypeId(containerTypeId);
		containerCode.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
		containerCode.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
		containerCode.setCreateRealName(sessionUser.getRealName());
		//新增器具代码
		int result = namedJdbcTemplate.update(insertContainerCode, new BeanPropertySqlParameterSource(containerCode));
		logService.addLogAccount(sessionUser, "新增器具代码[" + containerCode.getContainerCode() + "]"+(result==1?"成功":"失败"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 更新器具代码的禁用和启用
	 * @param sessionUser 
	 */
	public AjaxBean isActiveContainerCode(SystemUser sessionUser, ContainerCode containerCode) {
		ConcurrentHashMap paramMap = new ConcurrentHashMap();
		paramMap.put("active", containerCode.getIsActive());
		paramMap.put("containerCode", containerCode.getContainerCode());
		int result = namedJdbcTemplate.update(updateContainerCodeActive, paramMap);
		logService.addLogAccount(sessionUser, "更新器具代码[" + containerCode.getContainerCode() + "]为"+(containerCode.getIsActive()==0?"启用":"禁用"));
		return AjaxBean.returnAjaxResult(result);
	}

	/**
	 * 器具代码批量导入
	 * @param sessionUser 
	 */
	public void batchInsertcontainerCode(SystemUser sessionUser, ConcurrentLinkedQueue<ContainerCode> containerCodeList) {
		SqlParameterSource[] params = SqlParameterSourceUtils.createBatch(containerCodeList.toArray());
		namedJdbcTemplate.batchUpdate(insertContainerCode, params);
		logService.addLogAccount(sessionUser, "批量导入器具代码" + containerCodeList.size() + "条");
	}

	public List<ContainerCode> listAllContainerCode(){
		return namedJdbcTemplate.query(listContainerCode,new HashMap(),new BeanPropertyRowMapper(ContainerCode.class));
	}

	public List<ContainerCode> listActiveContainerCode() {
		return namedJdbcTemplate.query(listActiveContainerCode,new HashMap(),new BeanPropertyRowMapper(ContainerCode.class));
	}

	/**
	 * 处理器具代码批量导入
	 * @param ajaxBean
	 * @param sessionUser 当前登录用户
	 * @param file 批量导入文件
	 * @return
	 */
	public AjaxBean batchUpload(AjaxBean ajaxBean,SystemUser sessionUser, File file) {
		//组装所有器具类型为map格式，key=containerTypeId, value=器具类型对象
		ConcurrentHashMap<String, ContainerType> containerTypeMap = new ConcurrentHashMap<String, ContainerType>();

		//数据非空校验
		ajaxBean = validExcelData(file.getPath());
		//如果校验不通过，直接返回
		if(ajaxBean.getStatus() != StatusCode.STATUS_200) {
			return ajaxBean;
		}
		List<Map<Integer, String>> excelList = ajaxBean.getList();
		ConcurrentLinkedQueue<ContainerCode> containerCodeList = new ConcurrentLinkedQueue<>();

		for (int i = 0; i < excelList.size(); i++) {
			if(null == excelList.get(i)){ //如果是空行，直接跳过
				continue;
			}
			ContainerCode containerCode = new ContainerCode();
			containerCode.setContainerCode(excelList.get(i).get(1));
			containerCode.setContainerName(excelList.get(i).get(3));
			containerCode.setContainerCodeType(excelList.get(i).get(0));
			String containerTypeName = excelList.get(i).get(2); //器具类型名称
			ContainerType findType = containerTypeService.findOrInsertContainerType(sessionUser, containerTypeName);
			containerCode.setContainerTypeId(findType.getContainerTypeId()); //器具类型ID
			containerCode.setContainerTypeName(excelList.get(i).get(2));
			containerCode.setIsActive(0); //启用
			containerCode.setCreateAccount(sessionUser.getAccount());
			containerCode.setCreateRealName(sessionUser.getRealName());
			containerCode.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
			containerCode.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
			containerCode.setIsTray(SysConstants.YES.equals(excelList.get(i).get(4))?1:0);
			containerCode.setContainerSpecification(excelList.get(i).get(5));
			//添加到新增队列
			containerCodeList.add(containerCode);
			//如果达到批量插入条数，就进行批量插入
			if(containerCodeList.size() >= SysConstants.MAX_INSERT_NUMBER){
				batchInsertcontainerCode(sessionUser,containerCodeList);
				containerCodeList.clear();
			}
		}

		//如果还有需要批量插入的数据
		if(containerCodeList.size() > SysConstants.INTEGER_0){
			batchInsertcontainerCode(sessionUser,containerCodeList);
			containerCodeList.clear();
		}
		return ajaxBean;
	}

	/**
	 * 器具代码数据批量导入(Excel)的校验方法
	 * @param path
	 * @param account
	 * @param realName
	 * @return
	 */
	private AjaxBean validExcelData(String path) {
		//组装所有器具代码为map格式，key=containerCode, value=器具代码对象
		List<ContainerCode> allContainerCodeList = listAllContainerCode();

		AjaxBean ajaxBean = new AjaxBean();
		List<Map<Integer, String>> mapList = null;
		try {
			mapList = ExcelUtil.readExcel(path,3);
		} catch (Exception e) { //文件批量导入失败！
			LOG.error(StatusCode.STATUS_402_MSG, e);
			logService.addLogError(null, e, StatusCode.STATUS_402_MSG, null);
			ajaxBean.setStatus(StatusCode.STATUS_402);
			ajaxBean.setMsg(StatusCode.STATUS_402_MSG);
			return ajaxBean;
		}
		if(mapList.size() == 0){ //如果数据一行都没有，未检测到需导入数据，请检查上传文件的数据输入是否正确。
			LOG.error(StatusCode.STATUS_403_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_403);
			ajaxBean.setMsg(StatusCode.STATUS_403_MSG);
			return ajaxBean;
		}
		if(mapList.size() > SysConstants.MAX_UPLOAD_ROWS){ //文件数据行数超过上限多少行
			LOG.error(StatusCode.STATUS_401_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_401);
			ajaxBean.setMsg(StatusCode.STATUS_401_MSG);
			return ajaxBean;
		}
		LOG.info("器具代码，需批量导入" + mapList.size()+"条数据");
		int row = 1;
		List<String> errList = new ArrayList<String>();
		for (Map<Integer, String> map : mapList) {
			row = row + 1;
			if(null == map){ //如果是空行，直接跳过
				continue;
			}
			String containerCode = map.get(1);
			String containerName = map.get(3);
			String isTray = map.get(4);
			if (StringUtils.isBlank(containerCode)) { //器具代码不能为空
				errList.add("第"+row+"行，器具代码不能为空。");
			}
			if(StringUtils.isBlank(containerName)){ //器具名称不能为空
				errList.add("第"+row+"行，器具名称不能为空。");
			}else if(!containerCode.matches(SysConstants.REGEX_CONTAINER_CODE)){
				errList.add("第"+row+"行，"+StatusCode.STATUS_325_MSG);
			}else {
				ContainerCode findCode = queryByContainerCode(containerCode);
				if(null != findCode){
					errList.add("第"+row+"行，器具代码["+findCode.getContainerCode()+"]已存在。");
				}
				else{
					int row2 = 1;
	            	two:for(Map<Integer, String> map2 : mapList){
	            		row2 = row2 + 1;
	            		if(null != map2 && row != row2 && map.get(1).equals(map2.get(1))){ //如果excel内发现重复器具代码
	        				errList.add("第"+row+"行，器具代码["+containerCode+"]在excel内重复，请核查。");
	            			break two;
	            		}
	            	}
				}
			}
			if(StringUtils.isBlank(isTray)) {
				errList.add("第"+row+"行，是否是托盘不能为空。");
			}else if(!(SysConstants.YES.equals(isTray) || SysConstants.NO.equals(isTray))) { //既不是“是”也不是“否”
				errList.add("第"+row+"行，是否是托盘请输入合法的值：【是】或者【否】。");
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

	public List<String> queryCountByContainerCode(String codes){
		String sql = "select container_code from t_container_code where container_code in ( "+codes+" )";
		return jdbcTemplate.queryForList(sql,String.class);
	}

	/**
	 * 通过器具代码查询器具代码对象
	 * @param containerCode 器具代码
	 * @return
	 */
	public ContainerCode queryByContainerCode(String containerCode) {
		String sql = "select * from t_container_code where container_code=? ";
		List<ContainerCode> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerCode.class), containerCode);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}

	/**
	 * 查询指定的containerCode,是否存在
	 * @param containerCode
	 * @return
	 */
	public List<String> queryContainerCodes(String containerCode){
		String sql = "select container_code from t_container_code where container_code in( "+containerCode+" )";
		List<String> list = namedJdbcTemplate.queryForList(sql.toString(),new HashMap<>(),String.class);
		return list;
	}
}
