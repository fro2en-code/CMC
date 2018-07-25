package com.cdc.cdccmc.common.print.util;

import java.awt.*;
import java.util.List;

public class PrintUtil {

    /**
     * 换行打印方法
     * @param value 需要打印的字体
     * @param g2 打印类对象
     * @param maxSize 多少字体换行
     * @param x 打印时的x坐标点
     * @param y 打印时的y坐标点
     * @param top 行高
     */
    public static void printLineFeed(String value, Graphics2D g2,int maxSize,int x,int y,int top){
        for (int i=0;i<value.length()/maxSize;i++){
            String s = value.substring(i*maxSize,(i+1)*maxSize);
            g2.drawString(s,x,y+i*top);
        }
        String s = value.substring((value.length()/maxSize)*maxSize,value.length());
        g2.drawString(s, x, y+(value.length()/maxSize)*top);
    }

    public static void printLineFeedForMaxRow(String value, Graphics2D g2, int maxSize, int x, int y, int rowHeight, int maxRow) {
        List<String> vals = StringSegmented.getStrList(value, maxSize);
        List<String> printList;
        if (vals.size() > maxRow) {
            printList = vals.subList(0, maxRow);
        } else {
            printList = vals;
        }
        for (String printDesc : printList) {
            g2.drawString(printDesc, x, y);
            y += rowHeight;
        }
    }
}
