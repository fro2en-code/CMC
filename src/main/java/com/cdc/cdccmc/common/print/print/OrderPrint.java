package com.cdc.cdccmc.common.print.print;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.print.CirculateOrder.DrawCirculateOrder;
import com.cdc.cdccmc.common.print.service.PrintCirculateOrderService;
import com.cdc.cdccmc.common.print.util.PageFormatSub;
import com.cdc.cdccmc.common.print.vo.CirculateOrder;
import org.apache.log4j.Logger;

import java.awt.print.Book;
import java.awt.print.Paper;


public class OrderPrint {
    private Logger logger = Logger.getLogger(OrderPrint.class);


    public void print(String msg) {
        logger.debug("接收到到对象信息为" + msg);
        CirculateOrder order = JSON.parseObject(msg, CirculateOrder.class);
        logger.info("接收到包装流转单打印信息，开始判断仓库Id是否正确" + order.getOrgId());
        logger.debug("打印人Id:" + order.getPeopleId() + ";打印人姓名:" + order.getPeopleName() + ";打印时间:" + order.getPrintTime() + ";答应仓库名称:" + order.getOrgName());
        logger.info("打印信息为:" + msg);
        DrawCirculateOrder pageFirst = new DrawCirculateOrder(order);
        PrintCirculateOrderService printService = new PrintCirculateOrderService();
        logger.info("设置纸张大小");
        Paper p = new Paper();
        p.setSize(pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);// 纸张大小
        p.setImageableArea(0, 0, pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);

        PageFormatSub pf = new PageFormatSub();
        pf.setOrientation(PageFormatSub.PORTRAIT);
        pf.setPaper(p);
        logger.info("生成打印对象");
        Book book = new Book();
        book.append(printService, pf);
        logger.info("开始调用打印程序");
        printService.dealPrint(pageFirst, book, order);
    }
}
