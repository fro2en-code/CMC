package com.cdc.cdccmc.controller.web.container;

import com.cdc.cdccmc.common.util.*;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.container.ContainerType;
import com.cdc.cdccmc.domain.sys.SystemUser;

import com.cdc.cdccmc.service.ContainerTypeService;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.math.BigDecimal;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


/** 
 * 器具信息
 * @author Jerry
 * @date 2017-12-28
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ContainerController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerController.class);
	@Autowired
	private ContainerService containerService;
	@Autowired
	private ContainerTypeService containerTypeService;
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private LogService logService;
    @Value("${upload.container.pic.containerPic.path}")
    private String imgPath;

    @Value("${upload.file.xlsx.path}")
    private String excelPath;

    /**
     * 器具列表查询
     * @param sessionUser
     * @param paging
     * @param container
     * @return
     */
	@RequestMapping(value = "/container/pagingContainer")
    public Paging pagingContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging, Container container){
		LOG.info("request URL /container/listContainer");
		container.setIsOutmode(0); //0未过时  1过时
		paging = containerService.pagingContainer(paging,container,sessionUser.getFilialeSystemOrgIds(),null,null);
		return paging;
    }
    /**
     * 过时器具列表查询
     * @param systemUser
     * @param paging
     * @param container
     * @param startDate
     * @param endDate
     * @return
     */
	@RequestMapping(value = "/container/pagingOutmodeContainer")
    public Paging pagingOutmodeContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser, Paging paging, Container container,String startDate, String endDate){
		LOG.info("request URL /container/listContainer");
		container.setIsOutmode(1); //0未过时  1过时
		paging = containerService.pagingContainer(paging,container,systemUser.getFilialeSystemOrgIds(),startDate,endDate);
		return paging;
    }

    /**
     * 过时器具导出
     * 把查询出的结果写到excel中,导出给用户
     * @param sessionUser
     * @param container
     * @param response
     * @param startDate
     * @param endDate
     */
    @RequestMapping("/container/expertToExcel")
    public void expertToExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Container container, HttpServletResponse response,String startDate ,String endDate){
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0=sheet.createRow(0);
        row0.createCell(0).setCellValue("EPC编号");
        row0.createCell(1).setCellValue("印刷编号");
        row0.createCell(2).setCellValue("器具代码");
        row0.createCell(3).setCellValue("器具类型");
        row0.createCell(4).setCellValue("器具名称");
        row0.createCell(5).setCellValue("合同号");
        row0.createCell(6).setCellValue("领用单号");
        row0.createCell(7).setCellValue("操作人与操作时间");
        //写入内容
        int rowNumber = 1;

        //因为考虑到符合条件的数量很多，所以暂时屏蔽一次查询出所有的方法，改为分页查询，一次查询5000条
//		List<Container> outmodes = containerService.queryAllContainerList(container, startDate,endDate);
        //开始计算分页数据
        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while(whileFlag){
            paging.setCurrentPage(pageNum++);
            paging = this.pagingOutmodeContainer(sessionUser,paging, container, startDate, endDate);

            List<Container> list = (List<Container>) paging.getData();
            if(CollectionUtils.isEmpty(list)){
            	break;
            }
            for (Container o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
                	whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(o.getEpcId());
                row.createCell(1).setCellValue(o.getPrintCode());
                row.createCell(2).setCellValue(o.getContainerCode());
                row.createCell(3).setCellValue(o.getContainerTypeName());
                row.createCell(4).setCellValue(o.getContainerName());
                if (StringUtils.isNotBlank(o.getContractNumber())){
                    row.createCell(5).setCellValue(o.getContractNumber().trim());
                }else {
                    row.createCell(5).setCellValue(o.getContractNumber());
                }
                if (StringUtils.isNotBlank(o.getReceiveNumber())){
                    row.createCell(6).setCellValue(o.getReceiveNumber().trim());
                }else {
                    row.createCell(6).setCellValue(o.getReceiveNumber());
                }
                row.createCell(7).setCellValue(o.getModifyRealName() + " " + DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getModifyTime()));
                rowNumber++;
            }
            //如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
            if (list.size()<SysConstants.MAX_QUERY_ROWS){
                break;
            }
        }
        ExcelUtil.exportExcel(response,wb,"【过时器具列表】"+DateUtil.today_yyyyMMddHHmmssZH());
        try {
            if(null != wb){
                wb.close();
            }
        } catch (IOException e) {
            LOG.error(e.getMessage()+StatusCode.STATUS_408_MSG,e);
            logService.addLogError(sessionUser, e, e.getMessage()+StatusCode.STATUS_408_MSG, null);
        }

    }

    /**
     * 器具列表导出
     *
     * @param sessionUser
     * @param container
     * @param epcId
     * @param printCode
     * @param containerTypeName
     * @param containerCode
     * @return
     */
    @RequestMapping("/container/expertToExcelContainer")
    public void expertToExcelContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Container container, HttpServletResponse response) {
        // 建立工作簿和电子表格对象
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("sheet1");
        //创建标题行
        XSSFRow row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("EPC编号");
        row0.createCell(1).setCellValue("印刷编号");
        row0.createCell(2).setCellValue("器具代码");
        row0.createCell(3).setCellValue("器具类型");
        row0.createCell(4).setCellValue("器具名称");
        row0.createCell(5).setCellValue("是否是托盘");
        row0.createCell(6).setCellValue("EPC类型");
        row0.createCell(7).setCellValue("规格");
        row0.createCell(8).setCellValue("材质");
        row0.createCell(9).setCellValue("是否单独成托");
        row0.createCell(10).setCellValue("创建人与创建时间");
        row0.createCell(11).setCellValue("修改人与修改时间");
        row0.createCell(12).setCellValue("隶属仓库");
        row0.createCell(13).setCellValue("创建仓库");
        row0.createCell(14).setCellValue("最后所在仓库");
        //定义起始序列号
        int rowNumber = 1;

        Paging paging = new Paging();
        paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
        //分页查询，一次查询2000条，统一装入一个list对象里面
        int pageNum = 1;
        boolean whileFlag = true;
        while(whileFlag){
            paging.setCurrentPage(pageNum++);
            paging = this.pagingContainer(sessionUser,paging, container);

            List<Container> list = (List<Container>) paging.getData();
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            for (Container o : list) {
                if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
                    whileFlag = false;
                    break;
                }
                XSSFRow row = sheet.createRow(rowNumber);
                row.createCell(0).setCellValue(o.getEpcId());
                row.createCell(1).setCellValue(o.getPrintCode());
                row.createCell(2).setCellValue(o.getContainerCode());
                row.createCell(3).setCellValue(o.getContainerTypeName());
                row.createCell(4).setCellValue(o.getContainerName());
                if(o.getIsTray() == 0){
                    row.createCell(5).setCellValue("不是");
                }
                else{
                    row.createCell(5).setCellValue("是");
                }
                row.createCell(6).setCellValue(o.getEpcType());
                row.createCell(7).setCellValue(o.getContainerSpecification());
                row.createCell(8).setCellValue(o.getContainerTexture());
                if(o.getIsAloneGroup() == 0){
                    row.createCell(9).setCellValue("不是");
                }
                else{
                    row.createCell(9).setCellValue("是");
                }
                row.createCell(10).setCellValue(o.getCreateRealName() +" "+ DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
                if(o.getModifyRealName()==null || o.getModifyRealName()=="") {
                    row.createCell(11).setCellValue("");
                }else {
                    row.createCell(11).setCellValue(o.getModifyRealName() + " " + DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss, o.getModifyTime()));
                }
                row.createCell(12).setCellValue(o.getBelongOrgName());
                row.createCell(13).setCellValue(o.getCreateRealName());
                row.createCell(14).setCellValue(o.getLastOrgName());
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

    /**
     * 新增器具
     * @param sessionUser
     * @param container
     * @return
     */
	@RequestMapping(value = "/container/addContainer")
    public AjaxBean addContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Container container){
		AjaxBean ajaxBean = new AjaxBean();
        if (StringUtils.isBlank(container.getContainerName())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具名称"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getEpcType())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("EPC类型"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getEpcId())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("EPC编号"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getContainerTypeName())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具类型名称"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getContainerTypeName())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具类型名称"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getContainerCode())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        ContainerCode findCode = containerCodeService.queryByContainerCode(container.getContainerCode());
        if(null == findCode){
            ajaxBean.setStatus(StatusCode.STATUS_311);
            ajaxBean.setMsg("器具代码["+container.getContainerCode()+"]"+StatusCode.STATUS_311_MSG);
            return ajaxBean;
        }
        //是否为禁用状态
        if(findCode.getIsActive() == SysConstants.INTEGER_1){
        	ajaxBean.setStatus(StatusCode.STATUS_331);
        	ajaxBean.setMsg("器具代码["+container.getContainerCode()+"]被禁用，新增失败");
        	return ajaxBean;
        }
        if (container.getIsAloneGroup()==null){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("是否单独成拖"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
		Container findContainer = containerService.findContainerByEpcId(container.getEpcId());
		if (null != findContainer){
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("当前器具的EPC编号["+ findContainer.getEpcId() +"]"+StatusCode.STATUS_302_MSG);
			return ajaxBean;
		}
        ajaxBean = containerService.addContainer(sessionUser,container);
		return ajaxBean;
    }

    /**
     * 根据ecpId上传图片,如果epcId相同那么覆盖
     * @param multipartFile
     * @param epcId
     * @return
     */
    @RequestMapping("/container/uploadImage")
    public AjaxBean uploadImage(@RequestParam("file")MultipartFile multipartFile,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String epcId){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if (StringUtils.isEmpty(epcId)){
			ajaxBean.setMsg(StatusCode.STATUS_300_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_300);
			return ajaxBean;
		}

		try {
			//新建文件（自定义路径）
            String path = imgPath;
            String[] endWith = multipartFile.getOriginalFilename().split("\\.");
            File file = new File(path +"/"+ epcId+".png");
            File fileParent = file.getParentFile();
            if (!fileParent.exists()){
                fileParent.mkdirs();
            }
            //上传到服务器
            multipartFile.transferTo(file);
		} catch (IOException e) {
			LOG.error("器具图片上传异常",e);
			logService.addLogError(sessionUser, e, "器具["+epcId+"]图片上传异常", null);
		}
		return ajaxBean;
	}

    /**
     * 器具更新
     * @param sessionUser
     * @param container
     * @return
     */
    @RequestMapping("/container/updateContainer")
	public AjaxBean updateContainer(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Container container){
        AjaxBean ajaxBean = new AjaxBean();
        if (StringUtils.isBlank(container.getContainerName())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具名称"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getEpcType())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("EPC类型"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getEpcId())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("EPC编号"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getContainerCode())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        ContainerCode findCode = containerCodeService.queryByContainerCode(container.getContainerCode());
        if(null == findCode){
            ajaxBean.setStatus(StatusCode.STATUS_311);
            ajaxBean.setMsg("器具代码["+container.getContainerCode()+"]"+StatusCode.STATUS_311_MSG);
            return ajaxBean;
        }
        if(findCode.getIsActive() == SysConstants.INTEGER_1){
            ajaxBean.setStatus(StatusCode.STATUS_340);
            ajaxBean.setMsg("["+container.getContainerCode()+"]"+StatusCode.STATUS_340_MSG);
            return ajaxBean;
        }
        container.setModifyAccount(sessionUser.getAccount());
        container.setModifyRealName(sessionUser.getRealName());
        container.setLastOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
        if (StringUtils.isBlank(container.getContainerTypeName())){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("器具类型名称"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        //检测containerType是否存在,不存在则报错，器具更新不进行新增器具类型
        ContainerType findType = containerTypeService.findByName(container.getContainerTypeName());
        if(null == findType){
            ajaxBean.setStatus(StatusCode.STATUS_311);
            ajaxBean.setMsg("器具类型"+StatusCode.STATUS_311_MSG);
            return ajaxBean;
        }
        container.setContainerTypeId(findType.getContainerTypeId());
        ajaxBean = containerService.updateContainer(sessionUser,ajaxBean,container);
        return ajaxBean;
    }

    /**
     * excel模版下载
     */
    @RequestMapping("/container/excelDownload")
    public void excelDownload(HttpServletRequest req, HttpServletResponse resp){
        String fileName = "BatchInsert-Container.xlsx";
        ExcelUtil.downLoadExcel(req,resp,fileName);
    }

    /**
     * excel 上传,入库
     * @param multipartFile
     * @param sessionUser
     * @return
     */
    @RequestMapping("/container/batchUploadContainer")
    public AjaxBean batchUploadContainer(@RequestParam("file")MultipartFile multipartFile,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,AjaxBean ajaxBean){
        Long begin = System.currentTimeMillis();
        LOG.info("---batchUploadContainer  begin="+begin);
        try {
            //新建文件（自定义路径）
            String path = excelPath;
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
            LOG.info("---batchUploadContainer check uploadfile="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
            String[] endWith = multipartFile.getOriginalFilename().split("\\.");
            File file = new File(path +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-Container]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()){
                fileParent.mkdirs();
            }
            //上传到服务器
            multipartFile.transferTo(file);
            LOG.info("---batchUploadContainer new file="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
            ajaxBean = containerService.batchUpload(file,sessionUser);

        } catch (Exception e) {
            LOG.error("",e);
            logService.addLogError(sessionUser, e, "", null);
            ajaxBean.setStatus(StatusCode.STATUS_201);
            ajaxBean.setMsg(StatusCode.STATUS_201_MSG);
        }
        LOG.info("---batchUploadContainer end="+System.currentTimeMillis()+"   useTime="+(System.currentTimeMillis()-begin));
        return ajaxBean;
    }

    /**
     * 通过url请求返回图像的字节流
     */
    @RequestMapping("/container/showContainerPictrue")
    public void showContainerPictrue(HttpServletRequest request, HttpServletResponse response
    		, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String epcId) {
        String fileName = epcId + ".png";
        File file = new File(imgPath + fileName);
        //判断文件是否存在如果不存在就返回默认图标
        if (!(file.exists() && file.canRead())) {
            fileName = "default.png";
			String defaultPngPath = request.getServletContext().getRealPath("/");
            file = new File(defaultPngPath + fileName); //\src\main\webapp\default.png
        }else{
            file = new File(imgPath + fileName);
        }
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition","attachment;fileName=" + fileName);// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            os = response.getOutputStream();
            int i = -1;
            while ((i = bis.read(buffer)) != -1) {
                os.write(buffer, 0, i);
                os.flush();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
            logService.addLogError(sessionUser, e, e.getMessage(), null);
        } finally {
            try {
            	if (null != os) {
            		os.close();
                }
            } catch (IOException e) {
                LOG.error("图像输出流关闭失败！",e);
                logService.addLogError(sessionUser, e, "图像["+epcId+"]输出流关闭失败", null);
            }
            try {
            	if (null != bis) {
            		bis.close();
                }
            } catch (IOException e) {
                LOG.error("图像输入流BufferedInputStream对象关闭失败！",e);
                logService.addLogError(sessionUser, e, "图像["+epcId+"]输入流BufferedInputStream对象关闭失败！", null);
            }
            try {
            	if (null != fis) {
            		fis.close();
                }
            } catch (IOException e) {
                LOG.error("图像输入流FileInputStream对象关闭失败！",e);
                logService.addLogError(sessionUser, e, "图像["+epcId+"]输入流FileInputStream对象关闭失败！", null);
            }
        }
    }

    /**
     * 根据epcId查询出对应的数据
     * @param epcId
     * @return
     */
    @RequestMapping("/container/findContainerByEpcId")
    public AjaxBean findContainerByEpcId(String epcId){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if (StringUtils.isBlank(epcId)){
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("epcId"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        Container container = containerService.findContainerByEpcId(epcId);
        ajaxBean.setBean(container);
        return ajaxBean;
    }

}
