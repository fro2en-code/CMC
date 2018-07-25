package com.cdc.cdccmc.service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerType;
import com.cdc.cdccmc.domain.sys.SystemUser;

/** 
 * 器具类型
 * @author ZhuWen
 * @date 2017-12-29
 */
@Service
@EnableTransactionManagement // 启用注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ContainerTypeService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerTypeService.class);

	@Autowired
	private BaseService baseService;
	@Autowired
	private LogService logService;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@SuppressWarnings("unchecked")
	public List<ContainerType> listAllContainerType() {
		String sql = "SELECT * FROM T_CONTAINER_TYPE";
		return namedJdbcTemplate.query(sql,new HashMap(),new BeanPropertyRowMapper(ContainerType.class));
	}

    /**
     * 根据器具名称查询器具类型
     * @param containerTypeName
     * @param orgId
     * @return
     */
    public ContainerType queryContainerTypeByName(String containerTypeName) {
        String sql = "SELECT * FROM T_CONTAINER_TYPE WHERE container_type_name = :containerTypeName";
        HashMap<String,String> map = new HashMap();
        map.put("containerTypeName",containerTypeName);
        List<ContainerType> list = namedJdbcTemplate.query(sql,map,new BeanPropertyRowMapper<>(ContainerType.class));
        if (CollectionUtils.isEmpty(list)){
            return null;
        }else {
            return list.get(0);
        }
    }

    /**
     * 添加器具类别
     * @param containerType
     * @return
     */
    public AjaxBean addContainerType(SystemUser sessionUser, ContainerType containerType) {
	    String sql = "INSERT INTO t_container_type (container_type_id, container_type_name,create_time, create_account, create_real_name,create_org_id,create_org_name) VALUES (:containerTypeId,:containerTypeName,sysdate(),:createAccount,:createRealName ,:createOrgId,:createOrgName) ";
        if(StringUtils.isBlank(containerType.getContainerTypeId())){ //如果主键UUID为空，则新增一个
        	containerType.setContainerTypeId(UUIDUtil.creatUUID());
        }
        containerType.setContainerTypeName(containerType.getContainerTypeName());
        containerType.setCreateAccount(sessionUser.getAccount());
        containerType.setCreateRealName(sessionUser.getRealName());
        containerType.setCreateOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
        containerType.setCreateOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
	    int result = namedJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(containerType));
	    return AjaxBean.returnAjaxResult(result);
    }

	/**
	 * 检测指定器具名称是否存在,如果存在那么返回其ID,如果不存在那么新增一条,返回新增的Id
	 * @param sessionUser
	 * @param containerTypeName 器具类型名称
	 * @return 器具类型ID
	 */
	public ContainerType findOrInsertContainerType(SystemUser sessionUser, String containerTypeName) {
		ContainerType containerType = queryContainerTypeByName(containerTypeName);
		if (null == containerType) { //如果不存在则新增器具类型
			String containerTypeId = UUIDUtil.creatUUID();
			containerType = new ContainerType();
			containerType.setContainerTypeId(containerTypeId); // 设置主键
			containerType.setContainerTypeName(containerTypeName);
			//如果不存在则新增
			addContainerType(sessionUser, containerType);
			//查出刚刚新增的那条器具类型
			containerType = findById(containerTypeId);
		}
		return containerType;
	}
	/**
	 * 根据器具类型ID查询器具类型对象
	 * @param containerTypeId 器具类型ID
	 * @return
	 */
	public ContainerType findById(String containerTypeId){
		String sql = "select * from t_container_type where container_type_id = ?";
		List<ContainerType> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerType.class), containerTypeId);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
	/**
	 * 根据器具类型名称查询器具类型对象
	 * @param containerTypeName 器具类型名称
	 * @return
	 */
	public ContainerType findByName(String containerTypeName){
		String sql = "select * from t_container_type where container_type_name = ?";
		List<ContainerType> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ContainerType.class), containerTypeName);
		if(CollectionUtils.isEmpty(list)){
			return null;
		}
		return list.get(0);
	}
}
