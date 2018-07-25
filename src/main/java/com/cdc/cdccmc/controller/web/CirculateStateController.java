package com.cdc.cdccmc.controller.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;

/** 
 * 流转状态
 * @author Clm
 * @date 2018-01-24
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateStateController {

	/**
	 *流转状态列表
	 */
	@RequestMapping(value = "/circulate/listCirculateState")
	public AjaxBean listCirculateState(AjaxBean ajaxBean) {
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		ajaxBean.setList(CirculateState.listAll());
		return ajaxBean;
	}
}
