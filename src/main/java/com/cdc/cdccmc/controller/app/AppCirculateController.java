package com.cdc.cdccmc.controller.app;

import java.util.*;

import com.cdc.cdccmc.service.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;


import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.MaintainState;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.door.DoorEquipmentController;
import com.cdc.cdccmc.controller.web.circulate.CirculateOrderController;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.dto.ContainerDto;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.domain.sys.SystemUser;

import javax.servlet.http.HttpSession;

/**
 * app端包装流转单
 * 
 * @author Administrator
 *
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppCirculateController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppCirculateController.class); 

	@Autowired
	private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private CirculateOrderService circulateOrderService;
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private CirculateOrderController circulateOrderController;
    @Autowired
    private HandsetService handsetService;
	@Autowired
	private CirculateService circulateService;

	/**
	 * 通过门型设备账号查找未打印过的流转单
	 * @param doorAccount
	 * @return
	 */
	@RequestMapping("/appCirculate/queryCirculateOrderByDoorAccount")
	public AjaxBean queryCirculateOrderByDoorAccount(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
													 String doorAccount) {
		return circulateOrderService.queryCirculateOrderByDoorAccount(doorAccount);
	}

	/**
	 * 生成包装流转单
	 */
	@RequestMapping("/appCirculate/createCirculateOrder")
	public AjaxBean createCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			CirculateOrder circulateOrder) {
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderController.createCirculateOrder(sessionUser, circulateOrder);
		return ajaxBean;
	}

	/**
	 * 器具统计和器具明细查询
	 *
	 * @param orderCode
	 *            流转单单号
	 * @return
	 */
	@RequestMapping("/appCirculate/queryCirculateDetail")
	public Paging queryCirculateDetail(String orderCode) {
		Paging paging = new Paging();
		paging.setPageSize(20000);
		paging.setCurrentPage(1);
		paging.setTotalPage(1);
		// 包装流转单，器具明细列表
		List<CirculateDetail> circulateDetailList = circulateOrderService
				.queryCirculateDetailByOrderCodeInCreateTimeDesc(orderCode);
		// 包装流转单，器具统计列表
		CirculateOrder order = circulateOrderService
				.queryCirculateOrderByOrderCode(orderCode);
		List<EpcSumDto> epcSumList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(order);
		paging.setStatus(StatusCode.STATUS_200);
		paging.setData(circulateDetailList);
		paging.setBean(epcSumList);
		return paging;
	}

	/**
	 * 打印包装流转单
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/printCirculateOrder")
	public AjaxBean printCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String orderCode,HttpSession session) {
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderController.printCirculateOrder(sessionUser, orderCode,session);
		return ajaxBean;
	}

	/**
	 * 获取当前仓库下的流转单，app页面【创建流转单】
	 *
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/appCirculate/listCirculateOrderForCurrentOrg")
	public AjaxBean listCirculateOrderForCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean = circulateOrderService.listCirculateOrderForCurrentOrg(ajaxBean,sessionUser);
		return ajaxBean;
	}

	/**
	 * 绑定门型、重绑按钮点击事件，app页面【创建流转单】
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/bindDoorForCirculateOrder")
	public AjaxBean bindDoorForCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orderCode,String doorAccount) {
		AjaxBean ajaxBean = circulateOrderService.bindDoorForCirculateOrder(sessionUser,orderCode,doorAccount);
		return ajaxBean;
	}

	/**
	 * 获取当前仓库门型设备账号列表，app页面【创建流转单】
	 *
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/appCirculate/listDoorForCurrentOrg")
	public AjaxBean listDoorForCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean = circulateOrderService.listDoorForCurrentOrg(ajaxBean,sessionUser);
		return ajaxBean;
	}

	/**
	 * 获取流转单器具明细详情，app页面【流转单器具明细】，包含器具个数统计列表
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/queryCirculateDetailForContainer")
	public AjaxBean queryCirculateDetailForContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orderCode) {
		AjaxBean ajaxBean = circulateOrderService.circulateOrderDetail(orderCode);
		return ajaxBean;
	}

	/**
	 * app页面【流转单主单信息】，不包含器具明细
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/queryCirculateOrderByOrderCode")
	public AjaxBean queryCirculateOrderByOrderCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orderCode) {
		AjaxBean ajaxBean = circulateOrderController.queryCirculateOrderByOrderCode(orderCode);
		return ajaxBean;
	}

	/**
	 * 门型扫描-添加按钮
	 * 1.添加时要创建这个流转单明细（器具）.
	 * 2.操作回退时删除流转单明细,并且把door_scan表的上一次修改的数据恢复到数据库中
	 * 3.移除只是把door_scan表的数据物理清除
	 * @param session
	 * @param sessionUser
	 * @param doorAccount
	 * @param cirulateOrderCode
	 * @param groupId
	 * @param scanTime
	 * @return
	 */
	@RequestMapping("/appCirculate/doorScanAdd")
	public AjaxBean doorScanAdd(HttpSession session ,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
								String doorAccount, String cirulateOrderCode, String groupId,String scanTime)
	{
		LOG.info("请求url:/appCirculate/doorScanAdd,"+sessionUser.getAccount()+"用户接收到参数：doorAccount="+doorAccount+";cirulateOrderCode="+cirulateOrderCode+";groupId==null?"+(groupId == null)+";scanTime="+scanTime);
		if(StringUtils.isNotBlank(groupId) && StringUtils.equals(groupId, "null")) {
			groupId = StringUtils.EMPTY;
		}
		AjaxBean ajaxBean = containerGroupService.doorScanAdd(session,sessionUser,doorAccount,cirulateOrderCode,groupId,scanTime);
		return ajaxBean;
	}

	/**
	 * 门型扫描-移除按钮
	 * 移除现在没有要求回退所以不用放入session
	 * 也不用记录操作日志
	 * @param session
	 * @param sessionUser
	 * @param doorAccount
	 * @param groupId
	 * @param scanTime
	 * @return
	 */
	@RequestMapping("/appCirculate/doorScanRemove")
	public AjaxBean doorScanRemove(HttpSession session ,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
								String doorAccount, String groupId, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date scanTime)
	{
		AjaxBean ajaxBean = containerGroupService.doorScanRemove(session,sessionUser,doorAccount,groupId,scanTime);
		return ajaxBean;
	}

	/**
	 * 门型扫描-回退按钮
	 * @param session
	 * @return
	 */
	@RequestMapping("/appCirculate/rollBackDoorScan")
	public AjaxBean rollBackDoorScan(HttpSession session)
	{
		AjaxBean ajaxBean = containerGroupService.rollBackDoorScan(session);
		return ajaxBean;
	}

	/**
	 * 流转单收货明细页面：照单全单按钮点击
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/inOrgAll")
	public AjaxBean inOrgAll(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode) {
		return handsetService.inOrgAll(sessionUser, orderCode,SysConstants.DEVICE_APP_ALL);
	}

	/**
	 * 流转单收货明细页面：实收入库按钮点击
	 * @param sessionUser
	 * @param orderCode
	 * @param epcIdList
	 * @param differenceRemark
	 * @return
	 */
	@RequestMapping("/appCirculate/inOrgActualScan")
	public AjaxBean inOrgActualScan(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode,
			@RequestParam(required = false, value = "epcIdList[]") List<String> epcIdList, String differenceRemark) {
		LOG.info("请求url: /appCirculate/inOrgActualScan,orderCode=" + orderCode + ",differenceRemark" + differenceRemark);
		return handsetService.inOrgActualScan(sessionUser, orderCode, epcIdList, differenceRemark,SysConstants.DEVICE_APP_ACTUAL);
	}

	/**
	 * 【app端】流转单列表页面，获取流转单列表
	 * @return
	 */
	@RequestMapping("/appCirculate/pagingCirculateOrder")
	public Paging pagingCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, CirculateOrder circulateOrder) {
        paging = circulateOrderService.pagingCirculateOrder(paging, circulateOrder, sessionUser);
        return paging;
    }
    /**
     * 【app端】更新流转单车辆离开时间
     * @return
     */
    @RequestMapping("/appCirculate/updateLeaveTimeByOrderCode")
    public AjaxBean updateLeaveTimeByOrderCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String circulateOrderCode) {
        return circulateOrderService.updateLeaveTimeByOrderCode(sessionUser,circulateOrderCode);
    }

    /**
     * 【app端】器具重绑-解绑
     * @return
     */
    @RequestMapping("/appCirculate/relieveContainer")
    public AjaxBean relieveContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String circulateOrderCode,String epcId) {
        return circulateOrderService.relieveContainer(sessionUser,circulateOrderCode,epcId);
    }
	/**
	 * 更新车辆到达时间
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/updateCarArriveTime")
	public AjaxBean updateCarArriveTime(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										String orderCode) {
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderService.updateCarArriveTime(sessionUser, orderCode);
		return ajaxBean;
	}

	/**
	 * 当页面点击“装货完成了”时,更新装货完了时间
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/updateLoadingEndTime")
	public AjaxBean updateLoadingEndTime(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										String orderCode) {
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderService.updateLoadingEndTime(sessionUser, orderCode);
		return ajaxBean;
	}

	/**
	 * 点击修改按钮，修改司机姓名、司机联系方式。
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/updateDriverMsg")
	public AjaxBean updateDriverMsg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										 String orderCode, String driverName, String driverPhone) {
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderService.updateDriverMsg(sessionUser, orderCode, driverName, driverPhone);
		return ajaxBean;
	}

	/**
	 * 点击修改按钮，修改车牌号。
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/updateCarNo")
	public AjaxBean updateCarNo(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
										 String orderCode, String carNo) {
		AjaxBean ajaxBean = circulateOrderService.updateCarNo(sessionUser, orderCode, carNo);
		return ajaxBean;
	}

	/**
	 * 点击修改按钮，修改特别描述。
	 *
	 * @param sessionUser
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/appCirculate/updateSpecialDescription")
	public AjaxBean updateSpecialDescription(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String specialDescription,String orderCode) {
		AjaxBean ajaxBean = circulateOrderService.updateSpecialDescription(sessionUser, specialDescription,orderCode);
		return ajaxBean;
	}
	
	/**
	 * 器具流转历史查询
	 * @param paging
	 * @param sessionUser
	 * @param circulate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/appCirculate/pagingCirculateHistory")
	public Paging pagingCirculateHistory(Paging paging, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Circulate circulate, String startDate, String endDate) {
		paging = circulateService.pagingCirculateHistory(paging, sessionUser, circulate, startDate, endDate);
		return paging;
	}
	
	/**
	 * [收货门型监控]页面，移除按钮点击事件
	 * @param paging
	 * @param sessionUser
	 * @param circulate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/appCirculate/removeDoorReceive")
	public AjaxBean removeDoorReceive(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String doorAccount, String createTime) {
		AjaxBean ajaxBean = circulateService.removeDoorReceive(sessionUser, doorAccount, createTime);
		return ajaxBean;
	}

}
