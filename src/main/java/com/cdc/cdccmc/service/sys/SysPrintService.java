package com.cdc.cdccmc.service.sys;


import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.controller.web.manage.SysPrintController;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.sys.SysPrint;
import com.cdc.cdccmc.domain.sys.SysUserPrint;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.BaseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.cdc.cdccmc.common.util.SysConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 打印机设置
 * @author Licao
 * @date 2018-05-31
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class SysPrintService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SysPrintController.class);
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    @Autowired
    private BaseService baseService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("#{sql['addPrinter']}")
    private String addPrinter;
    @Value("#{sql['updatePrinter']}")
    private String updatePrinter;
    @Value("#{sql['deletePrinter']}")
    private String deletePrinter;
    @Value("#{sql['addPrint']}")
    private String addPrint;
    @Value("#{sql['deletePrint']}")
    private String deletePrint;
    @Value("#{sql['queryPrintNameByOrgAndAccount']}")
    private String queryPrintNameByOrgAndAccount;
    @Value("#{sql['listAllPrintName']}")
    private String listAllPrintName;

    //打印机名称列表
    public List<SysPrint> listAllPrintName(SystemUser sessionUser){
        return jdbcTemplate.query(listAllPrintName,new BeanPropertyRowMapper(SysPrint.class),sessionUser.getCurrentSystemOrg().getOrgId());
    }

    //查询指定仓库下打印机
    //public List<SysPrint> queryPrinter(Paging paging,String orgId){
    public Paging queryPrinter(Paging paging,String orgId){
//        Map paramMap = new HashMap();
//        String sql = "select * from t_system_print where org_id in (:orgId)";
//        paramMap.put("orgId",orgId);
//        paging = baseService.pagingParamMap(paging, sql, paramMap, SysPrint.class);
        String sql = "select * from t_system_print where org_id in ("+orgId+")";
        paging = baseService.pagingParamMap(paging, sql,null, SysPrint.class);
        return paging;
    }

    //增加打印机
    public AjaxBean addPrinter(SystemUser sessionUser, SysPrint sysPrint)
    {
        LOG.info("---addPrinter   sysPrint="+sysPrint);
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if(StringUtils.isEmpty(sysPrint.getPrintName()) || StringUtils.isBlank(sysPrint.getPrintName()))
        {
            ajaxBean = AjaxBean.FAILURE();
            ajaxBean.setMsg("打印机名称不能为空!");
            return ajaxBean;
        }
        try
        {
            namedJdbcTemplate.update(addPrinter, new BeanPropertySqlParameterSource(sysPrint));
        }
        catch (Exception e)
        {
            ajaxBean = AjaxBean.returnAjaxResult(0);
            ajaxBean.setMsg("增加打印机数据提交失败!");
        }
        return  ajaxBean;
    }

    //修改打印机
    public AjaxBean updatePrinter(SystemUser sessionUser, SysPrint sysPrint)
    {
        LOG.info("---updatePrinter   sysPrint="+sysPrint);
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if(StringUtils.isEmpty(sysPrint.getPrintName()) || StringUtils.isBlank(sysPrint.getPrintName()))
        {
            ajaxBean = AjaxBean.FAILURE();
            ajaxBean.setMsg("打印机名称不能为空!");
            return ajaxBean;
        }
        try {
            //namedJdbcTemplate.update(updatePrinter, new BeanPropertySqlParameterSource(sysPrint));
            jdbcTemplate.update(updatePrinter, sysPrint.getPrintName(),sysPrint.getOrgId(),sysPrint.getOrgName(),sysPrint.getPrintCode());
        }
        catch (Exception e)
        {
            ajaxBean = AjaxBean.returnAjaxResult(0);
            ajaxBean.setMsg("修改打印机名称数据提交失败!");
        }
        return  ajaxBean;
    }

    //删除打印机
    public AjaxBean deletePrinter(SystemUser sessionUser, SysPrint sysPrint)
    {
        LOG.info("---deletePrinter   sysPrint="+sysPrint);
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        try {
            jdbcTemplate.update(deletePrinter, sysPrint.getPrintCode());
        }
        catch (Exception e)
        {
            ajaxBean = AjaxBean.returnAjaxResult(0);
            ajaxBean.setMsg("删除打印机数据提交失败!");
        }
        return  ajaxBean;
    }



    //新增打印设置
    public AjaxBean addPrint(SystemUser sessionUser, SysPrint sysPrint, String printAccount){
        LOG.info("---addPrint   printAccount="+printAccount+"   sysPrint="+sysPrint);
        SysUserPrint sysUserPrint = new SysUserPrint();
        sysUserPrint.setAccount(printAccount);
        sysUserPrint.setOrgId(sessionUser.getCurrentSystemOrg().getOrgId());
        sysUserPrint.setOrgName(sessionUser.getCurrentSystemOrg().getOrgName());
        sysUserPrint.setCreateAccount(sessionUser.getAccount());
        sysUserPrint.setCreateRealName(sessionUser.getRealName());
        sysUserPrint.setPrintName(sysPrint.getPrintName());
        sysUserPrint.setPrintCode(sysPrint.getPrintCode());
        if(this.queryPrintNameByOrgAndAccount(printAccount,sessionUser.getCurrentSystemOrg().getOrgId()) == null){//如果指定帐号指定仓库没有设置打印机，直接添加
            namedJdbcTemplate.update(addPrint, new BeanPropertySqlParameterSource(sysUserPrint));
            return  AjaxBean.SUCCESS();
        }
        else{//如果已经设置，删除旧的，添加新的
            jdbcTemplate.update(deletePrint,printAccount,sessionUser.getCurrentSystemOrg().getOrgId());
            namedJdbcTemplate.update(addPrint, new BeanPropertySqlParameterSource(sysUserPrint));
        }
        return  AjaxBean.SUCCESS();
    }

    //如果保存时没有选择打印机，删除打印设置
    public AjaxBean deletePrint(AjaxBean ajaxBean, SystemUser sessionUser, String printAccount){
        jdbcTemplate.update(deletePrint,printAccount,sessionUser.getCurrentSystemOrg().getOrgId());
        return ajaxBean;
    }

    //查询指定帐号，指定仓库下是否已存在打印设置
    public SysUserPrint queryPrintNameByOrgAndAccount(String account,String orgId){
        List<SysUserPrint> list = jdbcTemplate.query(queryPrintNameByOrgAndAccount, new BeanPropertyRowMapper(SysUserPrint.class),orgId, account);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.get(0);
    }



}
