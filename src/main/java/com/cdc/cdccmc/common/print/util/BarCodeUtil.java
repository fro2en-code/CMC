package com.cdc.cdccmc.common.print.util;

import org.apache.log4j.Logger;
import org.jbarcode.JBarcode;
import org.jbarcode.encode.Code128Encoder;
import org.jbarcode.encode.EAN13Encoder;
import org.jbarcode.paint.BaseLineTextPainter;
import org.jbarcode.paint.EAN13TextPainter;
import org.jbarcode.paint.WidthCodedPainter;
import org.jbarcode.util.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 生产BarCode（条形码）
 * @author 石闯
 *
 */
public class BarCodeUtil {
    private static Logger logger = Logger.getLogger(BarCodeUtil.class);
    
    public static BufferedImage getBarCode(String str) {
    	//图片
        BufferedImage localBufferedImage = null;
        try {
            JBarcode localJBarcode = new JBarcode(EAN13Encoder.getInstance(),
                    WidthCodedPainter.getInstance(), EAN13TextPainter
                    .getInstance());
            // 生成. 欧洲商品条码(=European Article Number)
            // 这里我们用作图书条码
            localJBarcode.setEncoder(Code128Encoder.getInstance());
            localJBarcode.setPainter(WidthCodedPainter.getInstance());
            localJBarcode.setTextPainter(BaseLineTextPainter.getInstance());
            //设定高度
            localJBarcode.setBarHeight(12);
            //设定宽宽比例
            localJBarcode.setWideRatio(2);//原值2
            localJBarcode.setShowCheckDigit(false);
            localBufferedImage = localJBarcode.createBarcode(str);
            //输出到本地文件
            saveToPNG(localBufferedImage, "Code39.png");
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return localBufferedImage;
    }


//    static void saveToJPEG(BufferedImage paramBufferedImage, String paramString) {
//        saveToFile(paramBufferedImage, paramString, "jpeg");
//    }

    static void saveToPNG(BufferedImage paramBufferedImage, String paramString) {
        saveToFile(paramBufferedImage, paramString);
    }

//    static void saveToGIF(BufferedImage paramBufferedImage, String paramString) {
//        saveToFile(paramBufferedImage, paramString, "gif");
//    }

    //String barCodeImgPath,
    static void saveToFile(BufferedImage paramBufferedImage,String paramString ) {
    	FileOutputStream localFileOutputStream = null;
    	String filePath=PropertyUtil.getProperty("spring.profiles.path");
        try {
//            FileOutputStream localFileOutputStream = new FileOutputStream(
//                    "/Users/dengtianjiao/Downloads/" + paramString1);
            localFileOutputStream =new FileOutputStream(
                    filePath + paramString);
            //复选框尺寸：96*96
            ImageUtil.encodeAndWrite(paramBufferedImage, "png",
                    localFileOutputStream, 96, 96);
            localFileOutputStream.close();
        } catch (Exception localException) {
			logger.debug(paramString+"LOGO图片不存在。");
        }finally{
    		try {
            	if(null != localFileOutputStream){
            		localFileOutputStream.close();
            	}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
        }
    }

    public static void main(String[]  args){
        getBarCode("jBARCODE-39");
    }
}
