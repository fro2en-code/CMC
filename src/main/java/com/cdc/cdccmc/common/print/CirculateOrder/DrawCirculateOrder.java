package com.cdc.cdccmc.common.print.CirculateOrder;

import com.cdc.cdccmc.common.print.util.*;
import com.cdc.cdccmc.common.print.vo.CirculateOrder;
import com.cdc.cdccmc.common.print.vo.Header;
import com.cdc.cdccmc.common.print.vo.Point;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 画程序流转单
 */
public class DrawCirculateOrder implements ImageObserver,Serializable {
    private Logger logger = Logger.getLogger(DrawCirculateOrder.class);

    public static final int HEIGTH_NUM = 842;//A4纸张 595*842
    public static final int WIDTH_NUM = 595;

    CirculateOrder domain;
    Graphics2D g2;

    public DrawCirculateOrder(CirculateOrder domain){
        this.domain = domain;
    }

    /**
     * 画图方法
     * @param gra
     */
    public void drawPage(Graphics gra) {
        g2 = (Graphics2D) gra;
        g2.setColor(Color.BLACK);

        try {
            logger.info("开始画标题行");
            drawTitleArea();
            logger.info("标题行结束，开始画二级标题");
            drawSenderArea();
            logger.info("二级标题结束，开始画大表格");
            drawSendDetailArea();
            logger.info("大表格结束，开始画详情表格");
            drawTable();
            logger.info("详情表格结束，开始画底部表格");
            drawConfirmArea();
            logger.info("底部表格，开始画表格旁字体");
            drawRightSideString();
            logger.info("表格旁字体结束");
        } catch (Exception e) {
            logger.error("包装流转单打印报错",e);
        }
    }

    /**
     * 画标题行
     */
    private void drawTitleArea(){
    	//打印左上角条形码
        BufferedImage barCodeImage = BarCodeUtil.getBarCode(domain.getBarCode());
        g2.drawImage(barCodeImage, 10, 26, this);

        //打印右上角LOGO图片
        BufferedImage logoImage = loadImage("/img/"+domain.getOrgId()+".png");
        if(logoImage != null){
//            g2.drawImage(logoImage, 400, 26, this);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.drawImage(logoImage.getScaledInstance(160,58, Image.SCALE_SMOOTH),420, 26, null);
        }

        //打印标题“包装流转单”(粗体（Font.BOLD),正常(Font.PLAIN),黑体(Font.ITALIC),斜粗体(Font.BOLD+Font.ITALIC))
        Font font = new Font("宋体", Font.BOLD, 24);
        g2.setFont(font);
        String title = domain.getTitle();
        g2.drawString(title, 240, 54);
    }

    /**
     * 画二级标题
     */
    private void drawSenderArea(){
        int marginTop = 100;//(上边距)
        int maxSize = 10;//每一行最多允许有多少汉字，多出这个数字仓库名称会换行
        int top = 12;//行高

        Font fontBold = new Font("宋体", Font.BOLD, 12);
        g2.setFont(fontBold);

        g2.drawString("发货地点:", 17, marginTop);
        g2.drawString("收货地点:" , 212, marginTop);
        g2.drawString("交易类型:" , 427, marginTop);

        Font font = new Font("宋体", Font.PLAIN, 12);
        g2.setFont(font);

        String sendLocation = domain.getSendLocation();
        PrintUtil.printLineFeed(sendLocation,g2,maxSize,70,marginTop,top);

        String receiveLocation = domain.getReceiveLocation();

        PrintUtil.printLineFeed(receiveLocation,g2,maxSize,265,marginTop,top);

        String transactionType = domain.getTransactionType();
        g2.drawString(transactionType, 480, marginTop);


    }

