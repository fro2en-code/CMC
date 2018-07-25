package com.cdc.cdccmc;

import com.alibaba.fastjson.JSON;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.print.PritCirculateOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jerry
 * @date 2018/1/24 15:01
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class rabbitMqTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void jsonTest(){
        PritCirculateOrder domain = new PritCirculateOrder();
        domain.setTitle("包装流转单");
        domain.setBarCode("CMCCG2017083007");
        domain.setSendLocation("CMC");
        domain.setReceiveByShopFlag("SH027");
        domain.setTransactionType("出库");
        domain.setCmcReceiveFlag("YES");
        domain.setCmcReceiveCode("Code2");
        domain.setDescription("描述信息");
        domain.setCarNo("苏A1278888");
        domain.setTransportCompany("顺丰");
        domain.setConfirm("李书军");
        domain.setContact("15810665042");
        domain.setSenderConfirm("sender");
        domain.setSenderConfirmDate("2017-09-09");
        domain.setReceiverConfirm("LISHUJUN");
        domain.setReceiverConfirmDate("2017-10-11");
        domain.setBillNumber("A123433");
        domain.setOrgId("acbdefg");
        List<Map<String,Object>> list = new ArrayList<>();
        Integer i =0;
        while (i<21){
            CirculateDetail o = new CirculateDetail();
            o.setContainerCode("包装代码"+i);
            o.setContainerName("包装名称"+i);
            o.setContainerSpecification("规格x规格"+i);
            o.setPlanNumber(i+1);
            o.setSendNumber(i-1);
            o.setRemark("备注"+i);

            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",String.valueOf(i));
            map.put("code",o.getContainerCode());
            map.put("name",o.getContainerName());
            map.put("size",o.getContainerSpecification());
            map.put("planCount",o.getPlanNumber());
            map.put("sendCount",o.getSendNumber());
            map.put("remark",o.getRemark());
            list.add(map);
            i++;
        }
        String s = JSON.toJSONString(domain);
        rabbitTemplate.convertAndSend("orderExchange","topic.order.print",s);

        PritCirculateOrder orderDto = JSON.parseObject(s,PritCirculateOrder.class);
    }


    public static List makeDetailList(){
        List<Map<String, Object>> detail = new ArrayList<Map<String, Object>>();
        int code =1000;
        for(int i=0; i<10; i++){
            code++;
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("id",String.valueOf(i));
            map.put("code",String.valueOf(code));
            map.put("name",String.valueOf(code));
            map.put("size","10");
            map.put("planCount","20");
            map.put("sendCount","30");
            map.put("receiveCount","40");
            map.put("remark","remake");
            detail.add(map);
        }

        return detail;
    }
}
