package com.cdc.cdccmc.controller;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.StatusCode;

/**
 * 用于往前端返回ajax请求的json对象
 * @author ZhuWen
 * @date 2017-12-28
 */
public class AjaxBean {
	private String msg = StatusCode.STATUS_200_MSG;
	private Integer status = StatusCode.STATUS_200;
	private Object bean; //返回单个对象
	private List list; //返回集合对象
	/**
	 * 返回成功状态 200
	 * @param ajaxBean
	 * @return
	 */
	public static AjaxBean SUCCESS(){
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		return ajaxBean;
	}
	/**
	 * 返回失败状态 201
	 * @param ajaxBean
	 * @return
	 */
	public static AjaxBean FAILURE(){
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean.setMsg(StatusCode.STATUS_201_MSG);
		ajaxBean.setStatus(StatusCode.STATUS_201);
		return ajaxBean;
	}
	/**
	 * 如果updateResult == 0,则更新失败，大于0则更新成功
	 * @param updateResult update或add操作的jdbc返回int值
	 * @return
	 */
	public static AjaxBean returnAjaxResult(int updateResult){
		if(updateResult == 0){
			AjaxBean ajax = new AjaxBean();
			ajax.setStatus(StatusCode.STATUS_309);
			ajax.setMsg(StatusCode.STATUS_309_MSG);
			return ajax;
		}
		return AjaxBean.SUCCESS();
	}

	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getBean() {
		return bean;
	}
	public void setBean(Object bean) {
		this.bean = bean;
	}
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
