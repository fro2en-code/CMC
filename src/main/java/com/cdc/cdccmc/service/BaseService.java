package com.cdc.cdccmc.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.controller.Paging;

/** 
 * 
 * @author ZhuWen
 * @date 2017-12-28
 */
@Service
public class BaseService {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(BaseService.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	/**
	 * 根据给定的select sql,初始化paging对象里的分页值：分页数据、总页数
	 * @param paging
	 * @param selectSql  不带任何参数
	 * @param returnClazz
	 * @param dateFormat
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	public Paging basisPaging(Paging paging,String selectSql,Class returnClazz){
		basisPaging(paging,selectSql,returnClazz,null);
		return paging;
	}
	
	/**
	 * 根据给定的select sql,初始化paging对象里的分页值：分页数据、总页数
	 * @param paging
	 * @param selectSql 不带任何参数
	 * @param returnClazz
	 * @param dateFormat
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Paging basisPaging(Paging paging,String selectSql,Class returnClazz,String dateFormat){
		String limitSql = paging.sqlLimit(selectSql);
		LOG.info("PaingSQL - " + limitSql);
		List list = namedJdbcTemplate.query(limitSql, new BeanPropertyRowMapper(returnClazz));
		if(StringUtils.isNotBlank(dateFormat)){
			//格式化结果集里的日期，因为全局返回JSON日期格式化为yyyy-MM-dd HH:mm:ss(spring-servlet.xml)，这里只需要显示年月日
			paging.setDataAndFormatDate(list,dateFormat);
		}else{
			paging.setData(list);
		}
		String countSql = paging.sqlCount(selectSql);
		LOG.info("PaingSQL - " + countSql);
		Integer total = jdbcTemplate.queryForObject(countSql, Integer.class);
		paging.setTotal(total);
		return paging;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Paging pagingParamMap(Paging paging,String selectSql,Map paramMap,Class clazz){
		return pagingParamMap(paging,selectSql,paramMap,clazz,null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Paging pagingParamMap(Paging paging,String selectSql,Map paramMap,Class clazz,String dateFormat){
		String limitSql = paging.sqlLimit(selectSql);
		LOG.info("PaingSQL - " + limitSql);
		LOG.info("PaingParam - " + JSONObject.toJSONString(paramMap));
		List list = namedJdbcTemplate.query(limitSql, paramMap, new BeanPropertyRowMapper(clazz));
		if(StringUtils.isNotBlank(dateFormat)){
			//格式化结果集里的日期，因为全局返回JSON日期格式化为yyyy-MM-dd HH:mm:ss(spring-servlet.xml)，这里只需要显示年月日
			paging.setDataAndFormatDate(list,dateFormat);
		}else{
			paging.setData(list);
		}
		String countSql = paging.sqlCount(selectSql);
		LOG.info("PaingSQL - " + countSql);
		Integer total = namedJdbcTemplate.queryForObject(countSql, paramMap, Integer.class);
		paging.setTotal(total);
		return paging;
	}
	
	@SuppressWarnings({ "rawtypes" })
	public Paging pagingParamSource(Paging paging,String selectSql,SqlParameterSource paramSource,Class clazz){
		return pagingParamSource( paging, selectSql, paramSource,null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Paging pagingParamSource(Paging paging,String selectSql,SqlParameterSource paramSource,Class clazz,String dateFormat){
		String limitSql = paging.sqlLimit(selectSql);
		LOG.info("PaingSQL - " + limitSql);
		LOG.info("PaingParam - " + JSONObject.toJSONString(paramSource));
		List list = namedJdbcTemplate.query(limitSql, paramSource, new BeanPropertyRowMapper(clazz));
		if(StringUtils.isNotBlank(dateFormat)){
			//格式化结果集里的日期，因为全局返回JSON日期格式化为yyyy-MM-dd HH:mm:ss(spring-servlet.xml)，这里只需要显示年月日
			paging.setDataAndFormatDate(list,dateFormat);
		}else{
			paging.setData(list);
		}
		String countSql = paging.sqlCount(selectSql);
		LOG.info("PaingSQL - " + countSql);
		Integer total = namedJdbcTemplate.queryForObject(countSql, paramSource, Integer.class);
		paging.setTotal(total);
		return paging;
	}
}
