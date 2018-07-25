package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.circulate.CirculateDetail;

/**
 * 包装流转单详情
 * @author Jerry
 * @date 2018/1/31 13:09
 */
public class CirculateDetailDto extends CirculateDetail {
    private Integer num;//统计数量
    private Integer isManualOrder;//是否手工流转单
    private Integer inOrgNumber; //库存数量
    
    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getIsManualOrder() {
        return isManualOrder;
    }

    public void setIsManualOrder(Integer isManualOrder) {
        this.isManualOrder = isManualOrder;
    }

	public Integer getInOrgNumber() {
		return inOrgNumber;
	}

	public void setInOrgNumber(Integer inOrgNumber) {
		this.inOrgNumber = inOrgNumber;
	}
    
}
