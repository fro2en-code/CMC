package com.cdc.cdccmc.common.print.CirculateOrder;

import com.cdc.cdccmc.common.print.util.BarCodeUtil;
import com.cdc.cdccmc.common.print.util.PrintUtil;
import com.cdc.cdccmc.common.print.util.TableUtil;
import com.cdc.cdccmc.common.print.vo.Header;
import com.cdc.cdccmc.common.print.vo.Point;
import com.cdc.cdccmc.common.print.vo.PruchaseInbound;
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
public class DrawInbound implements ImageObserver,Serializable {
    private Logger logger = Logger.getLogger(DrawCirculateOrder.class);

    public static final int HEIGTH_NUM = 842;//A4纸张 595*842
    public static final int WIDTH_NUM = 595;


    PruchaseInbound domain;
    Graphics2D g2;

    public DrawInbound(PruchaseInbound domain){
        this.domain = domain;
    }

    /**
     * 画图方法
     * @param gra
     */
    public void drawPage(Graphics gra) {
        g2 = (Graphics2D) gra;
        g2.setColor(Color.BLACK);

        logger.info("开始画标题行");
        drawTitleArea();
        logger.info("标题行结束，开始画二级标题");
        drawSenderArea();
        logger.info("二级标题结束，开始画详情表格");
        drawTable();
        logger.info("详情表格结束，开始画底部表格");
        drawConfirmArea();
        logger.info("底部表格结束");
    }

    /**
     * 画标题行
     */
    private void drawTitleArea(){

        BufferedImage barCodeImage = BarCodeUtil.getBarCode(domain.getBarCode());
        g2.drawImage(barCodeImage, 10, 26, this);

        BufferedImage logoImage = loadImage("/img/"+domain.getOrgId()+".png");
        if(logoImage != null){
            g2.drawImage(logoImage, 400, 26, this);
        }

        Font font = new Font("宋体", Font.BOLD, 24);
        g2.setFont(font);

        String title = domain.getTitle();
        g2.drawString(title, 240, 54);
    }

    /**
     * 画二级标题
     */
    private void drawSenderArea(){
        int marginTop = 100;
        Font font = new Font("宋体", Font.PLAIN, 12);
        g2.setFont(font);

        String sendLocation = domain.getInBoundName();
        g2.drawString("收货仓库:", 10, marginTop);
        PrintUtil.printLineFeed(sendLocation,g2,14,63,marginTop,12);
//        g2.drawString(sendLocation,63,marginTop);

        String consignorOrgName = domain.getConsignorOrgName();
        g2.drawString("送货方:", 10, marginTop+40);
        PrintUtil.printLineFeed(consignorOrgName,g2,14,53,marginTop+40,12);
//        g2.drawString(consignorOrgName,53,marginTop+40);

        g2.drawString("单据类型:采购入库", 240, marginTop);

        g2.drawString("①收货方存联(白)" , 400, marginTop);
        g2.drawString("②财务存联(红)" , 400, marginTop+15);
    }

    private void drawTable(){

        List<Header> header = new ArrayList<Header>();
        header.add(new Header("序号", "id",5));
        header.add(new Header("包装代码", "code",20));
        header.add(new Header("包装名称", "name",20));
        header.add(new Header("尺寸(mm)", "size",20));
        header.add(new Header("计划数量", "planCount",10));
        header.add(new Header("实发数量","sendCount", 10));
        header.add(new Header("实收数量", "receiveCount", 10));
        header.add(new Header("备注", "remark",5));

        //开始画表格
        TableUtil tableUtil = new TableUtil();
        tableUtil.g2 = g2;
        tableUtil.tableWidth = WIDTH_NUM - 60;
        tableUtil.point = new Point(20,150);
        tableUtil.rowHeight = 20;
        tableUtil.headers = header;
        tableUtil.rows = domain.getDetails();
        tableUtil.rowCount = tableUtil.rows.size()+1;//所有列➕标题列
        tableUtil.drawTable();
    }

    /**
     * 画底部表格
     */
    private void drawConfirmArea(){

        int marginTop = 180 + domain.getDetails().size()*20;
        int initY = marginTop-50;
        int leftSide = 20;   //表格左边距
        int rightSide = WIDTH_NUM - 38; //表格右边距
        int leftText1 = 20;  // 第一列文字左边距,相对于页面


        int side = 47;
        int descriptionY = Math.round(marginTop+domain.getDescription().split(";").length*10/2);
        g2.drawString("描述", leftText1, descriptionY);

        //因为资产编码有多列，所以换行
        if (StringUtils.isNotBlank(domain.getDescription())){
            int top =2;
            for (String s:domain.getDescription().split(";")){
                g2.drawString(s+";",leftText1+50, marginTop+top);
                top=top+10;
            }
            marginTop += top;
        }else {
            g2.drawString(domain.getDescription(),leftText1 + 50, marginTop);
            marginTop+=10;
        }
        g2.drawLine(leftSide, marginTop, rightSide, marginTop);
        g2.drawLine(leftSide, initY, leftSide, marginTop ); //表格左侧边
        g2.drawLine(side, initY, side, marginTop ); //表格第2道竖线
        g2.drawLine(rightSide, initY, rightSide, marginTop); //表格右侧边

//        marginTop = 800;
        marginTop += 20;
        int leftText2 = 300; // 第二列文字左边距,相对于页面
        g2.drawString("收货确认:", leftText1, marginTop);
        g2.drawString(domain.getCreateRealName(), leftText1 + 100, marginTop);
        g2.drawString("质量确认:",leftText2, marginTop);
        g2.drawString(domain.getZhiliang(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线

        marginTop += 20;
        g2.drawString("日期&时间：", leftText1, marginTop);
        g2.drawString(domain.getCreateTime(), leftText1 + 100, marginTop);
        g2.drawString("日期&时间：",leftText2, marginTop);
        g2.drawString(domain.getZltime(), leftText2 + 100, marginTop);
        g2.drawLine(leftSide, marginTop + 4, rightSide, marginTop + 4); // 文字下方表格线

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
