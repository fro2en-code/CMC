package com.cdc.cdccmc.common.print.vo;

/**
 * Created by lishujun on 2017/9/19.
 */
public class Header {
    public String title;
    public String key;
    public Integer widthRate;

    public Header(String title, String key, Integer widthRate) {
        this.title = title;
        this.key = key;
        this.widthRate = widthRate;
    }
}
