package com.cdc.cdccmc.controller.web.circulate;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.circulate.CirculateOrder;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateDifferenceResultService;
import com.cdc.cdccmc.service.CirculateOrderService;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.basic.AreaService;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/**
 * 流转单差异处理
 * @author Clm
 * @date 2018-01-29
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateDifferenceResultController {
    private Logger LOG = Logger.getLogger(CirculateDifferenceResultController.class);
    @Autowired
    private CirculateDifferenceResultService circulateDifferenceResultService;
    @Autowired
    private AreaService areaService;
    @Autowired
	private LogService logService;
    @Autowired
	private JdbcTemplate jdbcTemplate;
    @Autowired
	private CirculateOrderService circulateOrderService;
    @Autowired
	private CirculateService circulateService;
    @Autowired
	private SystemOrgService systemOrgService;

    /**
     * 【流转单收货详情】查询
     * @param sessionUser
     * @param paging
     * @param circulateDifference
     * @param startDate
     * @param endDate
     * @param differenceId
     * @return
     */
    @RequestMapping(value = "/circulateDifferenceResult/pagingCirculateDetail")
    public Paging pagingCirculateDetail(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, CirculateDetail circulateDetail, String differenceId) {
        paging = circulateDifferenceResultService.pagingCirculateDetail(paging, sessionUser, circulateDetail, differenceId);
        return paging;
    }

    private CirculateOrder queryCirculateOrder(String orderCode) {
    	String sql = "select * from t_circulate_order where order_code =?";
		List<CirculateOrder> circulateList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(CirculateOrder.class), orderCode);
		if (CollectionUtils.isEmpty(circulateList)) {
			return null;
		}
		return circulateList.get(0);
	}

	private CirculateDetail queryCirculateDifference(String circulateDetailId) {
    	String sql = "select * from t_circulate_detail where circulate_detail_id =? order by create_time desc";
		List<CirculateDetail> circulateDetailList = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(CirculateDetail.class), circulateDetailId);
		if (CollectionUtils.isEmpty(circulateDetailList)) {
			return null;
		}
		return circulateDetailList.get(0);
	}


    /**
     * 【流转单收货详情】页面处理收货差异：EPC覆盖
     * @param sessionUser
     * @param circulateDetailId
     * @param ajaxBean
     * @param epcId
     * @param newEpcId
     * @return
     */
    @RequestMapping(value = "/circulateDifferenceResult/epcDispose")
    public AjaxBean epcDispose(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String circulateDetailId, AjaxBean ajaxBean, String epcId, String newEpcId) {
        if (StringUtils.isBlank(newEpcId)) {
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("EPC编号" + StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        //通过circulateDifferenceId查这个对象的epcId
        CirculateDetail circulateDetail= circulateDifferenceResultService.queryBycirculateDifferenceId(circulateDetailId);
        if(circulateDetail.getEpcId().equals(newEpcId.trim())) {
        	ajaxBean.setStatus(StatusCode.STATUS_302);
            ajaxBean.setMsg("当前是相同的EPC编号进行覆盖,建议做[入库处理]");
            return ajaxBean;
        }
    	CirculateDetail cd = queryCirculateDifference(circulateDetailId);
		// 通过OrderCode查Circulate对象
		CirculateOrder circulateOrder = queryCirculateOrder(cd.getOrderCode());
		Circulate circulateLatest = circulateService.queryCirculateLatestByEpcId(newEpcId, circulateOrder.getConsignorOrgId());
    	if(null == circulateLatest) {
    		SystemOrg org = systemOrgService.findById(circulateOrder.getConsignorOrgId());
    		ajaxBean.setStatus(StatusCode.STATUS_350);
            ajaxBean.setMsg("EPC编号["+ newEpcId +"]"+StatusCode.STATUS_350_MSG+"["+org.getOrgName()+"]");
            return ajaxBean;
    	}
		//进行EPC编号覆盖
        ajaxBean = circulateDifferenceResultService.epcCover(ajaxBean, sessionUser, circulateDetailId, epcId, newEpcId,circulateOrder);
        return ajaxBean;
    }
	/**
	 * 【流转单收货详情】页面处理收货差异：收货入库
	 * @param sessionUser
	 * @param ajaxBean
	 * @param circulateDetailId
	 * @param epcId
	 * @return
	 */
    @RequestMapping(value = "/circulateDifferenceResult/inOrgDispose")
    public AjaxBean inOrgDispose(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean, String circulateDetailId, String epcId) {
        //获取入库区域
        Area defaultArea = areaService.queryDefaultArea();
        if (null == defaultArea) {
            ajaxBean.setStatus(StatusCode.STATUS_317);
            ajaxBean.setMsg(StatusCode.STATUS_317_MSG);
            return ajaxBean;
        }
        //确认器具入库
        ajaxBean = circulateDifferenceResultService.inOrgDispose(ajaxBean, sessionUser, circulateDetailId, epcId, defaultArea);
        return ajaxBean;
    }

    /**
     * 【流转单收货详情】页面处理收货差异：索赔
     */
    @RequestMapping(value = "/circulateDifferenceResult/claimDispose")
    public AjaxBean claimDispose(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean, String circulateDetailId, String epcId) {
        if (StringUtils.isBlank(epcId)) {
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("当前数据的epcId编号" + StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        CirculateDetail diff = circulateDifferenceResultService.queryCirculateDetail(circulateDetailId);
        if(null == diff){
            ajaxBean.setStatus(StatusCode.STATUS_311);
            ajaxBean.setMsg("当前选中的流转单差异记录" + StatusCode.STATUS_311_MSG);
            return ajaxBean;
        }
        CirculateOrder order = circulateOrderService.queryCirculateOrderByOrderCode(diff.getOrderCode());
        //如果当前仓库不是包装流转单收货仓库，不能进行索赔
        if(!order.getTargetOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())){
            ajaxBean.setStatus(StatusCode.STATUS_351);
            ajaxBean.setMsg(StatusCode.STATUS_351_MSG+"["+ order.getTargetOrgName() +"],请重试！");
            return ajaxBean;
        }
        //确认索赔
        ajaxBean = circulateDifferenceResultService.claimDispose(ajaxBean,sessionUser, diff, epcId,order);
        return ajaxBean;
    }

    /**
     * 导出查询结果
     *
     * @param sessionUser
     * @param circulateDifference
     * @param startDate
     * @param endDate
     * @param orderCode
     * @param differenceId
     * @return
     */
    @RequestMapping("/circulateDifferenceResult/expertToExcel")
    public void outExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, HttpServletRequest request, HttpServletResponse response, String differenceId, CirculateDetail circulateDetail) {
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("包装流转单号");
        row0.createCell(1).setCellValue("EPC编号");
        row0.createCell(2).setCellValue("器具代码");
        row0.createCell(3).setCellValue("器具类型");
        row0.createCell(4).setCellValue("覆盖EPC编号");
        row0.createCell(5).setCellValue("差异处理结果");
        row0.createCell(6).setCellValue("收货日期和收货人");
        row0.createCell(7).setCellValue("收货仓库");
        row0.createCell(8).setCellValue("实收数量");
        row0.createCell(9).setCellValue("差异备注");
        //定义起始序列号
        int rowNumber = 1;

        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while (whileFlag) {
            paging.setCurrentPage(pageNum++);
            paging = this.pagingCirculateDetail(sessionUser, paging, circulateDetail, differenceId);

            List<CirculateDetail> list = (List<CirculateDetail>) paging.getData();
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            for (CirculateDetail o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS) {
                    whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(o.getOrderCode());
                row.createCell(1).setCellValue(o.getEpcId());
                row.createCell(2).setCellValue(o.getContainerCode());
                row.createCell(3).setCellValue(o.getContainerTypeName());
                row.createCell(4).setCellValue(o.getDealCoverageEpcId());
//                row.createCell(5).setCellValue(o.getDealResult());
                if(o.getDealResult().equals("1")){
                    row.createCell(5).setCellValue("待处理");
                }
                if(o.getDealResult().equals("2")){
                    row.createCell(5).setCellValue("入库处理");
                }
                if(o.getDealResult().equals("3")){
                    row.createCell(5).setCellValue("EPC覆盖");
                }
                if(o.getDealResult().equals("4")){
                    row.createCell(4).setCellValue("索赔");
                }
                if(o.getDealResult().equals("5")){
                    row.createCell(5).setCellValue("无差异");
                }

                if(o.getReceiveTime() !=null || o.getReceiveRealName() !=null){
                    row.createCell(6).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getReceiveTime())+" "+o.getReceiveRealName());
                }
                else{
                    row.createCell(6).setCellValue("");
                }
                row.createCell(7).setCellValue(o.getReceiveOrgName());
                row.createCell(8).setCellValue(o.getReceiveNumber());
                row.createCell(9).setCellValue(o.getRemark());
//                String value = "";
//                if (o.getDifferenceDealResult().equals(DealResult.UN_DISPOSE.getDifferenceId())) {
//                    value = DealResult.UN_DISPOSE.getDifferenceName();
//                } else if (o.getDifferenceDealResult().equals(DealResult.EPC_DISPOSE.getDifferenceId())) {
//                    value = DealResult.EPC_DISPOSE.getDifferenceName();
//                } else if (o.getDifferenceDealResult().equals(DealResult.INORG_DISPOSE.getDifferenceId())) {
//                    value = DealResult.INORG_DISPOSE.getDifferenceName();
//                } else if (o.getDifferenceDealResult().equals(DealResult.CLAIM_DISPOSE.getDifferenceId())) {
//                    value = DealResult.CLAIM_DISPOSE.getDifferenceName();
//                }
//                row.createCell(6).setCellValue(value);
                rowNumber++;
            }
            //如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
            if (list.size() < SysConstants.MAX_QUERY_ROWS) {
                break;
            }
        }
        ExcelUtil.exportExcel(response, wb, "【流转单收货详情】" + DateUtil.today_yyyyMMddHHmmssZH());
        try {
            if (null != wb) {
                wb.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage() + StatusCode.STATUS_408_MSG, e);
            logService.addLogError(sessionUser, e, e.getMessage() + StatusCode.STATUS_408_MSG, null);
        }
    }
}
