package com.cdc.cdccmc.controller.web.container;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.container.ContainerCode;
import com.cdc.cdccmc.domain.dto.InventoryHistoryDto;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.InventoryHistoryService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/**
 * 器具代码
 * @author Clm
 * @date 2018-01-04
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ContainerCodeController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerCodeController.class);
	@Autowired
	private ContainerCodeService containerCodeService;
	@Autowired
	private InventoryHistoryService inventoryHistoryService;
	@Autowired
	private LogService logService;

	@Value("${upload.file.xlsx.path}")
	private String uploadFileTempPath;

	/**
	 * 器具代码查询
	 * @param systemUser
	 * @param containerCode
	 * @return
	 */
	@RequestMapping(value = "/containerCode/pagingContainerCode")
	public Paging pagingContainerCode(Paging paging,
			@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, ContainerCode containerCode) {
		paging = containerCodeService.pagingContainerCode(paging, sessionUser, containerCode);
		return paging;
	}

	/**
	 * 器具代码获取器具信息
	 * @param ContainerCode
	 * @return
	 */
	@RequestMapping(value = "/containerCode/queryByContainerCode")
	@ResponseBody
	public AjaxBean queryByContainerCode(String containerCode){
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(containerCode)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ContainerCode container = containerCodeService.queryByContainerCode(containerCode);
		if(null == container) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具代码["+containerCode+"]不存在，请核查！");
			return ajaxBean;
		}
		ajaxBean.setBean(container);
		return ajaxBean;
	}

	/**
	 * 器具代码获取器具信息
	 * @param ContainerCode
	 * @return
	 */
	@RequestMapping(value = "/containerCode/queryContainerCodeAndInOrgNumber")
	@ResponseBody
	public AjaxBean queryContainerCodeAndInOrgNumber(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String containerCode){
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(containerCode)) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ContainerCode findCode = containerCodeService.queryByContainerCode(containerCode);
		if(null == findCode) {
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具代码["+containerCode+"]不存在，请核查！");
			return ajaxBean;
		}
		InventoryHistory history = inventoryHistoryService.queryInventoryLatest(sessionUser.getCurrentSystemOrg().getOrgId(),containerCode);
		if(null == history) {
			//如果为空，则代表这个仓库目前尚无该器具代码的库存记录，返回库存为0即可
			history = new InventoryHistory();
			history.setInOrgNumber(SysConstants.INTEGER_0);
			history.setContainerCode(containerCode);
			history.setContainerName(findCode.getContainerName());
			history.setContainerTypeId(findCode.getContainerTypeId());
			history.setContainerTypeName(findCode.getContainerTypeName());
		}
		InventoryHistoryDto dto = new InventoryHistoryDto();
		dto.setContainerCode(findCode);
		dto.setInventoryHistory(history);
		ajaxBean.setBean(dto);
		return ajaxBean;
	}

	/**
	 * 新增器具代码
	 * @param systemUser
	 * @param containerCode
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping(value = "/containerCode/addContainerCode")
	public AjaxBean addContainerCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			ContainerCode containerCode, AjaxBean ajaxBean) {
		if(StringUtils.isBlank(containerCode.getContainerCode())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具代码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(!containerCode.getContainerCode().matches(SysConstants.REGEX_CONTAINER_CODE)){
			ajaxBean.setStatus(StatusCode.STATUS_325);
			ajaxBean.setMsg(StatusCode.STATUS_325_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(containerCode.getContainerName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(containerCode.getContainerTypeName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具类型"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(!(containerCode.getContainerCodeType().matches(SysConstants.REGEX_CONTAINER_CODE_TYPE))){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("器具代码类型只能由数字或字母组成且长度为1到10位");
			return ajaxBean;
		}
		//通过器具代码查询是否已存在
		ContainerCode findContainerCode = containerCodeService.queryByContainerCode(containerCode.getContainerCode());
		if(null == findContainerCode) { //如果不存在则新增
			ajaxBean = containerCodeService.addContainerCode(ajaxBean,sessionUser, containerCode);
		}else {
			ajaxBean.setStatus(StatusCode.STATUS_302);
            ajaxBean.setMsg("当前器具代码["+containerCode.getContainerCode()+"]"+StatusCode.STATUS_302_MSG);
		}
		return ajaxBean;
	}

	/**
	 * 器具代码模板下载
	 * @return
	 */
	@RequestMapping(value = "/containerCode/downContainerCodeTemp")
	public void downContainerCodeTemp(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		// 文件名
		String containerCodeName = "BatchInsert-ContainerCode.xlsx".toString();
		ExcelUtil.downLoadExcel(req,resp,containerCodeName);
	}
	
	/**
	 * 器具代码Excel批量导入
	 * @param systemUser
	 * @param multipartFile
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping(value = "/containerCode/batchUploadContainerCode")
	public AjaxBean batchUploadContainerCode(@RequestParam("file") MultipartFile multipartFile,
			@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, AjaxBean ajaxBean) {
		try {
			// 新建文件（自定义路径）
			if (!multipartFile.getOriginalFilename().endsWith(SysConstants.XLSX)) {
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
			File file = new File(uploadFileTempPath +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-ContainerCode]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
			File fileParent = file.getParentFile();
			if (!fileParent.exists()){
				fileParent.mkdirs();
			}
			//上传到服务器
			multipartFile.transferTo(file);
			ajaxBean = containerCodeService.batchUpload(ajaxBean,sessionUser,file);

		} catch (Exception e) {
			LOG.error("", e);
			logService.addLogError(sessionUser, e, "", null);
			ajaxBean.setStatus(StatusCode.STATUS_400);
			ajaxBean.setMsg("新增仓库"+StatusCode.STATUS_400_MSG);
		}
		return ajaxBean;
	}
	
	/**
	 * 器具代码的禁用和启用
	 * @param systemUser
	 * @param containerCode
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping(value = "/containerCode/isActiveContainerCode")
	public AjaxBean isActiveContainerCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SystemUser systemUser, ContainerCode containerCode, AjaxBean ajaxBean) {
		ajaxBean = containerCodeService.isActiveContainerCode(sessionUser,containerCode);
		return ajaxBean;
	}

	/**
	 * 器具代码增加器具代码全量查询
	 * @param systemUser
	 * @return
	 */
    @RequestMapping(value = "/containerCode/listAllContainerCode")
	public AjaxBean listAllContainerCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<ContainerCode> containerCodeList = containerCodeService.listAllContainerCode();
		ajaxBean.setList(containerCodeList);
		return ajaxBean;
	}

	/**
	 * 列出所有未被禁用的器具代码列表
	 * @param systemUser
	 * @return
	 */
    @RequestMapping(value = "/containerCode/listActiveContainerCode")
	public AjaxBean listActiveContainerCode(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<ContainerCode> containerCodeList = containerCodeService.listActiveContainerCode();
		ajaxBean.setList(containerCodeList);
		return ajaxBean;
	}

}
