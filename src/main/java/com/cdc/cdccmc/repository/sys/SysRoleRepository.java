package com.cdc.cdccmc.repository.sys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.domain.sys.SysRole;
import com.cdc.cdccmc.domain.sys.SysUser;
@Repository
public class SysRoleRepository{
	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
    public List<SysRole> findRoleByid(Integer sys_user_id) throws Exception {
        StringBuffer listSql = new StringBuffer("select * from sys_role sr ");
        listSql.append(" where sr.id in (");
        listSql.append(" select sru.sys_role_id from sys_role_user sru where sru.sys_user_id = ? ");
        listSql.append(" )"); 

        return jdbcTemplate.query(listSql.toString(), new RoleRowMapper(), sys_user_id);
    }
    
    
    class RoleRowMapper implements RowMapper<SysRole>{  
        /** 
         * rs:结果集. 
         * rowNum:行号 
         */  
        public SysRole mapRow(ResultSet rs, int rowNum) throws SQLException {  
        	SysRole role = new SysRole();  
        	role.setId(rs.getInt("id"));  
        	role.setName(rs.getString("name"));
            return role;  
        }  
          
    }  
}
