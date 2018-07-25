package com.cdc.cdccmc.controller.web.circulate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateOrderReceiveService;
import com.cdc.cdccmc.service.CirculateOrderService;
import com.cdc.cdccmc.service.HandsetService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

/**
 * 包装流转单——收货
 * @author Clm
 * @date 2018-01-10
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateOrderReceiveController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateOrderReceiveController.class);
	@Autowired
	private CirculateOrderReceiveService purchaseReceiveService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private HandsetService handsetService;

	/**
	 * 发货——包装流转单查询
	 */
	@RequestMapping(value = "/circulateOrderReceive/queryCirculateOrder")
	public AjaxBean queryCirculateOrder(Paging paging, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, AjaxBean ajaxBean, String orderCode) {
		if(StringUtils.isBlank(orderCode)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("包装流转单单号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ajaxBean = purchaseReceiveService.queryCirculateOrderInfo(ajaxBean,orderCode);
		return ajaxBean;
	}

	/**
	 * 收货——收货入库
	 */
	@RequestMapping(value = "/circulateOrderReceive/confirmInOrg")
	public AjaxBean confirmInOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean, String orderCode, String circulateDetailsStr) {
		LOG.info("---/circulateOrderReceive/confirmInOrg   orderCode="+orderCode+"   circulateDetailsStr="+ circulateDetailsStr);
		if(StringUtils.isBlank(orderCode)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("包装流转单单号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//通过单号查包装流转单
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		if(null == circulateOrder){ //如果包装流转单不存在
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		//包装流转单配送目的地仓库不是当前仓库！不能进行器具入库操作！
		if(!sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())){ //如果包装流转单的配送目的地不是当前仓库
			ajaxBean.setStatus(StatusCode.STATUS_315);
			ajaxBean.setMsg("["+orderCode+"]，"+StatusCode.STATUS_315_MSG);
			return ajaxBean;
		}
		//包装流转单尚未发货，不能进行收货！
		if(circulateOrder.getPrintNumber() < SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_354);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_354_MSG);
			return ajaxBean;
		}
		//如果已经人工收货，则不能进行收货！
		if(StringUtils.equals(circulateOrder.getIsManualReceive(), SysConstants.STRING_2)){
			ajaxBean.setStatus(StatusCode.STATUS_363);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_363_MSG);
			return ajaxBean;
		}
		//手工流转单与非手工流转单需要分别收货，因为手工流转单是根据器具代码收货，可以填写收货数量，而非手工流转单则是照单全收的概念。
		JSONArray jsonArray = (JSONArray) JSONObject.parse(circulateDetailsStr);
		List<CirculateDetail> detailList = jsonArray.toJavaList(CirculateDetail.class);
		if(StringUtils.equals(circulateOrder.getIsManualOrder(), SysConstants.STRING_1)) {
			//手工流转单收货入库，不需要记录epc的流转记录等
			handsetService.inOrgWebManualOrder(sessionUser, orderCode,detailList);
		}else {//为保证原有逻辑不变，在页面了判断：当所有器具的收发数都相等时，该逻辑才会被执行
			//非手工流转单则是照单全收。
			ajaxBean = handsetService.inOrgAll(sessionUser, orderCode,SysConstants.DEVICE_WEB);
			//插入表circulate_detail_receive
			handsetService.insertCirculateDetailReceive(sessionUser, orderCode, detailList);
			//更新表circulate_order的is_circulate_detail_receive字段
			circulateOrderService.updateCirculateDetailReceive(orderCode);
		}
		return ajaxBean;
	}

	/**
	 * 收货——收货入库
	 */
	@RequestMapping(value = "/circulateOrderReceive/inOrgWebOrder")
	public AjaxBean inOrgWebNonManualOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean, String orderCode, String circulateDetailsStr) {
		LOG.info("---/circulateOrderReceive/inOrgWebOrder   orderCode="+orderCode+"   circulateDetailsStr="+ circulateDetailsStr);
		if(StringUtils.isBlank(orderCode)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("包装流转单单号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//通过单号查包装流转单
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		if(null == circulateOrder){ //如果包装流转单不存在
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		//包装流转单配送目的地仓库不是当前仓库！不能进行器具入库操作！
		if(!sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getTargetOrgId())){ //如果包装流转单的配送目的地不是当前仓库
			ajaxBean.setStatus(StatusCode.STATUS_315);
			ajaxBean.setMsg("["+orderCode+"]，"+StatusCode.STATUS_315_MSG);
			return ajaxBean;
		}
		//包装流转单尚未发货，不能进行收货！
		if(circulateOrder.getPrintNumber() < SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_354);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_354_MSG);
			return ajaxBean;
		}
		//如果已经人工收货，则不能进行收货！
		if(StringUtils.equals(circulateOrder.getIsManualReceive(), SysConstants.STRING_2)){
			ajaxBean.setStatus(StatusCode.STATUS_363);
			ajaxBean.setMsg("包装流转单["+orderCode+"]"+StatusCode.STATUS_363_MSG);
			return ajaxBean;
		}
		//手工流转单是根据器具代码收货，可以填写收货数量
		JSONArray jsonArray = (JSONArray) JSONObject.parse(circulateDetailsStr);
		List<CirculateDetail> detailList = jsonArray.toJavaList(CirculateDetail.class);
		handsetService.inOrgWebOrder(sessionUser, orderCode,detailList);
		return ajaxBean;
	}
}
