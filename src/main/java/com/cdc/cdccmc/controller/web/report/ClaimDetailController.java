package com.cdc.cdccmc.controller.web.report;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.cdc.cdccmc.common.enums.ClaimType;
import com.cdc.cdccmc.common.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.ClaimDetail;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.report.ClaimDetailService;


/**
 * 索赔明细
 * @author Jerry
 * @date 2018/1/22 15:53
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ClaimDetailController {
    private Logger logger = Logger.getLogger(ClaimDetailController.class);

    @Autowired
    private ClaimDetailService claimDetailService;
    @Autowired
	private LogService logService;

    /**
     * 索赔明细列表查询
     * @param sessionUser
     * @param paging
     * @return
     */
    @RequestMapping(value = "/claimDetail/pagingClaimDetail")
    public Paging pagingClaimDetail(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, String orderCode
    		,String startDate,String endDate){
        paging = claimDetailService.pagingClaimDetail(paging,sessionUser.getFilialeSystemOrgIds(), orderCode,startDate,endDate);
        return paging;
    }


    /**
     * 把查询出的结果写到excel中,导出给用户
     *
     * @param sessionUser
     * @param claimDetail
     * @param response
     */
    @RequestMapping("/claimDetail/expertToExcel")
    public void expertToExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orderCode,  HttpServletResponse response, String startDate,String endDate){

        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0=sheet.createRow(0);
        row0.createCell(0).setCellValue("索赔来源");
        row0.createCell(1).setCellValue("包装流转单号");
        row0.createCell(2).setCellValue("EPC编号");
        row0.createCell(3).setCellValue("器具代码");
        row0.createCell(4).setCellValue("器具类型");
        row0.createCell(5).setCellValue("数量");
        row0.createCell(6).setCellValue("差异备注");
        row0.createCell(7).setCellValue("发生日期");
        row0.createCell(8).setCellValue("创建仓库");
        row0.createCell(9).setCellValue("创建人");
        //写入内容
        int rowNumber = 1;

        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while(whileFlag){
            paging.setCurrentPage(pageNum++);
            paging = this.pagingClaimDetail(sessionUser,paging, orderCode, startDate, endDate);

            List<ClaimDetail> list = (List<ClaimDetail>) paging.getData();
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            for (ClaimDetail o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
                    whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                for (ClaimType claimType :ClaimType.values()) {
                    if (claimType.getClaimTypeId().equals(o.getClaimType())){
                        row.createCell(0).setCellValue(claimType.getClaimTypeName());
                    }
                }
                row.createCell(1).setCellValue(o.getOrderCode());
                row.createCell(2).setCellValue(o.getEpcId());
                row.createCell(3).setCellValue(o.getContainerCode());
                row.createCell(4).setCellValue(o.getContainerTypeName());
                row.createCell(5).setCellValue(1);
                row.createCell(6).setCellValue(o.getRemark());
                row.createCell(7).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss",o.getCreateTime()));
                row.createCell(8).setCellValue(o.getCreateOrgName());
                row.createCell(9).setCellValue(o.getCreateRealName());
                rowNumber++;
            }
            //如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
            if (list.size()<SysConstants.MAX_QUERY_ROWS){
                break;
            }
        }
        ExcelUtil.exportExcel(response,wb,"【索赔明细】"+DateUtil.today_yyyyMMddHHmmssZH());
        try {
            if(null != wb){
                wb.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage()+StatusCode.STATUS_408_MSG,e);
            logService.addLogError(sessionUser, e, e.getMessage()+StatusCode.STATUS_408_MSG, null);
        }
    }
}
