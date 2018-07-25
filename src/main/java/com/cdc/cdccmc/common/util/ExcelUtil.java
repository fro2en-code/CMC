package com.cdc.cdccmc.common.util;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.cdc.cdccmc.controller.web.container.ContainerController;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.util.ResourceUtils;
import sun.rmi.runtime.Log;

public class ExcelUtil {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerController.class);

    //excel2003扩展名
    public static final String EXCEL07_EXTENSION = ".xlsx";

    /**
     * 
     * 读取Excel文件，可能是03也可能是07版本
     * @param fileName 待读取的excel文件，必须是.xlsx格式文件
     * @param fileName
     * @return List &lt;Map&lt;Integer, String&gt;&gt; Integer=第几列，String=该单元格的值，list的索引+1就是excel第几行
     * @throws Exception
     */
    public static List<Map<Integer, String>> readExcel(String fileName, final Integer dataColumns) throws Exception {
        List<Map<Integer, String>> mapList = new ArrayList<Map<Integer, String>>();
        // 处理excel2003文件
        Excel2007Reader excel07 = new Excel2007Reader();
        excel07.setRowReader(new IRowReader() {
            @Override
            public void getRows(int sheetIndex, int curRow, Map<Integer, String> mapData) {
            	boolean blankRow = true;
            	for (int i = 0; i < dataColumns; i++) {
            		if(StringUtils.isNotBlank(mapData.get(i))){ //如果数据列中有任何一列有值，则此行不跳过，否则视为此行为空行，不视为数据行
            			blankRow = false;
            			break;
            		}
				}
				/* ypw 性能优化
            	if(blankRow){ //如果此行非空行，则计算为数据行
                    mapList.add(null);
            	}else{
                    mapList.add(mapData);
            	}
            	//ypw 性能优化 end
            	*/
                if(!blankRow) {
                    mapList.add(mapData);
                }
            }
        });
        excel07.process(fileName);
        mapList.remove(0);
        return mapList;
    }

    /**
     * excel模版下载
     * 此方法暂时废弃，使用下面的相对定位的下载方法
     * @param req
     * @param resp
     * @param fileName
     * @param path
     */
    /*public static void downLoadExcel(HttpServletRequest req, HttpServletResponse resp,String fileName,String path){
        LOG.debug("下载模板文件名称："+fileName);
        try {
            InputStream fis = new FileInputStream(System.getProperty("user.dir")+path+ fileName);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            resp.reset();
            resp.setContentType("bin");

            String fileNames = fileName;
            String agent = req.getHeader("USER-AGENT");

            String codedfilename = "";
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 != agent.indexOf("Trident")) {// ie
                String name = java.net.URLEncoder.encode(fileNames, "UTF8");
                codedfilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等
                codedfilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
            }

            resp.addHeader("Content-Disposition", "attachment; filename=\"" + codedfilename + "\"");
            resp.getOutputStream().write(buffer);
        } catch (IOException e) {
            LOG.error("下载文件报错"+e.getMessage(), e);
        }
    }*/
    
    /**
     * excel模版下载
     * @param req
     * @param resp
     * @param fileName
     */
    public static void downLoadExcel(HttpServletRequest req, HttpServletResponse resp,String fileName){
        LOG.debug("下载模板文件名称："+fileName);
        try {
            //InputStream fis = new FileInputStream(System.getProperty("user.dir")+path+ fileName);
        	InputStream fis = ExcelUtil.class.getResourceAsStream("/templates/"+fileName);
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            resp.reset();
            resp.setContentType("bin");

            String fileNames = fileName;
            String agent = req.getHeader("USER-AGENT");

            String codedfilename = "";
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 != agent.indexOf("Trident")) {// ie
                String name = java.net.URLEncoder.encode(fileNames, "UTF8");
                codedfilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {// 火狐,chrome等
                codedfilename = new String(fileNames.getBytes("UTF-8"), "iso-8859-1");
            }

            resp.addHeader("Content-Disposition", "attachment; filename=\"" + codedfilename + "\"");
            resp.getOutputStream().write(buffer);
        } catch (IOException e) {
            LOG.error("下载文件报错"+e.getMessage(), e);
        }
    }

    /**
     * 导出指定的excel对象
     * @param response
     * @param wb
     */
    public static void exportExcel(HttpServletResponse response, XSSFWorkbook wb,String fileName){
        //把创建的excel写到response中
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName + ".xlsx", "UTF-8"))));
            response.setContentType("application/ms-excel;charset=UTF-8");
            wb.write(out);
        } catch (IOException e) {
            LOG.error("response获取输出流失败",e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LOG.error("response输出流关闭失败",e);
            }
        }
    }
}