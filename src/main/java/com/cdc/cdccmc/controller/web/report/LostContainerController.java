package com.cdc.cdccmc.controller.web.report;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.ClaimDetail;
import com.cdc.cdccmc.domain.container.ContainerLost;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.report.ContainerLostService;

/**
 * 器具丢失
 *
 * @author Clm
 * @date 2018/1/23
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class LostContainerController {
    private Logger LOG = Logger.getLogger(LostContainerController.class);
    @Autowired
    private ContainerLostService containerLostService;
    @Autowired
	private LogService logService;

    /**
     * 丢失查询
     *
     * @param paging
     * @param containerLost
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping(value = "/lostContainer/pagingLostContainer")
    public Paging pagingLostContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, ContainerLost containerLost, String startDate, String endDate) {
        paging = containerLostService.pagingLostContainer(paging, sessionUser, containerLost, startDate, endDate);
        return paging;
    }

    /**
     * 报表中心--丢失器具页面：索赔按钮点击时间
     *
     * @param systemUser
     * @param containerLost
     * @param ajaxBean
     * @param claimDetail
     * @param claimTypeId
     * @return
     */
    @RequestMapping(value = "/lostContainer/setIsClaim")
    public AjaxBean setIsClaim(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, String containerLostId, AjaxBean ajaxBean, ClaimDetail claimDetail) {
        //通过containerLostId查询ContainerLost对象
        ContainerLost containerLost = containerLostService.queryContainerLostById(containerLostId);
        //修改索赔明细状态
        ajaxBean = containerLostService.setIsClaim(ajaxBean,sessionUser, containerLost, claimDetail);
        return ajaxBean;
    }

    /**
     * 导出器具报废查询结果
     *
     * @param containerLost
     * @param startDate
     * @param endDate
     * @return
     */
    @RequestMapping("/lostContainer/expertToExcel")
    public void outExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, ContainerLost containerLost, HttpServletRequest request, HttpServletResponse response, String startDate, String endDate) {
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("序号");
        row0.createCell(1).setCellValue("EPC编号");
        row0.createCell(2).setCellValue("印刷编号");
        row0.createCell(3).setCellValue("器具代码");
        row0.createCell(4).setCellValue("器具类型");
        row0.createCell(5).setCellValue("盘点编号");
        row0.createCell(6).setCellValue("创建仓库");
        row0.createCell(7).setCellValue("丢失时间");
        row0.createCell(8).setCellValue("丢失确认人");
        row0.createCell(9).setCellValue("丢失备注");
        row0.createCell(10).setCellValue("索赔状态");
        row0.createCell(11).setCellValue("是否丢失出库");
        row0.createCell(12).setCellValue("包装流转单号");
        //定义起始序列号
        int rowNumber = 1;

        //开始计算分页数据
        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while(whileFlag){
            paging.setCurrentPage(pageNum++);
            paging = this.pagingLostContainer(sessionUser,paging, containerLost, startDate, endDate);

            List<ContainerLost> list = (List<ContainerLost>) paging.getData();
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            for (ContainerLost o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
                    whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(rowNumber);
                row.createCell(1).setCellValue(o.getEpcId());
                row.createCell(2).setCellValue(o.getPrintCode());
                row.createCell(3).setCellValue(o.getContainerCode());
                row.createCell(4).setCellValue(o.getContainerTypeName());
                row.createCell(5).setCellValue(o.getInventoryId());
                row.createCell(6).setCellValue(o.getCreateOrgName());
                row.createCell(7).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
                row.createCell(8).setCellValue(o.getCreateRealName());
                if(o.getLostRemark() !=null){
                    row.createCell(9).setCellValue(o.getLostRemark());
                }
                if(o.getIsClaim()==0) {
                	row.createCell(10).setCellValue("未索赔");
                }else if(o.getIsClaim()==1){
                	row.createCell(10).setCellValue("已进索赔库");
                }else {
                	row.createCell(10).setCellValue("");
                }
                if(o.getIsOut().equals("0")){
                    row.createCell(11).setCellValue("是");
                }
                if(o.getIsOut().equals("1")){
                    row.createCell(11).setCellValue("否");
                }
                row.createCell(12).setCellValue(o.getOrderCode());
                rowNumber++;
            }
            //如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
            if (list.size()<SysConstants.MAX_QUERY_ROWS){
                break;
            }
        }
        ExcelUtil.exportExcel(response,wb,"【丢失器具】"+DateUtil.today_yyyyMMddHHmmssZH());
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
