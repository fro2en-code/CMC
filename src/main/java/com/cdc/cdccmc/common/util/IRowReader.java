package com.cdc.cdccmc.common.util;

import java.util.Map;

public interface IRowReader {

    /**业务逻辑实现方法
     * @param sheetIndex
     * @param curRow
     */
    //public  void getRows(int sheetIndex,int curRow, List<Map<Integer, String>> rowlist);
    public  void getRows(int sheetIndex,int curRow, Map<Integer, String> mapData);
}
