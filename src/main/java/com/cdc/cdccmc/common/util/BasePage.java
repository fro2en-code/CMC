package com.cdc.cdccmc.common.util;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.Serializable;

/**
 * Created by lishujun on 2017/9/15.
 */
public abstract class BasePage implements ImageObserver,Serializable {

    public static final int HEIGTH_NUM = 842;//A4纸张 595*842
    public static final int WIDTH_NUM = 595;
    public final int pageTotalNum = 3;

    /**
     * 定义接口函数，处理图形的绘制
     */
    public abstract void drawPage(Graphics gra);

    /**
     * 实现ImageObserver的方法
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y,
                               int width, int height) {
        return false;
    }
}
