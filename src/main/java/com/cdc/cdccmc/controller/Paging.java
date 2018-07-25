package com.cdc.cdccmc.controller;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.web.AreaController;

/** 
 * 前端分页辅助类
 * @author ZhuWen
 * @date 2017-12-28
 */
public class Paging {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(Paging.class);

	private Object data; //页面返回数据，可能是一个列表，可能是单个数值
	private Object bean; //额外返回对象
	private volatile Integer pageSize; //每页显示多少条
	private volatile Integer total; //总共有几条数据
	private volatile Integer currentPage; //当前第几页
	private volatile Integer totalPage;  //总共几页
	private volatile Integer selectSqlStartNo; //当前sql分页从第几条数据开始
	private volatile Integer selectSqlEndNo;  //当前sql分页从第几条结束
	private String msg;
	private Integer status = StatusCode.STATUS_200; //默认为成功
	
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public void setDataAndFormatDate(Object data, String dateFormat) {
		if(StringUtils.isBlank(dateFormat)){
			dateFormat = DateUtil.yyyy_MM_dd;
		}
		String jsonStr = JSONObject.toJSONStringWithDateFormat(data,dateFormat, SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue);
		this.data = JSONObject.parse(jsonStr);
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		initSelectSqlLimit();
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
		/*************** 计算总页数  ***************************************/
		BigDecimal b1 = new BigDecimal(total);
		BigDecimal b2 = new BigDecimal(pageSize);
		// SysConstants.INTEGER_0    表示表示需要精确到小数点以后几位。 
		totalPage = b1.divide(b2, SysConstants.INTEGER_0, BigDecimal.ROUND_UP).intValue();
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		initSelectSqlLimit();
	}
	
	public Integer getSelectSqlStartNo() {
		return selectSqlStartNo;
	}
	public Integer getSelectSqlEndNo() {
		return selectSqlEndNo;
	}
	
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	/**
	 * 如果当前页和每页显示条数均不为空，就计算select SQL需要的分页计算
	 */
	private synchronized void initSelectSqlLimit(){
		if(null != currentPage && currentPage > 0 
				&& null != pageSize && pageSize > 0 ){
			selectSqlStartNo = (currentPage - 1) * pageSize ;
			selectSqlEndNo = pageSize; 
		}
	}
	
	/*********************** MySql分页   ********************************************/
	// 查询SQL分页语句开始
	private static final String LIMIT_START_SQL = "select * from ("; 
	// 查询SQL分页语句结束
	private static final String LIMIT_END_SQL_STARTNO = ") t limit ";
	// 查询SQL分页语句结束
	private static final String LIMIT_END_SQL_ENDNO = ",";
	// 统计总数SQL语句开始
	private static final String COUNT_START_SQL = "select count(*) from ("; 
	// 统计总数SQL语句结束
	private static final String COUNT_END_SQL = ") t ";
	// 查询SQL "limit"关键字
	private static final String LIMIT_STR = " limit ";
	
	/**
	 * 对SQL进行分页
	 * @param selectSql 
	 * @return
	 */
	public String sqlLimit(String selectSql){
		StringBuffer sql = new StringBuffer();
		sql.append(selectSql);
		sql.append(LIMIT_STR);
		sql.append(selectSqlStartNo);
		sql.append(LIMIT_END_SQL_ENDNO);
		sql.append(selectSqlEndNo);
		return sql.toString();
	}
	/**
	 * 对SQL进行总数统计
	 * @param selectSql
	 * @return
	 */
	public String sqlCount(String selectSql){
		StringBuffer sql = new StringBuffer(COUNT_START_SQL);
		sql.append(selectSql);
		sql.append(COUNT_END_SQL);
		return sql.toString();
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}

	/*
	Oracle分页
	private void initSelectSqlLimit(){
		if(null != currentPage && currentPage > 0 
				&& null != pageSize && pageSize > 0 ){
			selectSqlStartNo = (currentPage - 1) * pageSize +1;
			selectSqlEndNo = currentPage * pageSize; 
		}
	}
	// 查询SQL分页语句开始
	private static final String LIMIT_START_SQL = "select * from (   select rownum rownumber,t.* from ("; 
	// 查询SQL分页语句结束
	private static final String LIMIT_END_SQL_STARTNO = ") t ) v where v.rownumber >= ";
	// 查询SQL分页语句结束
	private static final String LIMIT_END_SQL_ENDNO = " and v.rownumber <= "; 
	// 统计总数SQL语句开始
	private static final String COUNT_START_SQL = "select count(*) from ("; 
	// 统计总数SQL语句结束
	private static final String COUNT_END_SQL = ")";
	*/
}
