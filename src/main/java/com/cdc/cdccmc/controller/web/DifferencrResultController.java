package com.cdc.cdccmc.controller.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.DealResult;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;

/** 
 * 差异处理结果
 * @author Clm
 * @date 2018-01-29
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class DifferencrResultController {

	/**
	 *差异处理状态列表
	 */
	@RequestMapping(value = "/DifferencrResult/listDifferencrResult")
	public AjaxBean listDifferencrResult(AjaxBean ajaxBean) {
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setList(DealResult.listAll());
		return ajaxBean;
	}
}
