package com.cdc.cdccmc.repository.sys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.domain.sys.SysUser;
@Repository
public class SysUserRepository{
	@Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
    public List<SysUser> findAll(String username) throws Exception {
        String listSql = "select * from sys_user where username=?";  
        return jdbcTemplate.query(listSql, new UserRowMapper(), username);
    }
    
    
    class UserRowMapper implements RowMapper<SysUser>{  
        /** 
         * rs:结果集. 
         * rowNum:行号 
         */  
        public SysUser mapRow(ResultSet rs, int rowNum) throws SQLException {  
        	SysUser user = new SysUser();  
            user.setId(rs.getInt("id"));  
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            return user;  
        }  
          
    }  
}
