package com.cdc.cdccmc.controller.web.circulate;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.dto.CirculateDetailDto;
import com.cdc.cdccmc.domain.dto.CirculateDetailManualDto;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateOrderDeliveryService;
import com.cdc.cdccmc.service.CirculateOrderService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.InventoryHistoryService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/**
 * 包装流转单
 *
 * @author Jerry
 * @date 2018/1/16 19:57
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateOrderController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateOrderController.class);
    @Autowired
    private CirculateOrderService circulateOrderService;
    @Autowired
    private CirculateOrderDeliveryService circulateOrderDeliveryService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
    @Autowired
    private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;

    /**
     * 包装流转单列表查询
     * @param sessionUser
     * @param paging
     * @param detail
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/circulateOrder/pagingCirculateOrder")
    public Paging pagingCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, CirculateOrder circulateOrder) {
        paging = circulateOrderService.pagingCirculateOrder(paging, circulateOrder, sessionUser);
        return paging;
    }
    /**
     * 【手工流转单】页面，包装流转单列表查询，仅查询发货仓库为：当前选择仓库，并且是手工流转单
     * @param sessionUser
     * @param paging
     * @param detail
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/circulateOrder/pagingManualCirculateOrderCurrentOrg")
    public Paging pagingManualCirculateOrderCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, String orderCode) {
        paging = circulateOrderService.pagingManualCirculateOrderCurrentOrg(paging, orderCode, sessionUser);
        return paging;
    }

    /**
     * 包装流转单列表查询，仅查询收货仓库为：当前选择仓库
     * @param sessionUser
     * @param paging
     * @param detail
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/circulateOrder/pagingCirculateOrderReceiveCurrentOrg")
    public Paging pagingCirculateOrderReceiveCurrentOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, CirculateDetail detail) {
        paging = circulateOrderService.pagingCirculateOrderReceiveCurrentOrg(paging, detail, sessionUser);
        return paging;
    }

	/**
	 * 生成包装流转单
	 */
	@RequestMapping(value = "/circulateOrder/createCirculateOrder")
	public AjaxBean createCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, CirculateOrder circulateOrder) {
		LOG.info("请求url: /circulateOrderDelivery/createCirculateOrder");
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(circulateOrder.getCarNo())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("车牌号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(circulateOrder.getTargetOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("配送目的地"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//获取配送目的地
		SystemOrg targetOrg = systemOrgService.findById(circulateOrder.getTargetOrgId());
		if(null == targetOrg){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("配送目的地仓库不存在，请重新选择");
			return ajaxBean;
		}
		if(targetOrg.getIsActive() == SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("配送目的地仓库["+targetOrg.getOrgName()+"]被禁用，请重新选择");
			return ajaxBean;
		}
		if(circulateOrder.getTargetOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_313);
			ajaxBean.setMsg(StatusCode.STATUS_313_MSG);
			return ajaxBean;
		}
		String tradeTypeCode = circulateOrder.getTradeTypeCode(); //出库类别
		if(StringUtils.isBlank(tradeTypeCode)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("包装流转单出库类型"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(CirculateState.getCirculate(tradeTypeCode))){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("出库类别不存在，请重新选择");
			return ajaxBean;
		}

		ajaxBean = circulateOrderService.createCirculateOrder(ajaxBean,sessionUser,targetOrg,circulateOrder);
		return ajaxBean;
	}

	/**
	 * 【手工流转单】页面，创建一个流转单主单。专门针对像供应商这种原始的手工出库单，无法具体到个体器具，即无法记录epc编号的出库
	 */
	@RequestMapping(value = "/circulateOrder/createCirculateOrderForAsn")
	public AjaxBean createCirculateOrderForAsn(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, CirculateOrder circulateOrder) {
		LOG.info("请求url: /circulateOrder/createCirculateOrderForAsn");
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(circulateOrder.getOrderCode())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("Asn No. "+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		CirculateOrder findOrder = circulateOrderService.queryCirculateOrderByOrderCode(circulateOrder.getOrderCode());
		if(null != findOrder){
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("["+circulateOrder.getOrderCode()+"]Asn No. 单号已存在，请重新输入。");
			return ajaxBean;
		}
		if(StringUtils.isBlank(circulateOrder.getCarNo())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("车牌号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(circulateOrder.getTargetOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("配送目的地"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//获取配送目的地
		SystemOrg targetOrg = systemOrgService.findById(circulateOrder.getTargetOrgId());
		if(null == targetOrg){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("配送目的地仓库不存在，请重新选择");
			return ajaxBean;
		}
		if(targetOrg.getIsActive() == SysConstants.INTEGER_1){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("配送目的地仓库["+targetOrg.getOrgName()+"]被禁用，请重新选择");
			return ajaxBean;
		}
		if(circulateOrder.getTargetOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_313);
			ajaxBean.setMsg(StatusCode.STATUS_313_MSG);
			return ajaxBean;
		}
		String tradeTypeCode = circulateOrder.getTradeTypeCode(); //出库类别
		if(StringUtils.isBlank(tradeTypeCode)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("包装流转单出库类型"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(CirculateState.getCirculate(tradeTypeCode))){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("出库类别不存在，请重新选择");
			return ajaxBean;
		}
		ajaxBean = circulateOrderService.createCirculateOrderForAsn(ajaxBean,sessionUser,targetOrg,circulateOrder);
		return ajaxBean;
	}


	/**
	 * 【手工流转单】页面，点击按钮：提交器具明细
	 */
    @RequestMapping("/circulateOrder/addCirculateDetailForAsn")
	public AjaxBean addCirculateDetailForAsn(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, @RequestBody CirculateDetailManualDto cdmd) {
		String orderCode = cdmd.getOrderCode();
		List<CirculateDetail> circulateDetailList = cdmd.getCirculateDetailList();

		for (CirculateDetail c : circulateDetailList) {
			LOG.info(c.getSequenceNo() + " === " + c.getContainerCode() + " === " + c.getSendNumber());
		}

    	AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(CollectionUtils.isEmpty(circulateDetailList)) {
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("器具明细列表不能为空！");
			return ajaxBean;
		}
		CirculateOrder findOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		if(null == findOrder){
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("Asn No. ["+orderCode+"]单号不存在，请核查。");
			return ajaxBean;
		}
		if(findOrder.getPrintNumber() > 0) {
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("["+orderCode+"]流转单已发货，不能更改器具明细。");
			return ajaxBean;
		}
		ajaxBean = circulateOrderService.addCirculateDetailForAsn(ajaxBean,sessionUser, findOrder, circulateDetailList);
		return ajaxBean;
	}

    /**
     * 返回web端【包装流转单】页面【查看明细】弹出框内容，包含器具个数统计列表
     * @param orderCode 流转单单号
     * @return
     */
    @RequestMapping("/circulateOrder/queryCirculateDetail")
    public AjaxBean queryCirculateDetail(String orderCode) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
    	//包装流转单，器具明细列表
        List<CirculateDetailDto> circulateDetailList = circulateOrderService.listCirculateDetailByOrderCode(orderCode);
		CirculateOrder order = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		//包装流转单，器具统计列表
		List<EpcSumDto> epcSumList = circulateOrderDeliveryService.buildEpcSumDtoByOrderCode(order);
		//库存数量
		for (CirculateDetailDto dto : circulateDetailList) {
			InventoryHistory history = inventoryHistoryService.queryInventoryLatest(order.getConsignorOrgId(),dto.getContainerCode());
			if(null == history) {
				//如果为空，则代表这个仓库目前尚无该器具代码的库存记录，返回库存为0即可
				dto.setInOrgNumber(SysConstants.INTEGER_0);
			}else {
				dto.setInOrgNumber(history.getInOrgNumber());
			}
		}
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setList(circulateDetailList);
		ajaxBean.setBean(epcSumList);
        return ajaxBean;
    }

	/**
	 * 返回web端【流转单收货详情】页面【主单信息】弹出框内容，不包含器具明细	 *
	 * @param orderCode 流转单单号
	 * @return
	 */
	@RequestMapping("/circulateOrder/queryCirculateOrderByOrderCode")
	public AjaxBean queryCirculateOrderByOrderCode(String orderCode){
		AjaxBean ajaxBean = new AjaxBean();
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		ajaxBean.setBean(circulateOrder);
		return ajaxBean;
	}

	/**
	 * 【包装流转单】页面【作废】按钮点击，作废一个流转单，不可以恢复。
	 * @param orderCode 流转单单号
	 * @return
	 */
	@RequestMapping("/circulateOrder/invalidOrder")
	public AjaxBean invalidOrder(String orderCode){
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = circulateOrderService.invalidOrder(ajaxBean,orderCode);
		return ajaxBean;
	}
    /**
     * 打印包装流转单
     *
     * @param sessionUser
     * @param orderCode
     * @return
     */
    @RequestMapping("/circulateOrder/printCirculateOrder")
    public AjaxBean printCirculateOrder(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode, HttpSession session) {
    	AjaxBean ajaxBean = AjaxBean.SUCCESS();
		// 获取包装流转单
		CirculateOrder circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
    	if(!sessionUser.getCurrentSystemOrg().getOrgId().equals(circulateOrder.getConsignorOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_357);
			ajaxBean.setMsg(StatusCode.STATUS_357_MSG);
			return ajaxBean;
    	}
    	//不能打印已作废的流转单
    	if(circulateOrder.getIsInvalid() == 1) {
			ajaxBean.setStatus(StatusCode.STATUS_365);
			ajaxBean.setMsg("["+orderCode+"]"+StatusCode.STATUS_365_MSG);
			return ajaxBean;
    	}
    	//在打印之前，需要对器具做处理：增加流转记录、更新器具最后所在仓库、更新维修出库的相应维修仓库信息等等。。。。
    	ajaxBean = circulateOrderService.dealContainerPrePrintCirculateOrder(ajaxBean, orderCode,circulateOrder,sessionUser);
    	if(ajaxBean.getStatus() != StatusCode.STATUS_200){
            return ajaxBean;
    	}
    	//增加打印次数
    	circulateOrderService.addCirculateOrderPrintNumber(orderCode);
    	//获取最新值的流转单
    	circulateOrder = circulateOrderService.queryCirculateOrderByOrderCode(orderCode);
		//打印包装流转单
    	ajaxBean = circulateOrderService.printCirculateOrder(ajaxBean, sessionUser, orderCode,session);
    	if(!StatusCode.STATUS_200.equals(ajaxBean.getStatus())) {
    		String msg = StatusCode.STATUS_369_MSG + ajaxBean.getMsg();
    		//如果流转单打印次数大于0，则给予已发货成功的提示。
    		if((circulateOrder.getPrintNumber()+1) > SysConstants.INTEGER_0) {
    			msg = msg + "。此流转单已在"+DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss, circulateOrder.getPrintOrderTime()) +"发货成功。";
    		}
    		ajaxBean.setStatus(StatusCode.STATUS_369);
    		ajaxBean.setMsg(msg);
    	}
        return ajaxBean;
    }


	/**
	 * 包装流转单导出
	 *
	 * @param sessionUser
	 * @param circulateOrder
	 * @param carNo
	 * @param orderCode
	 * @return
	 */
	@RequestMapping("/circulateOrder/expertToExcelCirculateOrder")
	public void expertToExcelContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, CirculateOrder circulateOrder, HttpServletResponse response) {
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue("包装流转单号");
		row0.createCell(1).setCellValue("车牌号");
		row0.createCell(2).setCellValue("发货仓库");
		row0.createCell(3).setCellValue("收货仓库");
		row0.createCell(4).setCellValue("出库类别");
		row0.createCell(5).setCellValue("发货状态");
		row0.createCell(6).setCellValue("发货人和时间");
		row0.createCell(7).setCellValue("收货状态");
		row0.createCell(8).setCellValue("收货备注");
		row0.createCell(9).setCellValue("收货人和时间");
		row0.createCell(10).setCellValue("特别描述");
		row0.createCell(11).setCellValue("司机姓名");
		row0.createCell(12).setCellValue("司机联系方式");
		row0.createCell(13).setCellValue("车辆到达时间");
		row0.createCell(14).setCellValue("装货完毕时间");
		row0.createCell(15).setCellValue("首次打印流转单时间");
		row0.createCell(16).setCellValue("车辆离开时间");
		row0.createCell(17).setCellValue("打印次数");
		row0.createCell(18).setCellValue("操作日期");
		row0.createCell(19).setCellValue("操作人");

		//定义起始序列号
		int rowNumber = 1;

		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag){
			paging.setCurrentPage(pageNum++);
			paging = this.pagingCirculateOrder(sessionUser,paging,circulateOrder);

			List<CirculateOrder> list = (List<CirculateOrder>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (CirculateOrder o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(o.getOrderCode());
				row.createCell(1).setCellValue(o.getCarNo());
				row.createCell(2).setCellValue(o.getConsignorOrgName());
				row.createCell(3).setCellValue(o.getTargetOrgName());
				row.createCell(4).setCellValue(o.getTradeTypeName());
				if(o.getPrintNumber() > 0){
					row.createCell(5).setCellValue("已发货");
				}
				else{
					row.createCell(5).setCellValue("未发货");
				}
				row.createCell(6).setCellValue(o.getConsignorRealName() +" "+ DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getConsignorTime()));
				if(o.getIsReceive().equals("0")){
					row.createCell(7).setCellValue("未收货");
				}
				if(o.getIsReceive().equals("1")){
					row.createCell(7).setCellValue("已部分收货");
				}
				if(o.getIsReceive().equals("2")){
					row.createCell(7).setCellValue("已全部收货");
				}
				row.createCell(8).setCellValue(o.getRemark());
				if(o.getTargetRealName() == null){
					row.createCell(9).setCellValue("");
				}
				else{
					row.createCell(9).setCellValue(o.getTargetRealName() +" "+ DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getTargetTime()));
				}
				row.createCell(10).setCellValue(o.getSpecialDescription());
				row.createCell(11).setCellValue(o.getDriverName());
				row.createCell(12).setCellValue(o.getDriverPhone());
				if(o.getCarArriveTime() == null){
					row.createCell(13).setCellValue("");
				}
				else{
					row.createCell(13).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCarArriveTime()));
				}
				if(o.getLoadingEndTime() == null){
					row.createCell(14).setCellValue("");
				}
				else{
					row.createCell(14).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getLoadingEndTime()));
				}
				if(o.getPrintOrderTime() == null){
					row.createCell(15).setCellValue("");
				}
				else{
					row.createCell(15).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getPrintOrderTime()));
				}
				if(o.getCarLeaveTime() == null){
					row.createCell(16).setCellValue("");
				}
				else{
					row.createCell(16).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCarLeaveTime()));
				}
				row.createCell(17).setCellValue(o.getPrintNumber());
				row.createCell(18).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
				row.createCell(19).setCellValue(o.getCreateRealName());
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}
		ExcelUtil.exportExcel(response,wb,"器具列表"+DateUtil.today_yyyyMMddHHmmssZH());
		try {
			if(null != wb){
				wb.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage()+ StatusCode.STATUS_408_MSG,e);
			logService.addLogError(sessionUser, e, e.getMessage()+ StatusCode.STATUS_408_MSG, null);
		}
	}
}
