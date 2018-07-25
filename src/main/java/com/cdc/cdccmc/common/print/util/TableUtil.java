package com.cdc.cdccmc.common.print.util;

import com.cdc.cdccmc.common.print.vo.Header;
import com.cdc.cdccmc.common.print.vo.Point;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class TableUtil {

    public Graphics2D g2;
    public Point point;
    public int tableWidth;
    public int rowHeight;
    public int rowCount;

    public List<Header> headers;
    public List<Map<String, Object>> rows;


    public void drawTable(){
        //g2.drawRect(point.getX(), point.getY(), width, rowHeight);

        // 绘制表头
        Point tmp = point.copy();
        for(Header header : headers){
            int width = (int)Math.round(this.tableWidth * (header.widthRate / 100.0));
            g2.drawRect(tmp.getX(), tmp.getY(), width, rowHeight * rowCount);
            drawStringForCenter(tmp, width, header.title);
            tmp.setX(tmp.getX() + width);
        }

        // 画表格线
        tmp.setX(point.getX());
        tmp.setY(point.getY());
        for(int i=0; i < rowCount; i++){
            tmp.setX(point.getX());
            tmp.setY(tmp.getY() + rowHeight);
            g2.drawLine(tmp.getX() , tmp.getY(), tmp.getX() + tableWidth, tmp.getY());
        }

        // 绘制表格正文
        tmp.setX(point.getX());
        tmp.setY(point.getY() + rowHeight);
        for(Map<String, Object> row : rows){
            for(Header header : headers){
                int width = (int)Math.round(this.tableWidth * (header.widthRate / 100.0));
                Object o = row.get(header.key);
                String value = o==null? "" : o.toString();
//                g2.drawRect(tmp.getX(), tmp.getY(), width, rowHeight * rowCount);
//                drawStringForCenter(tmp, width, value);
                g2.drawString(value, tmp.getX()+3, tmp.getY() + rowHeight - 5);
                tmp.setX(tmp.getX() + width);
            }
            tmp.setX(point.getX());
            tmp.setY(tmp.getY() + rowHeight);
        }
    }

    // 单元格内写入正文
    private void drawStringForCenter(Point p, int width, String str) {
        int fontSize = g2.getFont().getSize();
        int x = (p.getX() + p.getX() + width - str.length() * fontSize) / 2;
        g2.drawString(str, x, p.getY() + rowHeight - 5);
    }
}
