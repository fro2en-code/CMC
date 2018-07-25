package com.cdc.cdccmc.common.print.print;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.common.print.CirculateOrder.DrawInbound;
import com.cdc.cdccmc.common.print.service.PrintdInboundServices;
import com.cdc.cdccmc.common.print.util.PageFormatSub;
import com.cdc.cdccmc.common.print.vo.PruchaseInbound;
import org.apache.log4j.Logger;

import java.awt.print.Book;
import java.awt.print.Paper;


public class InboundPrint {
    private final Logger logger = Logger.getLogger(InboundPrint.class);

//    public void print(String msg){
//        PruchaseInbound inbound = JSON.parseObject(msg,PruchaseInbound.class);
//        PrintdInboundServices InboundPrint = new PrintdInboundServices();
//        logger.info("接收到采购入库单打印信息，开始判断仓库Id是否正确"+inbound.getOrgId());
//        logger.info("打印人Id:"+inbound.getPeopleId()+";打印人姓名:"+inbound.getPeopleName()+";打印时间:"+inbound.getPrintTime()+";打印仓库名称:"+inbound.getOrgName());
//        String orgIds = PropertyUtil.getProperty("spring.profiles.printOrgIds");
//        for (String orgId:orgIds.split(";")){
//            if (orgId.equals(inbound.getOrgId())){
//                logger.info("仓库匹配成功仓库Id["+orgId+"]，开始生成打印对象");
//                logger.info("打印信息为："+msg);
//                DrawInbound pageFirst = new DrawInbound(inbound);
//                logger.info("设置纸张");
//                Paper p = new Paper();
//                p.setSize(pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);// 纸张大小
//                p.setImageableArea(0, 0, pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);
//
//                PageFormatSub pf = new PageFormatSub();
//                pf.setOrientation(PageFormatSub.PORTRAIT);
//                pf.setPaper(p);
//
//                Book book = new Book();
//                book.append(InboundPrint, pf);
//                logger.info("初始化打印");
//                InboundPrint.dealPrint(pageFirst, book,inbound);
//                System.out.println("Receiver  : " + msg);
//            }
//        }
//    }

    public void print(String msg) {
        PruchaseInbound inbound = JSON.parseObject(msg, PruchaseInbound.class);
        PrintdInboundServices InboundPrint = new PrintdInboundServices();
        logger.info("接收到采购入库单打印信息，开始判断仓库Id是否正确" + inbound.getOrgId());
        logger.info("打印人Id:" + inbound.getPeopleId() + ";打印人姓名:" + inbound.getPeopleName() + ";打印时间:" + inbound.getPrintTime() + ";打印仓库名称:" + inbound.getOrgName());
        logger.info("打印信息为：" + msg);
        DrawInbound pageFirst = new DrawInbound(inbound);
        logger.info("设置纸张");
        Paper p = new Paper();
        p.setSize(pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);// 纸张大小
        p.setImageableArea(0, 0, pageFirst.WIDTH_NUM, pageFirst.HEIGTH_NUM);

        PageFormatSub pf = new PageFormatSub();
        pf.setOrientation(PageFormatSub.PORTRAIT);
        pf.setPaper(p);

        Book book = new Book();
        book.append(InboundPrint, pf);
        logger.info("初始化打印");
        InboundPrint.dealPrint(pageFirst, book, inbound);
        System.out.println("Receiver  : " + msg);
    }
}
