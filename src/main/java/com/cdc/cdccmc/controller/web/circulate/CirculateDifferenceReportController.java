package com.cdc.cdccmc.controller.web.circulate;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cdc.cdccmc.common.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.container.ContainerCodeController;
import com.cdc.cdccmc.domain.dto.CirculateDifferenceDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateDifferenceReportService;
import com.cdc.cdccmc.service.LogService;

/**
 * 流转单差异报告
 *
 * @author Clm
 * @date 2018-01-26
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateDifferenceReportController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateDifferenceReportController.class);
    @Autowired
    private CirculateDifferenceReportService circulateDifferenceReportService;
    @Autowired
	private LogService logService;

    /**
     * 查询
     *
     * @param paging
     * @param sessionUser
     * @param circulateDifferenceDto
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/circulateDifferenceReport/pagingCirculateDifferenceReport")
    public Paging pagingCirculateDifferenceReport(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, CirculateDifferenceDto circulateDifferenceDto, String startDate, String endDate) {
        paging = circulateDifferenceReportService.pagingCirculateDifferenceReport(paging, sessionUser, startDate, endDate, circulateDifferenceDto);
        return paging;
    }

    /**
     * 导出查询结果
     *
     * @param sessionUser
     * @param circulateDifferenceDto
     * @param startDate
     * @param endDate
     * @param orderCode
     * @return
     */
    @RequestMapping("/circulateDifferenceReport/expertToExcel")
    public void outExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, CirculateDifferenceDto circulateDifferenceDto, HttpServletResponse response, String startDate, String endDate, String orderCode) {
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("包装流转单号");
        row0.createCell(1).setCellValue("收货日期");
        row0.createCell(2).setCellValue("EPC编号");
        row0.createCell(3).setCellValue("器具代码");
        row0.createCell(4).setCellValue("器具类型");
        row0.createCell(5).setCellValue("发货仓库");
        row0.createCell(6).setCellValue("收货仓库");
        row0.createCell(7).setCellValue("数量");
        row0.createCell(8).setCellValue("收货人");
        row0.createCell(9).setCellValue("差异备注");
        //定义起始序列号
        int rowNumber = 1;

        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while(whileFlag){
            paging.setCurrentPage(pageNum++);
            paging = this.pagingCirculateDifferenceReport(sessionUser,paging, circulateDifferenceDto, startDate, endDate);

            List<CirculateDifferenceDto> list = (List<CirculateDifferenceDto>) paging.getData();
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            for (CirculateDifferenceDto o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
                    whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(o.getOrderCode());
                row.createCell(1).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getTargetTime()));
                row.createCell(2).setCellValue(o.getEpcId());
                row.createCell(3).setCellValue(o.getContainerCode());
                row.createCell(4).setCellValue(o.getContainerTypeName());
                row.createCell(5).setCellValue(o.getConsignorOrgName());
                row.createCell(6).setCellValue(o.getTargetOrgName());
                row.createCell(7).setCellValue(o.getDifferenceNumber());
                row.createCell(8).setCellValue(o.getTargetRealName());
                row.createCell(9).setCellValue(o.getDifferenceRemark());
                rowNumber++;
            }
            //如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
            if (list.size()<SysConstants.MAX_QUERY_ROWS){
                break;
            }
        }
        ExcelUtil.exportExcel(response,wb,"流转单差异报告"+DateUtil.today_yyyyMMddHHmmss());
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
