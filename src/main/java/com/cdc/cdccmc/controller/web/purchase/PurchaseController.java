package com.cdc.cdccmc.controller.web.purchase;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cdc.cdccmc.service.LogService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.PurchaseInOrgDetail;
import com.cdc.cdccmc.domain.PurchaseInOrgMain;
import com.cdc.cdccmc.domain.PurchasePrepare;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.dto.EpcSumDto;
import com.cdc.cdccmc.domain.dto.PruchaseDetailDto;
import com.cdc.cdccmc.domain.dto.PurchaseSumDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.PurchasePrepareService;

/**
 * 采购预备表、采购入库
 * 
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class PurchaseController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PurchaseController.class);
	@Autowired
	private PurchasePrepareService purchasePrepareService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private LogService logService;
	@Value("${upload.file.xlsx.path}")
	private String uploadPath;

	/**
	 * 采购预备表分页
	 * @param sessionUser
	 * @param paging
	 * @param startDate 入库开始日期
	 * @param endDate 入库结束日期
	 * @return
	 */
	@RequestMapping("/purchase/pagingPurchasePrepare")
	public Paging pagingPurchasePrepare(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging,
			String startDate, String endDate) {
		LOG.info("request URL /purchase/pagingPurchasePrepare");
		return purchasePrepareService.pagingPurchasePrepare(sessionUser, paging, startDate, endDate);
	}
	/**
	 * 生成新采购入库单，和明细单
	 * @param epcIdList 采购入库单的epc编号器具
	 * @return
	 */
	@RequestMapping("/purchase/createPurchaseInOrg")
	public AjaxBean createPurchaseInOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,@RequestParam(value = "epcIdList[]",required=false) List<String> epcIdList
			,PurchaseInOrgMain purchaseInOrgMain) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		LOG.info("request URL /purchase/createPurchaseInOrg");
		if(CollectionUtils.isEmpty(epcIdList)){
			ajaxBean.setStatus(StatusCode.STATUS_334);
			ajaxBean.setMsg(StatusCode.STATUS_334_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(purchaseInOrgMain.getConsignorOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("发货方"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(sessionUser.getCurrentSystemOrg().getOrgId().equals(purchaseInOrgMain.getConsignorOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_335);
			ajaxBean.setMsg(StatusCode.STATUS_335_MSG);
			return ajaxBean;
		}
		PurchasePrepare p = null;
		for (String epc : epcIdList) {
			 p = purchasePrepareService.queryPurchasePrepareByEpcId(epc);
			 //如果此epc编号已经存在于某个包装入库单，则不能重复添加至别的包装入库单
			 if(null != p && StringUtils.isNotBlank(p.getPurchaseInOrgMainId()) ) {
				 ajaxBean.setStatus(StatusCode.STATUS_201);
				 ajaxBean.setMsg("EPC编号["+epc+"]已存在于包装入库单["+p.getPurchaseInOrgMainId()+"]");
				 return ajaxBean;
			 }
		}
		ajaxBean = purchasePrepareService.createPurchaseInOrg(ajaxBean,sessionUser,purchaseInOrgMain,epcIdList);
		return ajaxBean;
	}

	/**
	 * 模版下载
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/purchase/excelDownload", method = {RequestMethod.POST, RequestMethod.GET})
	public void downLoadFile(HttpServletRequest req, HttpServletResponse resp) {
		String fileDownName ="BatchInsert-PurchasePrepare.xlsx";
		ExcelUtil.downLoadExcel(req,resp,fileDownName);
	}

	/**
	 * excel 上传,入库
	 * @param multipartFile
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/purchase/batchUpload")
	public AjaxBean batchUpload(@RequestParam("file")MultipartFile multipartFile, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean){
		if(!multipartFile.getOriginalFilename().endsWith(SysConstants.XLSX)){
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
		String[] endWith = multipartFile.getOriginalFilename().split("\\.");
		File file = new File(uploadPath +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-Account]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()){
			fileParent.mkdirs();
		}

		try {
			multipartFile.transferTo(file); //上传到服务器
		} catch (IllegalStateException | IOException e) {
			LOG.error(e.getMessage(),e);
			ajaxBean.setStatus(StatusCode.STATUS_400);
			ajaxBean.setMsg(StatusCode.STATUS_400_MSG);
			return ajaxBean;
		}
		ajaxBean = purchasePrepareService.batchUpload(sessionUser,file);
		return ajaxBean;
	}
	
	/**
	 * 采购入库表分页
	 * @param purchaseInOrgMainId 入库单号
	 * @param paging
	 * @param startDate 入库开始日期
	 * @param endDate 入库结束日期
	 * @return
	 */
	@RequestMapping("/purchase/pagingPurchaseInOrg")
	public Paging pagingPurchaseInOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging,
			String startDate, String endDate, String purchaseInOrgMainId) {
		return purchasePrepareService.pagingPurchaseInOrg(sessionUser, paging, startDate, endDate, purchaseInOrgMainId);
	}
    
    /**
     * 入库单详情查询
     * @param purchaseInOrgMainId 入库单号
     * @return paging
     */
    @RequestMapping("/purchase/queryInOrgDetail")
    public Paging queryCirculateDetail(Paging paging,String purchaseInOrgMainId) {
    	//采购入库的器具明细列表
        List<PurchaseInOrgDetail> piodList = purchasePrepareService.queryCirculateDetail(purchaseInOrgMainId);
        //采购入库的器具统计列表
        List<PurchaseSumDto> psdList = purchasePrepareService.buildPurchaseSumDtoByPurchaseInOrgDetail(piodList);
        paging.setStatus(200);
        paging.setData(piodList);
        paging.setBean(psdList);
        return paging;
    }

	/**
	 * 打印采购入库单
	 *
	 * @param sessionUser
	 * @param purchaseInOrgMainId
	 * @return
	 */
	@RequestMapping("/purchase/printInbound")
	public AjaxBean printInbound(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String purchaseInOrgMainId) {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean = purchasePrepareService.printInbound(ajaxBean, sessionUser, purchaseInOrgMainId);
		return ajaxBean;
	}

	/**
	 * 采购入库单页面，点击按钮【下载资产编码excel】
	 */
	@RequestMapping("/purchase/exportToExcelAssets")
	public void exportToExcelAssets(@ModelAttribute(SysConstants.SESSION_USER)SystemUser sessionUser,String purchaseInOrgMainId,HttpServletResponse response){
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet1 = wb.createSheet("资产编码-器具明细");
		XSSFSheet sheet2 = wb.createSheet("器具统计");
		XSSFSheet sheet3 = wb.createSheet("包装入库单");
		//设置列宽
		sheet1.setColumnWidth(0, 25 * 256); //key
		sheet1.setColumnWidth(1, 20 * 256); //key
		sheet1.setColumnWidth(2, 50 * 256); //key
		sheet1.setColumnWidth(3, 25 * 256); //key
		
		sheet2.setColumnWidth(0, 25 * 256); //key
		sheet2.setColumnWidth(1, 50 * 256); //key
		sheet2.setColumnWidth(2, 10 * 256); //key
		
		sheet3.setColumnWidth(0, 25 * 256); //key
		sheet3.setColumnWidth(1, 80 * 256); //key
		sheet3.setColumnWidth(2, 10 * 256); //key
		//创建标题行
		XSSFRow row0 = sheet1.createRow(0);
		row0.createCell(0).setCellValue("EPC编号（资产编码）");
		row0.createCell(1).setCellValue("器具代码");
		row0.createCell(2).setCellValue("器具名称");
		row0.createCell(3).setCellValue("创建时间");
		XSSFRow row1 = sheet2.createRow(0);
		row1.createCell(0).setCellValue("器具代码");
		row1.createCell(1).setCellValue("器具名称");
		row1.createCell(2).setCellValue("数量");
		XSSFRow row2 = sheet3.createRow(0);
		row2.createCell(0).setCellValue("入库单号");
		row2.createCell(1).setCellValue("入库备注");
		row2.createCell(2).setCellValue("器具入库总数");

		//定义起始序列号
		int rowNumber1 = 1;
		int rowNumber2 = 1;
		int rowNumber3 = 1;
		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag){
			paging.setCurrentPage(pageNum++);
			paging = this.queryCirculateDetail(paging,purchaseInOrgMainId);

			List<PurchaseInOrgDetail> list = (List<PurchaseInOrgDetail>) paging.getData();
			List<PurchaseSumDto> purchaseCount = purchasePrepareService.buildPurchaseSumDtoByPurchaseInOrgDetail(list);
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (PurchaseInOrgDetail o : list) {
				if (rowNumber1 > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row_1 = sheet1.createRow(rowNumber1);
				row_1.createCell(0).setCellValue(o.getEpcId());
				row_1.createCell(1).setCellValue(o.getContainerCode());
				row_1.createCell(2).setCellValue(o.getContainerName());
				row_1.createCell(3).setCellValue(DateUtil.format("yyyy-MM-dd HH:mm:ss",o.getCreateTime()));
				rowNumber1++;
			}
			for (PurchaseSumDto  o : purchaseCount) {
				if (rowNumber2 > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row_2 = sheet2.createRow(rowNumber2);
				row_2.createCell(0).setCellValue(o.getContainerCode());
				row_2.createCell(1).setCellValue(o.getContainerName());
				row_2.createCell(2).setCellValue(o.getPurchaseCount());
				rowNumber2++;
			}
			XSSFRow row_3 = sheet3.createRow(rowNumber3);
			row_3.createCell(0).setCellValue(purchaseInOrgMainId);
			row_3.createCell(1).setCellValue(purchasePrepareService.queryPurchaseInOrgDetailByMainId(purchaseInOrgMainId).getInOrgRemark());
			row_3.createCell(2).setCellValue(purchasePrepareService.queryPurchaseInOrgDetailByMainId(purchaseInOrgMainId).getInOrgNumber());

			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}
		ExcelUtil.exportExcel(response,wb,"资产编码"+"（" +purchaseInOrgMainId+ "）");
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