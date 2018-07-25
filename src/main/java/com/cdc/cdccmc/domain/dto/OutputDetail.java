package com.cdc.cdccmc.domain.dto;

import com.cdc.cdccmc.domain.circulate.Circulate;

/**
 * @author Jerry
 * @date 2018/1/22 20:26
 */

//出库报表
public class OutputDetail extends Circulate {
    public Integer num;//相同包装流转单和器具代码的数量
    
    
    public String beginDate;

    public String endDate;

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
