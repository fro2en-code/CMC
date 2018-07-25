package com.cdc.cdccmc.common.print.service;


import com.cdc.cdccmc.common.print.CirculateOrder.DrawInbound;
import com.cdc.cdccmc.common.print.util.PropertyUtil;
import com.cdc.cdccmc.common.print.vo.PruchaseInbound;
import org.apache.log4j.Logger;

import javax.print.DocFlavor;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.print.*;
import java.io.Serializable;

/**
 *  基础打应类
 */

public class PrintdInboundServices implements Printable, Serializable {
    private Logger logger = Logger.getLogger(PrintdInboundServices.class);

    protected DrawInbound basePage;

    public volatile String printMachineName;

    /**
     * 构造方法,加载打应机名称
     */
    public PrintdInboundServices(){
       printMachineName = PropertyUtil.getProperty("printerName");
    }

    /**
     * 画布准备
     * @param graphics
     * @param pageFormat
     * @param pageIndex
     * @return
     * @throws PrinterException
     */
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        logger.info("开始准备画布ßß");
        switch (pageIndex) {
            case 0:
                // TODO:绘图
                if (basePage == null) {
                    logger.error("打印对象为空😢");
                    return NO_SUCH_PAGE;
                }
                logger.info("开始执行画图方法");
                basePage.drawPage(graphics);
                return PAGE_EXISTS;
            default:
                return NO_SUCH_PAGE;
        }
    }

    /**
     * 开始打应
     * @param job
     * @param printerName
     */
    public void printerJob(PrinterJob job, String printerName){
        try{
            logger.info("开始构建打印请求属性集😊");
            DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
            PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
            logger.info("构建打印请求属性集结束，匹配打印机😊");
            javax.print.PrintService printServiceList[] = PrintServiceLookup.lookupPrintServices(flavor, attrs);
            for(javax.print.PrintService printService : printServiceList){
                if (printerName.equals(printService.getName())) {//匹配打印机
                    logger.info("已经匹配到打印机，装配答应对象😊");
                    job.setPrintService(printService);
                }
            }
//            boolean isDialog = job.printDialog();
//            if (isDialog) {
            logger.info("对象装配结束，开始打印");
                job.print();
//            }

        }catch (Exception ex){
            logger.error("入库单答应报错了😢",ex);
        }
    }

    /**
     * 打印
     * @param pageFirst
     * @param book
     * @param inbound
     */
    public void dealPrint(DrawInbound pageFirst, Book book, PruchaseInbound inbound){
        try {
            logger.info("单号["+inbound.getBarCode()+"]开始画图");
            basePage = pageFirst;
            logger.info("orgId["+inbound.getBarCode()+"]初始化打印作业");
            PrinterJob job = PrinterJob.getPrinterJob();
            logger.info("orgId["+inbound.getBarCode()+"]绘画完毕");
            job.setPageable(book);
            logger.info("orgId["+inbound.getBarCode()+"]开始打印");
            printerJob(job, printMachineName);
            logger.info("orgId["+inbound.getBarCode()+"]打印成功");
        } catch (NullPointerException e) {
            logger.error("打印出错",e);
        }
    }
}
