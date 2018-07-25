package com.cdc.cdccmc.service.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.ClaimDetail;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.service.BaseService;

/**
 * 索赔明细
 * @author Jerry
 * @date 2018/1/22 15:58
 */
@Service
@EnableTransactionManagement // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@Transactional
public class ClaimDetailService {

    @Autowired
    private BaseService baseService;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    /**
     * 索赔明细列表查询
     * @param paging
     * @param filialeSystemOrgIds
     * @param claimDetail
     * @param endDate 
     * @param startDate 
     * @return
     */
    public Paging pagingClaimDetail(Paging paging, String filialeSystemOrgIds,String orderCode, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT * from T_CLAIM_DETAIL WHERE create_org_id in (").append(filialeSystemOrgIds).append(") ");;
        Map paramMap = new HashMap();
        if(StringUtils.isNotBlank(orderCode)){
        	if(orderCode.contains("-PD-")){
                sql.append(" and inventory_id = :orderCode ");
        	}else{
                sql.append(" and order_code = :orderCode ");
        	}
            paramMap.put("orderCode", orderCode);
        }
        if (StringUtils.isNotBlank(startDate)){
            sql.append(" and create_time>:startDate ");
            paramMap.put("startDate",startDate);
        }
        if (StringUtils.isNotBlank(endDate)){
            sql.append(" and create_time<:endDate ");
            paramMap.put("endDate",endDate);
        }
        sql.append(" ORDER BY create_time desc ");
        paging = baseService.pagingParamMap(paging, sql.toString(), paramMap, ClaimDetail.class);
        return paging;
    }
}
