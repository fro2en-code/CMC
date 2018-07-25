package com.cdc.cdccmc.runnable;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.DoorEquipmentService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DoorSendEpcRunnable implements Runnable {
    @Autowired
    private DoorEquipmentService doorEquipmentService;
    private AjaxBean ajaxBean;
    private SystemUser sessionUser;
    private List<String> epcIdList;
    public DoorSendEpcRunnable(AjaxBean ajaxBean, SystemUser sessionUser, List<String> epcIdList)
    {
        this.ajaxBean = ajaxBean;
        this.sessionUser = sessionUser;
        this.epcIdList = epcIdList;
    }
    @Override
    public void run() {
        System.out.println("----333");
//        doorEquipmentService.sendEpcByThread(ajaxBean,sessionUser,epcIdList);
        System.out.println("----444");
    }
}