    /**
     * 画副标题
     */
    private void drawSendDetailArea(){

        int leftCol1 = 30;
        int leftCol2 = 90;
        int leftCol3 = 220;
        int leftCol4 = 280;
        int leftCol5 = 440;

        int marginTop = 130;
        drawCheckBox(domain.getWsSendFlag().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("车间", leftCol1, marginTop);
        g2.drawString("道口代码"+domain.getWsSendCode(), leftCol2, marginTop);
        g2.drawLine(leftCol2 + 48, marginTop, leftCol2 + 48 + 50, marginTop);
        drawCheckBox(domain.getWsReceiveFlag().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("车间", leftCol3, marginTop);
        g2.drawString("道口代码"+domain.getWsReceiveCode(), leftCol4, marginTop);
        g2.drawLine(leftCol4 + 48, marginTop, leftCol4 + 48 + 50, marginTop);
        drawCheckBox(domain.getOutByWarehouse().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("仓库调拨", leftCol5, marginTop);

        marginTop += 20;
        drawCheckBox(domain.getCmcSendFlag().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("CMC", leftCol1, marginTop);
        g2.drawString("道口代码"+domain.getCmcSendCode(), leftCol2, marginTop);
        g2.drawLine(leftCol2 + 48, marginTop, leftCol2 + 48 + 50, marginTop);
        drawCheckBox(domain.getCmcReceiveFlag().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("CMC", leftCol3, marginTop);
        g2.drawString("道口代码"+domain.getCmcReceiveCode(), leftCol4, marginTop);
        g2.drawLine(leftCol4 + 48, marginTop, leftCol4 + 48 + 50, marginTop);
        drawCheckBox(domain.getOutByRepair().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("维修转移", leftCol5, marginTop);

        marginTop += 20;
        drawCheckBox(domain.getDcSendFlag().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("DC", leftCol1, marginTop);
        g2.drawString("道口代码"+domain.getDcSendCode(), leftCol2, marginTop);
        g2.drawLine(leftCol2 + 48, marginTop, leftCol2 + 48 + 50, marginTop);
        drawCheckBox(domain.getDcReceiveFlag().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("DC", leftCol3, marginTop);
        g2.drawString("道口代码"+domain.getDcReceiveCode(), leftCol4, marginTop);
        g2.drawLine(leftCol4 + 48, marginTop, leftCol4 + 48 + 50, marginTop);
        drawCheckBox(domain.getOutByReturn().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("客户退回", leftCol5, marginTop);

        marginTop += 20;
        drawCheckBox(domain.getProviderSendFlag().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("供应商", leftCol1, marginTop);
        g2.drawString("代码"+domain.getProviderSendCode(), leftCol2 + 24, marginTop);
        g2.drawLine(leftCol2 + 48, marginTop, leftCol2 + 48 + 50, marginTop);
        drawCheckBox(domain.getProviderReceiveFlag().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("供应商", leftCol3, marginTop);
        g2.drawString("道口代码"+domain.getProviderReceiveCode(), leftCol4, marginTop);
        g2.drawLine(leftCol4 + 48, marginTop, leftCol4 + 48 + 50, marginTop);
        drawCheckBox(domain.getOutByModify().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("料架修改出库", leftCol5, marginTop);
        drawCheckBox(domain.getOutByScrap().equals("YES") ,leftCol5 + 80, marginTop);
        g2.drawString("报废出库", leftCol5 + 92, marginTop);

        marginTop += 20;
        drawCheckBox(domain.getSendByShopFlag().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("维修工厂", leftCol1, marginTop);
        drawCheckBox(domain.getReceiveByShopFlag().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("维修工厂", leftCol3, marginTop);
        drawCheckBox(domain.getOutByNewPackage().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("新包装入库", leftCol5, marginTop);

        marginTop += 20;
        drawCheckBox(domain.getOtherSend().equals("YES") ,leftCol1 - 12, marginTop);
        g2.drawString("其他", leftCol1, marginTop);
        drawCheckBox(domain.getOtherReceive().equals("YES") ,leftCol3 - 12, marginTop);
        g2.drawString("其他", leftCol3, marginTop);
        drawCheckBox(domain.getOtherOut().equals("YES") ,leftCol5 - 12, marginTop);
        g2.drawString("其他", leftCol5, marginTop);
    }

    private void drawTable(){
//        Integer tableWidth = 0;

        List<Header> header = new ArrayList<Header>();
        header.add(new Header("序号", "id",5));
        header.add(new Header("包装代码", "code",15));
        header.add(new Header("包装名称", "name",25));
        header.add(new Header("尺寸(mm)", "size",20));
        header.add(new Header("计划数量", "planNumber",10));
        header.add(new Header("实发数量","sendNumber", 10));
        header.add(new Header("实收数量", "receiveNumber", 10));
        header.add(new Header("备注", "remark",5));

        TableUtil tableUtil = new TableUtil();
        tableUtil.g2 = g2;
        tableUtil.tableWidth = WIDTH_NUM - 60;
        tableUtil.point = new Point(20,248);
        tableUtil.rowHeight = 20;
        tableUtil.headers = header;
        tableUtil.rows = domain.getDetails();
        tableUtil.rowCount = 17;
        tableUtil.drawTable();
    }

    /**
     * 画底部表格
     */
    private void drawConfirmArea(){

        int marginTop = 604;
        int leftSide = 20;   //表格左边距
        int rightSide = WIDTH_NUM - 38; //表格右边距
        int leftText1 = 30;  // 第一列文字左边距,相对于页面
        int leftText2 = 300; // 第二列文字左边距,相对于页面

        g2.drawLine(leftSide, marginTop - 20, leftSide, marginTop + 146); //左侧边
        g2.drawLine(rightSide, marginTop - 20, rightSide, marginTop + 145); // 右侧边

        //domain.setDescription("是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是啊的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧无特的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧是的吧噶啊啊啊");
        int side = 110;
        this.drowBoldFront("特别描述:", leftText1, marginTop);
        // 分割字符处理
        if (StringUtils.isNotBlank(domain.getDescription())) {
            PrintUtil.printLineFeedForMaxRow(domain.getDescription(),g2,PrintConstants.DESC_SEG_STRLEN,leftText1 + 100,marginTop,20, 3);
        }
        g2.drawLine(leftSide, marginTop + 44, rightSide, marginTop + 44); //文字下方表格线
        g2.drawLine(side, marginTop - 15, side, marginTop + 42); //表格第2道竖线

        marginTop += 60;
        this.drowBoldFront("运输公司:", leftText1, marginTop);
        g2.drawString(domain.getTransportCompany(), leftText1 + 100, marginTop);
        this.drowBoldFront("司机确认:", leftText2, marginTop);
        g2.drawString(domain.getConfirm(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线

        marginTop += 20;
        this.drowBoldFront("车号:", leftText1, marginTop);
        g2.drawString(domain.getCarNo(), leftText1 + 100, marginTop);
        this.drowBoldFront("司机联系方式:", leftText2, marginTop);
        g2.drawString(domain.getContact(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线

        marginTop += 20;
        this.drowBoldFront("发货方确认:", leftText1, marginTop);
        g2.drawString(domain.getSenderConfirm(), leftText1 + 100, marginTop);
        this.drowBoldFront("收货方确认:", leftText2, marginTop);
        g2.drawString(domain.getReceiverConfirm(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线
      
        marginTop += 20;
        this.drowBoldFront("发货日期&时间:", leftText1, marginTop);
        g2.drawString(domain.getSenderConfirmDate(), leftText1 + 100, marginTop);
        this.drowBoldFront("收货日期&时间:", leftText2, marginTop);
        g2.drawString(domain.getReceiverConfirmDate(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线

        marginTop += 20;
        this.drowBoldFront("相关单号:", leftText1, marginTop);
        g2.drawString(domain.getBillNumber(), leftText1 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 6, rightSide, marginTop + 6); // 文字下方表格线
    }

    /**
     * 打印粗体字
     * @param value
     * @param x
     * @param y
     */
    private void drowBoldFront(String value,int x,int y){
        Font fontBold = new Font("宋体", Font.BOLD, 12);
        g2.setFont(fontBold);
        g2.drawString(value,x,y);
        Font fontPlan = new Font("宋体", Font.PLAIN, 12);
        g2.setFont(fontPlan);
    }

    /**
     * 画表格旁字体
     */
    private void drawRightSideString(){

        Font oldFont = g2.getFont();

        int fontSize = 8;
        g2.setFont(new Font("宋体", Font.PLAIN, fontSize));
        String description = "①发货方存联(白)②收货方存联(红)③运输存联(黄)④包装管理服务商存联(绿)⑤回执联(蓝)";

        String temp;
        int left = WIDTH_NUM - 30, top = 280;
        for(int i=0; i < description.length(); i++){
            String s = description.substring(i, i + 1);
            if(s.equals("(")){
                s = description.substring(i, i + 3);
                g2.drawString(s, left - 4, top);
                top += fontSize + 10;
                i += 2;
                continue;
            }

            g2.drawString(s, left, top);
            top += fontSize + 2;
        }

        g2.setFont(oldFont);
    }

    /**
     * 画勾选框
     * @param flag
     * @param x
     * @param y
     */
    private void drawCheckBox(boolean flag, int x, int y){
        Font oldFont = g2.getFont();
        //勾选框设置大小
        Font font = new Font("宋体", Font.PLAIN, 20);
        g2.setFont(font);
        g2.drawString("□", x-5, y+5);
        if(flag){
            Font font1 = new Font("宋体", Font.PLAIN, 12);
            g2.setFont(font1);
            g2.drawString("√", x, y);
        }
        g2.setFont(oldFont);
    }

    /**
     * 加载logo
     * @param filename
     * @return
     */
    private BufferedImage loadImage(String filename){
        BufferedImage img = null;
        try {
            File file = new File(System.getProperty("user.dir")+filename);
            if (file.exists()){
                InputStream str = new FileInputStream(System.getProperty("user.dir")+filename);
                if (str != null)
                    img = ImageIO.read(str);
            }

        } catch (IOException e) {
            logger.error("加载logo出错",e);
        }
        return img;
    }

    /**
     * 图片位置定义
     * @param img
     * @param infoflags
     * @param x
     * @param y
     * @param width
     * @param height
     * @return
     */
    public boolean imageUpdate(Image img, int infoflags, int x, int y,int width, int height) {
        return false;
    }
}
