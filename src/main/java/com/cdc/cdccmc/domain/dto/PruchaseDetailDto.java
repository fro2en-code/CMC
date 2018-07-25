package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.PurchaseInOrgDetail;

/**
 * 采购入库详情
 * @author Clm
 * @date 2018/2/7 
 */

public class PruchaseDetailDto extends PurchaseInOrgDetail{
    private Integer num;//统计数量

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
