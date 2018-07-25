package com.cdc.cdccmc;

import com.cdc.cdccmc.common.enums.MaintainState;
import com.cdc.cdccmc.common.enums.OrgType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jerry
 * @date 2018/1/26 16:55
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileTest {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void creatDirTest(){
        File file = new File(" d:\\cmc\\upload\\xlsx\\20180126165311[BatchInsert-Container][a].xlsx");
        File fileParent = file.getParentFile();
        if (!fileParent.exists()){
            fileParent.mkdirs();
        }
    }

    @Test
    public void testEnum(){
        for (MaintainState s: MaintainState.values()) {
            System.out.println(s.getCode()+"***"+s.getState());
        }
    }

    @Test
    public void testTemplate(){
        String sql = "select org_name from T_SYSTEM_ORG where org_name in ('虚拟报废库','2') ";
        List<String> list = namedParameterJdbcTemplate.queryForList(sql,new HashMap<>(),String.class);
        System.out.println(list.size());
    }

    @Test
    public void testString(){
        String s = "'1','2',";
        s = s.substring(0,s.length()-1);
        System.out.println(s);
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        System.out.println(list.toString());
        StringBuffer buffer = new StringBuffer("fasdfasdf");
        buffer.delete(0,buffer.length());
        System.out.println(buffer.toString()+"****");


    }

}
