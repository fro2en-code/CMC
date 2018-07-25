package com.cdc.cdccmc.common.util;


import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.esotericsoftware.minlog.Log;

public class Excel2007Reader extends DefaultHandler{

    private static String cs;
    private static StylesTable stylesTable;
    private static Map<String, String> map = new HashMap<String, String>();

    //共享字符串表
    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;

    private Map<Integer, String> mapData = new HashMap<Integer, String>();

    private int curRow = 0;
    private int curCol = 0;

    // 定义前一个元素和当前元素的位置，用来计算其中空的单元格数量，如A6和A8等
    private String preRef = null, ref = null;
    // 定义该文档一行最大的单元格数，用来补全一行最后可能缺失的单元格
    private String maxRef = null;

    private CellDataType nextDataType = CellDataType.SSTINDEX;
    private final DataFormatter formatter = new DataFormatter();
    private short formatIndex;
    private String formatString;



    // 用一个enum表示单元格可能的数据类型
    enum CellDataType {
        BOOL, ERROR, FORMULA, INLINESTR, SSTINDEX, NUMBER, DATE, NULL
    }


    private int sheetIndex = -1;
    private IRowReader rowReader;
    public void setRowReader(IRowReader rowReader){
        this.rowReader = rowReader;
    }

    /**
     * 处理一个sheet
     * @param filename
     * @throws Exception
     */
    public void processOneSheet(String filename) throws Exception {

        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        stylesTable = r.getStylesTable();
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            sheetIndex++;
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }

    }

    /**
     * 处理所有sheet
     * @param filename
     * @throws Exception
     */
    public void process(String filename) throws Exception{
       try {
			OPCPackage pkg = OPCPackage.open(filename);
	        XSSFReader r = new XSSFReader(pkg);
	        stylesTable = r.getStylesTable();
	        SharedStringsTable sst = r.getSharedStringsTable();
	
	        XMLReader parser = fetchSheetParser(sst);
	
	        Iterator<InputStream> sheets = r.getSheetsData();
	        while (sheets.hasNext()) {
	            sheetIndex++;
	            InputStream sheet = sheets.next();
	            InputSource sheetSource = new InputSource(sheet);
	            parser.parse(sheetSource);
	            sheet.close();
	        }
		} catch (Exception e) {
			Log.error(e.getMessage(), e);
			throw e;
		}
    }

    /**
     * 获取解析器
     *
     * @param sst
     * @return
     * @throws org.xml.sax.SAXException
     */
    public XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * 解析一个element的开始时触发事件
     */
    public void startElement(String uri, String localName, String name,
                             Attributes attributes) throws SAXException {

        map.put("flag", "start");
        // c => cell
        if (name.equals("c")) {
            // 前一个单元格的位置
            if (preRef == null) {
                preRef = attributes.getValue("r");
            } else {
                preRef = ref;
            }
            // 当前单元格的位置
            ref = attributes.getValue("r");

            setNextDataType(attributes);

            // Figure out if the value is an index in the SST
            String cellType = attributes.getValue("t");
            if (cellType == null) { //处理空单元格问题
                nextIsString = true;
                cs = "x";
            } else if (cellType != null && cellType.equals("s")) {
                cs = "s";
                nextIsString = true;
            } else {
                nextIsString = false;
                cs = "";
            }
        }
        // Clear contents cache
        lastContents = "";
    }

    /**
     * 根据element属性设置数据类型
     *
     * @param attributes
     */
    public void setNextDataType(Attributes attributes) {

        nextDataType = CellDataType.NUMBER;
        formatIndex = -1;
        formatString = null;
        String cellType = attributes.getValue("t");
        String cellStyleStr = attributes.getValue("s");
        if ("b".equals(cellType)) {
            nextDataType = CellDataType.BOOL;
        } else if ("e".equals(cellType)) {
            nextDataType = CellDataType.ERROR;
        } else if ("inlineStr".equals(cellType)) {
            nextDataType = CellDataType.INLINESTR;
        } else if ("s".equals(cellType)) {
            nextDataType = CellDataType.SSTINDEX;
        } else if ("str".equals(cellType)) {
            nextDataType = CellDataType.FORMULA;
        }
        if (cellStyleStr != null) {
            int styleIndex = Integer.parseInt(cellStyleStr);
            XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
            formatIndex = style.getDataFormat();
            formatString = style.getDataFormatString();
            if ("m/d/yy".equals(formatString)) {
                nextDataType = CellDataType.DATE;
                formatString = "yyyy-MM-dd";
            }
            if (formatString == null) {
                nextDataType = CellDataType.NULL;
                formatString = BuiltinFormats.getBuiltinFormat(formatIndex);
            }
        }
    }

    /**
     * 解析一个element元素结束时触发事件
     */
    public void endElement(String uri, String localName, String name)
            throws SAXException {
        // Process the last contents as required.
        // Do now, as characters() may be called more than once
        String flag = (String) map.get("flag");

        if (nextIsString) {
            if ("s".equals(cs)) {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                nextIsString = false;
            }
            if ("c".equals(name) && "x".equals(cs)) {
                if ("start".equals(flag)) {
                    mapData.put(curCol, "");
                    curCol++;
                }
            }
        }

        map.put("flag", "end");

        // v => contents of a cell
        // Output after we've seen the string contents
        if ("v".equals(name) || "t".equals(name)) {
            String value = this.getDataValue(lastContents.trim(), "");
            // 补全单元格之间的空单元格
            if(!"A1".equals(ref)&&mapData.size()==0){
                //取得的当前单元格是一行中第一个有值的单元格,并且该单元格不为A列.
                int len = countNullCell(ref, "A1");
                for (int i = 0; i < len+1; i++) {
                    mapData.put(curCol, "");
                    curCol++;
                }
            }

            if (!ref.equals(preRef)) {
                //获取空单元格数量以""补齐
                int len = countNullCell(ref, preRef);
                for (int i = 0; i < len; i++) {
                    mapData.put(curCol, "");
                    curCol++;
                }
            }
            mapData.put(curCol, value);
            curCol++;
        } else {
            // 如果标签名称为 row，这说明已到行尾，调用 optRows() 方法
            if (name.equals("row")) {
                String value = "";
                // 默认第一行为表头，以该行单元格数目为最大数目
                if (curRow == 0) {
                    maxRef = ref;
                }

                rowReader.getRows(sheetIndex,curRow,mapData);
                // 一行的末尾重置一些数据
                mapData = new HashMap<Integer, String>();
                curRow++;
                curCol = 0;
                preRef = null;
                ref = null;
            }
        }
    }

    /**
     * 根据数据类型获取数据
     *
     * @param value
     * @param thisStr
     * @return
     */
    public String getDataValue(String value, String thisStr)

    {
        switch (nextDataType) {
            // 这几个的顺序不能随便交换，交换了很可能会导致数据错误
            case BOOL:
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;
            case ERROR:
                thisStr = "\"ERROR:" + value.toString() + '"';
                break;
            case FORMULA:
                thisStr = '"' + value.toString() + '"';
                break;
            case INLINESTR:
                XSSFRichTextString rtsi = new XSSFRichTextString(
                        value.toString());
                thisStr = rtsi.toString();
                rtsi = null;
                break;
            case SSTINDEX:
                String sstIndex = value.toString();
                thisStr = value.toString();
                break;
            case NUMBER:
                if (formatString != null) {
                    thisStr = formatter.formatRawCellContents(
                            Double.parseDouble(value), formatIndex,
                            formatString).trim();
                } else {
                    thisStr = value;
                }
                thisStr = thisStr.replace("_", "").trim();
                break;
            case DATE:
                try {
                    thisStr = formatter.formatRawCellContents(Double.parseDouble(value), formatIndex, "yyyy-MM-dd");
                } catch (NumberFormatException ex) {
                    thisStr = value.toString();
                }
                thisStr = thisStr.replace(" ", "");
                break;
            default:
                thisStr = "";
                break;
        }
        return thisStr;
    }

    /**
     * 获取element的文本数据
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        lastContents += new String(ch, start, length);
    }

    /**
     * 计算两个单元格之间的单元格数目(同一行)
     *
     * @param ref 当前单元格
     * @param preRef 最后一个单元格
     * @return
     */
    public int countNullCell(String ref, String preRef) {
        // excel2007最大行数是1048576，最大列数是16384，最后一列列名是XFD
        String xfd = ref.replaceAll("\\d+", "");
        String xfd_1 = preRef.replaceAll("\\d+", "");

        xfd = fillChar(xfd, 3, '@', true);
        xfd_1 = fillChar(xfd_1, 3, '@', true);

        char[] letter = xfd.toCharArray();
        char[] letter_1 = xfd_1.toCharArray();
        int res = (letter[0] - letter_1[0]) * 26 * 26
                + (letter[1] - letter_1[1]) * 26
                + (letter[2] - letter_1[2]);
        return res - 1;
    }

    /**
     * 字符串的填充
     *
     * @param str
     * @param len
     * @param let
     * @param isPre
     * @return
     */
    String fillChar(String str, int len, char let, boolean isPre) {
        int len_1 = str.length();
        if (len_1 < len) {
            if (isPre) {
                for (int i = 0; i < (len - len_1); i++) {
                    str = let + str;
                }
            } else {
                for (int i = 0; i < (len - len_1); i++) {
                    str = str + let;
                }
            }
        }
        return str;
    }

}