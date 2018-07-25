package com.cdc.cdccmc.service;

import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.domain.Car;
import com.cdc.cdccmc.service.basic.CarService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CarServiceTest {
    @Autowired
    private CarService carService;

    @Test
    public void testAdd(){
        Car car = new Car();
        car.setCarNo("车牌1");
        car.setShipperId(UUIDUtil.creatUUID());
        car.setShipperName("承运商1");
        car.setDrivingLicense("License1");
        car.setCreateAccount(UUIDUtil.creatUUID());
        java.util.Date date = new java.util.Date();
        car.setLicenseValidDate(new Date(date.getTime()));
        car.setCreateRealName("测试1");
    }

    @Test
    public void testUpdate(){
        Car car = new Car();
        car.setCarNo("车牌1");
        car.setShipperId(UUIDUtil.creatUUID());
        car.setShipperName("承运商2");
        car.setDrivingLicense("License2");
        car.setModifyAccount(UUIDUtil.creatUUID());
        java.util.Date date = new java.util.Date();
        car.setLicenseValidDate(new Date(date.getTime()));
        car.setModifyRealName("测试2");
//        carService.updateCar(car);
    }

    @Test
    public void testBatchInsert(){
        /*Map[] maps = new HashMap[10000];
        java.util.Date date = new java.util.Date();
        for (int i=0;i<5;i++){
            Map m = new HashMap();
            m.put("carNo","车牌"+i);
            m.put("orgId",String.valueOf(i));
            m.put("shipperId",UUIDUtil.creatUUID());
            m.put("shipperName","承运商"+i);
            m.put("drivingLicense","License"+i);
            m.put("createAccount",UUIDUtil.creatUUID());
            m.put("licenseValidDate",new Date(date.getTime()));
            m.put("createRealName","测试"+i);
            maps[i] = m;
        }
        carService.batchInsertCar(maps);*/
        List<Car> carList = new ArrayList<>();
        for (int i=0;i<5;i++) {
            Car car = new Car();
            car.setCarNo("车牌"+i);
            car.setShipperId(UUIDUtil.creatUUID());
            car.setShipperName("承运商"+i);
            car.setDrivingLicense("License"+i);
            car.setCreateAccount(UUIDUtil.creatUUID());
            java.util.Date date = new java.util.Date();
            car.setLicenseValidDate(new Date(date.getTime()));
            car.setCreateRealName("测试"+i);
            carList.add(car);
        }
//        carService.batchInsertCar(carList);
    }
}
