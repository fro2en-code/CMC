package com.cdc.cdccmc.controller.web.inventory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cdc.cdccmc.domain.dto.InventorySumDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdc.cdccmc.common.enums.InventoryDifferent;
import com.cdc.cdccmc.common.enums.OperatingSystemPlatform;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.OperatingSystemUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.InventoryDetailService;
import com.cdc.cdccmc.service.InventoryHistoryService;
import com.cdc.cdccmc.service.InventoryMainService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;
/**
 * 盘点
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class InventoryController {
	private Logger LOG = Logger.getLogger(InventoryController.class);
	@Autowired
	private InventoryMainService inventoryMainService;
	@Autowired
	private InventoryDetailService inventoryDetailService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;
	
    @Value("${upload.file.xlsx.path}")
    private String excelPath;
    @Value("${upload.file.xlsx.path.Windows}")
    private String excelPathWindows;
	
    /**
     * 盘点单列表
     * 
     * @param systemUser 
     * @param paging
     * @param inventoryMain
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    @RequestMapping("/inventory/pagingInventoryMainList")
    public Paging pagingInventoryMainList(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser, Paging paging,InventoryMain inventoryMain, String startDate,String endDate) {
        paging = inventoryMainService.queryInventoryMainList(systemUser,paging, inventoryMain, startDate, endDate);
        return paging;
    }
    
	/**
	 * 【当前库存】列表查询
	 * @param sessionUser 当前登录用户
	 * @param paging 分页支持
	 * @param containerCodeId 器具代码
	 * @param containerTypeId 器具类型
	 * @return
	 */
	@RequestMapping(value = "/inventory/pagingInventoryLatest")
    public Paging pagingInventoryLatest(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, String containerCodeId, String containerTypeId, String targetOrgId){
		LOG.info("request URL /inventory/pagingInventoryLatest");
		paging = inventoryHistoryService.pagingInventoryLatest(paging,sessionUser,containerCodeId,containerTypeId,targetOrgId);
		return paging;
    }
    
	/**
	 * 【库存历史】列表查询
	 * @param sessionUser 当前登录用户
	 * @param paging 分页支持
	 * @param containerCodeId 器具代码
	 * @param containerTypeId 器具类型
	 * @return
	 */
	@RequestMapping(value = "/inventory/pagingInventoryHistory")
    public Paging pagingInventoryHistory(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, String containerCodeId, String containerTypeId, String targetOrgId){
		LOG.info("request URL /inventory/pagingInventoryHistory");
		paging = inventoryHistoryService.pagingInventoryHistory(paging,sessionUser,containerCodeId,containerTypeId,targetOrgId);
		return paging;
    }

	/**
	 * 【库存历史】点击按钮：导出当前查询结果
	 * @param containerSell
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("/inventory/expertExcelForInventoryHistory")
	public void expertExcelForInventoryHistory(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String containerCodeId, String containerTypeId, String targetOrgId, HttpServletResponse response, String startDate, String endDate) {
		// 查询器具报废的所有信息
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue("序号");
		row0.createCell(1).setCellValue("仓库名称");
		row0.createCell(2).setCellValue("器具代码");
		row0.createCell(3).setCellValue("器具类型");
		row0.createCell(4).setCellValue("在库数量");
		row0.createCell(5).setCellValue("收货数量");
		row0.createCell(6).setCellValue("发货数量");
		row0.createCell(7).setCellValue("创建时间");
		row0.createCell(8).setCellValue("流转单号");
		row0.createCell(9).setCellValue("创建人");
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
			paging = this.pagingInventoryHistory(sessionUser,paging, containerCodeId,containerTypeId,targetOrgId);

			List<InventoryHistory> list = (List<InventoryHistory>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (InventoryHistory o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(rowNumber);
				row.createCell(1).setCellValue(o.getOrgName());
				row.createCell(2).setCellValue(o.getContainerCode());
				row.createCell(3).setCellValue(o.getContainerTypeName());
				row.createCell(4).setCellValue(o.getInOrgNumber());
				if(o.getReceiveNumber() == null){
					row.createCell(5).setCellValue("");
				}
				else{
					row.createCell(5).setCellValue(o.getReceiveNumber());
				}
				row.createCell(6).setCellValue(o.getSendNumber());
				row.createCell(7).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
				row.createCell(8).setCellValue(o.getOrderCode());
				row.createCell(9).setCellValue(o.getCreateRealName());
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}
		ExcelUtil.exportExcel(response,wb,"【器具汇总】"+ DateUtil.today_yyyyMMddHHmmssZH());
		try {
			if(null != wb){
				wb.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage()+ StatusCode.STATUS_408_MSG,e);
			logService.addLogError(sessionUser, e, e.getMessage()+ StatusCode.STATUS_408_MSG, null);
		}
	}

	/**
	 * 【当前库存】点击按钮：导出当前查询结果
	 * @param containerSell
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping("/inventory/expertExcelForInventoryLatest")
	public void expertExcelForInventoryLatest(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String containerCodeId, String containerTypeId, String targetOrgId, HttpServletResponse response, String startDate, String endDate) {
		// 查询器具报废的所有信息
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0 = sheet.createRow(0);
		row0.createCell(0).setCellValue("序号");
		row0.createCell(1).setCellValue("仓库名称");
		row0.createCell(2).setCellValue("器具代码");
		row0.createCell(3).setCellValue("器具类型");
		row0.createCell(4).setCellValue("在库数量");
		row0.createCell(5).setCellValue("收货数量");
		row0.createCell(6).setCellValue("发货数量");
		row0.createCell(7).setCellValue("创建时间");
		row0.createCell(8).setCellValue("流转单号");
		row0.createCell(9).setCellValue("创建人");
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
			paging = this.pagingInventoryLatest(sessionUser,paging, containerCodeId,containerTypeId,targetOrgId);

			List<InventoryHistory> list = (List<InventoryHistory>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (InventoryHistory o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(rowNumber);
				row.createCell(1).setCellValue(o.getOrgName());
				row.createCell(2).setCellValue(o.getContainerCode());
				row.createCell(3).setCellValue(o.getContainerTypeName());
				row.createCell(4).setCellValue(o.getInOrgNumber());
				if(o.getReceiveNumber() == null){
					row.createCell(5).setCellValue("");
				}
				else{
					row.createCell(5).setCellValue(o.getReceiveNumber());
				}
				row.createCell(6).setCellValue(o.getSendNumber());
				row.createCell(7).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
				row.createCell(8).setCellValue(o.getOrderCode());
				row.createCell(9).setCellValue(o.getCreateRealName());
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}
		ExcelUtil.exportExcel(response,wb,"【器具汇总】"+ DateUtil.today_yyyyMMddHHmmssZH());
		try {
			if(null != wb){
				wb.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage()+ StatusCode.STATUS_408_MSG,e);
			logService.addLogError(sessionUser, e, e.getMessage()+ StatusCode.STATUS_408_MSG, null);
		}
	}

    /**
     * 新建指定仓库的盘点单
     * @param sessionUser
     * @param orgId 指定仓库ID
     * @return
     */
    @RequestMapping(value = "/inventory/addInventoryMain")
    public AjaxBean addInventoryMain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orgId,String contactName,String contactPhone){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if(StringUtils.isBlank(orgId)){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("盘点仓库"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        //盘点仓库
        SystemOrg inventoryOrg = systemOrgService.findById(orgId);
        if(null == inventoryOrg){
            ajaxBean.setStatus(StatusCode.STATUS_311);
            ajaxBean.setMsg("盘点仓库"+StatusCode.STATUS_311_MSG);
            return ajaxBean;
        }
        //查询当前仓库下是否存在状态为“盘点中”的盘点单
        InventoryMain main = inventoryMainService.listNotFinishInventoryMain(inventoryOrg);
        if (null != main){ //如果当前仓库下已存在“盘点中”盘点单，则不能新增
            ajaxBean.setStatus(StatusCode.STATUS_319);
            ajaxBean.setMsg("["+inventoryOrg.getOrgName()+"]"+StatusCode.STATUS_319_MSG);
            return ajaxBean;
        }
        //盘点功能改造新逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
        ajaxBean = inventoryMainService.startInventory(ajaxBean,sessionUser,inventoryOrg,contactName,contactPhone);
        //盘点功能原老逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
        //ajaxBean = inventoryMainService.addInventoryMain(ajaxBean,sessionUser,inventoryOrg);
        return ajaxBean;
    }

	/**
	 * 新建指定仓库的盘点单
	 * @param sessionUser
	 * @param orgId 指定仓库ID
	 * @return
	 */
	@RequestMapping(value = "/inventory/addInventoryMainThread")
	public AjaxBean addInventoryMainThread(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orgId,String contactName,String contactPhone){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(orgId)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("盘点仓库"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		//盘点仓库
		SystemOrg inventoryOrg = systemOrgService.findById(orgId);
		if(null == inventoryOrg){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("盘点仓库"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		//查询当前仓库下是否存在状态为“盘点中”的盘点单
		InventoryMain main = inventoryMainService.listNotFinishInventoryMain(inventoryOrg);
		if (null != main){ //如果当前仓库下已存在“盘点中”盘点单，则不能新增
			ajaxBean.setStatus(StatusCode.STATUS_319);
			ajaxBean.setMsg("["+inventoryOrg.getOrgName()+"]"+StatusCode.STATUS_319_MSG);
			return ajaxBean;
		}
		//盘点功能改造新逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
		ajaxBean = inventoryMainService.startInventoryByThread(ajaxBean,sessionUser,inventoryOrg,contactName,contactPhone);
		//盘点功能原老逻辑 盘点功能20180528这周再做所以暂时维持老逻辑
		//ajaxBean = inventoryMainService.addInventoryMain(ajaxBean,sessionUser,inventoryOrg);
		return ajaxBean;
	}

    /**
     * 盘点完毕
     * @param sessionUser
     * @param inventoryId
     * @return
     */
    @RequestMapping(value = "/inventory/finishInventoryDetail")
    public AjaxBean finishInventoryDetail(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String inventoryId){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        ajaxBean = inventoryDetailService.finishInventoryDetail(sessionUser,inventoryId);
        return ajaxBean;
    }

    /**
     * 盘点完毕
     * @param sessionUser
     * @param inventoryMain
     * @return
     */
    @RequestMapping(value = "/inventory/finishInventoryMain")
    public AjaxBean finishInventoryMain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String inventoryId){
        // 获取当前仓库盘点单信息（主单）
    	InventoryMain main = inventoryMainService.findInventoryMainById(inventoryId);
    	AjaxBean ajaxBean = AjaxBean.SUCCESS();
    	if(null == main){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("盘点单"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
    	}
    	//如果盘点单不是“盘点中”状态，则不能操作盘点完毕
    	if(main.getInventoryState() != SysConstants.INTEGER_0){
			ajaxBean.setStatus(StatusCode.STATUS_321);
			ajaxBean.setMsg("["+main.getInventoryId()+"]"+StatusCode.STATUS_321_MSG);
			return ajaxBean;
    	}
		ajaxBean = inventoryMainService.finishInventoryMain(ajaxBean,sessionUser,main);
		return ajaxBean;
	}
    

	private void pvGetXCell(XSSFRow inRow, int columnIdx, XSSFCellStyle cs, String value) {
        XSSFCell c = inRow.createCell(columnIdx);
        if (cs != null) {
            c.setCellStyle(cs);
        }
        c.setCellValue(value);
    }
    /**
     * [库存盘点]页面,盘点差异Excel下载
     * @param sessionUser
     * @param claimDetail
     * @param response
     */
    @RequestMapping("/inventory/expertDifferentInventory")
    public void expertDifferentInventory(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
    		String inventoryId,  HttpServletResponse response){

        // 因为过时器具表和器具表融和,把此方法暂时挪到器具列表service中
        InventoryMain im = inventoryMainService.findInventoryMainById(inventoryId);
    	List<InventoryDetail> inventoryDetailList = inventoryDetailService.listInventoryDetailByInventoryId(inventoryId);
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFCellStyle cs = wb.createCellStyle();
        Font ft = wb.createFont();
        ft.setBold(true);
        cs.setFont(ft);
        // sheet1【盘点单信息】
        {
            XSSFSheet sheet = wb.createSheet("盘点单信息");
            //设置列宽
            sheet.setColumnWidth(0, 25 * 256); //key
            sheet.setColumnWidth(1, 25 * 256); //value
            //创建标题行
            XSSFRow row0=sheet.createRow(0);
            pvGetXCell(row0, 0, cs, "盘点单号");
            pvGetXCell(row0, 1, null, inventoryId);
            XSSFRow row1=sheet.createRow(1);
            pvGetXCell(row1, 0, cs, "盘点仓库");
            pvGetXCell(row1, 1, null, im.getInventoryOrgName());
            XSSFRow row2=sheet.createRow(2);
            pvGetXCell(row2, 0, cs, "创建人");
            pvGetXCell(row2, 1, null, im.getCreateRealName());
            XSSFRow row3=sheet.createRow(3);
            pvGetXCell(row3, 0, cs, "盘点日期");
            pvGetXCell(row3, 1, null, DateUtil.format("yyyy-MM-dd HH:mm:ss",im.getInventoryTime()));
            XSSFRow row4=sheet.createRow(4);
            pvGetXCell(row4, 0, cs, "盘点状态");
            pvGetXCell(row4, 1, null, im.getInventoryState() == SysConstants.INTEGER_1 ? "盘点完毕":"盘点中");
            XSSFRow row5=sheet.createRow(5);
            pvGetXCell(row5, 0, cs, "盘点完毕账号");
            pvGetXCell(row5, 1, null, im.getFinishAccount());
            XSSFRow row6=sheet.createRow(6);
            pvGetXCell(row6, 0, cs, "盘点完毕时间");
            pvGetXCell(row6, 1, null, DateUtil.format("yyyy-MM-dd HH:mm:ss",im.getFinishTime()));
            XSSFRow row7=sheet.createRow(7);
            pvGetXCell(row7, 0, cs, "盘点仓库联系人");
            pvGetXCell(row7, 1, null, im.getContactName());
            XSSFRow row8=sheet.createRow(8);
            pvGetXCell(row8, 0, cs, "盘点仓库联系人的联系方式");
            pvGetXCell(row8, 1, null, im.getContactPhone());
        }
        // sheet2【盘点明细】
        {
            XSSFSheet sheet = wb.createSheet("盘点明细");
            //设置列宽
            sheet.setColumnWidth(0, 25 * 256); //盘点编号
            sheet.setColumnWidth(1, 22 * 256); //EPC编号
            sheet.setColumnWidth(2, 15 * 256); //器具代码
			sheet.setColumnWidth(3, 15 * 256); //需盘点数量
			sheet.setColumnWidth(4, 15 * 256); //盘点到数量
			sheet.setColumnWidth(5, 15 * 256); //盘点区域
            sheet.setColumnWidth(6, 15 * 256); //差异类型
            sheet.setColumnWidth(7, 15 * 256); //旧区域
            sheet.setColumnWidth(8, 10 * 256); //盘点人
            sheet.setColumnWidth(9, 20 * 256); //盘点时间
			sheet.setColumnWidth(10, 10 * 256); //创建人
			sheet.setColumnWidth(11, 20 * 256); //创建时间
            //创建标题行
            XSSFRow row0=sheet.createRow(0);
            pvGetXCell(row0, 0, cs, "盘点编号");
            pvGetXCell(row0, 1, cs, "EPC编号");
            pvGetXCell(row0, 2, cs, "器具代码");
            pvGetXCell(row0, 3, cs, "需盘点数量");
            pvGetXCell(row0, 4, cs, "盘点到数量");
            pvGetXCell(row0, 5, cs, "盘点区域");
            pvGetXCell(row0, 6, cs, "差异类型");
            pvGetXCell(row0, 7, cs, "旧区域");
			pvGetXCell(row0, 8, cs, "盘点人");
			pvGetXCell(row0, 9, cs, "盘点时间");
			pvGetXCell(row0, 10, cs, "创建人");
			pvGetXCell(row0, 11, cs, "创建时间");
            //写入内容
            int rowNumber = 1;
            for (InventoryDetail detail : inventoryDetailList) {
                XSSFRow row=sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(detail.getInventoryId()); //盘点编号
                row.createCell(1).setCellValue(detail.getEpcId()); //EPC编号
                row.createCell(2).setCellValue(detail.getContainerCode()); //器具代码
				row.createCell(3).setCellValue(detail.getSystemNumber()); //需盘点数量
				row.createCell(4).setCellValue(detail.getInventoryNumber()); //盘点到数量
				row.createCell(5).setCellValue(detail.getAreaName()); //盘点区域
				//如果EPC有值，才有必要显示差异类型
				if(StringUtils.isNotBlank(detail.getEpcId())) {
	                row.createCell(6).setCellValue(InventoryDifferent.getName(String.valueOf(detail.getIsHaveDifferent())));  //差异类型
				}
                row.createCell(7).setCellValue(StringUtils.trim(detail.getOldAreaName())); //旧区域
                row.createCell(8).setCellValue(StringUtils.trim(detail.getInventoryRealName())); //盘点人
                row.createCell(9).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss",detail.getInventoryTime())); //盘点时间
				row.createCell(10).setCellValue(StringUtils.trim(detail.getCreateRealName())); //创建人
				row.createCell(11).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss",detail.getCreateTime())); //创建时间
                rowNumber++;
            }
        }
        // sheet3【盘点统计】
        {
            XSSFSheet sheet = wb.createSheet("盘点统计");
            //设置列宽
            sheet.setColumnWidth(0, 15 * 256); //器具代码
            sheet.setColumnWidth(1, 20 * 256); //器具类型
            sheet.setColumnWidth(2, 20 * 256); //器具名称
            sheet.setColumnWidth(3, 20 * 256); //系统保有量
            sheet.setColumnWidth(4, 20 * 256); //实际盘点数
			//创建标题行
			XSSFRow row0=sheet.createRow(0);
			pvGetXCell(row0, 0, cs, "器具代码");
			pvGetXCell(row0, 1, cs, "器具类型");
			pvGetXCell(row0, 2, cs, "器具名称");
			pvGetXCell(row0, 3, cs, "系统保有量");
			pvGetXCell(row0, 4, cs, "实际盘点数");
			pvGetXCell(row0, 5, cs, "差异数");
			//写入内容
			int rowNumber = 1;
			Paging paging = new Paging();
			paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
			//分页查询，一次查询2000条，统一装入一个list对象里面
			int pageNum = 1;
			boolean whileFlag = true;
			while(whileFlag){
				paging.setCurrentPage(pageNum++);
				paging = inventoryDetailService.pagingInventoryDetailSum(paging, inventoryId,sessionUser,null);
				List<InventorySumDto> list = (List<InventorySumDto>) paging.getData();
				if(CollectionUtils.isEmpty(list)){
					break;
				}
				for (InventorySumDto o : list) {
					if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
						whileFlag = false;
						break;
					}
					XSSFRow row = sheet.createRow(rowNumber);
					row.createCell(0).setCellValue(o.getContainerCode());
					row.createCell(1).setCellValue(o.getContainerTypeName());
					row.createCell(2).setCellValue(o.getContainerName());
					row.createCell(3).setCellValue(o.getAllNum());
					row.createCell(4).setCellValue(o.getActualNum());
					row.createCell(5).setCellValue((o.getActualNum()) - o.getAllNum());
					rowNumber++;
				}
				//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
				if (list.size()<SysConstants.MAX_QUERY_ROWS){
					break;
				}

			}

        }
        //把创建的excel写到response中
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode("【" + inventoryId + "】盘点单详情.xlsx", "UTF-8"))));
            wb.write(out);
        } catch (IOException e) {
            LOG.error(StatusCode.STATUS_406_MSG,e);
            logService.addLogError(sessionUser, e, inventoryId+StatusCode.STATUS_406_MSG, null);
        } finally {
            try { 
                out.close();
            } catch (IOException e) {
            	LOG.error(StatusCode.STATUS_407_MSG,e);
                logService.addLogError(sessionUser, e, inventoryId+StatusCode.STATUS_407_MSG, null);
            }
            try {
            	wb.close();
            } catch (IOException e) {
            	LOG.error(StatusCode.STATUS_408_MSG,e);
                logService.addLogError(sessionUser, e, inventoryId+StatusCode.STATUS_408_MSG, null);
            }
        }

    }

    /**
     * [库存历史]页面，库存调整
     * @param sessionUser
     * @param orgId 指定仓库ID
     * @return
     */
    @RequestMapping(value = "/inventory/adjustNumber")
    public AjaxBean adjustNumber(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String orgId
    		,String containerCode,Integer adjustNumber,Integer adjustRadio,String remark){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if(StringUtils.isBlank(orgId)){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("仓库"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if(StringUtils.isBlank(containerCode)){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if(null == adjustNumber){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("加减库存"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if(StringUtils.isBlank(remark)){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("备注"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        ajaxBean = inventoryHistoryService.adjustNumber(ajaxBean,sessionUser,orgId,containerCode,adjustNumber,adjustRadio,remark);
        return ajaxBean;
    }

    /**
     * [库存历史]页面，“excel批量初始化库存数量”按钮
     * @param sessionUser
     * @param orgId 指定仓库ID
     * @return
     */
    @RequestMapping(value = "/inventory/batchInitInOrgNumber")
    public AjaxBean batchInitInOrgNumber(@RequestParam("file")MultipartFile multipartFile,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,AjaxBean ajaxBean){
    	Long begin = System.currentTimeMillis();
        LOG.info("---batchInitInOrgNumber  begin="+begin);
        try {
            //新建文件（自定义路径）
            String path = excelPath;
            //如果是Windows系统，则使用另一个符合传统Windows路径的上传路径
            if(OperatingSystemPlatform.Windows.equals(OperatingSystemUtil.getOperatingSystemName())) {
            	path = excelPathWindows;
            }
            if(!multipartFile.getOriginalFilename().endsWith("xlsx")){
                ajaxBean.setStatus(StatusCode.STATUS_405);
                ajaxBean.setMsg(StatusCode.STATUS_405_MSG);
                return ajaxBean;
            }
    		long fileSize = multipartFile.getSize();
    		if(fileSize > SysConstants.MAX_FILE_UPLOAD_SIZE){ //如果文件大小超出上限
    			new BigDecimal(SysConstants.MAX_FILE_UPLOAD_SIZE);
    			ajaxBean.setStatus(StatusCode.STATUS_409);
    			ajaxBean.setMsg(StatusCode.STATUS_409_MSG);
    			return ajaxBean;
    		}
            LOG.info("---batchInitInOrgNumber check uploadfile="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
            String[] endWith = multipartFile.getOriginalFilename().split("\\.");
            File file = new File(path +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[batchInitInOrgNumber]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()){
                fileParent.mkdirs();
            }
            //上传到服务器
            multipartFile.transferTo(file);
            LOG.info("---batchInitInOrgNumber new file="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
            ajaxBean = inventoryHistoryService.batchInitInOrgNumber(file,sessionUser);

        } catch (Exception e) {
            LOG.error("",e);
            logService.addLogError(sessionUser, e, "", null);
            ajaxBean.setStatus(StatusCode.STATUS_201);
            ajaxBean.setMsg(StatusCode.STATUS_201_MSG);
        }
        LOG.info("---batchInitInOrgNumber end="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
        return ajaxBean;
    }

    /**
     * excel模版下载【批量初始化库存-模板下载】
     */
    @RequestMapping("/inventory/excelDownload")
    public void excelDownload(HttpServletRequest req, HttpServletResponse resp){
        String fileName = "BatchInsert-inOrgNumber.xlsx";
        ExcelUtil.downLoadExcel(req,resp,fileName);
    }
    
}