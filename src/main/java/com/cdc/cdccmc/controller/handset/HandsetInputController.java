package com.cdc.cdccmc.controller.handset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateOrderService;
import com.cdc.cdccmc.service.HandsetService;

/**
 * 手持机端--器具入库
 * 
 * @author
 *
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class HandsetInputController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HandsetLoginController.class);
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private HandsetService handsetService;

	/**
	 * 获取包装流转单的收货状态
	 * 
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/handsetInput/getCirculateOrderStatus")
	public AjaxBean getCirculateOrderStatus(String orderCode) {
		AjaxBean ajaxBean = circulateOrderService.circulateOrderDetail(orderCode);
	    return ajaxBean;
	}

	/**
	 * 包装流转单入库：实收入库
	 * 
	 * @param sessionUser
	 * @param orderCode
	 * @param epcIdList
	 * @param differenceRemark
	 * @return
	 */
	@RequestMapping("/handsetInput/inOrgActualScan")
	public AjaxBean inOrgActualScan(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode,
			@RequestParam(value = "epcIdList[]") List<String> epcIdList, String differenceRemark) {
		LOG.info(
				"请求url: /handsetInput/inOrgActualScan,orderCode=" + orderCode + ",differenceRemark" + differenceRemark);
		return handsetService.inOrgActualScan(sessionUser, orderCode, epcIdList, differenceRemark,SysConstants.DEVICE_HANDSET_ACTUAL);
	}

	/**
	 * 包装流转单入库：照单全单
	 * 
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/handsetInput/inOrgAll")
	public AjaxBean inOrgAll(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode) {
		return handsetService.inOrgAll(sessionUser, orderCode,SysConstants.DEVICE_HANDSET_ALL);
	}
}
